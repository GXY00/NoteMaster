package com.example.notemaster;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;

public class GetVcodeButton extends androidx.appcompat.widget.AppCompatButton {
    private Context mContext;
    public int vcode;
    public GetVcodeButton(Context context) {
        super(context);
        mContext = context;
    }

    public GetVcodeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public GetVcodeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if( event.getAction() == MotionEvent.ACTION_UP){
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Log.e(TAG, "eventX = " + eventX + "; eventY = " + eventY);
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 100;
            if (rect.contains(eventX, eventY)){
                //调取系统的NotificationManager服务
                Log.e(TAG,"1");
                final NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
                //创建NotificationChannel对象，实现创建通知渠道
                Log.e(TAG,"2");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel
                            ("1","CHannel1",NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                //创建一个Notification对象
                Log.e(TAG,"3");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Notification.Builder notification = new Notification.Builder(mContext,"1");
                    notification.setSmallIcon(R.drawable.wx);
                    notification.setContentTitle("验证码");
                    int vc = generateVcode();
                    notification.setContentText(String.valueOf(vc));
                    notification.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    //设置发送时间
                    notification.setWhen(System.currentTimeMillis());
                    notificationManager.notify(1,notification.build());
                    Log.e(TAG,"4");
                }

            }


        }
        return super.onTouchEvent(event);
    }

    public int getVcode(){
        return vcode;
    }

    private int generateVcode() {
        Random random = new Random();

        // 生成一个 4 位的随机数字
        int min = 1000; // 最小值为 1000
        int max = 9999; // 最大值为 9999
        int randomNumber = random.nextInt(max - min + 1) + min;
        vcode = randomNumber;
        return randomNumber;
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }
}
