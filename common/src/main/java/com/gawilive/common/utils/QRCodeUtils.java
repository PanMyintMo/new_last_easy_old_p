package com.gawilive.common.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import com.hjq.toast.ToastUtils;
import com.gawilive.common.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

// 二维码生成工具类
public class QRCodeUtils {


    /**
     * 保存图片到指定路径
     *
     * @param context
     * @param bitmap   要保存的图片
     * @param fileName 自定义图片名称
     * @return
     */
    public static boolean saveImageToGallery(Context context, Bitmap bitmap, String fileName) {
        // 保存图片至指定路径
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "qrcode";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片(80代表压缩20%)
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            if (isSuccess) {
                ToastUtils.show(WordUtil.getString(R.string.save_success));
                return true;
            } else {
                ToastUtils.show(WordUtil.getString(R.string.save_failed));
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
