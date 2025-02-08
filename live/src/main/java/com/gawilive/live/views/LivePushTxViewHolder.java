package com.gawilive.live.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.bean.TiRotation;
import cn.tillusory.tiui.TiPanelLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.tencent.liteav.audio.TXAudioEffectManager;
import com.gawilive.beauty.bean.MeiYanValueBean;
import com.gawilive.beauty.interfaces.IBeautyEffectListener;
import com.gawilive.beauty.utils.MhDataManager;
import com.gawilive.beauty.utils.SimpleDataManager;
import com.tencent.liteav.beauty.TXBeautyManager;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.http.CommonHttpConsts;
import com.gawilive.common.http.CommonHttpUtil;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.BitmapUtil;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.common.utils.L;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.live.LiveConfig;
import com.gawilive.live.R;
import com.gawilive.live.activity.LiveActivity;
import com.gawilive.live.bean.LiveConfigBean;
import com.gawilive.live.http.LiveHttpConsts;
import com.gawilive.live.http.LiveHttpUtil;

import static com.tencent.rtmp.TXLiveConstants.CUSTOM_MODE_VIDEO_CAPTURE;

/**
 * Created by cxf on 2018/10/7.
 * 腾讯云直播推流
 */

public class LivePushTxViewHolder extends AbsLivePushViewHolder implements ITXLivePushListener {

    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;
    private Handler mMixHandler;
    private boolean mMirror;
    private LiveConfigBean mLiveConfigBean;



    public LivePushTxViewHolder(Context context, ViewGroup parentView, LiveConfigBean liveConfigBean) {
        super(context, parentView, liveConfigBean);
    }

    @Override
    protected void processArguments(Object... args) {
        if (args.length > 0) {
            mLiveConfigBean = (LiveConfigBean) args[0];
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_push_tx;
    }

    @Override
    public void init() {
        super.init();
        if (mLiveConfigBean == null) {
            mLiveConfigBean = LiveConfig.getDefaultTxConfig();
        }



        mLivePusher = new TXLivePusher(mContext);
        mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.setVideoFPS(30);//视频帧率 15
        mLivePushConfig.setVideoEncodeGop(mLiveConfigBean.getTargetGop());//GOP大小  1
        mLivePushConfig.setVideoBitrate(mLiveConfigBean.getVideoKBitrate());//1200
        mLivePushConfig.setVideoResolution(mLiveConfigBean.getTargetResolution());//分辨率546_960_1
        mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_HARDWARE);//硬件加速
        Bitmap bitmap = decodeResource(mContext.getResources(), R.mipmap.bg_live_tx_pause);
        mLivePushConfig.setPauseImg(bitmap);
        mLivePushConfig.setCustomModeType(TXLiveConstants.CUSTOM_MODE_VIDEO_PREPROCESS);
        mLivePushConfig.setTouchFocus(false);//自动对焦
        mLivePushConfig.enableAEC(true);//开启回声消除：连麦时必须开启，非连麦时不要开启
//        mLivePushConfig.setAudioSampleRate(mLiveConfigBean.getAudioKBitrate());//48000
//        mLivePushConfig.setAudioChannels(1);//声道数量
        mLivePusher.setConfig(mLivePushConfig);
        mMirror = true;
        mLivePusher.setMirror(mMirror);
        mLivePusher.setPushListener(this);
        mPreView = findViewById(R.id.camera_preview);
        mLivePusher.startCameraPreview((TXCloudVideoView) mPreView);
        getBeautyValue();

    }

