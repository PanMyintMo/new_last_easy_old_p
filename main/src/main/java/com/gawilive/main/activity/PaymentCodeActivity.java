package com.gawilive.main.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.utils.BarcodeGenerator;
import com.gawilive.common.utils.MMKVUtils;
import com.gawilive.common.utils.OkHttp;
import com.gawilive.common.utils.ScreenDimenUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;
import com.gawilive.main.bean.PayUserBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

// 付款码
public class PaymentCodeActivity extends AbsActivity {
    private FrameLayout mFlTop;
    private TextView mTitleView;
    private ImageView mBtnBack;
    private ImageView mImgUserHead;
    private TextView mTvNickName, tvMoney;
    private ImageView mImgTxm;
    private ImageView mImgQrCode;

    private TextView tvCollection;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_payment_code;
    }

    @Override
    protected void main() {
        super.main();

        initView();

    }


    public void initView() {
        setTitle(WordUtil.getString(R.string.string_fkm));
        mBtnBack = findViewById(R.id.btn_back);
        mImgUserHead = findViewById(R.id.user_head);
        mTvNickName = findViewById(R.id.tvNickName);
        mImgTxm = findViewById(R.id.imgTxm);
        mImgQrCode = findViewById(R.id.imgQrCode);
        mTitleView = findViewById(R.id.titleView);
        tvMoney = findViewById(R.id.tvMoney);
        tvCollection = findViewById(R.id.llCollection);
        Map<String, String> map = new HashMap<>();
        map.put("uid", MMKVUtils.getPayUid());
        createBarCode(new Gson().toJson(map));
        createQRCode(new Gson().toJson(map));

        getPayData();

        getQrCodeData();
        tvCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(CollectionActivity.class);
            }
        });

    }

    private void getPayData() {
        OkHttp.getAsync(CommonAppConfig.HOST2 + "?act=findUser&phone=" + CommonAppConfig.getInstance().getUserBean().getMobile(), new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
               // Log.d("Query user information and return data", result);
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) { // 进行信息同步
                    JsonObject dataObject = object.getAsJsonObject("data");
                    PayUserBean bean = new Gson().fromJson(dataObject.toString(), PayUserBean.class);

                    tvMoney.setText(String.format(WordUtil.getString(R.string.string_yezf_sy), bean.getMoney()));
                    ImgLoader.displayAvatar(PaymentCodeActivity.this, bean.getAvatar(), mImgUserHead);
                    mTvNickName.setText(bean.getUsername());
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                //Log.d("Query user information and return data", "something went wrong");
            }
        });
    }


    // 生成条形码
    private void createBarCode(final String content) {
        Bitmap bitmap = BarcodeGenerator.createBarcode(content, ScreenDimenUtil.getInstance().getScreenWdith(), 260);
        mImgTxm.setImageBitmap(bitmap);
    }

    // 生成二维码
    private void createQRCode(final String content) {
        Bitmap bitmap = BarcodeGenerator.createQRCodeBitmap(content, 440, 440, "UTF-8", "L", "0", Color.BLACK, Color.WHITE);
        mImgQrCode.setImageBitmap(bitmap);
    }


    // 获取收款码信息
    private void getQrCodeData() {
        Map<String, String> map = new HashMap<>();
        map.put("uid", MMKVUtils.getPayUid());
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=user_scan_user", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {

            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

}
