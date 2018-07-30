package com.n0texpecterr0r.rhapsody.permission;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.n0texpecterr0r.rhapsody.R;

/**
 * @author Created by Nullptr
 * @date 2018/7/30 10:07
 * @describe 权限申请界面
 */
public class PermissionActivity extends AppCompatActivity {
    public static final int PERMISSIONS_GRANTED = 0;                // 权限授权
    public static final int PERMISSIONS_DENIED = 1;                 // 权限拒绝

    private PermissionChecker mChecker;                             // 权限检测工具
    private static final String PERMISSIONS = "TAG_PERMISSIONS";    // 权限参数
    private boolean isRequireCheck;                                 // 是否需要系统权限检测
    private static final int PERMISSION_REQUEST_CODE = 0;           // 系统权限管理页面的参数
    private static final String PACKAGE_URL_SCHEME = "package:";    // 方案

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || !getIntent().hasExtra(PERMISSIONS)){
            // 如果没有传入相应参数
            throw new RuntimeException("需要使用startActivityForResult方法打开");
        }
        setContentView(R.layout.activity_permission);

        mChecker = new PermissionChecker(this);
        isRequireCheck = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRequireCheck){
            String[] permissions = getPermissions();
            if (mChecker.lackPermissions(permissions)){
                requestPermissions(permissions);    // 请求权限
            }else{
                allPermissionGranted(); // 所有权限已获取
            }
        }else{
            isRequireCheck = true;
        }
    }

    /**
     * 启动当前权限页面的公开接口
     */
    public static void startActivityForResult(Activity activity, int requestCode, String... permissions) {
        Intent intent = new Intent(activity, PermissionActivity.class);
        intent.putExtra(PERMISSIONS, permissions);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    /**
     * 所有权限已获取，返回结果
     */
    private void allPermissionGranted() {
        setResult(PERMISSIONS_GRANTED);
        finish();
    }

    /**
     * 请求权限
     * @param permissions 权限数组
     */
    private void requestPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(this,permissions,PERMISSION_REQUEST_CODE);
    }



    /**
     * 获取权限数组
     * @return 权限String组成的数组
     */
    private String[] getPermissions() {
        return getIntent().getStringArrayExtra(PERMISSIONS);
    }

    /**
     * 用户权限处理
     * 如果全部获取，则通过
     * 如果权限缺失，则弹出提示
     * @param requestCode  请求码
     * @param permissions  权限数组
     * @param grantResults 请求结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionGranted(grantResults)){
            isRequireCheck = true;
            allPermissionGranted();
        }else{
            isRequireCheck = false;
            showMissingPermissionDialog();
        }
    }

    /**
     * 检测是否含有所有权限
     * @param grantResults 请求结果数组
     * @return 是否有全部权限
     */
    private boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示权限缺失窗口
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限\n请点击\"设置\"-\"权限\"-打开所需权限\n然后点击两次后退按钮即可返回");

        // 拒绝, 退出应用
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                setResult(PERMISSIONS_DENIED);
                finish();
            }
        });

        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });

        builder.show();
    }

    /**
     * 打开应用设置
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }
}
