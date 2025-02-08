package com.gawilive.common.utils;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.util.FileUtils;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.R;
import com.gawilive.common.interfaces.ActivityResultCallback;
import com.gawilive.common.interfaces.ImageResultCallback;
import com.gawilive.common.interfaces.PermissionCallback;
import com.gawilive.common.interfaces.VideoResultCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by cxf on 2018/9/29.
 * 选择图片 裁剪,录视频等
 */

public class MediaUtil {

    private static final String FILE_PROVIDER = "com.gawilive.phonelivex.fileprovider";

    /**
     * 拍照获取图片
     */
    public static void getImageByCamera(final FragmentActivity activity, final boolean needCrop, final ImageResultCallback imageResultCallback) {
        //请求拍照和存储的权限的回调
        PermissionCallback permissionCallback = new PermissionCallback() {
            @Override
            public void onAllGranted() {
                if (imageResultCallback != null) {
                    imageResultCallback.beforeCamera();
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                final File cameraResult = getNewFile();
                Uri uri = null;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(activity, FILE_PROVIDER, cameraResult);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    uri = Uri.fromFile(cameraResult);
                }
                final Uri finalURI = uri;
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                //开始拍照
                ActivityResultUtil.startActivityForResult(activity, intent, new ActivityResultCallback() {
                    @Override
                    public void onSuccess(Intent intent) {
                        if (needCrop) {//需要裁剪
                            if (finalURI != null) {
                                crop(activity, finalURI, imageResultCallback);
                            }
                        } else {
                            if (imageResultCallback != null) {
                                imageResultCallback.onSuccess(cameraResult);
                            }
                        }
                    }

                    @Override
                    public void onFailure() {
                        ToastUtil.show(R.string.img_camera_cancel);
                    }
                });
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {

            }
        };

        //请求拍照和存储的权限
        PermissionUtil.request(
                activity,
                permissionCallback,
                Manifest.permission.CAMERA
        );
    }

    /**
     * 拍照获取图片
     */
    public static void getImageByCamera(FragmentActivity activity, ImageResultCallback imageResultCallback) {
        getImageByCamera(activity, true, imageResultCallback);
    }


    /**
     * 相册获取图片
     */
    public static void getImageByAlbum(final FragmentActivity activity, final boolean needCrop, final ImageResultCallback imageResultCallback) {
        //请求存储的权限的回调
        PermissionCallback permissionCallback = new PermissionCallback() {
            @Override
            public void onAllGranted() {
                Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                if (Build.VERSION.SDK_INT < 19) {
                    Log.d("IMAGE", "sDK IS NOT GREATER THAN 19");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    Log.d("IMAGE", "GREATER THAN 19");
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                }
                ActivityResultUtil.startActivityForResult(activity, intent, new ActivityResultCallback() {
                    @Override
                    public void onSuccess(Intent intent) {
                        Uri dataUri = intent.getData();
                        if (dataUri != null) {
                            if (needCrop) {
                                crop(activity, dataUri, imageResultCallback);
                            } else {
                                if (imageResultCallback != null) {
                                    String path = FileUtils.getPath(activity, dataUri);
                                    if (!TextUtils.isEmpty(path)) {
                                        imageResultCallback.onSuccess(new File(path));
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure() {
                        ToastUtil.show(R.string.img_alumb_cancel);
                    }
                });

            }

            @Override
            public void onDenied(List<String> deniedPermissions) {

            }
        };

        //请求存储的权限
        PermissionUtil.request(
                activity,
                permissionCallback,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
        );
    }

    /**
     * 相册获取图片
     */
    public static void getImageByAlbum(FragmentActivity activity, ImageResultCallback imageResultCallback) {
        getImageByAlbum(activity, true, imageResultCallback);
    }

    /**
     * 录制视频
     */
    public static void startVideoRecord(final FragmentActivity activity, final VideoResultCallback videoResultCallback) {
        PermissionCallback permissionCallback = new PermissionCallback() {
            @Override
            public void onAllGranted() {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);// 表示跳转至相机的录视频界面
                final File videoResult = getNewVideoFile();
                Uri uri = null;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(activity, FILE_PROVIDER, videoResult);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    uri = Uri.fromFile(videoResult);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
                //开始录制
                ActivityResultUtil.startActivityForResult(activity, intent, new ActivityResultCallback() {
                    @Override
                    public void onSuccess(Intent intent) {
                        if (intent != null && intent.getData() != null && videoResultCallback != null) {
                            if (videoResult != null && videoResult.exists() && videoResult.length() > 0) {
                                String path = videoResult.getAbsolutePath();
                                long duration = 0;
                                MediaMetadataRetriever mmr = null;
                                try {
                                    mmr = new MediaMetadataRetriever();
                                    mmr.setDataSource(path);
                                    String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                    if (!TextUtils.isEmpty(durationStr) && StringUtil.isInt(durationStr)) {
                                        duration = Long.parseLong(durationStr);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    if (mmr != null) {
                                        try {
                                            mmr.release();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    mmr = null;
                                }
                                saveVideoInfo(activity, path, duration);
                                videoResultCallback.onSuccess(videoResult, duration);
                            }
                        }
                    }

                    @Override
                    public void onFailure() {
                        ToastUtil.show(R.string.record_cancel);
                    }
                });
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {

            }
        };
        PermissionUtil.request(
                activity,
                permissionCallback,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        );
    }

    private static File getNewFile() {
        // 裁剪头像的绝对路径
        File dir = new File(CommonAppConfig.CAMERA_IMAGE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, DateFormatUtil.getCurTimeString() + ".png");
    }

    private static File getNewFile2() {
        // 裁剪头像的绝对路径
        File dir = new File(CommonAppConfig.INNER_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, DateFormatUtil.getCurTimeString() + ".png");
    }


    /**
     * 裁剪
     */
    private static void crop(FragmentActivity activity, Uri inputUri, final ImageResultCallback imageResultCallback) {

        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        File copyFile = null;
        try {
            inputStream = activity.getContentResolver().openInputStream(inputUri);
            File dir = activity.getFilesDir();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            copyFile = new File(dir, StringUtil.generateFileName() + ".png");
            outputStream = new FileOutputStream(copyFile);
            byte[] buf = new byte[4096];
            int len = 0;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            copyFile = null;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (copyFile == null || copyFile.length() == 0) {
            return;
        }

        final File corpResult = getNewFile2();
        try {
            Uri resultUri = Uri.fromFile(corpResult);
            if (resultUri == null) {
                return;
            }
            UCrop uCrop = UCrop.of(Uri.fromFile(copyFile), resultUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(400, 400);
            Intent intent = uCrop.getIntent(activity);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            ActivityResultUtil.startActivityForResult(activity, intent, new ActivityResultCallback() {
                @Override
                public void onSuccess(Intent intent) {
                    if (imageResultCallback != null) {
                        imageResultCallback.onSuccess(corpResult);
                    }
                }

                @Override
                public void onFailure() {
                    ToastUtil.show(R.string.img_crop_cancel);
                }
            });
        } catch (Exception e) {
            try {
                Uri resultUri = FileProvider.getUriForFile(activity, FILE_PROVIDER, corpResult);
                if (resultUri == null) {
                    return;
                }
                UCrop uCrop = UCrop.of(Uri.fromFile(copyFile), resultUri)
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(400, 400);
                Intent intent = uCrop.getIntent(activity);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                ActivityResultUtil.startActivityForResult(activity, intent, new ActivityResultCallback() {
                    @Override
                    public void onSuccess(Intent intent) {
                        if (imageResultCallback != null) {
                            imageResultCallback.onSuccess(corpResult);
                        }
                    }

                    @Override
                    public void onFailure() {
                        ToastUtil.show(R.string.img_crop_cancel);
                    }
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }


    private static File getNewVideoFile() {
        File dir = new File(CommonAppConfig.VIDEO_PATH_RECORD);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, DateFormatUtil.getCurTimeString() + ".mp4");
    }

    /**
     * 把视频保存到ContentProvider,在选择上传的时候能找到
     */
    public static void saveVideoInfo(Context context, String videoPath, long duration) {
        try {
            File videoFile = new File(videoPath);
            String fileName = videoFile.getName();
            long currentTimeMillis = System.currentTimeMillis();
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.TITLE, fileName);
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.DATE_MODIFIED, currentTimeMillis);
            values.put(MediaStore.MediaColumns.DATE_ADDED, currentTimeMillis);
            values.put(MediaStore.MediaColumns.DATA, videoPath);
            values.put(MediaStore.MediaColumns.SIZE, videoFile.length());
            values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, currentTimeMillis);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            values.put(MediaStore.Video.VideoColumns.DURATION, duration);
            // context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(videoFile)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
