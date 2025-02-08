package com.gawilive.main.popup;

import android.content.Context;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.lxj.xpopup.core.CenterPopupView;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.main.R;
import com.gawilive.main.bean.RuleModel;
import com.gawilive.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

// 抽奖规则弹窗
public class RulePopupWindow extends CenterPopupView {
    private TextView mTvTitle;
    private TextView mTvTitleOne;
    private TextView mTvTime;
    private TextView mTvTitleTwo;
    private TextView mTvContent;
    private ImageView mBtnClose;


    public RulePopupWindow(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_rule;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        initView();
    }

    private void initView() {
        mTvTitle = findViewById(R.id.tvTitle);
        mTvTitleOne = findViewById(R.id.tvTitleOne);
        mTvTime = findViewById(R.id.tvTime);
        mTvTitleTwo = findViewById(R.id.tvTitleTwo);
        mTvContent = findViewById(R.id.tvContent);
        mBtnClose = findViewById(R.id.btn_close);
        getData();
        mBtnClose.setOnClickListener(v -> {
            this.dialog.dismiss();
        });
    }

    private void getData() {
        MainHttpUtil.LotteryRules(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<RuleModel> list = JSON.parseArray(Arrays.toString(info), RuleModel.class);
                    mTvTitleOne.setText("" + "Event Time");
                    mTvTime.setText(list.get(0).getValue());
                    mTvTitleTwo.setText("" + "Event Summary");
                    mTvContent.setText(list.get(1).getValue());
                }
            }
        });
    }
}
