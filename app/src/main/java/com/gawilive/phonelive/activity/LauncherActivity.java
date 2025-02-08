package com.gawilive.phonelive.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.event.InitEvent;
import com.gawilive.common.http.HttpCallback;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.project.codeinstallsdk.CodeInstall;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.CommonAppContext;
import com.gawilive.common.Constants;
import com.gawilive.common.bean.AdBean;
import com.gawilive.common.bean.ConfigBean;
import com.gawilive.common.bean.UserBean;
import com.gawilive.common.custom.CircleProgress;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.CommonHttpConsts;
import com.gawilive.common.http.CommonHttpUtil;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.utils.*;
import com.gawilive.live.views.LauncherAdViewHolder;
import com.gawilive.main.activity.MainActivity;
import com.gawilive.main.dialog.OnShelfLoginTipDialogFragment;
import com.gawilive.main.http.MainHttpConsts;
import com.gawilive.main.http.MainHttpUtil;
import com.gawilive.phonelive.AppContext;
import com.gawilive.phonelive.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/17.
 */
public class LauncherActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LauncherActivity";
    private static final int WHAT_GET_CONFIG = 0;
    private static final int WHAT_COUNT_DOWN = 1;
    private Handler mHandler;
    protected Context mContext;
    private ViewGroup mRoot;
    private ImageView mCover;
    private ViewGroup mContainer;
    private CircleProgress mCircleProgress;
    private List<AdBean> mAdList;
    private List<ImageView> mImageViewList;
    private int mMaxProgressVal;
    private int mCurProgressVal;
    private int mAdIndex;
    private int mInterval = 2000;
    private View mBtnSkipImage;
    private View mBtnSkipVideo;
    private TXCloudVideoView mTXCloudVideoView;
    private TXVodPlayer mPlayer;
    private LauncherAdViewHolder mLauncherAdViewHolder;
    private boolean mPaused;
    private int mVideoLastProgress;
    private boolean mForward;
    private boolean mWaitEnd;
    private View mAdTip;
    private boolean mGetConfig;
    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    Toast.makeText(this, "Please enable notification permission", Toast.LENGTH_SHORT).show();
                }
            });
    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Toast.makeText(this, "Please enable notification permission", Toast.LENGTH_SHORT).show();
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //The code below is to prevent a bug:
        // After receiving the Aurora notification, click the notification. If the app is not started, start the app. Then switch to the background and click the desktop icon again. The app will restart instead of returning to the foreground.。
        Intent intent = getIntent();
        if (!isTaskRoot()
                && intent != null
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }
        setStatusBar();
        setContentView(R.layout.activity_launcher);
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mContext = this;
        mRoot = findViewById(R.id.root);
        mCover = findViewById(R.id.cover);
        mCircleProgress = findViewById(R.id.progress);
        mContainer = findViewById(R.id.container);
        mBtnSkipImage = findViewById(R.id.btn_skip_img);
        mBtnSkipVideo = findViewById(R.id.btn_skip_video);
        mBtnSkipImage.setOnClickListener(this);
        mBtnSkipVideo.setOnClickListener(this);
        mAdTip = findViewById(R.id.ad_tip);
        EventBus.getDefault().register(this);
        askNotificationPermission();

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_GET_CONFIG:
                        mWaitEnd = true;
                        getConfig();
                        break;
                    case WHAT_COUNT_DOWN:
                        updateCountDown();
                        break;
                }
            }
        };
        String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(
                SpUtil.UID, SpUtil.TOKEN);
        //Logged in, display the boot page
        if (!TextUtils.isEmpty(uidAndToken[0]) && !TextUtils.isEmpty(uidAndToken[1])
                && CommonAppConfig.getInstance().isLogin()
        ) {
            CodeInstall.init(LauncherActivity.this);
            AppContext.initSdk();
            mHandler.sendEmptyMessageDelayed(WHAT_GET_CONFIG, 1000);
        } else {
            //Not logged in, show privacy policy
            OnShelfLoginTipDialogFragment fragment = new OnShelfLoginTipDialogFragment();
            fragment.setOnConfirmClick(new Runnable() {
                @Override
                public void run() {
                    AppContext.initSdk();
                    mHandler.sendEmptyMessageDelayed(WHAT_GET_CONFIG, 1000);
                }
            });
            fragment.show(getSupportFragmentManager(), "LoginTipDialogFragment");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInitEvent(InitEvent e) {
        if (mWaitEnd) {
            getConfig();
        }
    }

    private void checkHasAdLink(int index) {
        if (index >= 0 && index < mAdList.size()) {
            AdBean adBean = mAdList.get(index);
            if (adBean != null && !TextUtils.isEmpty(adBean.getLink())) {
                if (mAdTip.getVisibility() != View.VISIBLE) {
                    mAdTip.setVisibility(View.VISIBLE);
                }
            } else {
                if (mAdTip.getVisibility() == View.VISIBLE) {
                    mAdTip.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 图片倒计时
     */
    private void updateCountDown() {
        mCurProgressVal += 100;
        if (mCurProgressVal > mMaxProgressVal) {
            return;
        }
        if (mCircleProgress != null) {
            mCircleProgress.setCurProgress(mCurProgressVal);
        }
        int index = mCurProgressVal / mInterval;
        if (index < mAdList.size() && mAdIndex != index) {
            View v = mImageViewList.get(mAdIndex);
            if (v != null && v.getVisibility() == View.VISIBLE) {
                v.setVisibility(View.INVISIBLE);
            }
            mAdIndex = index;
            checkHasAdLink(index);
        }
        if (mCurProgressVal < mMaxProgressVal) {
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_COUNT_DOWN, 100);
            }
        } else if (mCurProgressVal == mMaxProgressVal) {
            checkUidAndToken();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(MultiLanguageUtil.attachBaseContext(newBase));
        //appIt will be called after the killing process is started. Activity attachBaseContext
        MultiLanguageUtil.getInstance().setConfiguration(newBase);
    }

    /**
     * 获取Config信息
     */

    private void getConfig() {
        CommonHttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                if (mGetConfig) {
                    return;
                }
                mGetConfig = true;
                if (bean != null) {
                    ((AppContext) CommonAppContext.getInstance()).initBeautySdk(bean.getBeautyAppId(), bean.getBeautyKey());
                    String adInfo = bean.getAdInfo();
                    if (!TextUtils.isEmpty(adInfo)) {
                        JSONObject obj = JSON.parseObject(adInfo);
                        if (obj.getIntValue("switch") == 1) {
                            List<AdBean> list = JSON.parseArray(obj.getString("list"), AdBean.class);
                            if (list != null && list.size() > 0) {
                                mAdList = list;
                                mInterval = obj.getIntValue("time") * 1000;
                                if (mContainer != null) {
                                    mContainer.setOnClickListener(LauncherActivity.this);
                                }
                                playAD(obj.getIntValue("type") == 0);
                            } else {
                                checkUidAndToken();
                            }
                        } else {
                            checkUidAndToken();
                        }
                    } else {
                        checkUidAndToken();
                    }
                }
            }
        });
    }

    // 强制修改直播状态
    private void updateIsLive() {
//        CommonHttpUtil.updateIsLive(new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//
//            }
//        });
    }

    /**
     * 检查uid和token是否存在
     */
    private void checkUidAndToken() {
        if (mForward) {
            return;
        }
        mForward = true;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(
                SpUtil.UID, SpUtil.TOKEN);
        final String uid = uidAndToken[0];
        final String token = uidAndToken[1];
        if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
            if (CommonAppConfig.getInstance().isLogin()) {
                MainHttpUtil.getBaseInfo(uid, token, new CommonCallback<UserBean>() {
                    @Override
                    public void callback(UserBean bean) {
                        if (bean != null) {
                            updateIsLive();
                            CommonAppConfig.getInstance().setLoginInfo(uid, token, false);
                            forwardMainActivity();
                        }
                    }
                }, () -> {
                    updateIsLive();
                    MMKVUtils.removeAll();
                    CommonAppConfig.getInstance().clearLoginInfo();
                    forwardMainActivity();
                });
            } else {
                updateIsLive();
                forwardMainActivity();
            }
        } else {
            updateIsLive();
            CommonAppConfig.getInstance().setLoginInfo(Constants.NOT_LOGIN_UID, Constants.NOT_LOGIN_TOKEN, false);
            forwardMainActivity();
        }
    }


    /**
     * 跳转到首页
     */
    private void forwardMainActivity() {
        releaseVideo();
        MainActivity.forward(mContext);
        finish();
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        releaseVideo();
        if (mLauncherAdViewHolder != null) {
            mLauncherAdViewHolder.release();
        }
        mLauncherAdViewHolder = null;
        super.onDestroy();
        //L.e(TAG, "----------> onDestroy");
    }

    /**
     * 设置透明状态栏
     */
    private void setStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(0);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_skip_img || i == R.id.btn_skip_video) {
            if (mBtnSkipImage != null) {
                mBtnSkipImage.setClickable(false);
            }
            if (mBtnSkipVideo != null) {
                mBtnSkipVideo.setClickable(false);
            }
            checkUidAndToken();
        } else if (i == R.id.container) {
            clickAD();
        }
    }

    /**
     * 点击广告
     */
    private void clickAD() {
        if (mAdList != null && mAdList.size() > mAdIndex) {
            AdBean adBean = mAdList.get(mAdIndex);
            if (adBean != null) {
                String link = adBean.getLink();
                if (!TextUtils.isEmpty(link)) {
                    if (mHandler != null) {
                        mHandler.removeCallbacksAndMessages(null);
                    }
                    if (mContainer != null) {
                        mContainer.setClickable(false);
                    }
                    releaseVideo();
                    if (mLauncherAdViewHolder == null) {
                        mLauncherAdViewHolder = new LauncherAdViewHolder(mContext, mRoot, link);
                        mLauncherAdViewHolder.addToParent();
                        mLauncherAdViewHolder.loadData();
                        mLauncherAdViewHolder.show();
                        mLauncherAdViewHolder.setActionListener(new LauncherAdViewHolder.ActionListener() {
                            @Override
                            public void onHideClick() {
                                checkUidAndToken();
                            }
                        });
                    }
                }
            }
        }
    }

    private void releaseVideo() {
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
            mPlayer.setPlayListener(null);
        }
        mPlayer = null;
    }


    /**
     * 播放广告
     */
    private void playAD(boolean isImage) {
        if (mContainer == null) {
            return;
        }
        if (isImage) {
            int imgSize = mAdList.size();
            if (imgSize > 0) {
                mImageViewList = new ArrayList<>();
                for (int i = 0; i < imgSize; i++) {
                    final ImageView imageView = new ImageView(mContext);
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setBackgroundColor(0xffffffff);
                    mImageViewList.add(imageView);
                    ImgLoader.displayDrawable(mContext, mAdList.get(i).getUrl(), new ImgLoader.DrawableCallback() {
                        @Override
                        public void onLoadSuccess(Drawable drawable) {
                            imageView.setImageDrawable(drawable);
                            if (mCover != null && mCover.getVisibility() == View.VISIBLE) {
                                mCover.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onLoadFailed() {

                        }
                    });
                }
                for (int i = imgSize - 1; i >= 0; i--) {
                    mContainer.addView(mImageViewList.get(i));
                }
                if (mBtnSkipImage != null && mBtnSkipImage.getVisibility() != View.VISIBLE) {
                    mBtnSkipImage.setVisibility(View.VISIBLE);
                }
                mMaxProgressVal = imgSize * mInterval;
                if (mCircleProgress != null) {
                    mCircleProgress.setMaxProgress(mMaxProgressVal);
                }
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(WHAT_COUNT_DOWN, 100);
                }
                checkHasAdLink(0);
            } else {
                checkUidAndToken();
            }
        } else {
            if (mAdList == null || mAdList.size() == 0) {
                checkUidAndToken();
                return;
            }
            String videoUrl = mAdList.get(0).getUrl();
            if (TextUtils.isEmpty(videoUrl)) {
                checkUidAndToken();
                return;
            }
            String videoFileName = MD5Util.getMD5(videoUrl);
            if (TextUtils.isEmpty(videoFileName)) {
                checkUidAndToken();
                return;
            }
            File file = new File(getCacheDir(), videoFileName);
            if (file.exists()) {
                playAdVideo(file);
            } else {
                downloadAdFile(videoUrl, videoFileName);
            }
        }
    }

    @Override
    protected void onPause() {
        mPaused = true;
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.setMute(true);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused) {
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.setMute(false);
            }
        }
        mPaused = false;
    }

    /**
     * 下载视频
     */
    private void downloadAdFile(String url, String fileName) {
        DownloadUtil downloadUtil = new DownloadUtil();
        downloadUtil.download("ad_video", getCacheDir(), fileName, url, new DownloadUtil.Callback() {
            @Override
            public void onSuccess(File file) {
                playAdVideo(file);
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onError(Throwable e) {
                checkUidAndToken();
            }
        });
    }

    /**
     * 播放视频
     */
    private void playAdVideo(File videoFile) {
        if (mBtnSkipVideo != null && mBtnSkipVideo.getVisibility() != View.VISIBLE) {
            mBtnSkipVideo.setVisibility(View.VISIBLE);
        }
        mTXCloudVideoView = new TXCloudVideoView(mContext);
        mTXCloudVideoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // mTXCloudVideoView.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mContainer.addView(mTXCloudVideoView);
        mPlayer = new TXVodPlayer(mContext);
        mPlayer.setPlayerView(mTXCloudVideoView);
        mPlayer.setAutoPlay(true);
        mPlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int e, Bundle bundle) {
                if (e == TXLiveConstants.PLAY_EVT_PLAY_END) {//获取到视频播放完毕的回调
                    checkUidAndToken();
                    //  L.e(TAG, "convert to BASIC Latin------>");
                } else if (e == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {////获取到视频宽高回调
                    float videoWidth = bundle.getInt("EVT_PARAM1", 0);
                    float videoHeight = bundle.getInt("EVT_PARAM2", 0);
                    if (mTXCloudVideoView != null && videoWidth > 0 && videoHeight > 0) {
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTXCloudVideoView.getLayoutParams();
                        int targetH = 0;
                        if (videoWidth >= videoHeight) {//横屏
                            params.gravity = Gravity.CENTER_VERTICAL;
                            targetH = (int) (mTXCloudVideoView.getWidth() / videoWidth * videoHeight);
                        } else {
                            targetH = ViewGroup.LayoutParams.MATCH_PARENT;
                        }
                        if (targetH != params.height) {
                            params.height = targetH;
                            mTXCloudVideoView.requestLayout();
                        }
                    }
                } else if (e == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {
                    if (mCover != null && mCover.getVisibility() == View.VISIBLE) {
                        mCover.setVisibility(View.INVISIBLE);
                    }
                } else if (e == TXLiveConstants.PLAY_ERR_NET_DISCONNECT ||
                        e == TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND) {
                    ToastUtil.show(WordUtil.getString(R.string.live_play_error));
                    checkUidAndToken();
                } else if (e == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
                    int progress = bundle.getInt("EVT_PLAY_PROGRESS_MS");
                    if (mVideoLastProgress == progress) {
                        //     L.e(TAG, "Video playback ends------>");
                        checkUidAndToken();
                    } else {
                        mVideoLastProgress = progress;
                    }
                }
            }

            @Override
            public void onNetStatus(Bundle bundle) {

            }

        });
        mPlayer.startVodPlay(videoFile.getAbsolutePath());
        checkHasAdLink(0);
    }
}
