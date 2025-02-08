package com.gawilive.main.activity;

import android.app.Dialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.custom.CommonRefreshView;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.DialogUitl;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.im.utils.ImMessageUtil;
import com.gawilive.main.R;
import com.gawilive.main.adapter.GoodsShareAdapter;
import com.gawilive.main.bean.GoodsShareUserBean;
import com.gawilive.main.http.MainHttpConsts;
import com.gawilive.main.http.MainHttpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoodsShareFriendActivity extends AbsActivity implements View.OnClickListener {

    private CommonRefreshView mRefreshView;
    private GoodsShareAdapter mAdapter;
    private String mGoodsId;
    private Dialog mLoading;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_goods_share_friend;
    }

    @Override
    protected void main() {
        mGoodsId = getIntent().getStringExtra(Constants.MALL_GOODS_ID);
        findViewById(R.id.btn_share).setOnClickListener(this);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_follow);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<GoodsShareUserBean>() {
            @Override
            public RefreshAdapter<GoodsShareUserBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new GoodsShareAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getFollowList(CommonAppConfig.getInstance().getUid(), p, callback);
            }

            @Override
            public List<GoodsShareUserBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), GoodsShareUserBean.class);
            }

            @Override
            public void onRefreshSuccess(List<GoodsShareUserBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<GoodsShareUserBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_share) {
            share();
        }
    }

    private void share() {
        if (TextUtils.isEmpty(mGoodsId) || mAdapter == null) {
            return;
        }
        List<GoodsShareUserBean> list = mAdapter.getList();
        if (list == null || list.size() == 0) {
            return;
        }
        List<String> toUids = null;
        for (GoodsShareUserBean bean : list) {
            if (bean.isChecked()) {
                if (toUids == null) {
                    toUids = new ArrayList<>();
                }
                toUids.add(bean.getId());
            }
        }
        if (toUids != null && toUids.size() > 0) {
            if (mLoading == null) {
                mLoading = DialogUitl.loadingDialog(mContext, WordUtil.getString(R.string.a_074));
            }
            mLoading.show();
            ImMessageUtil.getInstance().sendGoodsMessage(toUids, mGoodsId, new Runnable() {
                @Override
                public void run() {
                    if (mLoading != null && mLoading.isShowing()) {
                        mLoading.dismiss();
                        mLoading = null;
                    }
                    finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
            mLoading = null;
        }
        MainHttpUtil.cancel(MainHttpConsts.GET_FOLLOW_LIST);
        super.onDestroy();
    }
}
