package com.gawilive.common.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gawilive.common.R;

import java.util.List;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2023/01/02
 * desc   : 权限描述转换器
 */
public final class PermissionDescriptionConvert {

    /**
     * 获取权限描述
     */
    public static String getPermissionDescription(Context context, List<String> permissions) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> permissionNames = PermissionNameConvert.permissionsToNames(context, permissions);
        for (String permissionName : permissionNames) {
            stringBuilder.append(permissionName)
                    .append(context.getString(R.string.common_permission_colon))
                    .append(permissionsToDescription(context, permissionName))
                    .append("\n");
        }
        return stringBuilder.toString().trim();
    }

    /**
     * 将权限名称列表转换成对应权限描述
     */
    @NonNull
    public static String permissionsToDescription(Context context, String permissionName) {

        // 请根据权限名称转换成对应权限说明
        return context.getString(R.string.permission_8) + businessDesc(context, permissionName) + context.getString(R.string.permission_9);
    }

    public static String businessDesc(Context context, String permission) {
        if (permission.contains(context.getString(R.string.common_permission_camera))) {
            return context.getString(R.string.permission_1);
        } else if (permission.contains(context.getString(R.string.common_permission_phone))) {
            return context.getString(R.string.permission_2);
        } else if (permission.contains(context.getString(R.string.common_permission_sms))) {
            return context.getString(R.string.permission_3);
        } else if (permission.contains(context.getString(R.string.common_permission_location))) {
            return context.getString(R.string.permission_4);
        } else if (permission.contains(context.getString(R.string.common_permission_storage))) {
            return context.getString(R.string.permission_5);
        } else if (permission.contains(context.getString(R.string.common_permission_storage))) {
            return context.getString(R.string.permission_6);
        } else if (permission.contains(context.getString(R.string.common_permission_music_and_audio))) {
            return context.getString(R.string.permission_7);
        } else {
            return permission;
        }

    }
}