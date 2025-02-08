package com.gawilive.common.utils;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import com.gawilive.common.interfaces.PermissionCallback;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.List;

/**
 * 申请权限
 */
public class PermissionUtil {

    public static void request(FragmentActivity activity, PermissionCallback callback, String... permissions) {
        // Check if the Android version is 13 (API level 33) or higher
        XXPermissions.with(activity)
                .permission(permissions)
                .interceptor(new PermissionInterceptor())
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> grantedPermissions, boolean all) {
                        if (!all) {
                            ToastUtil.show("Obtained some permissions successfully, but some permissions were not granted normally.");
                            return;
                        }
                        callback.onAllGranted();
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions, boolean never) {
                        if (never) {
                            ToastUtil.show("Permanently denied authorization, please manually grant necessary permissions");
                            // If permanently denied, open the app's permission settings
                            XXPermissions.startPermissionActivity(activity, deniedPermissions);
                        } else {
                            ToastUtil.show("Failed to obtain permission");
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private static @NonNull List<String> getStrings(String[] permissions) {
        List<String> updatedPermissions = new ArrayList<>();

        for (String permission : permissions) {
            // Map READ_EXTERNAL_STORAGE to the new media-specific permissions for Android 13+
            if (permission.equals(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                updatedPermissions.add(android.Manifest.permission.READ_MEDIA_IMAGES);
                updatedPermissions.add(android.Manifest.permission.READ_MEDIA_VIDEO);
                updatedPermissions.add(android.Manifest.permission.READ_MEDIA_AUDIO);
            } else {
                updatedPermissions.add(permission);
            }
        }
        return updatedPermissions;
    }
}
