package tools;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.notemaster.R;

public class AlarmReceiver extends BroadcastReceiver {
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        // 从Intent中获取消息
        String message = intent.getStringExtra("message");

        // 创建一个通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarm_channel_id")
                .setSmallIcon(R.drawable.logo) // 通知小图标
                .setContentTitle("闹钟提醒") // 通知标题
                .setContentText(message) // 通知内容
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // 或者使用通知渠道（对于Android 8.0及以上）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("alarm_channel_id", "Alarm", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // 发送通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = builder.build();
            notificationManager.notify(3,notification);
        }
    }
}
