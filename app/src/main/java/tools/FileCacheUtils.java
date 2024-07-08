package tools;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.health.connect.datatypes.AppInfo;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class FileCacheUtils  {
    private Context mcontext;

    public FileCacheUtils (Context context) {
        this.mcontext = context;
    }

    // 获取应用的缓存目录
    File cacheDir = mcontext.getCacheDir();

    // 计算缓存目录大小
    long cacheSize = getDirectorySize(cacheDir);

    // 将缓存大小转换成人类可读的格式
    String cacheSizeFormatted = formatSize(cacheSize);

    // 打印应用缓存大小
    public String getSize() {
        return cacheSizeFormatted;
    }

    //清除缓存
    // 删除目录的递归方法
    public void deleteDirectory(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteDirectory(child);
            }
        }
        fileOrDirectory.delete();
    }

    // 获取目录大小的递归方法
    private static long getDirectorySize(File directory) {
        long size = 0;
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        size += file.length();
                    } else {
                        size += getDirectorySize(file);
                    }
                }
            }
        }
        return size;
    }

    // 将文件大小转换成人类可读的格式方法
    private static String formatSize(long size) {
        String hrSize;

        double bytes = size;
        double kilobytes = bytes / 1024;
        double megabytes = kilobytes / 1024;
        double gigabytes = megabytes / 1024;

        DecimalFormat dec = new DecimalFormat("0.00");

        if (gigabytes > 1) {
            hrSize = dec.format(gigabytes).concat(" GB");
        } else if (megabytes > 1) {
            hrSize = dec.format(megabytes).concat(" MB");
        } else if (kilobytes > 1) {
            hrSize = dec.format(kilobytes).concat(" KB");
        } else {
            hrSize = dec.format(bytes).concat(" Bytes");
        }

        return hrSize;
    }


}
