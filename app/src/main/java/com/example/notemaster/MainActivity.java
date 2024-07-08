package com.example.notemaster;

import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

public class MainActivity extends AppCompatActivity {
    private TextView ctv;
    private int cntdown = 3;
    Timer timer = new Timer();
    Timer timer1 = new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);
        ctv = findViewById(R.id.circleTextView);
        ctv.setText("3");
        TimerTask tmtask = new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "启动动画播放完毕");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        TimerTask tmtask1 = new TimerTask() {
            @Override
            public void run() {
                ctv.setText(String.valueOf(--cntdown));
            }
        };
        timer.schedule(tmtask, 3000);
        timer1.scheduleAtFixedRate(tmtask1, 1000, 1000);


    }

    @Override
    protected void onStop () {
        super.onStop();
        timer.cancel();
        timer1.cancel();
    }
}