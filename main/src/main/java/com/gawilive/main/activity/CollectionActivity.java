package com.gawilive.main.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.makeramen.roundedimageview.RoundedImageView;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.utils.BarcodeGenerator;
import com.gawilive.common.utils.OkHttp;
import com.gawilive.main.R;
import com.gawilive.main.bean.PayUserBean;
import com.gawilive.main.bean.QrCodeModel;
import okhttp3.Request;

import java.io.IOException;

public class CollectionActivity extends AbsActivity {
    private FrameLayout mFlTop;
    private TextView mTitleView;
    private ImageView mBtnBack;
    private RoundedImageView mUserHead;
    private TextView mTvNickName;
    private ImageView mImgQrCode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collecion_money;
    }

    @Override
    protected void main() {
        super.main();
        setTitle("收款码");
        initView();
    }

    private void initView() {
        mFlTop = findViewById(R.id.fl_top);
        mTitleView = findViewById(R.id.titleView);
        mBtnBack = findViewById(R.id.btn_back);
        mUserHead = findViewById(R.id.user_head);
        mTvNickName = findViewById(R.id.tvNickName);
        mImgQrCode = findViewById(R.id.imgQrCode);
        getPayData();
    }

    private void getPayData() {
        OkHttp.getAsync(CommonAppConfig.HOST2 + "?act=findUser&phone=" + CommonAppConfig.getInstance().getUserBean().getMobile(), new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.d("查询用户信息返回数据", result);
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) { // 进行信息同步
                    JsonObject dataObject = object.getAsJsonObject("data");
                    PayUserBean bean = new Gson().fromJson(dataObject.toString(), PayUserBean.class);
                    mTvNickName.setText(bean.getUsername());
                    ImgLoader.displayAvatar(CollectionActivity.this, bean.getAvatar(), mUserHead);

                    QrCodeModel model = new QrCodeModel();
                    model.setUid(bean.getUid());
                    model.setAvatar(bean.getAvatar());
                    model.setType("2");
                    model.setNickname(bean.getUsername());
                    model.setCertName(bean.getCertname());
                    model.setCertNo(bean.getCertno());
                    createQRCode(new Gson().toJson(model));
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Log.d("查询用户信息返回数据", "出错了");
            }
        });
    }

    // 生成二维码
    private void createQRCode(final String content) {
        Bitmap bitmap = BarcodeGenerator.createQRCodeBitmap(content, 440, 440, "UTF-8", "L", "0", Color.BLACK, Color.WHITE);
        mImgQrCode.setImageBitmap(bitmap);
    }
}
