package com.example.notemaster;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class ResetPwdActivity extends AppCompatActivity {

    private int vcode;

    private ImageView backBtn;
    private EditText edtAccount,edtPwd,edtEmail,edtVcode;
    private Button confirm;
    private GetVcodeButton getVcode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpwd);
        UserDatabaseHelper udh = new UserDatabaseHelper(this);
        backBtn = findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        getVcode = findViewById(R.id.getcode);
        getVcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vcode = getVcode.getVcode();
            }
        });

        confirm = findViewById(R.id.confirm_button);
        edtAccount = findViewById(R.id.edt_account);
        edtPwd = findViewById(R.id.edt_pwd);
        edtEmail = findViewById(R.id.edt_mail);
        edtVcode = findViewById(R.id.vcode);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtAccount.getText().toString();
                String pwd = edtPwd.getText().toString();
                String mail = edtEmail.getText().toString();
                int input_vcode = Integer.parseInt(edtVcode.getText().toString());
                int result = udh.verifyUser(name,pwd);
                if(result != 0){
                    if (vcode == input_vcode && validateEmail(mail)){
                        udh.updateUser(name,pwd,mail);
                        Toast.makeText(ResetPwdActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        Toast.makeText(ResetPwdActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private boolean validateEmail(String str){
        // 执行邮箱验证
        if(isEmail(str)) {
            return true;
        }
        else {
            Toast.makeText(ResetPwdActivity.this, "邮箱格式错误", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static boolean isEmail(String input) {
        if (input == null || input.length() < 1 || input.length() > 256) {
            return false;
        }
        Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        return pattern.matcher(input).matches();
    }
}
