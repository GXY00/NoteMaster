package com.example.notemaster;

import static android.content.ContentValues.TAG;

import static com.qweather.sdk.view.HeContext.context;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.zip.Inflater;

import tools.ApiCallback;
import tools.ApiUtils;
import tools.HeaderUtils;
import tools.Note;
import tools.NoteAdapter;
import tools.NoteDatabase;

public class IndexActivity  extends AppCompatActivity implements NoteAdapter.OnNoteItemClickListener , NoteAdapter.OnNoteItemLongClickListener{
    //fragment页面
    private NoteAdapter adapter; // 笔记适配器
    private ListView lv;
    private List<Note> noteList = new ArrayList<>(); // 笔记列表
    // 创建数据库操作对象，打开数据库连接
    NoteDatabase op;
    private long backPressedTime;
    private Toast backToast;
    PopupWindow popupWindow;
    private View popupWindow_view;
    private RelativeLayout navHead;
    private ImageView slider_header;
    private TextView slider_username,slider_signature;
    private ImageButton setBtn,navBtn,newNoteBtn;
    private Button addFolderBtn;
    private Set<String> categories;
    private NavigationView nav_view;
    private DrawerLayout drawer_layout;
    private String[] userInfo,titles;
    private String uname,signature,upwd,headimg;
    //工具类
    private UserDatabaseHelper udh;
    private HeaderUtils headerUtils;
    private ApiUtils apiUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        HeConfig.init("HE2407030945271058", "7ea5a2c1cf43471695661dac8d70406f");
        HeConfig.switchToDevService();
        queryWeather();
        init();
        showMyDialog();
        setNoteList();
        //悬浮窗
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(nav_view_ls);
        navBtn = findViewById(R.id.mybtn);
        drawer_layout = findViewById(R.id.drawer_layout);
        navHead = (RelativeLayout) nav_view.getHeaderView(0);
        slider_username = navHead.findViewById(R.id.slider_username);
        slider_signature = navHead.findViewById(R.id.slider_signature);
        slider_header = navHead.findViewById(R.id.slider_header);
        setHeader();
        slider_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 定义选项数组
                String[] items = {"拍照", "从相册选择"};
                new AlertDialog.Builder(IndexActivity.this)
                        .setTitle("更换头像")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent Imgintent = null;
                                switch (which){
                                    case 0:
                                        Imgintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(Imgintent, 100); //启动Intent并要求返回数据
                                        break;
                                    case 1:
                                        //创建Intent并设置动作为调用系统图库
                                        Imgintent = new Intent(Intent.ACTION_GET_CONTENT);
                                        Imgintent.setType("image/*");
                                        startActivityForResult(Imgintent, 101);
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("取消",null)
                        .create()
                        .show();
            }
        });
        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(GravityCompat.START);
            }
        });

        setBtn = findViewById(R.id.settingbtn);
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });

        newNoteBtn = findViewById(R.id.newNote);
        newNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this,EditNote.class);
                startActivityForResult(intent, 102);
            }
        });
    }

    private View getHistoryTodayAndLocalNews(){
        CountDownLatch latch = new CountDownLatch(1);
        // 调用第一个API
        apiUtils.getHistoryToday(new ApiCallback() {
            @Override
            public void onSuccess(String loc) {

            }
            @Override
            public void onNewsSuccess(String[] news) {
                titles = news;
                latch.countDown();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG,"错误"+error);
                latch.countDown();
            }
        });
        try {
            latch.await(); // 等待异步操作完成
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        View dialogView = getLayoutInflater().inflate(R.layout.history_today, null, false);
        TextView t1 = dialogView.findViewById(R.id.textView1);
        TextView t2 = dialogView.findViewById(R.id.textView2);
        TextView t3 = dialogView.findViewById(R.id.textView3);
        t1.setText(titles[0]);
        t2.setText(titles[1]);
        t3.setText(titles[2]);
        return dialogView;
    }

    private void showMyDialog(){
        new AlertDialog.Builder(IndexActivity.this)
                .setTitle("历史上的今天")
                .setView(getHistoryTodayAndLocalNews())
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    NavigationView.OnNavigationItemSelectedListener nav_view_ls = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.fragment_01:
                    changeHeader();
                    break;
                case R.id.fragment_02:
                    changeInfoDialog(1);
                    break;
                case R.id.fragment_03:
                    changeInfoDialog(2);
                    break;
                case R.id.fragment_04:
                    changeInfoDialog(3);
                    break;
                case R.id.fragment_05:
                    new AlertDialog.Builder(IndexActivity.this)
                            .setTitle("退出应用")
                            .setMessage("是否确认退出?")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //关闭当前任务栈中的所有活动
                                    finishAffinity();
                                    System.exit(0);
                                }
                            })
                            .setNegativeButton("取消",null)
                            .create()
                            .show();
                    break;
            }
            return false;
        }
    };

    //更换头像
    private void changeHeader() {
        // 定义选项数组
        String[] items = {"拍照", "从相册选择"};
        new AlertDialog.Builder(IndexActivity.this)
                .setTitle("更换头像")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent Imgintent = null;
                        switch (which){
                            case 0:
                                Imgintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(Imgintent, 100); //启动Intent并要求返回数据
                                break;
                            case 1:
                                //创建Intent并设置动作为调用系统图库
                                Imgintent = new Intent(Intent.ACTION_GET_CONTENT);
                                Imgintent.setType("image/*");
                                startActivityForResult(Imgintent, 101);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消",null)
                .create()
                .show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {//判断如果拍到照片后
            Bundle bundle = data.getExtras(); //获取Intent的附加数据
            Bitmap bitmap = (Bitmap) bundle.get("data"); //获取数据并转换为Bitmap
            // 将Bitmap对象转换为Glide可加载的图片
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytes = stream.toByteArray();

            // 使用Glide加载和显示Bitmap到ImageView
            Glide.with(this)
                    .asBitmap()
                    .load(bytes)
                    .transform(new CircleCrop())//圆角显示
                    .into(slider_header);

            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));;
            headerUtils.addHeaderToPrefs(uname,uri);
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == 101 && data != null) {
            Uri selectPicture = data.getData();//获取图片Uri数据

            // 使用Glide加载和显示图片到ImageView
            Glide.with(this)
                    .load(selectPicture) // 图片的Uri地址
                    .transform(new CircleCrop())//圆角显示
                    .into(slider_header); // 要显示图片的ImageView

            headerUtils.addHeaderToPrefs(uname,selectPicture);
        }
        else if (resultCode == Activity.RESULT_OK && (requestCode == 102 || requestCode == 103) && data != null) {
            getNoteInfo(data);
        } else {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    private void init(){
        SharedPreferences prefs = getSharedPreferences("MyPrefsFolder",MODE_PRIVATE);
        //判断是否首次创建
        boolean containsCategories = prefs.contains("categories");
        if (containsCategories) {
            categories = prefs.getStringSet("categories", new HashSet<String>());
        }
        else {
            categories = new HashSet<>();
        }
        udh = new UserDatabaseHelper(this);
        headerUtils = new HeaderUtils(this);
        apiUtils = new ApiUtils(this);
        // 创建数据库操作对象，打开数据库连接
        op = new NoteDatabase(this);
    }

    private void setHeader() {
        userInfo = fetchUserInfo();
        uname = userInfo[0];
        upwd = userInfo[1];
        slider_username.setText(uname);
        //使用同步机制解决异步问题
        CountDownLatch latch = new CountDownLatch(1);
        apiUtils.getYiyan(new ApiCallback() {
            @Override
            public void onSuccess(String yiyan) {
                signature = yiyan;
                latch.countDown(); // 减少计数，表示操作完成
            }

            @Override
            public void onNewsSuccess(String[] news) {

            }

            @Override
            public void onError(String error) {
                signature = error;
                Log.e(TAG,"错误"+error);
                latch.countDown(); // 减少计数，表示操作完成
            }
        });

        try {
            latch.await(); // 等待异步操作完成
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        slider_signature.setText(signature);
        Uri savedUri = headerUtils.getSavedImageUri(uname); // 从SharedPreferences中获取保存的图片Uri
        if (savedUri != null) {
            // 使用Glide加载保存的图片Uri到ImageView slider_header
            Glide.with(this)
                    .load(savedUri)
                    .transform(new CircleCrop())
                    .into(slider_header);
        }
    }

    public void addfolderClick() {
        final EditText inputEditText = new EditText(this);
            //设置视图的宽度通常使用 setLayoutParams 方法，因为直接调用 setWidth 方法并不会改变视图的尺寸
            // 创建LayoutParams对象
           /*LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, // 宽度设置为包裹内容
                    LinearLayout.LayoutParams.WRAP_CONTENT); // 高度设置为包裹内容
            // 设置宽度为260dp
           lp.width = 260;
            // 应用LayoutParams到EditText
           inputEditText.setLayoutParams(lp);*/
        inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(this) // 'this' should be your Activity or Context
                .setTitle("新建分类")
                .setView(inputEditText) // Set the custom view with an EditText
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String folderName = inputEditText.getText().toString();
                        Toast.makeText(IndexActivity.this,"创建文件夹：" + folderName,Toast.LENGTH_SHORT).show();
                        addFolderToPrefs(folderName);
                        addNewFolderBtn(folderName);
                    }
                })
                .setNegativeButton("取消", null) // Set the cancel button
                .create()
                .show();
    }

    private void addNewFolderBtn(String folderName) {
        for (String category : categories) {
            // 创建按钮
            Button newButton = new Button(this);
            // 设置Button的属性
            newButton.setId(View.generateViewId()); // 给新Button生成一个唯一的ID
            newButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            newButton.setPadding(20, 0, 40, 0); // 左右内边距，上下内边距
            newButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF"))); // 背景颜色
            newButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.addfolder, 0, 0, 0); // 设置左侧图标
            newButton.setCompoundDrawablePadding(10); // 图标与文字间距
            newButton.setText(category); // 文字内容
            newButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // 文字大小
            // 获取popup window的根布局
            LinearLayout popupRoot = (LinearLayout) popupWindow_view.findViewById(R.id.popupLayout);
            // 将新按钮添加到popup window的根布局中
            popupRoot.addView(newButton);
        }
        // 刷新popup window，使其显示新按钮
        popupWindow.update();
    }


    private void addFolderToPrefs(String fname){
        SharedPreferences prefs = getSharedPreferences("MyPrefsFolder",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        categories.add(fname);
        editor.putStringSet("categories", categories);
        editor.apply();
        categories = prefs.getStringSet("categories", new HashSet<String>());
    }

    public void queryWeather(){
        QWeather.getWeatherNow(IndexActivity.this, "101010300", Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener(){
            public static final String TAG="he_feng_now";
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: "+ e);
            }

            @Override


            public void onSuccess(WeatherNowBean weatherBean) {
                Log.i(TAG, "getWeather onSuccess: " + new Gson().toJson(weatherBean));
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK == weatherBean.getCode()) {
                    WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                    String tianqi=now.getText();
                    String wendu=now.getTemp()+"℃";
                    String fengli=now.getWindScale();
                    String fengxiang=now.getWindDir();

                    /*tv_tianqi.setText("当前天气:"+tianqi);
                    tv_wendu.setText("当前温度:"+wendu);
                    tv_fengxiang.setText("风向："+fengxiang);
                    tv_fengli.setText("风力："+fengli+"级");*/
                    showWeather(tianqi,wendu,fengli,fengxiang);
                } else {
                    //在此查看返回数据失败的原因
                    Code code = weatherBean.getCode();
                    Log.i(TAG, "failed code: " + code);
                }
            }

        });
    };

    private void showWeather(String tianqi,String wendu,String fengli,String fengxiang){
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("2","channel2",NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String content = "当前天气:"+tianqi + "\n" +"当前温度:"+wendu+ "\n" +"风向："+fengxiang+ "\n" +"风力："+fengli+"级";
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(content);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(IndexActivity.this, "2")
                    .setContentTitle("今日天气")
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.weather)
                    .setStyle(bigTextStyle);
            Notification notification = builder.build();
            manager.notify(2,notification);
        }
    }

    //获取用户名和密码
    private String[] fetchUserInfo(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        return new String[]{bundle.getString("name"),bundle.getString("password")};
    }

    //修改个性签名/密码/昵称的页面
    private void changeInfoDialog(int type){
        String title = "";
        final EditText inputEditText = new EditText(this);
        inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        switch (type) {
            case 1:
                title = "修改昵称";
                break;
            case 2:
                title = "修改个性签名";
                break;
            case 3:
                title = "修改密码";
                break;
        }
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(inputEditText)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = inputEditText.getText().toString();
                        switch (type) {
                            case 1:
                                String id = "";
                                Cursor cursor = udh.getUserID(uname);
                                if (cursor != null && cursor.moveToFirst()) {
                                    id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                                } else {
                                    Log.d("UserDetails", "No user found with username: " + uname);
                                }

                                if (cursor != null) cursor.close();
                                udh.updateUsername(id,content);
                                break;
                            case 2:
                                udh.updateUserSignature(uname,content);
                                break;
                            case 3:
                                udh.updateUserPassword(uname,content);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消",null)
                .create()
                .show();
    }

    public void getNoteInfo(Intent intent){
        Log.e(TAG,"接收成功");
        long noteId = intent.getLongExtra("note_id",-1L);
        // 从 EditActivity 返回的内容和时间
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String html = intent.getStringExtra("html");
        String time = intent.getStringExtra("time");

        Log.e(TAG,title+" "+content+" "+time);
        if (noteId == -1) {
            Note note = new Note(title,content, time,html); // 创建笔记对象
            // 打开数据库连接，将笔记添加到数据库
            op.addNote(note);
            Log.e(TAG,"正在添加笔记");
            refreshListView(); // 刷新笔记列表
        }
        else {
            Note updateNote = new Note(title,content, time,html);
            updateNote.setId(noteId);
            op.updateNote(updateNote);
            Log.e(TAG,"正在更新笔记");
            refreshListView();
        }

    }

    public void setNoteList() {
        lv = findViewById(R.id.lv);
        adapter = new NoteAdapter(getApplicationContext(), noteList , this,this);//初始化一个笔记适配器，并将应用的上下文对象和笔记列表传递给适配器
        refreshListView();
        lv.setAdapter(adapter);
    }

    // 刷新笔记列表
    public void refreshListView() {
        Log.e(TAG,"开始刷新数据库");
        if (noteList.size() > 0) noteList.clear(); // 清空笔记列表
        noteList.addAll(op.getAllNotes()); // 获取数据库中所有笔记
        adapter.notifyDataSetChanged(); // 通知适配器数据已更改，刷新列表视图
        lv.setAdapter(adapter);
        Log.e(TAG,"刷新成功");
    }

    @Override
    public void onNoteItemClick(long noteId) {
        // 处理项点击，启动 EditActivity 并传递选定笔记以进行编辑
        Intent intent = new Intent(IndexActivity.this, EditNote.class);
        intent.putExtra("note_id", noteId);
        startActivityForResult(intent,103);
    }

    @Override
    public void onNoteItemLongClick(long noteId) {
        // 当笔记项长按时触发，显示删除确认对话框
        showDeleteConfirmationDialog(noteId);
    }

    private void showDeleteConfirmationDialog(long noteId) {
        // 创建一个AlertDialog.Builder实例，用于构建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 设置对话框消息和按钮
        builder.setMessage("确定要删除此笔记吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 在用户确认后删除笔记
                        op.deleteNoteById(noteId);
                        refreshListView();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 用户取消对话框，不执行任何操作
                    }
                });

        // 创建并显示对话框
        builder.create().show();
    }

}
