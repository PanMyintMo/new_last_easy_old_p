package com.gawilive.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.activity.WebViewActivity;
import com.gawilive.common.bean.ConfigBean;
import com.gawilive.common.event.LoginChangeEvent;
import com.gawilive.common.http.CommonHttpConsts;
import com.gawilive.common.http.CommonHttpUtil;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.utils.*;
import com.gawilive.im.utils.ImMessageUtil;
import com.gawilive.im.utils.ImPushUtil;
import com.gawilive.main.R;
import com.gawilive.main.adapter.SettingAdapter;
import com.gawilive.main.bean.SettingBean;
import com.gawilive.main.http.MainHttpConsts;
import com.gawilive.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/30.
 */

public class SettingActivity extends AbsActivity implements SettingAdapter.ActionListener {

    private RecyclerView mRecyclerView;
    private Handler mHandler;
    private SettingAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.setting));
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        MainHttpUtil.getSettingList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<SettingBean> list0 = JSON.parseArray(Arrays.toString(info), SettingBean.class);
                List<SettingBean> list = new ArrayList<>();
                SettingBean bean0 = new SettingBean();
                bean0.setId(-1);
                bean0.setName(WordUtil.getString(R.string.setting_brightness));
                bean0.setChecked(SpUtil.getInstance().getBrightness() == 0.05f);
                list.add(bean0);
                SettingBean bean1 = new SettingBean();
                bean1.setId(-2);
                bean1.setName(WordUtil.getString(R.string.setting_msg_ring));
                bean1.setChecked(SpUtil.getInstance().isImMsgRingOpen());
                list.add(bean1);
               list.addAll(list0);
                SettingBean bean = new SettingBean();
                bean.setName(WordUtil.getString(R.string.setting_exit));
                bean.setLast(true);
                list.add(bean);
                mAdapter = new SettingAdapter(mContext, list, VersionUtil.getVersion(), getCacheSize());
                mAdapter.setActionListener(SettingActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }


    @Override
    public void onItemClick(SettingBean bean, int position) {
        String href = bean.getHref();
        if (TextUtils.isEmpty(href)) {
            if (bean.isLast()) {
                //Log out
                new DialogUitl.Builder(mContext)
                        .setContent(WordUtil.getString(R.string.logout_confirm))
                        .setConfrimString(WordUtil.getString(R.string.logout_confirm_2))
                        .setCancelable(true)
                        .setIsHideTitle(true)
                        .setBackgroundDimEnabled(true)
                        .setClickCallback(new DialogUitl.SimpleCallback() {
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                logout();
                            }
                        })
                        .build()
                        .show();

            } else if (bean.getId() == Constants.SETTING_MODIFY_PWD) {//Change password
                forwardModifyPwd();
            } else if (bean.getId() == Constants.SETTING_UPDATE_ID) {//Check for updates
                checkVersion();
            } else if (bean.getId() == Constants.SETTING_CLEAR_CACHE) {//clear cache
                clearCache(position);
            }
        } else {
            if (bean.getId() == 19) {
                //Cancel account
                CancelConditionActivity.forward(mContext, href);
                return;
            }
            if (bean.getId() == 17) {
                //For feedback, please add the version number and device number to the URL.
                if (!href.contains("?")) {
                    href = StringUtil.contact(href, "?");
                }
                href = StringUtil.contact(href, "&version=", android.os.Build.VERSION.RELEASE, "&model=", android.os.Build.MODEL);
            }
            WebViewActivity.forward(mContext, href);
        }
    }

    @Override
    public void onCheckChanged(SettingBean bean) {
        int id = bean.getId();
        if (id == -1) {
            SpUtil.getInstance().setBrightness(bean.isChecked() ? 0.05f : -1f);
            updateBrightness();
        } else if (id == -2) {
            SpUtil.getInstance().setImMsgRingOpen(bean.isChecked());
        }
    }

    /**
     * 检查更新
     */
    private void checkVersion() {
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (VersionUtil.isLatest(configBean.getVersion())) {
                        ToastUtil.show(R.string.version_latest);
                    } else {
                        VersionUtil.showDialog(mContext, configBean, configBean.getDownloadApkUrl());
                    }
                }
            }
        });

    }

    /**
     * 退出登录
     */
    private void logout() {
        MMKVUtils.removeAll();
        ImPushUtil.getInstance().logout();
        CommonHttpUtil.updatePushId("");
        CommonAppConfig.getInstance().clearLoginInfo();
        //exit aurora
        ImMessageUtil.getInstance().logoutImClient();
        //Umeng Statistics Logout
//        MobclickAgent.onProfileSignOff();
        // Log out chat login
        CommonAppConfig.logoutIm();
        EventBus.getDefault().post(new LoginChangeEvent(false, false));
        finish();
    }

    /**
     * 修改密码
     */
    private void forwardModifyPwd() {
        startActivity(new Intent(mContext, ModifyPwdActivity.class));
    }

    /**
     * 获取缓存
     */
    private String getCacheSize() {
        return GlideCatchUtil.getInstance().getCacheSize();
    }

    /**
     * 清除缓存
     */
    private void clearCache(final int position) {
        final Dialog dialog = DialogUitl.loadingDialog(mContext, getString(R.string.setting_clear_cache_ing));
        dialog.show();
        GlideCatchUtil.getInstance().clearImageAllCache();
        File gifGiftDir = new File(CommonAppConfig.GIF_PATH);
        if (gifGiftDir.exists() && gifGiftDir.length() > 0) {
            gifGiftDir.delete();
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (mAdapter != null) {
                    mAdapter.setCacheString(getCacheSize());
                    mAdapter.notifyItemChanged(position);
                }
                ToastUtil.show(R.string.setting_clear_cache);
            }
        }, 2000);
    }


    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        MainHttpUtil.cancel(MainHttpConsts.GET_SETTING_LIST);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        super.onDestroy();
    }

}
