package com.gawilive.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.interfaces.TUICallback;
import com.gawilive.common.bean.ConfigBean;
import com.gawilive.common.bean.LevelBean;
import com.gawilive.common.bean.UserBean;
import com.gawilive.common.http.CommonHttpUtil;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.utils.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2017/8/4 .
 */

public class CommonAppConfig {
    public static final String PACKAGE_NAME = "com.gawilive.phonelivex";
    //Http请求头 Header
    public static final Map<String, String> HEADER = new HashMap<>();
    //域名
    public static final String HOST = getHost();

    public static final String domain = "";

    public static final String HOST2 = "https://ezpay.best/api.php";

    public static final String PAY_H5_URL = "https://ezpay.best/ewm/index.html#/?";


    //外部sd卡
//    public static final String DCMI_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    public static final String DCMI_PATH = getOutPath();
    //内部存储 /data/data/<application package>/files目录
    public static final String INNER_PATH = CommonAppContext.getInstance().getFilesDir().getAbsolutePath();
    //文件夹名字
    private static final String DIR_NAME = "yunbao";
    //保存视频的时候，在sd卡存储短视频的路径DCIM下
    public static final String VIDEO_DOWNLOAD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + DIR_NAME + "/video/";
    public static final String VIDEO_PATH = StringUtil.contact(DCMI_PATH, "/video");
    public static final String VIDEO_PATH_RECORD = StringUtil.contact(VIDEO_PATH, "/record");
    public static final String VIDEO_RECORD_TEMP_PATH = StringUtil.contact(VIDEO_PATH, "/recordParts");
    //下载音乐的时候保存的路径
    public static final String MUSIC_PATH = DCMI_PATH + "/" + DIR_NAME + "/music/";
    //拍照时图片保存路径
    public static final String CAMERA_IMAGE_PATH = VIDEO_DOWNLOAD_PATH + "/" + DIR_NAME + "/camera/";
    //log保存路径
    public static final String LOG_PATH = DCMI_PATH + "/" + DIR_NAME + "/log/";

    public static final String GIF_PATH = INNER_PATH + "/gif/";
    public static final String WATER_MARK_PATH = INNER_PATH + "/water/";

    //QQ登录是否与PC端互通
    public static final boolean QQ_LOGIN_WITH_PC = false;
    //是否使用游戏
    public static final boolean GAME_ENABLE = true;
    //是否上下滑动切换直播间
    public static final boolean LIVE_ROOM_SCROLL = true;
    //直播sdk类型是否由后台控制的
    public static final boolean LIVE_SDK_CHANGED = true;
    //使用指定的直播sdk类型
    public static final int LIVE_SDK_USED = Constants.LIVE_SDK_TX;

    // 腾讯im
    public static final int IM_APP_ID = 1600045894;

    private static String getOutPath() {
        String outPath = null;
        try {
            File externalFilesDir = CommonAppContext.getInstance().getExternalFilesDir(DIR_NAME);
            if (externalFilesDir != null) {
                if (!externalFilesDir.exists()) {
                    externalFilesDir.mkdirs();
                }
                outPath = externalFilesDir.getAbsolutePath();
            }
        } catch (Exception e) {
            outPath = null;
        }
        if (TextUtils.isEmpty(outPath)) {
            outPath = CommonAppContext.getInstance().getFilesDir().getAbsolutePath();
        }
        return outPath;
    }

    private static CommonAppConfig sInstance;

    private CommonAppConfig() {

    }

    public static CommonAppConfig getInstance() {
        if (sInstance == null) {
            synchronized (CommonAppConfig.class) {
                if (sInstance == null) {
                    sInstance = new CommonAppConfig();
                }
            }
        }
        return sInstance;
    }

