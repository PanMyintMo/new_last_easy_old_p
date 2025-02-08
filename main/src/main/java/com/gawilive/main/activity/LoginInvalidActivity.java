package com.gawilive.main.activity;

import android.view.View;
import android.widget.TextView;

import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.utils.MMKVUtils;
import com.gawilive.common.utils.RouteUtil;
import com.gawilive.im.utils.ImMessageUtil;
import com.gawilive.im.utils.ImPushUtil;
import com.gawilive.main.R;

/**
 * Created by cxf on 2017/10/9.
 * 登录失效的时候以dialog形式弹出的activity
 */
public class LoginInvalidActivity extends AbsActivity implements View.OnClickListener {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_invalid;
    }

    @Override
    protected void main() {
        TextView textView = findViewById(R.id.content);
        String tip = getIntent().getStringExtra(Constants.TIP);
        textView.setText(tip);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MMKVUtils.removeAll();
        CommonAppConfig.getInstance().clearLoginInfo();
        //退出极光
        ImMessageUtil.getInstance().logoutImClient();
        ImPushUtil.getInstance().logout();
        RouteUtil.forwardLogin(mContext);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
