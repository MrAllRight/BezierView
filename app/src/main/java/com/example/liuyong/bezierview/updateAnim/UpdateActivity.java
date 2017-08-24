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
                downloadFile("http://p.gdown.baidu.com/576bd2d8cbe9e0d224baa5d72ab324da0370e106710ca22c5c5af72f6d6053e6e24d435f93d9718eac1705d5a59cf48d01994d1f2869ddb30883cb41e5199bf1b3adc23d1c0368c49156f885168afee819f40157490ec53d803e68b02388247a6dc962afe14ab7180f44f8360a2072f5ce1fbf5d20f67f62fd1dfbde828bb48f8a2dd500b950372e7bacbc7d32e15969cc664d10721eb57cc5871d30198891e5c1250aa4f4faf971beff89cff18cab21df9ff681cef81db1d127c84296fe951fe41b13411881562d2c75f0bd0ffec6acebdd9fd02cdc6d23debfc4d2b350f9c8fa58482baa014eaff20f8321ea081ee8a78a7e712ec861fb5ea46e5f0033788b");
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