    /**
     * 获取美颜参数
     */
    private void getBeautyValue() {

        if (mLivePusher != null) {
            // MhDataManager.getInstance().init().setMeiYanChangedListener(getMeiYanChangedListener());
//                            MhDataManager.getInstance().createBeautyManager();



            mLivePusher.setVideoProcessListener(new TXLivePusher.VideoCustomProcessListener() {
                @Override
                public int onTextureCustomProcess(int texture, int width, int height) {
                    int textureId = TiSDKManager.getInstance().renderTexture2D(texture,
                            width, height, TiRotation.CLOCKWISE_ROTATION_0, true);
                    return textureId;
                }

                @Override
                public void onDetectFacePoints(float[] floats) {
                }

                @Override
                public void onTextureDestoryed() {
                    TiSDKManager.getInstance().destroy();
                }
            });
//                            MeiYanValueBean meiYanValueBean = JSON.parseObject(info[0], MeiYanValueBean.class);
//                            MhDataManager.getInstance()
//                                    .setMeiYanValue(meiYanValueBean)
//                                    .useMeiYan().restoreBeautyValue();

        } else {
        }
        CommonHttpUtil.getBeautyValue(new HttpCallback() {

            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);

//                    if (CommonAppConfig.getInstance().isMhBeautyEnable()) {
//
//                    } else {
//                        SimpleDataManager.getInstance().create().setMeiYanChangedListener(getMeiYanChangedListener());
//                        int meiBai = obj.getIntValue("skin_whiting");
//                        int moPi = obj.getIntValue("skin_smooth");
//                        int hongRun = obj.getIntValue("skin_tenderness");
//                        SimpleDataManager.getInstance().setData(meiBai, moPi, hongRun);
//                    }


                }
            }
        });
    }

    @Override
    public IBeautyEffectListener getMeiYanChangedListener() {
        return new IBeautyEffectListener() {
            @Override
            public void onMeiYanChanged(int meiBai, boolean meiBaiChanged, int moPi, boolean moPiChanged, int hongRun, boolean hongRunChanged) {
                if (meiBaiChanged || moPiChanged || hongRunChanged) {
                    if (mLivePusher != null) {
                        mLivePusher.setBeautyFilter(TXLiveConstants.BEAUTY_STYLE_SMOOTH, moPi, meiBai, hongRun);
                    }
                }
            }

            @Override
            public void onFilterChanged(int filterName) {
                if (!CommonAppConfig.getInstance().isMhBeautyEnable()) {
                    if (mLivePusher != null) {
                        TXBeautyManager manager = mLivePusher.getBeautyManager();
                        if (manager != null) {
                            if (filterName == 0) {
                                manager.setFilter(null);
                            } else {
                                Bitmap bitmap = BitmapUtil.getInstance().decodeBitmap(filterName);
                                manager.setFilter(bitmap);
                            }
                        }
                    }
                }
            }

            @Override
            public boolean isUseMhFilter() {
                return true;
            }

            @Override
            public boolean isTieZhiEnable() {
                return !mIsPlayGiftSticker;
            }
        };
    }


    @Override
    public void changeToLeft() {
        if (mPreView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPreView.getLayoutParams();
            params.width = mPreView.getWidth() / 2;
            params.height = DpUtil.dp2px(250);
            params.topMargin = DpUtil.dp2px(130);
            mPreView.setLayoutParams(params);
        }
    }

    @Override
    public void changeToBig() {
        if (mPreView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPreView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.topMargin = 0;
            mPreView.setLayoutParams(params);
        }
    }

    /**
     * 切换镜像
     */
    @Override
    public void togglePushMirror() {
        if (mLivePusher != null) {
            mMirror = !mMirror;
            mLivePusher.setMirror(mMirror);
            if (mMirror) {
                ToastUtil.show(R.string.live_mirror_1);
            } else {
                ToastUtil.show(R.string.live_mirror_0);
            }
        }
    }


    /**
     * 切换镜头
     */
    @Override
    public void toggleCamera() {
        if (mLivePusher != null) {
            if (mFlashOpen) {
                toggleFlash();
            }
            mLivePusher.switchCamera();
            mCameraFront = !mCameraFront;
            if (!mCameraFront) {
                mLivePusher.setMirror(false);
            } else {
                mLivePusher.setMirror(mMirror);
            }
        }
    }

    /**
     * 打开关闭闪光灯
     */
    @Override
    public void toggleFlash() {
        if (mCameraFront) {
            ToastUtil.show(R.string.live_open_flash);
            return;
        }
        if (mLivePusher != null) {
            boolean open = !mFlashOpen;
            if (mLivePusher.turnOnFlashLight(open)) {
                mFlashOpen = open;
            }
        }
    }

    /**
     * 开始推流
     *
     * @param pushUrl 推流地址
     */
    @Override
    public void startPush(String pushUrl) {
        if (mLivePusher != null) {
            mLivePusher.startPusher(pushUrl);
        }
        startCountDown();
    }


    @Override
    public void onPause() {
        mPaused = true;
        if (mStartPush && mLivePusher != null) {
            mLivePusher.pauseBGM();
            mLivePusher.pausePusher();
        }
    }

    @Override
    public void onResume() {
        if (mPaused && mStartPush && mLivePusher != null) {
            mLivePusher.resumePusher();
            mLivePusher.resumeBGM();
        }
        mPaused = false;
    }

    @Override
    public void startBgm(String path) {
        if (mLivePusher != null) {
            TXAudioEffectManager audioEffectManager = mLivePusher.getAudioEffectManager();
            if (audioEffectManager != null) {
                audioEffectManager.setAllMusicVolume(40);//设置所有背景音乐的本地音量和远端音量的大小,如果将 volume 设置成 100 之后感觉音量还是太小，可以将 volume 最大设置成 150，但超过 100 的 volume 会有爆音的风险，请谨慎操作。
                audioEffectManager.setVoiceEarMonitorVolume(40);//通过该接口您可以设置耳返特效中声音的音量大小。取值范围为0 - 100，默认值：100。如果将 volume 设置成 100 之后感觉音量还是太小，可以将 volume 最大设置成 150，但超过 100 的 volume 会有爆音的风险，请谨慎操作。
                TXAudioEffectManager.AudioMusicParam audioMusicParam = new TXAudioEffectManager.AudioMusicParam(1, path);
                audioMusicParam.publish = true;
                audioMusicParam.endTimeMS = 0;
                audioEffectManager.startPlayMusic(audioMusicParam);
            }
        }
    }

    @Override
    public void pauseBgm() {
        if (mLivePusher != null) {
            TXAudioEffectManager audioEffectManager = mLivePusher.getAudioEffectManager();
            if (audioEffectManager != null) {
                audioEffectManager.pausePlayMusic(1);
            }
        }
    }

    @Override
    public void resumeBgm() {
        if (mLivePusher != null) {
            TXAudioEffectManager audioEffectManager = mLivePusher.getAudioEffectManager();
            if (audioEffectManager != null) {
                audioEffectManager.resumePlayMusic(1);
            }
        }
    }

    @Override
    public void stopBgm() {
        if (mLivePusher != null) {
            TXAudioEffectManager audioEffectManager = mLivePusher.getAudioEffectManager();
            if (audioEffectManager != null) {
                audioEffectManager.stopPlayMusic(1);
            }
        }
    }

    @Override
    protected void onCameraRestart() {
        if (mLivePusher != null && mPreView != null) {
            mLivePusher.startCameraPreview((TXCloudVideoView) mPreView);
        }
    }

    @Override
    public void release() {
        super.release();
        CommonHttpUtil.cancel(CommonHttpConsts.GET_BEAUTY_VALUE);
        if (CommonAppConfig.getInstance().isMhBeautyEnable()) {
            MhDataManager.getInstance().release();
        } else {
            SimpleDataManager.getInstance().release();
        }
        if (mMixHandler != null) {
            mMixHandler.removeCallbacksAndMessages(null);
        }
        mMixHandler = null;
        LiveHttpUtil.cancel(LiveHttpConsts.LINK_MIC_TX_MIX_STREAM);
        if (mLivePusher != null) {
            mLivePusher.stopBGM();
            mLivePusher.stopPusher();
            mLivePusher.stopScreenCapture();
            mLivePusher.stopCameraPreview(false);
            mLivePusher.setVideoProcessListener(null);
            mLivePusher.setBGMNofify(null);
            mLivePusher.setPushListener(null);
        }
        mLivePusher = null;
        if (mLivePushConfig != null) {
            mLivePushConfig.setPauseImg(null);
        }
        mLivePushConfig = null;
    }

    @Override
    public void onPushEvent(int e, Bundle bundle) {
        // L.e(TAG, "---------->" + e);
        if (e == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {
            ToastUtil.show(R.string.live_push_failed_1);

        } else if (e == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) {
            ToastUtil.show(R.string.live_push_failed_2);

        } else if (e == TXLiveConstants.PUSH_ERR_NET_DISCONNECT || e == TXLiveConstants.PUSH_ERR_INVALID_ADDRESS) {
            //L.e(TAG, "网络断开，推流失败------>");

        } else if (e == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            //  L.e(TAG, "不支持硬件加速------>");
            if (mLivePushConfig != null && mLivePusher != null) {
                mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
                mLivePusher.setConfig(mLivePushConfig);
            }
        } else if (e == TXLiveConstants.PUSH_EVT_FIRST_FRAME_AVAILABLE) {//预览成功
            //  L.e(TAG, "mStearm--->初始化完毕");
            if (mLivePushListener != null) {
                mLivePushListener.onPreviewStart();
            }
        } else if (e == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {//推流成功
            //   L.e(TAG, "mStearm--->推流成功");
            if (!mStartPush) {
                mStartPush = true;
                if (mLivePushListener != null) {
                    mLivePushListener.onPushStart();
                }
            }
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    /**
     * 腾讯sdk连麦时候主播混流
     *
     * @param linkMicType 混流类型 1主播与主播连麦  0 用户与主播连麦
     * @param toStream    对方的stream
     */
    public void onLinkMicTxMixStreamEvent(int linkMicType, String toStream) {
        String txAppId = getTxAppId();
        String selfAnchorStream = getLiveStream();
        if (TextUtils.isEmpty(txAppId) || TextUtils.isEmpty(selfAnchorStream)) {
            return;
        }
        String mixParams = null;
        if (linkMicType == Constants.LINK_MIC_TYPE_NORMAL) {
            mixParams = createMixParams(txAppId, selfAnchorStream, toStream);
        } else if (linkMicType == Constants.LINK_MIC_TYPE_ANCHOR) {
            mixParams = createMixParams2(txAppId, selfAnchorStream, toStream);
        }
        if (TextUtils.isEmpty(mixParams)) {
            return;
        }
        final String finalMixParams = mixParams;
        if (mMixHandler != null) {
            mMixHandler.removeCallbacksAndMessages(null);
        }
        LiveHttpUtil.linkMicTxMixStream(finalMixParams, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
//                L.e("linkMicTxMixStream---1---code---> " + code + " ---msg---> " + msg);
                if (true) {//原来这里判断是 code != 0，但还是有混流失败的情况

                    //第2次 5秒后重新请求混流接口
                    if (mMixHandler == null) {
                        mMixHandler = new Handler();
                    }
                    mMixHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            LiveHttpUtil.linkMicTxMixStream(finalMixParams, new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
//                                    L.e("linkMicTxMixStream---2---code---> " + code + " ---msg---> " + msg);
                                    if (true) {//原来这里判断是 code != 0，但还是有混流失败的情况

                                        //第3次 5秒后重新请求混流接口
                                        if (mMixHandler == null) {
                                            mMixHandler = new Handler();
                                        }
                                        mMixHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                LiveHttpUtil.linkMicTxMixStream(finalMixParams, new HttpCallback() {
                                                    @Override
                                                    public void onSuccess(int code, String msg, String[] info) {
//                                                        L.e("linkMicTxMixStream---3---code---> " + code + " ---msg---> " + msg);
                                                    }
                                                });
                                            }
                                        }, 5000);
                                    }
                                }
                            });


                        }
                    }, 5000);
                }
            }
        });
    }


    private String createMixParams(String txAppId, String selfAnchorStream, String toStream) {
        JSONObject para = new JSONObject();
        para.put("MixStreamSessionId", selfAnchorStream);
        JSONArray array = new JSONArray();
        JSONObject mainAnchor = new JSONObject();//大主播
        mainAnchor.put("InputStreamName", selfAnchorStream);
        JSONObject mainLayoutParams = new JSONObject();
        mainLayoutParams.put("ImageLayer", 1);
        mainLayoutParams.put("InputType", 0);
        mainLayoutParams.put("LocationX", 0);
        mainLayoutParams.put("LocationY", 0);
        mainAnchor.put("LayoutParams", mainLayoutParams);
        array.add(mainAnchor);

        if (!TextUtils.isEmpty(toStream)) {
            JSONObject smallAnchor = new JSONObject();//小主播
            smallAnchor.put("InputStreamName", toStream);
            JSONObject smallLayoutParams = new JSONObject();
            smallLayoutParams.put("ImageLayer", 2);
            smallLayoutParams.put("ImageWidth", 0.25);
            smallLayoutParams.put("ImageHeight", 0.21);
            smallLayoutParams.put("LocationX", 0.75);
            smallLayoutParams.put("LocationY", 0.6);
            smallAnchor.put("LayoutParams", smallLayoutParams);
            array.add(smallAnchor);
        }

        JSONObject outPara = new JSONObject();
        outPara.put("OutputStreamName", selfAnchorStream);

        para.put("InputStreamList", array);

        para.put("OutputParams", outPara);

        JSONObject controlParams = new JSONObject();
        controlParams.put("UseMixCropCenter", 1);
        para.put("ControlParams", controlParams);

        return para.toString();
    }

    private String createMixParams2(String txAppId, String selfAnchorStream, String toStream) {
        JSONObject para = new JSONObject();
        para.put("MixStreamSessionId", selfAnchorStream);
        JSONArray array = new JSONArray();
        if (!TextUtils.isEmpty(toStream)) {
            JSONObject bg = new JSONObject();//背景
            bg.put("InputStreamName", "canvas1");//背景的id,这个字符串随便写
            JSONObject bgLayoutParams = new JSONObject();
            bgLayoutParams.put("ImageLayer", 1);
            bgLayoutParams.put("InputType", 3);
//            bgLayoutParams.put("image_width", ScreenDimenUtil.getInstance().getScreenWdith());
//            bgLayoutParams.put("image_height", ScreenDimenUtil.getInstance().getScreenHeight());
//            bgLayoutParams.put("location_x", 0);
            bg.put("LayoutParams", bgLayoutParams);
            array.add(bg);

            JSONObject selfAnchor = new JSONObject();//自己主播
            selfAnchor.put("InputStreamName", selfAnchorStream);
            JSONObject selfLayoutParams = new JSONObject();
            selfLayoutParams.put("ImageLayer", 2);
            selfLayoutParams.put("ImageWidth", 0.5);
            selfLayoutParams.put("ImageHeight", 0.5);
            selfLayoutParams.put("LocationX", 0);
            selfLayoutParams.put("LocationY", 0.25);
            selfAnchor.put("LayoutParams", selfLayoutParams);
            array.add(selfAnchor);

            JSONObject toAnchor = new JSONObject();//对方主播
            toAnchor.put("InputStreamName", toStream);
            JSONObject toLayoutParams = new JSONObject();
            toLayoutParams.put("ImageLayer", 3);
            toLayoutParams.put("ImageWidth", 0.5);
            toLayoutParams.put("ImageHeight", 0.5);
            toLayoutParams.put("LocationX", 0.5);
            toLayoutParams.put("LocationY", 0.25);
            toAnchor.put("LayoutParams", toLayoutParams);
            array.add(toAnchor);
        } else {
            JSONObject mainAnchor = new JSONObject();//大主播
            mainAnchor.put("InputStreamName", selfAnchorStream);
            JSONObject mainLayoutParams = new JSONObject();
            mainLayoutParams.put("ImageLayer", 1);
            mainAnchor.put("LayoutParams", mainLayoutParams);
            array.add(mainAnchor);
        }
        para.put("InputStreamList", array);

        JSONObject outPara = new JSONObject();
        outPara.put("OutputStreamName", selfAnchorStream);
        para.put("OutputParams", outPara);

        return para.toString();
    }

    private String getLiveStream() {
        return ((LiveActivity) mContext).getStream();
    }

    private String getTxAppId() {
        return ((LiveActivity) mContext).getTxAppId();
    }


}
