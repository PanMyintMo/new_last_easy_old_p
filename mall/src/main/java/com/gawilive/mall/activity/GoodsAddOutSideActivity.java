package com.gawilive.mall.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.auth.AuthActivity;
import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.glide.GlideEngine;
import com.gawilive.common.glide.ImageFileCompressEngine;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.interfaces.ImageResultCallback;
import com.gawilive.common.interfaces.PermissionCallback;
import com.gawilive.common.upload.UploadBean;
import com.gawilive.common.upload.UploadCallback;
import com.gawilive.common.upload.UploadStrategy;
import com.gawilive.common.upload.UploadUtil;
import com.gawilive.common.utils.*;
import com.gawilive.mall.R;
import com.gawilive.mall.http.MallHttpConsts;
import com.gawilive.mall.http.MallHttpUtil;
import com.hjq.permissions.Permission;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加站外商品
 */
public class GoodsAddOutSideActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context, String goodsId) {
        Intent intent = new Intent(context, GoodsAddOutSideActivity.class);
        intent.putExtra(Constants.MALL_GOODS_ID, goodsId);
        context.startActivity(intent);
    }


    private EditText mLink;
    private EditText mName;
    private EditText mPriceOrigin;
    private EditText mPriceNow;
//    private EditText mPriceYong;
    private EditText mDes;
    //    private View mBtnImgDel;
    private ImageView mImg;
    private File mImgFile;
    private View mBtnConfirm;
    private Dialog mLoading;
    private String mGoodsId;
    private String mImgUrl;
    private final ImageResultCallback mImageResultCallback = new ImageResultCallback() {
        @Override
        public void beforeCamera() {

        }

        @Override
        public void onSuccess(File file) {
            if (file != null && file.exists()) {
                mImgFile = file;
                mImgUrl = null;
                if (mImg != null) {
                    ImgLoader.display(mContext, file, mImg);
                }
                setSubmitEnable();
            }
        }


        @Override
        public void onFailure() {
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.activity_goods_out_side;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.mall_375));
        mLink = findViewById(R.id.link);
        mName = findViewById(R.id.name);
        mPriceOrigin = findViewById(R.id.price_origin);
        mPriceNow = findViewById(R.id.price_now);
