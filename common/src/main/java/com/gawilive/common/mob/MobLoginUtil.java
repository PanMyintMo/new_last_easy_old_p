package com.gawilive.common.mob;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.gawilive.common.R;
import com.gawilive.common.utils.ToastUtil;

import java.util.HashMap;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;

/**
 * Created by cxf on 2017/8/29.
 * mob登录
 */

public class MobLoginUtil {

    private static final int CODE_SUCCESS = 200;//成功
    private static final int CODE_ERROR = 300;//失败
    private static final int CODE_CANCEL = 400;//取消

    private final PlatformActionListener mPlatformActionListener;
    private Handler mHandler;
    private MobCallback mMobCallback;


    public MobLoginUtil() {
        mPlatformActionListener = new PlatformActionListener() {

            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (mHandler != null) {
                    Message msg = Message.obtain();
                    msg.what = CODE_SUCCESS;
                    msg.obj = platform;
                    mHandler.sendMessage(msg);
                }
                String data = platform.getDb().exportData();
               // Log.d("Sunday", "onComplete data:" + data);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(CODE_ERROR);
                }
               // Log.d("Sunday", Log.getStackTraceString(throwable));
            }

            @Override
            public void onCancel(Platform platform, int i) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(CODE_CANCEL);
                }
            }
        };
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (mMobCallback == null) {
                    return;
                }
                switch (msg.what) {
                    case CODE_SUCCESS:
                        Platform platform = (Platform) msg.obj;
                        if (platform == null) {
                            return;
                        }
                        PlatformDb platDB = platform.getDb();
                        if (platDB == null) {
                            return;
                        }
                        final LoginData data = new LoginData();
                        data.setNickName(platDB.getUserName());
                        data.setAvatar(platDB.getUserIcon());
                        String platformName = platDB.getPlatformNname();
//                        if (platformName.equals(Wechat.NAME)) {
//                            data.setOpenID(platDB.get("unionid"));
//                            data.setType(Constants.MOB_WX);
//                            data.setAccessToken(platDB.getToken());
//                        } else if (platformName.equals(QQ.NAME)) {
//                            data.setType(Constants.MOB_QQ);
//                            data.setAccessToken(platDB.getToken());
//                            if (CommonAppConfig.QQ_LOGIN_WITH_PC) {
//                                CommonHttpUtil.getQQLoginUnionID(platDB.getToken(), new CommonCallback<String>() {
//                                    @Override
//                                    public void callback(String unionID) {
//                                        data.setOpenID(unionID);
//                                        mMobCallback.onSuccess(data);
//                                        ToastUtil.show(R.string.login_auth_success);
//                                        mMobCallback.onFinish();
//                                    }
//                                });
//                                return;
//                            } else {
//                                data.setOpenID(platDB.getUserId());
//                            }
//                        }else {
//                            data.setOpenID(platDB.getUserId());
//                            if (platformName.equals(Facebook.NAME)) {
//                                data.setType(Constants.MOB_FACEBOOK);
//                            } else if (platformName.equals(Twitter.NAME)) {
//                                data.setType(Constants.MOB_TWITTER);
//                            } else if (platformName.equals(GooglePlus.NAME)) {
//                                data.setType(Constants.MOB_GOOGLE);
//                                data.setAccessToken(platDB.getToken());
//                            }
//                        }
                        mMobCallback.onSuccess(data);
                        ToastUtil.show(R.string.login_auth_success);
                        break;
                    case CODE_ERROR:
                        mMobCallback.onError();
                        ToastUtil.show(R.string.login_auth_failure);
                        break;
                    case CODE_CANCEL:
                        mMobCallback.onCancel();
                        ToastUtil.show(R.string.login_auth_cancle);
                        break;
                }
                mMobCallback.onFinish();
                mMobCallback = null;
            }
        };
    }

    public void execute(String platType, MobCallback callback) {
        if (TextUtils.isEmpty(platType) || callback == null) {
            return;
        }
        String platName = MobConst.MAP.get(platType);
        if (TextUtils.isEmpty(platName)) {
            return;
        }
        mMobCallback = callback;
        try {
            Platform platform = ShareSDK.getPlatform(platName);
            platform.setPlatformActionListener(mPlatformActionListener);
            platform.SSOSetting(false);
            platform.removeAccount(true);
            platform.showUser(null);
        } catch (Exception e) {
          //  e.printStackTrace();
            callback.onError();
        }
    }


    public void release() {
        mMobCallback = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }
}
