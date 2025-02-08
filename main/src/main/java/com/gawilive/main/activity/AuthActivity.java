package com.gawilive.main.activity;

import android.Manifest;
import android.app.Dialog;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.gawilive.main.R;
import com.gawilive.main.dialog.AuthExampleDialogFragment;
import com.gawilive.main.http.MainHttpConsts;
import com.gawilive.main.http.MainHttpUtil;
import com.hjq.permissions.Permission;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AuthActivity extends AbsActivity implements View.OnClickListener {

    private EditText mName;
    private EditText mPhone;
    private EditText mCardNo;
    private ImageView mImgFront;
    private ImageView mImgBack;
    private ImageView mImgFrontHand;
    private File mImgFrontFile;
    private File mImgBackFile;
    private File mImgFrontHandFile;
    private String mImgFrontUrl;
    private String mImgBackUrl;
    private String mImgFrontHandUrl;
    private int mImgPosition;
    private Dialog mLoading;
    private final ImageResultCallback mImageResultCallback = new ImageResultCallback() {
        @Override
        public void beforeCamera() {}

        @Override
        public void onSuccess(File file) {
            if (file != null && file.exists()) {
                if (mImgPosition == 0) {
                    mImgFrontFile = file;
                    if (mImgFront != null) {
                        ImgLoader.display(mContext, file, mImgFront);
                    }
                } else if (mImgPosition == 1) {
                    mImgBackFile = file;
                    if (mImgBack != null) {
                        ImgLoader.display(mContext, file, mImgBack);
                    }
                } else if (mImgPosition == 2) {
                    mImgFrontHandFile = file;
                    if (mImgFrontHand != null) {
                        ImgLoader.display(mContext, file, mImgFrontHand);
                    }
                }
            } else {
                ToastUtil.show(com.gawilive.mall.R.string.file_not_exist);
            }
        }


        @Override
        public void onFailure() {}
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_auth;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.a_019));
        mName = findViewById(R.id.name);
        mPhone = findViewById(R.id.phone);
        mCardNo = findViewById(R.id.card_no);
        mImgFront = findViewById(R.id.img_front);
        mImgBack = findViewById(R.id.img_back);
        mImgFrontHand = findViewById(R.id.img_front_hand);
        findViewById(R.id.btn_img_front).setOnClickListener(this);
        findViewById(R.id.btn_img_back).setOnClickListener(this);
        findViewById(R.id.btn_img_front_hand).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        findViewById(R.id.btn_example).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_img_front) {
            mImgPosition = 0;
            chooseImage();
        } else if (id == R.id.btn_img_back) {
            mImgPosition = 1;
            chooseImage();
        } else if (id == R.id.btn_img_front_hand) {
            mImgPosition = 2;
            chooseImage();
        } else if (id == R.id.btn_submit) {
            submit();
        } else if (id == R.id.btn_example) {
            AuthExampleDialogFragment fragment = new AuthExampleDialogFragment();
            fragment.show(getSupportFragmentManager(), "AuthExampleDialogFragment");
        }
    }


    /**
     * 选择图片
     */
    private void chooseImage() {

        PermissionUtil.request(AuthActivity.this,
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
                                                if (mImgPosition == 0) {
                                                    mImgFrontFile = file;
                                                    if (mImgFront != null) {
                                                        ImgLoader.display(mContext, file, mImgFront);
                                                    }
                                                } else if (mImgPosition == 1) {
                                                    mImgBackFile = file;
                                                    if (mImgBack != null) {
                                                        ImgLoader.display(mContext, file, mImgBack);
                                                    }
                                                } else if (mImgPosition == 2) {
                                                    mImgFrontHandFile = file;
                                                    if (mImgFrontHand != null) {
                                                        ImgLoader.display(mContext, file, mImgFrontHand);
                                                    }
                                                }
                                            } else {
                                                ToastUtil.show(com.gawilive.mall.R.string.file_not_exist);
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

    public void submit() {
        final String name = mName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.show(R.string.a_008);
            return;
        }
        final String phone = mPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(R.string.a_010);
            return;
        }
        final String cardNo = mCardNo.getText().toString();
        if (TextUtils.isEmpty(cardNo)) {
            ToastUtil.show(R.string.a_012);
            return;
        }
        if (mImgFrontFile == null || mImgBackFile == null || mImgFrontHandFile == null) {
            ToastUtil.show(R.string.a_061);
            return;
        }
        final List<UploadBean> uploadList = new ArrayList<>();
        if (mImgFrontFile != null) {
            UploadBean bean = new UploadBean(mImgFrontFile, UploadBean.IMG);
            bean.setTag(0);
            uploadList.add(bean);
        }
        if (mImgBackFile != null) {
            UploadBean bean = new UploadBean(mImgBackFile, UploadBean.IMG);
            bean.setTag(1);
            uploadList.add(bean);
        }
        if (mImgFrontHandFile != null) {
            UploadBean bean = new UploadBean(mImgFrontHandFile, UploadBean.IMG);
            bean.setTag(2);
            uploadList.add(bean);
        }
        showLoading();
        UploadUtil.startUpload(new CommonCallback<UploadStrategy>() {
            @Override
            public void callback(UploadStrategy strategy) {
                strategy.upload(uploadList, true, new UploadCallback() {
                    @Override
                    public void onFinish(List<UploadBean> list, boolean success) {
                        if (!success) {
                            hideLoading();
                            return;
                        }
                        for (UploadBean bean : list) {
                            int tag = (int) bean.getTag();
                            if (tag == 0) {
                                mImgFrontUrl = bean.getRemoteFileName();
                            } else if (tag == 1) {
                                mImgBackUrl = bean.getRemoteFileName();
                            } else if (tag == 2) {
                                mImgFrontHandUrl = bean.getRemoteFileName();
                            }
                        }

                        MainHttpUtil.setAuth(name, phone, cardNo, mImgFrontUrl, mImgBackUrl, mImgFrontHandUrl, new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0) {
                                    setResult(RESULT_OK);
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
                });
            }
        });

    }


    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.SET_AUTH);
        UploadUtil.cancelUpload();
        hideLoading();
        super.onDestroy();
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
}