//        mPriceYong = (EditText) findViewById(R.id.price_yong);
        mDes = findViewById(R.id.des);
        mImg = findViewById(R.id.img);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
        findViewById(R.id.btn_img_add).setOnClickListener(this);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSubmitEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mLink.addTextChangedListener(textWatcher);
        mName.addTextChangedListener(textWatcher);
        mPriceOrigin.addTextChangedListener(textWatcher);
        mPriceNow.addTextChangedListener(textWatcher);
        mDes.addTextChangedListener(textWatcher);
        mGoodsId = getIntent().getStringExtra(Constants.MALL_GOODS_ID);
        if (!TextUtils.isEmpty(mGoodsId)) {
            getGoodsInfo();
        }
    }


    /**
     * 获取商品详情，展示数据
     */
    private void getGoodsInfo() {
        MallHttpUtil.getGoodsInfo(mGoodsId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    JSONObject goodsInfo = obj.getJSONObject("goods_info");
                    if (mLink != null) {
                        mLink.setText(goodsInfo.getString("href"));
                    }
                    if (mName != null) {
                        mName.setText(goodsInfo.getString("name"));
                    }
                    if (mDes != null) {
                        mDes.setText(goodsInfo.getString("goods_desc"));
                    }
                    if (mPriceOrigin != null) {
                        mPriceOrigin.setText(goodsInfo.getString("original_price"));
                    }
                    if (mPriceNow != null) {
                        mPriceNow.setText(goodsInfo.getString("present_price"));
                    }
                    mImgUrl = goodsInfo.getString("thumbs");
                    JSONArray array = goodsInfo.getJSONArray("thumbs_format");
                    if (array != null && array.size() > 0) {
                        String thumb = array.getString(0);
                        if (mImg != null) {
                            ImgLoader.display(mContext, thumb, mImg);
                        }
//                        if (mBtnImgDel != null && mBtnImgDel.getVisibility() != View.VISIBLE) {
//                            mBtnImgDel.setVisibility(View.VISIBLE);
//                        }
                        setSubmitEnable();
                    }
                }
            }
        });
    }


    /**
     * 添加图片
     */
    private void addImage() {
        PermissionUtil.request(GoodsAddOutSideActivity.this,
                new PermissionCallback() {
                    @Override
                    public void onAllGranted() {
                        PictureSelector.create(mContext)
                                .openGallery(SelectMimeType.ofImage())
                                .setCompressEngine(new ImageFileCompressEngine())
                                .setImageEngine(GlideEngine.createGlideEngine())
                                .setMaxSelectNum(1)
                                .setMinSelectNum(1) // 设置最小选择数量
                                .setImageSpanCount(4) // 设置每行显示的图片数量

                                .forResult(new OnResultCallbackListener<LocalMedia>() {
                                    @Override
                                    public void onResult(ArrayList<LocalMedia> result) {
                                        for (LocalMedia localMedia : result) {
                                            String filePath = localMedia.getPath();
                                            String path = ImageUriPathUtil.getPathFromUri(mContext, Uri.parse(filePath));
                                            File file = new File(path);
                                            if (file != null && file.exists()) {
                                                mImgFile = file;
                                                mImgUrl = null;
                                                if (mImg != null) {
                                                    ImgLoader.display(mContext, file, mImg);
                                                }
                                                setSubmitEnable();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {

                    }
                },
                Permission.READ_MEDIA_IMAGES,
                Manifest.permission.CAMERA
        );
    }

    /**
     * 删除图片
     */
    private void deleteImage() {
        if (mImg != null) {
            mImg.setImageDrawable(null);
        }
        mImgUrl = null;
        mImgFile = null;
//        if (mBtnImgDel != null && mBtnImgDel.getVisibility() == View.VISIBLE) {
//            mBtnImgDel.setVisibility(View.INVISIBLE);
//        }
        setSubmitEnable();
    }


    private void setSubmitEnable() {
        if (mBtnConfirm != null) {
            String link = mLink.getText().toString().trim();
            String name = mName.getText().toString().trim();
            String priceNow = mPriceNow.getText().toString().trim();
            String priceOrigin = mPriceOrigin.getText().toString().trim();
            String des = mDes.getText().toString().trim();
            mBtnConfirm.setEnabled(!TextUtils.isEmpty(link)
                    && !TextUtils.isEmpty(name)
                    && !TextUtils.isEmpty(priceNow)
                    && !TextUtils.isEmpty(priceOrigin)
                    && !TextUtils.isEmpty(des)
                    && (mImgFile != null || !TextUtils.isEmpty(mImgUrl))
            );
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_img_add) {
            addImage();
        }
//        else if (i == R.id.btn_img_del) {
//            deleteImage();
//        }
        else if (i == R.id.btn_confirm) {
            submit();
        }
    }

    private void submit() {
        final String link = mLink.getText().toString().trim();
        final String name = mName.getText().toString().trim();
        final String priceOrigin = mPriceOrigin.getText().toString().trim();
        final String priceNow = mPriceNow.getText().toString().trim();
//        final String priceYong = mPriceYong.getText().toString().trim();
        final String des = mDes.getText().toString().trim();
        if (mImgFile != null && mImgFile.exists()) {
            final List<UploadBean> list = new ArrayList<>();
            list.add(new UploadBean(mImgFile, UploadBean.IMG));
            showLoading();
            UploadUtil.startUpload(new CommonCallback<UploadStrategy>() {
                @Override
                public void callback(UploadStrategy strategy) {
                    strategy.upload(list, true, new UploadCallback() {
                        @Override
                        public void onFinish(List<UploadBean> list, boolean success) {
                            if (!success) {
                                hideLoading();
                                return;
                            }
                            if (list != null && list.size() > 0) {
                                String remoteFileName = list.get(0).getRemoteFileName();
                                MallHttpUtil.setOutsideGoods(mGoodsId, link, name, priceOrigin, priceNow,des, remoteFileName, new HttpCallback() {
                                    @Override
                                    public void onSuccess(int code, String msg, String[] info) {
                                        if (code == 0) {
                                            finish();
                                        }
                                        ToastUtil.show(msg);
                                    }

                                    @Override
                                    public void onFinish() {
                                        hideLoading();
                                    }
                                });
                            }
                        }
                    });
                }
            });

        } else {
            if (TextUtils.isEmpty(mImgUrl)) {
                if (TextUtils.isEmpty(mGoodsId)) {
                    ToastUtil.show(R.string.mall_108);
                }
            } else {
                MallHttpUtil.setOutsideGoods(mGoodsId, link, name, priceOrigin, priceNow,des, mImgUrl, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            finish();
                        }
                        ToastUtil.show(msg);
                    }

                    @Override
                    public void onFinish() {
                        hideLoading();
                    }
                });
            }
        }

    }


    private void showLoading() {
        if (mLoading == null) {
            mLoading = DialogUitl.loadingDialog(mContext, WordUtil.getString(R.string.video_pub_ing));
        }
        if (!mLoading.isShowing()) {
            mLoading.show();
        }
    }


    private void hideLoading() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        MallHttpUtil.cancel(MallHttpConsts.GET_GOODS_INFO);
        MallHttpUtil.cancel(MallHttpConsts.SET_OUTSIDE_GOODS);
        hideLoading();
        super.onDestroy();
    }


}
