package com.n0texpecterr0r.rhapsody;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 10:07
 * @describe 选择的图片的类型枚举
 */
public class SelectActivity extends AppCompatActivity {
    private static final int PERMISSIONS_READ_STORAGE = 233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        checkStoragePression();
    }

    /**
     * android6.0动态权限申请：SD卡读写权限
     */
    public void checkStoragePression() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkResult = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkResult != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_READ_STORAGE);
            }
        }
    }

    /**
     * 权限申请回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        boolean permit = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        switch (requestCode) {
            case PERMISSIONS_READ_STORAGE:
                if (!permit) {
                    Toast.makeText(this,"需要读取权限才可正常使用",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
}
