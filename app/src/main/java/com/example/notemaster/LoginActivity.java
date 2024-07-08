package com.example.notemaster;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditTextWithDel pwd;
    private TextView forgetPwd;
    private TextView register;
    private ImageView QQLogin,WXLogin;
    private Button LoginBtn;
    private CheckBox RememberBtn;
    private boolean rememberAccount = false;
    private String name,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UserDatabaseHelper udh = new UserDatabaseHelper(this);
        ifHasAccountStored();
        username = findViewById(R.id.edt_account);
        pwd = findViewById(R.id.edt_pwd);
        if (name != null && password !=null) {
            username.setText(name);
            pwd.setText(password);
        }
        forgetPwd = findViewById(R.id.forgetPwd);
        forgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ResetPwdActivity.class);
                startActivity(intent);
            }
        });

        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        RememberBtn = findViewById(R.id.rememberbtn);
        RememberBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(RememberBtn.isChecked()) rememberAccount = true;
            }
        });
        LoginBtn = findViewById(R.id.login);
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = username.getText().toString();
                password = pwd.getText().toString();
                int result = udh.verifyUser(name,password);
                switch (result){
                    case 0:
                        Toast.makeText(LoginActivity.this,"不存在该用户",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        if (rememberAccount) saveAccountInfo(name,password);
                        Intent intent = new Intent(LoginActivity.this,IndexActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putCharSequence("name",name);
                        bundle.putCharSequence("password",password);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        break;
                    case 2:
                        Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void ifHasAccountStored(){
        // 检查user_data.json文件是否存在
        File file = new File(getFilesDir(), "user_data.json"); // 使用getFilesDir()获取应用专属的内部存储目录
        if (file.exists()) {
            // 文件存在，读取文件内容
            String jsonString = readTextFromFile(file);
            if (jsonString != null) {
                // 解析JSON字符串
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    // 提取用户名和密码
                    name = jsonObject.getString("username");
                    password = jsonObject.getString("password");

                    // 输出用户名和密码
                    Log.i("AccountInfo", "Username: " + name + ", Password: " + password);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("AccountInfo", "Error parsing JSON", e);
                }
            }
        } else {
            // 文件不存在
            Log.i("AccountInfo", "user_data.json does not exist");
        }
    }

    private String readTextFromFile(File file) {
        // 读取文件并返回字符串
        try {
            //创建一个 FileInputStream 对象，用于从文件读取字节
            FileInputStream fis = new FileInputStream(file);
            //创建一个 InputStreamReader 对象，将 FileInputStream 包装起来，使其能够读取字符而不是字节
            InputStreamReader isr = new InputStreamReader(fis);
            //创建一个 BufferedReader 对象，它使用 InputStreamReader 作为其底层字符流，并提供缓冲功能，以提高读取效率
            BufferedReader bufferedReader = new BufferedReader(isr);
            //创建一个 StringBuilder 对象，用于构建最终的字符串
            StringBuilder stringBuilder = new StringBuilder();
            //声明一个 String 类型的变量 line，用来暂存从 BufferedReader 读取的每一行文本
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            isr.close();
            fis.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("AccountInfo", "Error reading file", e);
        }
        return null;
    }

    private void saveAccountInfo(String name,String pwd) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", name);
            jsonObject.put("password", pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //将json字符串写入文件
        String jsonString = jsonObject.toString();
        try {
            FileOutputStream fos = openFileOutput("user_data.json", MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getView(){



        QQLogin = findViewById(R.id.qqlogin);
        WXLogin = findViewById(R.id.wxlogin);
    }


}
