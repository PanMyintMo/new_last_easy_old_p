package com.gawilive.im.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.CommonAppContext;
import com.gawilive.common.Constants;
import com.gawilive.common.R;
import com.gawilive.common.utils.L;
import com.gawilive.common.utils.RouteUtil;
import com.gawilive.common.utils.SpUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.im.event.SystemMsgEvent;
import com.gawilive.im.utils.ImMessageUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;




/**
 * Created by cxf on 2018/10/30.
 */

public class JPushReceiver extends BroadcastReceiver {

    private static final String TAG = "极光推送";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "cn.jpush.android.intent.REGISTRATION":
                //L.e(TAG, "-------->User registration");
                break;
            case "cn.jpush.android.intent.MESSAGE_RECEIVED":
               // L.e(TAG, "-------->User receives SDK message");
                break;
            case "cn.jpush.android.intent.NOTIFICATION_RECEIVED":
               // L.e(TAG, "-------->User receives notification bar information");
                onReceiveNotification(context, intent);
                break;
            case "cn.jpush.android.intent.NOTIFICATION_OPENED":
              //  L.e(TAG, "-------->The user opens the notification bar information");
                onClickNotification(context, intent);
                break;
        }
    }

    /**
     * 收到通知
     */
    private void onReceiveNotification(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        String extras = "";
       // L.e(TAG, "------extras----->" + extras);
        if (TextUtils.isEmpty(extras)) {
            return;
        }
        JSONObject obj = JSON.parseObject(extras);
        if (obj == null || obj.containsKey("local")) {//收到的是本地通知
            return;
        }
        if (obj.getIntValue("type") == Constants.JPUSH_TYPE_MESSAGE) {//系统消息通知
            SpUtil.getInstance().setBooleanValue(SpUtil.HAS_SYSTEM_MSG, true);
            EventBus.getDefault().post(new SystemMsgEvent());
            ImMessageUtil.getInstance().playRing();
        }
        if (!CommonAppConfig.getInstance().isFrontGround()) {
          //  L.e(TAG, "---------->In background, showing notifications");
            String content = obj.getString("title");
            if (TextUtils.isEmpty(content)) {
                return;
            }
            showNotification(context, obj, content);
        } else {
          //  L.e(TAG, "---------->In foreground, don't show notifications");
        }
    }

    /**
     * 显示通知
     */
    private void showNotification(Context context, JSONObject extrasJson, String content) {

    }

    /**
     * 点击通知栏
     */
    private void onClickNotification(Context context, Intent intent) {
       // L.e(TAG, "------->Notification clicked");
        if (intent == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        String extras = "";
      //  L.e(TAG, "------extras----->" + extras);
        if (TextUtils.isEmpty(extras)) {
            return;
        }
        JSONObject obj = JSON.parseObject(extras);
        if (obj == null) {
            return;
        }
        if (!CommonAppConfig.getInstance().isLaunched()) {
            RouteUtil.forwardLauncher();
        } else {
            ActivityManager mAm = (ActivityManager) CommonAppContext.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
            //获得当前运行的task
            List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo rti : taskList) {
                //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
                if (CommonAppConfig.PACKAGE_NAME.equals(rti.topActivity.getPackageName())) {
                    break;
                }
            }
        }

    }


}
