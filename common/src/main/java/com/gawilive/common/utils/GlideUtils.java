package com.gawilive.common.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.gawilive.common.R;

/**
 * 网络图片加载工具类
 */
public class GlideUtils {

    /**
     * 加载直播图片ADB
     *
     * @param context
     * @param url
     * @param view
     */
    public static void setLiveImage(Context context, String url, ImageView view) {
        if (!isDestroy((Activity) context)) {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.mipmap.push_chat_default)
                    .error(R.mipmap.push_chat_default)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view);
        }
    }

    public static void setUserHead(Context context, String url, ImageView view) {

        if (!isDestroy((Activity) context)) {
            Glide.with(context)
                    .load(url)
                    .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                    .placeholder(R.mipmap.push_chat_default)
                    .error(R.mipmap.push_chat_default)
                    .into(view);
        }
    }

    public static void setLottery(Context context, String url, ImageView view) {

        if (!isDestroy((Activity) context)) {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.mipmap.push_chat_default)
                    .error(R.mipmap.push_chat_default)
                    .into(view);
        }
    }

    public static void setLottery(Context context, int url, ImageView view) {
        if (!isDestroy((Activity) context)) {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.mipmap.push_chat_default)
                    .error(R.mipmap.push_chat_default)
                    .into(view);
        }
    }


    public static void setUserHead(Context context, int url, ImageView view) {
        if (!isDestroy((Activity) context)) {
            Glide.with(context)
                    .load(url)
                    .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                    .error(R.mipmap.push_chat_default)
                    .placeholder(R.mipmap.push_chat_default)
                    .into(view);
        }

    }


    public static boolean isDestroy(Activity mActivity) {
        return mActivity == null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed());
    }

    public static void setImages(Context context, String url, ImageView imageView, int radius) {
        Glide.with(context)
                .load(url)
                .transform(new RoundedCorners(radius))
                .error(R.mipmap.push_chat_default)
                .into(imageView);
    }

    public static void setImages(Context context, int url, ImageView imageView, int radius) {
        Glide.with(context)
                .load(url)
                .transform(new RoundedCorners(radius))
                .error(R.mipmap.push_chat_default)
                .into(imageView);
    }

    public static void setImages(Context context, Uri uri, ImageView imageView, int radius) {
        Glide.with(context)
                .load(uri)
                .transform(new RoundedCorners(radius))
                .into(imageView);
    }
}
