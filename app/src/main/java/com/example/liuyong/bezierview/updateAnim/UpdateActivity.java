package com.example.liuyong.bezierview.updateAnim;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.liuyong.bezierview.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by liuyong
 * Data: 2017/8/21
 * Github:https://github.com/MrAllRight
 */

public class UpdateActivity extends AppCompatActivity implements UpdateProgressView.StartDownLoadListener {
    private UpdateProgressView updateProgressView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        updateProgressView = (UpdateProgressView) findViewById(R.id.updateview);
        updateProgressView.setStartDownLoadListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
            return;
        }
    }

    @Override
    public void downLoad() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"权限被禁用，无法正常下载",Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                downloadFile("http://resget.91.com/Soft/Controller.ashx?action=download&tpl=1&id=40595115");
            }
        }).start();
    }

    private boolean downloadFile(String path) {
        InputStream is = null;
        try {
            File myTempFile = getApkSDUrl();
            FileOutputStream fos = new FileOutputStream(myTempFile);
            URL myURL = new URL(path);
            URLConnection conn = myURL.openConnection();
            conn.connect();
            is = conn.getInputStream();
            int fileSize = conn.getContentLength();//下载总进度
            int currentSize = 0;//当前进度
            if (is == null) {
                throw new RuntimeException("stream is null");
            }
            byte buf[] = new byte[128];
            updateProgressView.setMax(fileSize);
            do {
                int numread = is.read(buf);
                currentSize += numread;
                if (numread <= 0) {
                    break;
                }
                fos.write(buf, 0, numread);
                updateProgressView.setProgress(currentSize);
                Log.d("info","vvvvvvvvvv"+currentSize);
            } while (true);
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static File getApkSDUrl() {

        File dir = new File(getSDPath());

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File f = new File(dir + File.separator + "MrAllRight" + ".apk");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;
    }

    public static String getSDPath() {
        String sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        Log.d("info", "ssdddd=" + sdCardExist);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory().toString();// 获取跟目录
        }
        return sdDir;

    }
}
