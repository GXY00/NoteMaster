package tools;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HeaderUtils {
    private Context mContext;

    public HeaderUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void addHeaderToPrefs(String uname,Uri uri){
        SharedPreferences prefs = mContext.getSharedPreferences("MyPrefsFolder",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(uname,uri.toString());
        editor.apply();
    }

    //获取头像
    public Uri getSavedImageUri(String uname) {
        SharedPreferences prefs = mContext.getSharedPreferences("MyPrefsFolder", MODE_PRIVATE);
        String uriString = prefs.getString(uname, null); // 获取保存的图片Uri
        if (uriString != null) {
            return Uri.parse(uriString); // 如果uriString不为null，则解析为Uri对象
        } else {
            // 处理uriString为null的情况，例如返回默认Uri或者抛出一个自定义异常
            // 这里返回一个默认Uri作为示例
            return Uri.parse("android.resource://" + mContext.getPackageName() + "/mipmap/ic_launcher");
        }
    }
}
