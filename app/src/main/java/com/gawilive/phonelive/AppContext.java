package com.gawilive.phonelive;

import android.text.TextUtils;

import cn.tillusory.sdk.TiSDK;
import com.gawilive.phonelive.activity.LauncherActivity;
import com.google.firebase.FirebaseApp;
import com.hjq.toast.ToastInterceptor;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastBlackStyle;
import com.meihu.beautylibrary.MHSDK;
import com.mob.MobSDK;
import com.project.codeinstallsdk.CodeInstall;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.imsdk.v2.V2TIMLogListener;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.tencent.rtmp.TXLiveBase;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.CommonAppContext;
import com.gawilive.common.Constants;
import com.gawilive.common.http.HttpClient;
import com.gawilive.common.utils.DecryptUtil;
import com.gawilive.common.utils.L;
import com.gawilive.common.utils.SpUtil;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.ugc.TXUGCBase;

public class AppContext extends CommonAppContext {

    private boolean mBeautyInited;

    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(android.R.color.white);
            return new ClassicsHeader(context);
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupLanguage();
        initToast();

        String listener = "https://license.vod2.myqcloud.com/license/v2/1333822644_1/v_cube.license";
        String key = "b228d214767ed7e3853094253dd5766a";

        TXLiveBase.getInstance().setLicence(this, listener, key);
        TXUGCBase.getInstance().setLicence(this, listener, key);
        FirebaseApp.initializeApp(this);

    }

    private void setupLanguage() {
        String language = SpUtil.getInstance().getStringValue(SpUtil.LANGUAGE);
        if (language.contains(Constants.CHINESE_CODE)) {
            HttpClient.getInstance().setLanguage(Constants.CHINESE_CODE);
        } else if (language.contains(Constants.AR_CODE)) {
            HttpClient.getInstance().setLanguage(Constants.AR_CODE);
        } else if (language.contains(Constants.TH_CODE)) {
            HttpClient.getInstance().setLanguage(Constants.TH_CODE);
        } else if (language.contains(Constants.MY_CODE)) {
            HttpClient.getInstance().setLanguage(Constants.MY_CODE);
        } else {
            HttpClient.getInstance().setLanguage(Constants.ENGLISH_CODE);
        }
    }

    private void initToast() {
        ToastUtils.init(this, new ToastBlackStyle(this) {
            @Override
            public int getCornerRadius() {
                return 999;
            }
        });
        ToastUtils.setToastInterceptor(new ToastInterceptor());
    }

    public static void initSdk() {
        CommonAppContext context = CommonAppContext.getInstance();
        boolean isDebug = BuildConfig.DEBUG;
        L.setDeBug(isDebug);



        CrashReport.initCrashReport(context);
        CrashReport.setAppVersion(context, CommonAppConfig.getInstance().getVersion());
        //配置为null 注释了
        MobSDK.init(context, BuildConfig.MOB_SDK_APP_ID, BuildConfig.MOB_SDK_APP_KEY);
        MobSDK.submitPolicyGrantResult(true);

        V2TIMSDKConfig config = new V2TIMSDKConfig();
        config.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_INFO);
        config.setLogListener(new V2TIMLogListener() {
            @Override
            public void onLog(int logLevel, String logContent) {
                // Handle log content here
            }
        });

        // 初始化美颜
        TiSDK.initSDK("5448618ef60b428d8f092791e2fab1a5", context);

    }

    public void initBeautySdk(String beautyAppId, String beautyKey) {
        if (!TextUtils.isEmpty(beautyAppId) && !TextUtils.isEmpty(beautyKey) && !mBeautyInited) {
            mBeautyInited = true;

            // Decrypt the app ID and key if needed
            if (CommonAppConfig.isYunBaoApp()) {
                beautyAppId = DecryptUtil.decrypt(beautyAppId);
                beautyKey = DecryptUtil.decrypt(beautyKey);
            }

            // Initialize the beauty SDK with the provided app ID and key
            MHSDK.init(this, beautyAppId, beautyKey);
            CommonAppConfig.getInstance().setMhBeautyEnable(true);
        } else {
            CommonAppConfig.getInstance().setMhBeautyEnable(false);
        }
    }

}
