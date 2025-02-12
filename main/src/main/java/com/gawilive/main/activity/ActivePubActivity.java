package com.gawilive.main.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.bean.ConfigBean;
import com.hjq.permissions.Permission;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.gawilive.beauty.glide.ImgLoader;
import com.gawilive.common.CommonAppContext;
import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.activity.ChooseLocationActivity;
import com.gawilive.common.custom.ActiveVoiceLayout;
import com.gawilive.common.custom.ItemDecoration;
import com.gawilive.common.dialog.ActiveVideoPreviewDialog;
import com.gawilive.common.dialog.ImagePreviewDialog;
import com.gawilive.common.glide.GlideEngine;
import com.gawilive.common.glide.ImageFileCompressEngine;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.interfaces.ImageResultCallback;
import com.gawilive.common.interfaces.PermissionCallback;
import com.gawilive.common.upload.UploadBean;
import com.gawilive.common.upload.UploadCallback;
import com.gawilive.common.upload.UploadStrategy;
import com.gawilive.common.upload.UploadUtil;
import com.gawilive.common.utils.*;
import com.gawilive.im.utils.VoiceMediaPlayerUtil;
import com.gawilive.main.R;
import com.gawilive.main.adapter.ActiveImageAdapter;
import com.gawilive.main.http.MainHttpConsts;
import com.gawilive.main.http.MainHttpUtil;
import com.gawilive.main.views.ActiveRecordVoiceViewHolder2;
import com.gawilive.mall.bean.GoodsChooseSpecBean;
import com.gawilive.mall.http.MallHttpConsts;
import com.gawilive.mall.http.MallHttpUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 发布动态
 */
