package com.gawilive.main.utils.permission;

import android.content.Context;

import androidx.annotation.NonNull;

import com.gawilive.main.R;
import com.gawilive.main.utils.PermissionNameConvert;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/01/02
 *    desc   : 权限描述转换器
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
//       String location =  context.getResources().getString(R.string.common_permission_location);
       // 请根据权限名称转换成对应权限说明
       String description ="";
       switch (permissionName){
           case "相机权限":
               description = "用于调用相机拍照获取影像";
               break;
           case "存储权限":
               description = "用于读取设本上的照片";
               break;
           case "麦克风权限":
               description = "用于收录麦克风中的声音";
               break;
           case "Location permissions":
               description = "Get access to your location and use the location feature to help you post your dynamic location";
               break;
       }
       return description;
   }
}