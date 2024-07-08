package com.example.notemaster;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

import tools.FileCacheUtils;

public class SettingActivity extends AppCompatActivity {
    private ImageView backBtn;
    private TextView phoneNum,cacheText;
    private FileCacheUtils fcu;
    private String[] units = {
            "B", "KB", "MB", "GB", "TB"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //fcu = new FileCacheUtils(SettingActivity.this);
        //显示缓存
        //cacheText = findViewById(R.id.cachetext);
        //String cache = fcu.getSize();
        //cacheText.setText(cache);
        backBtn = findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //隐式intent
        phoneNum = findViewById(R.id.phonenum);
        phoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:19358489682"));
                startActivity(intent);
            }
        });



    }


    private void queryStorage(){
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());

        //存储块总数量
        long blockCount = statFs.getBlockCount();
        //块大小
        long blockSize = statFs.getBlockSize();
        //可用块数量
        long availableCount = statFs.getAvailableBlocks();
        //剩余块数量，注：这个包含保留块（including reserved blocks）即应用无法使用的空间
        long freeBlocks = statFs.getFreeBlocks();
        //这两个方法是直接输出总内存和可用空间，也有getFreeBytes
        //API level 18（JELLY_BEAN_MR2）引入
        long totalSize = statFs.getTotalBytes();
        long availableSize = statFs.getAvailableBytes();

        Log.d("statfs","total = " + getUnit(totalSize));
        Log.d("statfs","availableSize = " + getUnit(availableSize));

        //这里可以看出 available 是小于 free ,free 包括保留块。
        Log.d("statfs","total = " + getUnit(blockSize * blockCount));
        Log.d("statfs","available = " + getUnit(blockSize * availableCount));
        Log.d("statfs","free = " + getUnit(blockSize * freeBlocks));
    }


    /**
     * 单位转换
     */
    private String getUnit(float size) {

        int index = 0;
        while (size > 1024 && index < 4) {

            size = size / 1024;
            index++;
        }
        return String.format(Locale.getDefault(), " %.2f %s", size, units[index]);
    }

}