public class ActivePubActivity extends AbsActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_IMG = 10001;
    private static final int REQUEST_CODE_VIDEO = 10002;
    private static final int REQUEST_CODE_LOCATION = 10003;
    private static final int REQUEST_CODE_TOPIC = 10004;
    private static final int REQUEST_CODE_VOICE_PERMISSIONS = 1001; // Unique request code for voice permissions

    private EditText mEditText;
    private TextView mTextCount;
    private View mOptionGroup;
    private TextView mLocationText;
    private RecyclerView mRecyclerViewImage;
    private ActiveImageAdapter mImageAdapter;
    private String mLocationVal = "";//详细地址
    private ActiveRecordVoiceViewHolder2 mRecordVoiceViewHolder;
    private VoiceMediaPlayerUtil mPlayerUtil;
    private File mVoiceFile;//语音文件
    private int mVoiceSeconds;//语音文件时长 秒数
    private View mVoiceGroup;
    private ActiveVoiceLayout mVoiceLayout;
    private View mVideoGroup;
    private ImageView mImgVideo;
    private File mVideoFile;
    private int mType = Constants.ACTIVE_TYPE_TEXT;
    private Dialog mLoading;
    private View mBtnPub;
    private String mTopicId;
    private TextView mTvTopicName;
    private String mGoodsId;
    private ImageView mGoodsThumb;
    private TextView mGoodsPrice;
    private TextView mGoodsTitle;
    private TextView mGoodsSaleNum;
    private final ImageResultCallback mImageResultCallback = new ImageResultCallback() {
        @Override
        public void beforeCamera() {

        }

        @Override
        public void onSuccess(File file) {
            if (file != null && file.exists()) {
                setImage(file);
            }
        }

        @Override
        public void onFailure() {

        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_active;
    }

    @Override
    protected void main() {
        mBtnPub = findViewById(R.id.btn_publish);
        mBtnPub.setOnClickListener(this);
        findViewById(R.id.btn_topic).setOnClickListener(this);
        findViewById(R.id.btn_location).setOnClickListener(this);
        mTvTopicName = findViewById(R.id.topic_name);
        mOptionGroup = findViewById(R.id.option_group);
        mLocationText = findViewById(R.id.location_text);
        mEditText = findViewById(R.id.edit);
        mTextCount = findViewById(R.id.text_count);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTextCount != null) {
                    mTextCount.setText(StringUtil.contact(String.valueOf(s.length()), "/200"));
                }
                if (mBtnPub != null) {
                    mBtnPub.setEnabled(!((mType == Constants.ACTIVE_TYPE_TEXT || mType == Constants.ACTIVE_TYPE_GOODS) && TextUtils.isEmpty(s)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mGoodsId = getIntent().getStringExtra(Constants.MALL_GOODS_ID);
        if (TextUtils.isEmpty(mGoodsId)) {
            findViewById(R.id.btn_image).setOnClickListener(this);
            findViewById(R.id.btn_video).setOnClickListener(this);
            findViewById(R.id.btn_voice).setOnClickListener(this);
            mVoiceGroup = findViewById(R.id.voice_group);
            mVoiceLayout = findViewById(R.id.voice_layout);
            mVoiceLayout.setActionListener(new ActiveVoiceLayout.ActionListener() {
                @Override
                public void onPlayStart(ActiveVoiceLayout voiceLayout, File voiceFile) {
                    playVoiceFile(voiceFile);
                }

                @Override
                public void onPlayStop() {
                    stopPlayVoiceFile();
                }

                @Override
                public void onNeedDownload(ActiveVoiceLayout voiceLayout, String url) {

                }

                @Override
                public File getLocalFile() {
                    return mVoiceFile;
                }
            });
            findViewById(R.id.btn_voice_delete).setOnClickListener(this);
            mVideoGroup = findViewById(R.id.video_group);
            mImgVideo = findViewById(R.id.img_video);
            mVideoGroup.setOnClickListener(this);
            mRecyclerViewImage = findViewById(R.id.recyclerView_image);
            mRecyclerViewImage.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
            ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 15, 15);
            decoration.setOnlySetItemOffsetsButNoDraw(true);
            mRecyclerViewImage.addItemDecoration(decoration);
            mImageAdapter = new ActiveImageAdapter(mContext);
            mImageAdapter.setActionListener(new ActiveImageAdapter.ActionListener() {
                @Override
                public void onAddClick() {
                    chooseImage();
                }

                @Override
                public void onItemClick(int position) {
                    if (mImageAdapter == null) {
                        return;
                    }
                    final List<File> imageFileList = mImageAdapter.getImageFileList();
                    if (imageFileList == null || imageFileList.size() == 0) {
                        return;
                    }
                    ImagePreviewDialog dialog = new ImagePreviewDialog();
                    dialog.setImageInfo(imageFileList.size(), position, true, new ImagePreviewDialog.ActionListener() {
                        @Override
                        public void loadImage(ImageView imageView, int position) {
                            ImgLoader.display(mContext, imageFileList.get(position), imageView);
                        }

                        @Override
                        public void onDeleteClick(int position) {
                            if (mImageAdapter != null) {
                                mImageAdapter.deleteItem(position);
                            }
                        }

                        @Override
                        public String getImageUrl(int position) {
                            return null;
                        }
                    });
                    dialog.show(getSupportFragmentManager(), "ImagePreviewDialog");
                }

                @Override
                public void onDeleteAll() {
                    if (mOptionGroup != null && mOptionGroup.getVisibility() != View.VISIBLE) {
                        mOptionGroup.setVisibility(View.VISIBLE);
                    }
                    mType = Constants.ACTIVE_TYPE_TEXT;
                    if (mBtnPub != null) {
                        String text = mEditText.getText().toString();
                        mBtnPub.setEnabled(!TextUtils.isEmpty(text));
                    }
                }
            });
            mRecyclerViewImage.setAdapter(mImageAdapter);
        } else {
            setTitle(WordUtil.getString(R.string.a_082));
            mType = Constants.ACTIVE_TYPE_GOODS;
            mOptionGroup.setVisibility(View.GONE);
            findViewById(R.id.goods_group).setVisibility(View.VISIBLE);
            mGoodsThumb = findViewById(R.id.goods_thumb);
            mGoodsPrice = findViewById(R.id.goods_price);
            mGoodsTitle = findViewById(R.id.goods_title);
            mGoodsSaleNum = findViewById(R.id.goods_sale_num);
            MallHttpUtil.getGoodsInfo(mGoodsId, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    JSONObject goodsInfo = obj.getJSONObject("goods_info");
                    List<String> thumbs = JSON.parseArray(goodsInfo.getString("thumbs_format"), String.class);
                    if (thumbs != null && thumbs.size() > 0) {
                        if (mGoodsThumb != null) {
                            ImgLoader.display(mContext, thumbs.get(0), mGoodsThumb);
                        }
                    }
                    if (mGoodsTitle != null) {
                        mGoodsTitle.setText(goodsInfo.getString("name"));
                    }
                    List<GoodsChooseSpecBean> specList = JSON.parseArray(goodsInfo.getString("specs_format"), GoodsChooseSpecBean.class);
                    if (specList != null && specList.size() > 0) {
                        GoodsChooseSpecBean bean = specList.get(0);
                        if (mGoodsPrice != null) {
                            mGoodsPrice.setText(bean.getPrice());
                        }
                    }
                    if (mGoodsSaleNum != null) {
                        mGoodsSaleNum.setText(String.format(WordUtil.getString(R.string.mall_114), goodsInfo.getString("sale_nums")));
                    }
                }
            });
        }
        getLocation();
    }

    @Override
    public void onClick(View v) {
        if (!ClickUtil.canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_publish) {
            publish();
        } else if (i == R.id.btn_image) {
            chooseImage();
        } else if (i == R.id.btn_video) {
            chooseVideo();
        } else if (i == R.id.btn_voice) {

            clickVoice();
        } else if (i == R.id.btn_topic) {
            chooseTopic();
        } else if (i == R.id.btn_location) {
            chooseLocation();
        } else if (i == R.id.btn_voice_delete) {
            deleteVoice();
        } else if (i == R.id.video_group) {
            previewVideo();
        }
    }

    /**
     * 开始录音
     */
    private void clickVoice() {
        if (!FloatWindowHelper.checkVoice(false)) {
            //L.e("CHECK", "Voice click blocked by FloatWindowHelper.checkVoice()");
            return;
        }

        PermissionUtil.request(this,
                new PermissionCallback() {
                    @Override
                    public void onAllGranted() {
                        initializeVoiceFeature();
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {

                    }
                },
                Permission.READ_MEDIA_IMAGES,
                Permission.READ_MEDIA_VIDEO,
                Permission.CAMERA,
                Permission.RECORD_AUDIO,
                Permission.WRITE_EXTERNAL_STORAGE
        );

    }

    private void requestPermissionsManually(List<String> permissions) {
        // Filter permissions that are not yet granted
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        // Request permissions if there are any ungranted
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_CODE_VOICE_PERMISSIONS);
        } else {

        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_VOICE_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                initializeVoiceFeature();
            } else {
                Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeVoiceFeature() {
        try {
            if (mRecordVoiceViewHolder == null) {
                View rootView = findViewById(R.id.root);
                if (rootView != null) {
                    mRecordVoiceViewHolder = new ActiveRecordVoiceViewHolder2(mContext, (ViewGroup) rootView);
                    mRecordVoiceViewHolder.subscribeActivityLifeCycle();
                } else {
                    ToastUtil.show("出错了");
                  //  L.e("CHECK", "Error: rootView is null. Check R.id.root in your layout.");
                    return;
                }
            }
            if (!mRecordVoiceViewHolder.isShowing()) {
                mRecordVoiceViewHolder.addToParent();
            }
        } catch (Exception e) {

            //L.e("CHECK", "Exception in initializing voice view holder: " + e.getMessage());
        }
    }











    private static @NonNull List<String> getStrings() {
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 or higher
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO);
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO);
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        return permissions;
    }


    /**
     * 选择图片
     */
    private void chooseImage() {

        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setCompressEngine(new ImageFileCompressEngine())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(9)
                .setMinSelectNum(1) // Set minimum selection quantity
                .setImageSpanCount(4) //Set the number of images displayed in each row

                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        List<String> imagePathList = new ArrayList<>();
                        for (LocalMedia localMedia : result) {
                            String filePath = localMedia.getAvailablePath();
                            if (PictureMimeType.isContent(filePath)) {
                                if (localMedia.isCut() || localMedia.isCompressed()) {
                                    imagePathList.add(filePath);
                                } else {
                                    Uri uri = Uri.parse(filePath);
                                    String path =
                                            ImageUriPathUtil.getPathFromUri(CommonAppContext.getInstance(), uri);
                                    imagePathList.add(path);
                                }
                            } else {
                                imagePathList.add(filePath);
                            }
                        }
                        if (mImageAdapter != null) {
                            mImageAdapter.insertList(imagePathList);
                        }
                        if (mOptionGroup != null && mOptionGroup.getVisibility() != View.GONE) {
                            mOptionGroup.setVisibility(View.GONE);
                        }
                        mType = Constants.ACTIVE_TYPE_IMAGE;
                        if (mBtnPub != null) {
                            mBtnPub.setEnabled(true);
                        }

                    }

                    @Override
                    public void onCancel() {

                    }
                });
