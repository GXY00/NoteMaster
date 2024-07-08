package com.example.notemaster;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.widemouth.library.toolitem.WMToolAlignment;
import com.widemouth.library.toolitem.WMToolBackgroundColor;
import com.widemouth.library.toolitem.WMToolBold;
import com.widemouth.library.toolitem.WMToolImage;
import com.widemouth.library.toolitem.WMToolItalic;
import com.widemouth.library.toolitem.WMToolItem;
import com.widemouth.library.toolitem.WMToolListBullet;
import com.widemouth.library.toolitem.WMToolListClickToSwitch;
import com.widemouth.library.toolitem.WMToolListNumber;
import com.widemouth.library.toolitem.WMToolQuote;
import com.widemouth.library.toolitem.WMToolSplitLine;
import com.widemouth.library.toolitem.WMToolStrikethrough;
import com.widemouth.library.toolitem.WMToolTextColor;
import com.widemouth.library.toolitem.WMToolTextSize;
import com.widemouth.library.toolitem.WMToolUnderline;
import com.widemouth.library.wmview.WMEditText;
import com.widemouth.library.wmview.WMToolContainer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import tools.AlarmReceiver;
import tools.ApiCallback;
import tools.ApiUtils;
import tools.Note;
import tools.NoteDatabase;

public class EditNote extends AppCompatActivity {

    private boolean alarmset = false;
    private Calendar calendar;
    private Note note;
    private EditText edt;
    private ImageView backBtn, moreBtn;
    private View popupWindow_view;
    private TextView loctext, ttext;
    private PopupWindow popupWindow;
    private Button completeBtn;
    private String city,password;
    private ApiUtils apiUtils;
    private NoteDatabase ndb;