    private String mUid;
    private String mToken;
    private ConfigBean mConfig;
    private double mLng;
    private double mLat;
    private String mProvince;//省
    private String mCity;//市
    private String mDistrict;//区
    private UserBean mUserBean;
    private UserBean mEmptyUserBean;//未登录游客
    private String mVersion;
    private boolean mLoginIM;//IM是否登录了
    private boolean mLaunched;//App是否启动了
    private String mJPushAppKey;//极光推送的AppKey
    private SparseArray<LevelBean> mLevelMap;
    private SparseArray<LevelBean> mAnchorLevelMap;
    private String mGiftListJson;
    private String mGiftDaoListJson;
    private String mTxMapAppKey;//腾讯定位，地图的AppKey
    private String mTxMapAppSecret;//腾讯地图的AppSecret
    private boolean mFrontGround;
    private int mAppIconRes;
    private String mAppName;
    private Boolean mMhBeautyEnable;//是否使用美狐 true使用美狐 false 使用基础美颜
    private String mDeviceId;
    private Boolean mTeenagerType;//是否是青少年模式

    public String getUid() {
        if (TextUtils.isEmpty(mUid)) {
            String[] uidAndToken = SpUtil.getInstance()
                    .getMultiStringValue(SpUtil.UID, SpUtil.TOKEN);
            if (uidAndToken != null) {
                if (!TextUtils.isEmpty(uidAndToken[0]) && !TextUtils.isEmpty(uidAndToken[1])) {
                    mUid = uidAndToken[0];
                    mToken = uidAndToken[1];
                } else {
                    mUid = Constants.NOT_LOGIN_UID;
                    mToken = Constants.NOT_LOGIN_TOKEN;
                }
            } else {
                mUid = Constants.NOT_LOGIN_UID;
                mToken = Constants.NOT_LOGIN_TOKEN;
            }
        }
        return mUid;
    }

    public String getToken() {
        return mToken;
    }

    public boolean isLogin() {
        return !Constants.NOT_LOGIN_UID.equals(getUid());
    }

