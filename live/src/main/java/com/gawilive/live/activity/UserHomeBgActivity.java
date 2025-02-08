package com.gawilive.live.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.event.UpdateFieldEvent;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.interfaces.ImageResultCallback;
import com.gawilive.common.interfaces.PermissionCallback;
import com.gawilive.common.upload.UploadBean;
import com.gawilive.common.upload.UploadCallback;
import com.gawilive.common.upload.UploadStrategy;
import com.gawilive.common.upload.UploadUtil;
import com.gawilive.common.utils.BitmapUtil;
import com.gawilive.common.utils.DialogUitl;
import com.gawilive.common.utils.DownloadUtil;
import com.gawilive.common.utils.MediaUtil;
import com.gawilive.common.utils.PermissionUtil;
import com.gawilive.common.utils.StringUtil;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.live.R;
import com.gawilive.live.http.LiveHttpConsts;
import com.gawilive.live.http.LiveHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 云豹科技 on 2022/6/6.
 */
public class UserHomeBgActivity extends AbsActivity implements View.OnClickListener {

    private ImageView mImg;
    private String mImgUrl;

    private final ImageResultCallback mImageResultCallback = new ImageResultCallback() {
        @Override
        public void beforeCamera() {

        }

        @Override
        public void onSuccess(File file) {
            if (file != null && file.exists()) {
                ImgLoader.display(mContext, file, mImg);

                final List<UploadBean> list = new ArrayList<>();
                list.add(new UploadBean(file, UploadBean.IMG));

                final Dialog loading = DialogUitl.loadingDialog(mContext);
                loading.show();

                UploadUtil.startUpload(new CommonCallback<UploadStrategy>() {
                    @Override
                    public void callback(UploadStrategy uploadStrategy) {
                        uploadStrategy.upload(list, true, new UploadCallback() {
                            @Override
                            public void onFinish(List<UploadBean> list, boolean success) {
                                if (success) {
                                    LiveHttpUtil.setUserHomeBgImg(list.get(0).getRemoteFileName(), new HttpCallback() {
                                        @Override
                                        public void onSuccess(int code, String msg, String[] info) {
                                            if (code == 0) {
                                                if (info.length > 0) {
                                                    mImgUrl = JSON.parseObject(info[0]).getString("bg_img");
                                                }
                                                EventBus.getDefault().post(new UpdateFieldEvent());
                                            }
                                            ToastUtil.show(msg);
                                        }

                                        @Override
                                        public void onFinish() {
                                            loading.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });


            } else {
                ToastUtil.show(R.string.file_not_exist);
            }
        }


        @Override
        public void onFailure() {
        }
    };

    public static void forward(Context context, String url, boolean self) {
        Intent intent = new Intent(context, UserHomeBgActivity.class);
        intent.putExtra(Constants.URL, url);
        intent.putExtra("self", self);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_home_bg;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        boolean self = intent.getBooleanExtra("self", false);
        if (self) {
            findViewById(R.id.btn_choose).setOnClickListener(this);
            findViewById(R.id.btn_download).setOnClickListener(this);
        } else {
            findViewById(R.id.btn_choose).setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_download).setVisibility(View.INVISIBLE);
        }
        mImg = findViewById(R.id.img);
        mImg.setOnClickListener(this);
        mImgUrl = intent.getStringExtra(Constants.URL);
        ImgLoader.display(mContext, mImgUrl, mImg);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_choose) {
            chooseImage();
        } else if (i == R.id.btn_download) {
            download();
        } else if (i == R.id.img) {
            finish();
        }
    }

    /**
     * 选择图片
     */
    private void chooseImage() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{R.string.alumb, R.string.camera}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.camera) {
                    MediaUtil.getImageByCamera(UserHomeBgActivity.this, false, mImageResultCallback);
                } else if (tag == R.string.alumb) {
                    MediaUtil.getImageByAlbum(UserHomeBgActivity.this, false, mImageResultCallback);
                }
            }
        });
    }

    private void download() {
        PermissionUtil.request((AbsActivity) mContext,
                new PermissionCallback() {
                    @Override
                    public void onAllGranted() {
                        new DownloadUtil().download("save_img", CommonAppConfig.CAMERA_IMAGE_PATH, StringUtil.generateFileName() + ".png", mImgUrl, new DownloadUtil.Callback() {
                            @Override
                            public void onSuccess(File file) {
                                BitmapUtil.saveImageInfo(file);
                                ToastUtil.show(R.string.save_success);
                            }

                            @Override
                            public void onProgress(int progress) {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {

                    }
                },   Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.SET_USER_HOME_BG_IMG);
        super.onDestroy();
    }
}