//        PictureSelector.create(ActivePubActivity.this)
//                .openGallery(SelectMimeType.ofImage()) // 设置图片选择的类型，这里是图片
//
//                .forResult();
    }

    // 输入视频路径

    // 输出视频路径
    String outputVideoPath = "/sdcard/output_video.mp4";



    /**
     * 设置水印
     */
    private void getWaterMark(final CommonCallback<Bitmap> commonCallback) {
        if (commonCallback == null) {
            return;
        }
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean == null) {
            commonCallback.callback(null);
            return;
        }
        String waterMarkUrl = configBean.getWaterMarkUrl();
        if (TextUtils.isEmpty(waterMarkUrl)) {
            commonCallback.callback(null);
            return;
        }
        File dir = new File(CommonAppConfig.WATER_MARK_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = MD5Util.getMD5(waterMarkUrl);
        File waterMarkFile = new File(dir, fileName);
        if (waterMarkFile.exists()) {
            commonCallback.callback(BitmapUtil.getInstance().decodeBitmap(waterMarkFile));
            return;
        }
        DownloadUtil downloadUtil = new DownloadUtil();
        downloadUtil.download("waterMark", dir, fileName, waterMarkUrl, new DownloadUtil.Callback() {
            @Override
            public void onSuccess(File resultFile) {
                if (resultFile != null && resultFile.exists()) {
                    commonCallback.callback(BitmapUtil.getInstance().decodeBitmap(resultFile));
                }
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }


    /**
     * 选择视频
     */
    private void chooseVideo() {
        if (!FloatWindowHelper.checkVoice(false)) {
            return;
        }

        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofVideo())
                .setCompressEngine(new ImageFileCompressEngine())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(1)
                .setMinSelectNum(1) // Set minimum selection quantity
                .setImageSpanCount(4) // Set the number of images displayed in each row
                .setFilterVideoMinSecond(120)
                .setFilterVideoMaxSecond(60)
                .setRecordVideoMaxSecond(120)

                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        if (result.size() > 0) {
                            LocalMedia localMedia = result.get(0);
                            String filePath = localMedia.getAvailablePath();
                            String videoPath = "";
                            if (PictureMimeType.isContent(filePath)) {
                                if (localMedia.isCut() || localMedia.isCompressed()) {
                                    videoPath = filePath;
                                } else {
                                    Uri uri = Uri.parse(filePath);
                                    videoPath =
                                            ImageUriPathUtil.getPathFromUri(CommonAppContext.getInstance(), uri);

                                }
                            } else {
                                videoPath = filePath;
                            }

                            mVideoFile = new File(videoPath);
                            if (mVideoGroup != null && mVideoGroup.getVisibility() != View.VISIBLE) {
                                mVideoGroup.setVisibility(View.VISIBLE);
                            }
                            ImgLoader.displayVideoThumb(mContext, mVideoFile, mImgVideo);
                            if (mOptionGroup != null && mOptionGroup.getVisibility() != View.GONE) {
                                mOptionGroup.setVisibility(View.GONE);
                            }
                            mType = Constants.ACTIVE_TYPE_VIDEO;
                            if (mBtnPub != null) {
                                mBtnPub.setEnabled(true);
                            }
                        }

                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    /**
     * 选择话题
     */
    private void chooseTopic() {
        startActivityForResult(new Intent(mContext, ActiveChooseTopicActivity.class), REQUEST_CODE_TOPIC);
    }

    /**
     * 选择地址
     */
    private void chooseLocation() {
        PermissionUtil.request(this, new PermissionCallback() {
            @Override
            public void onAllGranted() {
                startActivityForResult(new Intent(mContext, ChooseLocationActivity.class), REQUEST_CODE_LOCATION);
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {

            }
        }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && intent != null) {
            if (requestCode == REQUEST_CODE_IMG) {
                setImage(intent);
            } else if (requestCode == REQUEST_CODE_VIDEO) {
                setVideo(intent);
            } else if (requestCode == REQUEST_CODE_LOCATION) {
                setLocation(intent);
            } else if (requestCode == REQUEST_CODE_TOPIC) {
                String topicName = intent.getStringExtra(Constants.CLASS_NAME);
                if (mTvTopicName != null) {
                    mTvTopicName.setHint("");
                    mTvTopicName.setText(topicName);
                }
                mTopicId = intent.getStringExtra(Constants.CLASS_ID);
            }
        }
    }


    /**
     * 选择图片
     */

    private void setImage(File file) {
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add(file.getAbsolutePath());
        if (mImageAdapter != null) {
            mImageAdapter.insertList(imagePathList);
        }
        if (mOptionGroup != null && mOptionGroup.getVisibility() != View.GONE) {
            mOptionGroup.setVisibility(View.GONE);
        }
        mType = Constants.ACTIVE_TYPE_IMAGE;
        if (mBtnPub != null) {
            mBtnPub.setEnabled(true);
        }
    }


    /**
     * 选择图片
     */
    private void setImage(Intent intent) {
        List<String> imagePathList = intent.getStringArrayListExtra(Constants.CHOOSE_IMG);
        if (imagePathList == null || imagePathList.size() == 0) {
            return;
        }
        if (mImageAdapter != null) {
            mImageAdapter.insertList(imagePathList);
        }
        if (mOptionGroup != null && mOptionGroup.getVisibility() != View.GONE) {
            mOptionGroup.setVisibility(View.GONE);
        }
        mType = Constants.ACTIVE_TYPE_IMAGE;
        if (mBtnPub != null) {
            mBtnPub.setEnabled(true);
        }
    }


    /**
     * 选择视频
     */
    private void setVideo(Intent intent) {

        String videoPath = intent.getStringExtra(Constants.VIDEO_PATH);
        if (TextUtils.isEmpty(videoPath)) {
            return;
        }
        mVideoFile = new File(videoPath);
        if (mVideoGroup != null && mVideoGroup.getVisibility() != View.VISIBLE) {
            mVideoGroup.setVisibility(View.VISIBLE);
        }
        ImgLoader.displayVideoThumb(mContext, mVideoFile, mImgVideo);
        if (mOptionGroup != null && mOptionGroup.getVisibility() != View.GONE) {
            mOptionGroup.setVisibility(View.GONE);
        }
        mType = Constants.ACTIVE_TYPE_VIDEO;
        if (mBtnPub != null) {
            mBtnPub.setEnabled(true);
        }
    }


    /**
     * 取消视频
     */
    private void cancelVideo() {
        mVideoFile = null;
        if (mImgVideo != null) {
            mImgVideo.setImageDrawable(null);
        }
        if (mVideoGroup != null && mVideoGroup.getVisibility() != View.GONE) {
            mVideoGroup.setVisibility(View.GONE);
        }
        if (mOptionGroup != null && mOptionGroup.getVisibility() != View.VISIBLE) {
            mOptionGroup.setVisibility(View.VISIBLE);
        }
        mType = Constants.ACTIVE_TYPE_TEXT;
        if (mBtnPub != null) {
            String text = mEditText.getText().toString();
            mBtnPub.setEnabled(!TextUtils.isEmpty(text));
        }
    }

    /**
     * 预览视频
     */
    private void previewVideo() {
        if (mVideoFile == null) {
            return;
        }
        ActiveVideoPreviewDialog dialog = new ActiveVideoPreviewDialog();
        dialog.setActionListener(new ActiveVideoPreviewDialog.ActionListener() {
            @Override
            public void onDeleteClick() {
                cancelVideo();
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(Constants.VIDEO_PATH, mVideoFile.getAbsolutePath());
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "ActiveVideoPreviewDialog");
    }


    /**
     * 选择地址
     */
    public void setLocation(Intent intent) {
        mLocationVal = intent.getStringExtra(Constants.CHOOSE_LOCATION);
        if (mLocationText != null) {
            if (TextUtils.isEmpty(mLocationVal)) {
                mLocationText.setText(R.string.location_no);
            } else {
                mLocationText.setText(mLocationVal);
            }
        }
    }


    /**
     * 播放语音
     */
    public void playVoiceFile(File file) {
        if (file == null) {
            return;
        }
        if (mPlayerUtil == null) {
            mPlayerUtil = new VoiceMediaPlayerUtil(mContext);
            mPlayerUtil.setActionListener(new VoiceMediaPlayerUtil.ActionListener() {
                @Override
                public void onPlayEnd() {
                    if (mRecordVoiceViewHolder != null && mRecordVoiceViewHolder.isShowing()) {
                        mRecordVoiceViewHolder.onListenEnd();
                    }
                    if (mVoiceGroup.getVisibility() == View.VISIBLE && mVoiceLayout != null) {
                        mVoiceLayout.stopPlay();
                    }
                }
            });
        }
        mPlayerUtil.startPlay(file.getAbsolutePath());
    }


    /**
     * 停止播放语音
     */
    public void stopPlayVoiceFile() {
        if (mPlayerUtil != null) {
            mPlayerUtil.stopPlay();
        }
    }

    /**
     * 使用语音
     */
    public void useVoice(File voiceFile, int voiceSeconds) {
        mVoiceFile = voiceFile;
        mVoiceSeconds = voiceSeconds;
        if (mVoiceGroup != null && mVoiceGroup.getVisibility() != View.VISIBLE) {
            mVoiceGroup.setVisibility(View.VISIBLE);
        }
        if (mOptionGroup != null && mOptionGroup.getVisibility() != View.GONE) {
            mOptionGroup.setVisibility(View.GONE);
        }
        if (mVoiceLayout != null) {
            mVoiceLayout.setSecondsMax(voiceSeconds);
        }
        mType = Constants.ACTIVE_TYPE_VOICE;
        if (mBtnPub != null) {
            mBtnPub.setEnabled(true);
        }
    }


    /**
     * 删除语音文件
     */
    private void deleteVoice() {
        if (mVoiceLayout != null) {
            mVoiceLayout.stopPlay();
        }
        mVoiceSeconds = 0;
        if (mVoiceFile != null && mVoiceFile.exists()) {
            mVoiceFile.delete();
        }
        mVoiceFile = null;
        if (mVoiceGroup != null && mVoiceGroup.getVisibility() != View.GONE) {
            mVoiceGroup.setVisibility(View.GONE);
        }
        if (mOptionGroup != null && mOptionGroup.getVisibility() != View.VISIBLE) {
            mOptionGroup.setVisibility(View.VISIBLE);
        }
        mType = Constants.ACTIVE_TYPE_TEXT;
        if (mBtnPub != null) {
            String text = mEditText.getText().toString();
            mBtnPub.setEnabled(!TextUtils.isEmpty(text));
        }
    }

    private void release() {
        MainHttpUtil.cancel(MainHttpConsts.ACTIVE_PUBLISH);
        MallHttpUtil.cancel(MallHttpConsts.GET_GOODS_INFO);
        if (mPlayerUtil != null) {
            mPlayerUtil.destroy();
        }
        mPlayerUtil = null;
        if (mVoiceLayout != null) {
            mVoiceLayout.release();
        }
        mVoiceLayout = null;
        UploadUtil.cancelUpload();
        hideLoading();
        mLoading = null;
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        boolean showDialog = false;
        if (mType == Constants.ACTIVE_TYPE_TEXT) {
            if (mEditText != null && mEditText.getText().length() > 0) {
                showDialog = true;
            }
        } else {
            showDialog = true;
        }
        if (showDialog) {
            new DialogUitl.Builder(mContext)
                    .setContent(WordUtil.getString(R.string.active_cancel))
                    .setCancelable(true)
                    .setBackgroundDimEnabled(true)
                    .setClickCallback(new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            release();
                            ActivePubActivity.super.onBackPressed();
                        }
                    })
                    .build()
                    .show();

        } else {
            super.onBackPressed();
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

    /**
     * 发布动态
     */
    private void publish() {
        if (mType == Constants.ACTIVE_TYPE_IMAGE) {
            uploadImage();
        } else if (mType == Constants.ACTIVE_TYPE_VOICE) {
            uploadVoice();
        } else if (mType == Constants.ACTIVE_TYPE_VIDEO) {
            uploadVideo();
        } else {
            submit("", "", "", "");
        }
    }

    /**
     * 上传图片
     */
    private void uploadImage() {
        if (mImageAdapter == null) {
            return;
        }
        List<File> imageFileList = mImageAdapter.getImageFileList();
        if (imageFileList == null || imageFileList.size() == 0) {
            return;
        }
        final List<UploadBean> uploadList = new ArrayList<>();
        for (File file : imageFileList) {
            uploadList.add(new UploadBean(file, UploadBean.IMG));
        }
        showLoading();
        UploadUtil.startUpload(new CommonCallback<UploadStrategy>() {
            @Override
            public void callback(UploadStrategy uploadStrategy) {
                uploadStrategy.upload(uploadList, true, new UploadCallback() {
                    @Override
                    public void onFinish(List<UploadBean> list, boolean success) {
                        if (success) {
                            StringBuilder sb = new StringBuilder();
                            for (UploadBean bean : list) {
                                sb.append(bean.getRemoteFileName());
                                sb.append(";");
                            }
                            String s = sb.toString();
                            if (s.length() > 0) {
                                s = s.substring(0, s.length() - 1);
                            }
                            submit(s, "", "", "");
                        }
                    }
                });
            }
        });
    }


    /**
     * 上传语音
     */
    private void uploadVoice() {
        if (mVoiceFile == null || mVoiceFile.length() == 0 || !mVoiceFile.exists()) {
            return;
        }
        final List<UploadBean> uploadList = new ArrayList<>();
        uploadList.add(new UploadBean(mVoiceFile, UploadBean.VOICE));
        showLoading();
        UploadUtil.startUpload(new CommonCallback<UploadStrategy>() {
            @Override
            public void callback(UploadStrategy strategy) {
                strategy.upload(uploadList, false, new UploadCallback() {
                    @Override
                    public void onFinish(List<UploadBean> list, boolean success) {
                        submit("", "", "", list.get(0).getRemoteFileName());
                    }
                });
            }
        });
    }


    /**
     * 上传视频
     */
    private void uploadVideo() {
        if (mVideoFile == null || mVideoFile.length() == 0 || !mVideoFile.exists()) {
            return;
        }
        final List<UploadBean> uploadList = new ArrayList<>();
        uploadList.add(new UploadBean(mVideoFile, UploadBean.VIDEO));
        File coverImageFile = createVideoCoverImage(mVideoFile.getAbsolutePath());
        if (coverImageFile != null && coverImageFile.length() > 0 && coverImageFile.exists()) {
            uploadList.add(new UploadBean(coverImageFile, UploadBean.IMG));
        }
        showLoading();
        UploadUtil.startUpload(new CommonCallback<UploadStrategy>() {
            @Override
            public void callback(UploadStrategy strategy) {
                strategy.upload(uploadList, true, new UploadCallback() {
                    @Override
                    public void onFinish(List<UploadBean> list, boolean success) {
                        String videoUrl = list.get(0).getRemoteFileName();
                        String videoImage = "";
                        if (list.size() > 1) {
                            videoImage = list.get(1).getRemoteFileName();
                        }
                        submit("", videoImage, videoUrl, "");
                    }
                });
            }
        });

    }

    /**
     * 生成视频封面图
     */
    private File createVideoCoverImage(String videoPath) {
        MediaMetadataRetriever mmr = null;
        Bitmap bitmap = null;
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            bitmap = mmr.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            bitmap = null;
            e.printStackTrace();
        } finally {
            if (mmr != null) {
                try {
                    mmr.release();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (bitmap == null) {
            return null;
        }
        String coverImagePath = videoPath.replace(".mp4", ".jpg");
        File imageFile = new File(coverImagePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        return imageFile;

    }

    private void submit(String images, String videoImage, String videoUrl, String voiceUrl) {
        String text = mEditText.getText().toString();
        MainHttpUtil.activePublish(mType, text, images, videoImage, videoUrl, mLocationVal, voiceUrl, mVoiceSeconds, mTopicId, mGoodsId, new HttpCallback() {

            @Override
            public void onSuccess(int code, String msg, String[] info) {

                if (code == 0) {
                    if (mPlayerUtil != null) {
                        mPlayerUtil.destroy();
                    }
                    mPlayerUtil = null;
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

    /**
     * 获取所在位置
     */
    private void getLocation() {
        PermissionUtil.request(this,
                new PermissionCallback() {
                    @Override
                    public void onAllGranted() {
                        LocationUtil.getInstance().startLocation();
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {

                    }
                },
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        );
    }
}
