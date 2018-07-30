package com.n0texpecterr0r.rhapsody.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * @author Created by Nullptr
 * @date 2018/7/30 10:40
 * @describe 检查权限工具类
 */
public class PermissionChecker {
    private final Context mContext;

    public PermissionChecker(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 判断是否缺少一组权限
     * @param permissions 一组权限名
     * @return 是否缺乏权限
     */
    public boolean lackPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lackPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否缺少某权限
     * @param permission 权限
     * @return 是否缺少该权限
     */
    private boolean lackPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_DENIED;
    }
}