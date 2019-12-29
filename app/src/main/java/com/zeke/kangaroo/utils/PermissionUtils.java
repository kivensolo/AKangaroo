package com.zeke.kangaroo.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import com.zeke.kangaroo.permission.PermissionConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * author：KingZ
 * date：2019/11/7
 * description：动态权限申请工具类
 * 支持8.0
 */
public class PermissionUtils {
    LinkedHashSet<String> mPermissions;
    private static List<String> PERMISSIONS = null;
    public PermissionUtils sInstance;

    /**
     * 获取应用权限
     *
     * @return 清单文件中的权限列表
     */
    public static List<String> getPermissions(Context ctx) {
        return getPermissions(ctx, ctx.getPackageName());
    }

    /**
     * 获取应用权限
     *
     * @param packageName 包名
     * @return 清单文件中的权限列表
     */
    public static List<String>  getPermissions(Context ctx, final String packageName) {
        PackageManager pm = ctx.getPackageManager();
        try {
            return Arrays.asList(
                    pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions
            );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 设置请求权限
     *
     * @param permissions 要请求的权限
     * @return {@link PermissionUtils}
     */
    public static PermissionUtils permission(Context ctx,
                            @PermissionConstants.Permission
                            final String... permissions) {
        PERMISSIONS = getPermissions(ctx);
        return new PermissionUtils(permissions);
    }

    private PermissionUtils(final String... permissions) {
        mPermissions = new LinkedHashSet<>();
        for (String permission : permissions) {
            for (String aPermission : PermissionConstants.getPermissions(permission)) {
                if (PERMISSIONS.contains(aPermission)) {
                    mPermissions.add(aPermission);
                }
            }
        }
        sInstance = this;
    }
}
