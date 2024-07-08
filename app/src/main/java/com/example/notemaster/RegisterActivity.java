package com.example.notemaster;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;

import java.util.Random;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {
    private EditText edtAccount,edtPwd,edtConfirmPwd,edtPhoneNum,vCode;
    private Button getVcode,RegisterBtn;
    private ImageView backBtn;
    private CheckBox agreebtn;
    private TextView agreement;
    private String name,password,email;
    private int gvcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        UserDatabaseHelper udh = new UserDatabaseHelper(this);
        backBtn = findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getVcode = findViewById(R.id.getvcode);
        getVcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterActivity.this, "获取验证码", Toast.LENGTH_SHORT).show();
                //调取系统的NotificationManager服务
                final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                //创建NotificationChannel对象，实现创建通知渠道
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel
                            ("1","CHannel1",NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                //创建一个Notification对象
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Notification.Builder notification = new Notification.Builder(RegisterActivity.this,"1");
                    notification.setSmallIcon(R.drawable.wx);
                    notification.setContentTitle("验证码");
                    int vc = generateVcode();
                    notification.setContentText(String.valueOf(vc));
                    notification.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    //设置发送时间
                    notification.setWhen(System.currentTimeMillis());
                    notificationManager.notify(1,notification.build());
                }

            }
        });
        agreement = findViewById(R.id.agreement);
        agreebtn = findViewById(R.id.agreeBtn);
        agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建对话框对象
                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                alertDialog.setIcon(R.drawable.logo);
                alertDialog.setTitle("用户注册协议");
                alertDialog.setMessage("这是一份用户注册协议");
                //设置取消按钮
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "不同意",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                System.exit(0);
                            }
                        });
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "同意",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                agreebtn.setChecked(true);
                            }
                        });
                alertDialog.show();
            }
        });
        RegisterBtn = findViewById(R.id.registerBtn);
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtAccount = findViewById(R.id.edt_account);
                name = edtAccount.getText().toString();
                if(validateEmail() && validatePwd() && validateVcode() && !name.isEmpty()) {
                    if (agreebtn.isChecked()){
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        udh.addUser(name,email,password," ");
                        finish();
                    }
                    else Toast.makeText(RegisterActivity.this, "请阅读协议", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RegisterActivity.this, "邮箱/密码/验证码有误", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validateVcode() {
        vCode = findViewById(R.id.vcode);
        if(vCode.getText().toString().isEmpty()) return false;
        Log.e(TAG,vCode.getText().toString());
        return gvcode == Integer.parseInt(vCode.getText().toString());
    }

    private int generateVcode() {
        Random random = new Random();

        // 生成一个 4 位的随机数字
        int min = 1000; // 最小值为 1000
        int max = 9999; // 最大值为 9999
        int randomNumber = random.nextInt(max - min + 1) + min;
        gvcode = randomNumber;
        return randomNumber;
    }

    private boolean validateEmail(){
        // 执行邮箱验证
        edtPhoneNum = findViewById(R.id.phoneEmailNum);
        String str = edtPhoneNum.getText().toString();
        if(isEmail(str)) {
            email = str;
            return true;
        }
        else {
            Toast.makeText(RegisterActivity.this, "格式错误", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean validatePwd(){
        edtConfirmPwd  = findViewById(R.id.confirm_pwd);
        edtPwd = findViewById(R.id.edt_pwd);
        String pwd1 = edtPwd.getText().toString();
        String pwd2 = edtConfirmPwd.getText().toString();
        if (!pwd1.isEmpty() && !pwd2.isEmpty() && pwd1.equals(pwd2)){
            password = pwd1;
            return true;
        }
        else return false;
    }

    public static boolean isEmail(String input) {
        if (input == null || input.length() < 1 || input.length() > 256) {
            return false;
        }
        Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        return pattern.matcher(input).matches();
    }
}