    public String getCoinName() {
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            return configBean.getCoinName();
        }
        return Constants.DIAMONDS;
    }

    public String getVotesName() {
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            return configBean.getVotesName();
        }
        return Constants.VOTES;
    }


    public String getScoreName() {
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            return configBean.getScoreName();
        }
        return Constants.SCORE;
    }

    public ConfigBean getConfig() {
        if (mConfig == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                mConfig = JSON.parseObject(configString, ConfigBean.class);
            }
        }
        return mConfig;
    }


    public void getConfig(CommonCallback<ConfigBean> callback) {
        if (callback == null) {
            return;
        }
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            callback.callback(configBean);
        } else {
            CommonHttpUtil.getConfig(callback);
        }
    }

    public void setConfig(ConfigBean config) {
        mConfig = config;
    }


    /**
     * 经度
     */
    public double getLng() {
        if (mLng == 0) {
            String lng = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_LNG);
            if (!TextUtils.isEmpty(lng)) {
                try {
                    mLng = Double.parseDouble(lng);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mLng;
    }

    /**
     * 纬度
     */
    public double getLat() {
        if (mLat == 0) {
            String lat = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_LAT);
            if (!TextUtils.isEmpty(lat)) {
                try {
                    mLat = Double.parseDouble(lat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mLat;
    }

    /**
     * 省
     */
    public String getProvince() {
        if (TextUtils.isEmpty(mProvince)) {
            mProvince = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_PROVINCE);
        }
        return mProvince == null ? "" : mProvince;
    }

    /**
     * 市
     */
    public String getCity() {
        if (TextUtils.isEmpty(mCity)) {
            mCity = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_CITY);
        }
        return mCity == null ? "" : mCity;
    }

    /**
     * 区
     */
    public String getDistrict() {
        if (TextUtils.isEmpty(mDistrict)) {
            mDistrict = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_DISTRICT);
        }
        return mDistrict == null ? "" : mDistrict;
    }

    public void setUserBean(UserBean bean) {
        mUserBean = bean;
    }

    public UserBean getUserBean() {
        if (mUserBean == null) {
            String userBeanJson = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
            if (!TextUtils.isEmpty(userBeanJson)) {
                mUserBean = JSON.parseObject(userBeanJson, UserBean.class);
            }
        }
        if (mUserBean == null) {
            mUserBean = getEmptyUserBean();
        }
        return mUserBean;
    }

    /**
     * 设置美狐是否可用
     */
    public void setMhBeautyEnable(boolean mhBeautyEnable) {
        mMhBeautyEnable = mhBeautyEnable;
        SpUtil.getInstance().setBooleanValue(SpUtil.MH_BEAUTY_ENABLE, mhBeautyEnable);
    }

    /**
     * 美狐是否可用
     */
    public boolean isMhBeautyEnable() {
        if (mMhBeautyEnable == null) {
            mMhBeautyEnable = SpUtil.getInstance().getBooleanValue(SpUtil.MH_BEAUTY_ENABLE);
        }
        return mMhBeautyEnable;
    }


    public void setTeenagerType(boolean teenagerType) {
        mTeenagerType = teenagerType;
        SpUtil.getInstance().setBooleanValue(SpUtil.TEENAGER, teenagerType);
    }

    public boolean isTeenagerType() {
        if (mTeenagerType == null) {
            mTeenagerType = SpUtil.getInstance().getBooleanValue(SpUtil.TEENAGER);
        }
        return mTeenagerType;
    }


    /**
     * 设置登录信息
     */
    public void setLoginInfo(String uid, String token, boolean save) {
        L.e("Login successful", "uid------>" + uid);
        L.e("Login successful", "token------>" + token);
        mUid = uid;
        mToken = token;
        if (save) {
            SpUtil.getInstance().setBooleanValue(SpUtil.TEENAGER_SHOW, false);
        }
        Map<String, String> map = new HashMap<>();
        map.put(SpUtil.UID, uid);
        map.put(SpUtil.TOKEN, token);
        SpUtil.getInstance().setMultiStringValue(map);

    }

    public void setPinNumber(String uid, String token, boolean save) {
        mUid = uid;
        mToken = token;

        if (save) {
            SpUtil.getInstance().setBooleanValue(SpUtil.PIN, true);
        }
        Map<String, String> map = new HashMap<>();
        map.put(SpUtil.UID, uid);
        map.put(SpUtil.TOKEN, token);
        SpUtil.getInstance().setMultiStringValue(map);

    }

    public void clearPinNumber() {
        // Clear stored PIN data
        mUid = null;
        mToken = null;
        SpUtil.getInstance().removeValue(
                SpUtil.UID, SpUtil.TOKEN, SpUtil.PIN
        );
    }


    /**
     * 清除登录信息
     */
    public void clearLoginInfo() {
        mUid = null;
        mToken = null;
        mLoginIM = false;
        SpUtil.getInstance().removeValue(
                SpUtil.UID, SpUtil.TOKEN, SpUtil.USER_INFO,
                Constants.CASH_ACCOUNT_ID, Constants.CASH_ACCOUNT, Constants.CASH_ACCOUNT_TYPE,
                SpUtil.TEENAGER, SpUtil.TEENAGER_SHOW
        );
        CommonAppConfig.getInstance().setUserBean(getEmptyUserBean());
    }

    /**
     * 未登录，游客
     */
    private UserBean getEmptyUserBean() {
        if (mEmptyUserBean == null) {
            UserBean bean = new UserBean();
            bean.setId(Constants.NOT_LOGIN_UID);
            bean.setUserNiceName("User");
            bean.setLevel(1);
            bean.setLevelAnchor(1);
            String defaultAvatar = CommonAppConfig.HOST + "/default.jpg";
            bean.setAvatar(defaultAvatar);
            bean.setAvatarThumb(defaultAvatar);
            bean.setSignature(Constants.EMPTY_STRING);
            bean.setCoin(Constants.EMPTY_STRING);
            bean.setVotes(Constants.EMPTY_STRING);
            bean.setConsumption(Constants.EMPTY_STRING);
            bean.setVotestotal(Constants.EMPTY_STRING);
            bean.setProvince(Constants.EMPTY_STRING);
            bean.setCity(Constants.EMPTY_STRING);
            bean.setLocation(Constants.EMPTY_STRING);
            bean.setBirthday(Constants.EMPTY_STRING);
            bean.setVip(new UserBean.Vip());
            bean.setLiang(new UserBean.Liang());
            bean.setCar(new UserBean.Car());
            mEmptyUserBean = bean;
        }
        return mEmptyUserBean;
    }


    /**
     * 设置位置信息
     *
     * @param lng      经度
     * @param lat      纬度
     * @param province 省
     * @param city     市
     */
    public void setLocationInfo(double lng, double lat, String province, String city, String district) {
        mLng = lng;
        mLat = lat;
        mProvince = province;
        mCity = city;
        mDistrict = district;
        Map<String, String> map = new HashMap<>();
        map.put(SpUtil.LOCATION_LNG, String.valueOf(lng));
        map.put(SpUtil.LOCATION_LAT, String.valueOf(lat));
        map.put(SpUtil.LOCATION_PROVINCE, province);
        map.put(SpUtil.LOCATION_CITY, city);
        map.put(SpUtil.LOCATION_DISTRICT, district);
        SpUtil.getInstance().setMultiStringValue(map);
    }

    /**
     * 清除定位信息
     */
    public void clearLocationInfo() {
        mLng = 0;
        mLat = 0;
        mProvince = null;
        mCity = null;
        mDistrict = null;
        SpUtil.getInstance().removeValue(
                SpUtil.LOCATION_LNG,
                SpUtil.LOCATION_LAT,
                SpUtil.LOCATION_PROVINCE,
                SpUtil.LOCATION_CITY,
                SpUtil.LOCATION_DISTRICT);

    }


    public boolean isLoginIM() {
        return mLoginIM;
    }

    public void setLoginIM(boolean loginIM) {
        mLoginIM = loginIM;
    }

    /**
     * 获取版本号
     */
    public String getVersion() {
        if (TextUtils.isEmpty(mVersion)) {
            try {
                PackageManager manager = CommonAppContext.getInstance().getPackageManager();
                PackageInfo info = manager.getPackageInfo(PACKAGE_NAME, 0);
                mVersion = info.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mVersion;
    }

    public static boolean isYunBaoApp() {
        if (!TextUtils.isEmpty(PACKAGE_NAME)) {
            return PACKAGE_NAME.contains("com.gawilive.phoneliv");
        }
        return false;
    }

    /**
     * 获取App名称
     */
    public String getAppName() {
        if (TextUtils.isEmpty(mAppName)) {
            int res = CommonAppContext.getInstance().getResources().getIdentifier("app_name", "string", "com.gawilive.phonelive");
            mAppName = WordUtil.getString(res);
        }
        return mAppName;
    }


    /**
     * 获取App图标的资源id
     */
    public int getAppIconRes() {
        if (mAppIconRes == 0) {
            mAppIconRes = CommonAppContext.getInstance().getResources().getIdentifier("icon_app", "mipmap", "com.gawilive.phonelive");
        }
        return mAppIconRes;
    }

    /**
     * 获取MetaData中的极光AppKey
     */
    public String getJPushAppKey() {
        if (mJPushAppKey == null) {
            mJPushAppKey = getMetaDataString("JPUSH_APPKEY");
        }
        return mJPushAppKey;
    }


    /**
     * 获取MetaData中的腾讯定位，地图的AppKey
     *
     * @return
     */
    public String getTxMapAppKey() {
        if (mTxMapAppKey == null) {
            mTxMapAppKey = getMetaDataString("TencentMapSDK");
        }
        return mTxMapAppKey;
    }


    /**
     * 获取MetaData中的腾讯定位，地图的AppSecret
     *
     * @return
     */
    public String getTxMapAppSecret() {
        if (mTxMapAppSecret == null) {
            mTxMapAppSecret = getMetaDataString("TencentMapAppSecret");
        }
        return mTxMapAppSecret;
    }


    public static String getMetaDataString(String key) {
        String res = null;
        try {
            ApplicationInfo appInfo = CommonAppContext.getInstance().getPackageManager().getApplicationInfo(PACKAGE_NAME, PackageManager.GET_META_DATA);
            res = appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static boolean getMetaDataBoolean(String key) {
        boolean res = false;
        try {
            ApplicationInfo appInfo = CommonAppContext.getInstance().getPackageManager().getApplicationInfo(PACKAGE_NAME, PackageManager.GET_META_DATA);
            res = appInfo.metaData.getBoolean(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static int getMetaDataInt(String key) {
        int res = 0;
        try {
            ApplicationInfo appInfo = CommonAppContext.getInstance().getPackageManager().getApplicationInfo(PACKAGE_NAME, PackageManager.GET_META_DATA);
            res = appInfo.metaData.getInt(key, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static String getHost() {
        String host = getMetaDataString("SERVER_HOST");
        HEADER.put("referer", host);
        return host;
    }


    /**
     * 保存用户等级信息
     */
    public void setLevel(String levelJson) {
        if (TextUtils.isEmpty(levelJson)) {
            return;
        }
        List<LevelBean> list = JSON.parseArray(levelJson, LevelBean.class);
        if (list == null || list.size() == 0) {
            return;
        }
        if (mLevelMap == null) {
            mLevelMap = new SparseArray<>();
        }
        mLevelMap.clear();
        for (LevelBean bean : list) {
            mLevelMap.put(bean.getLevel(), bean);
        }
    }

    /**
     * 保存主播等级信息
     */
    public void setAnchorLevel(String anchorLevelJson) {
        if (TextUtils.isEmpty(anchorLevelJson)) {
            return;
        }
        List<LevelBean> list = JSON.parseArray(anchorLevelJson, LevelBean.class);
        if (list == null || list.size() == 0) {
            return;
        }
        if (mAnchorLevelMap == null) {
            mAnchorLevelMap = new SparseArray<>();
        }
        mAnchorLevelMap.clear();
        for (LevelBean bean : list) {
            mAnchorLevelMap.put(bean.getLevel(), bean);
        }
    }

    /**
     * 获取用户等级
     */
    public LevelBean getLevel(int level) {
        if (mLevelMap == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                JSONObject obj = JSON.parseObject(configString);
                setLevel(obj.getString("level"));
            }
        }
        if (mLevelMap == null || mLevelMap.size() == 0) {
            return null;
        }
        return mLevelMap.get(level);
    }

    /**
     * 获取主播等级
     */
    public LevelBean getAnchorLevel(int level) {
        if (mAnchorLevelMap == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                JSONObject obj = JSON.parseObject(configString);
                setAnchorLevel(obj.getString("levelanchor"));
            }
        }
        if (mAnchorLevelMap == null || mAnchorLevelMap.size() == 0) {
            return null;
        }
        return mAnchorLevelMap.get(level);
    }

    public String getGiftListJson() {
        return mGiftListJson;
    }

    public void setGiftListJson(String getGiftListJson) {
        mGiftListJson = getGiftListJson;
    }


    public String getGiftDaoListJson() {
        return mGiftDaoListJson;
    }

    public void setGiftDaoListJson(String getGiftDaoListJson) {
        mGiftDaoListJson = getGiftDaoListJson;
    }


    /**
     * 判断某APP是否安装
     */
    public static boolean isAppExist(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            PackageManager manager = CommonAppContext.getInstance().getPackageManager();
            List<PackageInfo> list = manager.getInstalledPackages(0);
            for (PackageInfo info : list) {
                if (packageName.equalsIgnoreCase(info.packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isLaunched() {
        return mLaunched;
    }

    public void setLaunched(boolean launched) {
        mLaunched = launched;
    }

    //app是否在前台
    public boolean isFrontGround() {
        return mFrontGround;
    }

    //app是否在前台
    public void setFrontGround(boolean frontGround) {
        mFrontGround = frontGround;
    }

    public String getDeviceId() {
        if (TextUtils.isEmpty(mDeviceId)) {
            String deviceId = SpUtil.getInstance().getStringValue(SpUtil.DEVICE_ID);
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = DeviceUtils.getDeviceId();
                SpUtil.getInstance().setStringValue(SpUtil.DEVICE_ID, deviceId);
            }
            mDeviceId = deviceId;
        }
        L.e("getDeviceId---mDeviceId-----> " + mDeviceId);
        return mDeviceId;
    }

    public static void loginIm(Context context, String userID) {
        final String userSig = GenerateTestUserSig.genTestUserSig(userID);
        TUILogin.login(context, CommonAppConfig.IM_APP_ID, userID, userSig, new TUICallback() {
            @Override
            public void onSuccess() {
                Log.d("登录", "登录成功");
            }

            @Override
            public void onError(final int code, final String desc) {
                Log.d("登录", code + "----" + desc);
            }
        });

    }

    public static void logoutIm() {
        TUILogin.logout(new TUICallback() {
            @Override
            public void onSuccess() {
                Log.d("退出", "退出成功");
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.d("退出", errorCode + "----" + errorMessage);
            }
        });
    }
}