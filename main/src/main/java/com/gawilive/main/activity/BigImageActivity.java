package com.gawilive.main.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.utils.OkHttp;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.main.R;
import okhttp3.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BigImageActivity extends AbsActivity {
    private ImageView mImgPay;

    private String url;

    private String orderNo;

    public static void start(Context context, String url, String orderNo) {
        Intent intent = new Intent(context, BigImageActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("orderNo", orderNo);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_big_image;
    }

    @Override
    protected void main() {
        super.main();
        url = getIntent().getStringExtra("url");
        orderNo = getIntent().getStringExtra("orderNo");
        initView();
    }

    private void initView() {
        setTitle("扫码充值");
        mImgPay = findViewById(R.id.imgPay);
        Glide.with(this)
                .asBitmap()
                .load(url)
                .skipMemoryCache(true)
                .centerCrop()
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        // 启动延迟任务，延迟1秒后执行
        handler.postDelayed(pollingRunnable, 2000);
    }

    private final Handler handler = new Handler();
    private final Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            getOrder();
            handler.postDelayed(this, 2000); // 每5秒执行一次
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(pollingRunnable);
    }

    private void getOrder() {
        Map<String, String> map = new HashMap<>();
        map.put("order_sn", orderNo);
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=find_order", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) {
                    // 移除定时任务
                    handler.removeCallbacks(pollingRunnable);
                    TransferSuccessfulActivity.start(BigImageActivity.this, "充值成功");
                    finish();
                } else if (code == 1) {
                    String msg = object.get("msg").getAsString();
                    ToastUtil.show(msg);
                    handler.removeCallbacks(pollingRunnable);
                    finish();
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }
}