    //富文本编辑器初始化
    WMEditText editText;//编辑区
    WMToolContainer toolContainer;//工具栏
    //具体工具
    private WMToolItem toolBold = new WMToolBold();
    private WMToolItem toolItalic = new WMToolItalic();
    private WMToolItem toolUnderline = new WMToolUnderline();
    private WMToolItem toolStrikethrough = new WMToolStrikethrough();
    private WMToolItem toolImage = new WMToolImage();
    private WMToolItem toolTextColor = new WMToolTextColor();
    private WMToolItem toolBackgroundColor = new WMToolBackgroundColor();
    private WMToolItem toolTextSize = new WMToolTextSize();
    private WMToolItem toolListNumber = new WMToolListNumber();
    private WMToolItem toolListBullet = new WMToolListBullet();
    private WMToolItem toolAlignment = new WMToolAlignment();
    private WMToolItem toolQuote = new WMToolQuote();
    private WMToolItem toolListClickToSwitch = new WMToolListClickToSwitch();
    private WMToolItem toolSplitLine = new WMToolSplitLine();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_note);
        apiUtils = new ApiUtils(this);
        ndb = new NoteDatabase(this);
        initEditText();
        initText();
        getLocation();
        edt = findViewById(R.id.noteTitle);
        backBtn = findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        moreBtn = findViewById(R.id.morebtn);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow_view = getLayoutInflater().inflate(R.layout.more_menu, null, false);
                popupWindow = new PopupWindow(popupWindow_view, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAsDropDown(moreBtn, 0, 0);
                Button setAlarm = popupWindow_view.findViewById(R.id.setAlarm);
                setAlarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmset = true;
                        showDatePickerDialog();
                    }
                });
               Button setPwd = popupWindow_view.findViewById(R.id.setPwd);
               setPwd.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       new AlertDialog.Builder(EditNote.this)
                               .setTitle("设置密码")
                               .setView(new EditText(EditNote.this))
                               .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       Toast.makeText(EditNote.this,"成功设置密码",Toast.LENGTH_SHORT).show();
                                   }
                               })
                               .setNegativeButton("取消",null)
                               .create()
                               .show();
                   }
               });
            }
        });



        completeBtn = findViewById(R.id.completeButton);
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alarmset) setAlarm(calendar);
                saveAndSendNote();
            }
        });

        // 检查是否存在note_id额外信息
        long noteId = getIntent().getLongExtra("note_id", -1);
        if (noteId != -1) {
            // 加载现有笔记数据以便编辑
            loadNoteData(noteId);
        }
    }

    private void showDatePickerDialog() {
        // 创建DatePickerDialog实例
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // 更新全局的Calendar对象
                        calendar.set(year,month,dayOfMonth);
                        // 用户设置了日期后，可以在这里调用时间选择器
                        showTimePickerDialog(calendar);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog(Calendar c) {
        // 创建TimePickerDialog实例
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // 设置是否使用24小时制
        );
        timePickerDialog.show();
    }

    private void setAlarm(Calendar calendar) {
        Log.e(TAG,"开始设置闹钟");
        // 创建一个Intent，当闹钟时间到达时触发
        Intent intent = new Intent(this, AlarmReceiver.class);
        String message = editText.getText().toString();
        intent.putExtra("message", message);
        Log.e(TAG,"设置闹钟");
        // 创建一个PendingIntent，用于AlarmManager
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }else{
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // 获取AlarmManager服务
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // 计算触发时间（毫秒）
        long triggerTime = calendar.getTimeInMillis();

        // 设置闹钟
        // AlarmManager.RTC_WAKEUP 表示使用实际时间，并且会唤醒设备
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

    }

    private void initEditText(){
        editText = findViewById(R.id.WMEditText);

        toolContainer = findViewById(R.id.WMToolContainer);

        toolContainer.addToolItem(toolTextColor);
        toolContainer.addToolItem(toolBackgroundColor);
        toolContainer.addToolItem(toolTextSize);
        toolContainer.addToolItem(toolBold);
        toolContainer.addToolItem(toolItalic);
        toolContainer.addToolItem(toolUnderline);
        toolContainer.addToolItem(toolStrikethrough);
        toolContainer.addToolItem(toolListNumber);
        toolContainer.addToolItem(toolListBullet);
        toolContainer.addToolItem(toolAlignment);
        toolContainer.addToolItem(toolQuote);
        toolContainer.addToolItem(toolListClickToSwitch);
        toolContainer.addToolItem(toolSplitLine);

        editText.setupWithToolContainer(toolContainer);

    }

    private void initText() {

        ttext = findViewById(R.id.timetext);

        // 获取当前时间
        Date currentTime = new Date();

        // 设置日期时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        // 格式化当前时间
        String formattedTime = sdf.format(currentTime);

        // 输出格式化后的时间
        ttext.setText(formattedTime);
    }

    private void getLocation() {
        CountDownLatch latch = new CountDownLatch(1);
        apiUtils.getLocation(new ApiCallback() {
            @Override
            public void onSuccess(String loc) {
                city = loc;
                latch.countDown();
            }

            @Override
            public void onNewsSuccess(String[] news) {

            }

            @Override
            public void onError(String error) {
                Log.e(TAG,"错误："+error);
                latch.countDown();
            }
        });

        try {
            latch.await(); // 等待异步操作完成
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        loctext = findViewById(R.id.loctext);
        Log.e(TAG,"城市："+city);
        loctext.setText(city);
    }

    private void loadNoteData(long noteId){
        this.note = ndb.getNoteById(noteId);
        edt.setText(note.getTitle());
        String html = note.getHtml();
        editText.fromHtml(html);
    }

    private void saveAndSendNote(){
        String title = edt.getText().toString();
        String content = editText.getText().toString();
        String html = editText.getHtml();
        String time = ttext.getText().toString();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("content", content);
        resultIntent.putExtra("html", html);
        resultIntent.putExtra("time", time);
        long noteId = getIntent().getLongExtra("note_id",-1L);
        if (noteId == -1L) {
            Log.e(TAG,"发送新笔记");
            // 对于新笔记，直接传递数据
            setResult(RESULT_OK, resultIntent);
        } else {
            Log.e(TAG,"更新笔记");
            // 对于现有笔记，传递ID
            resultIntent.putExtra("note_id", noteId);
            setResult(RESULT_OK, resultIntent);
        }
        finish();
    }
}