package com.gawilive.main.views;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.bean.LiveClassBean;
import com.gawilive.common.custom.CommonRefreshView;
import com.gawilive.common.custom.ItemDecoration;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.OnItemClickListener;
import com.gawilive.live.bean.LiveBean;
import com.gawilive.live.utils.LiveStorge;
import com.gawilive.main.R;
import com.gawilive.main.adapter.MainHomeNearAdapter;
import com.gawilive.main.adapter.MainLiveClassAdapter;
import com.gawilive.main.http.MainHttpConsts;
import com.gawilive.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 首页 附近
 */

public class MainHomeNearViewHolder extends AbsMainHomeChildViewHolder implements OnItemClickListener<LiveBean> {

    private CommonRefreshView mRefreshView;
    private MainHomeNearAdapter mAdapter;
    private int mLiveClassId;


    public MainHomeNearViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_near;
    }

    @Override
    public void init() {
        RecyclerView classRecyclerView = findViewById(R.id.recyclerView_class);
        classRecyclerView.setHasFixedSize(true);
        classRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        MainLiveClassAdapter classAdapter = new MainLiveClassAdapter(mContext);
        classAdapter.setOnItemClickListener(new OnItemClickListener<LiveClassBean>() {
            @Override
            public void onItemClick(LiveClassBean bean, int position) {
                mLiveClassId = bean.getId();
                if (mRefreshView != null) {
                    mRefreshView.initData();
                }
            }
        });
        classRecyclerView.setAdapter(classAdapter);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live_near);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveBean>() {
            @Override
            public RefreshAdapter<LiveBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeNearAdapter(mContext);
                    mAdapter.setOnItemClickListener(MainHomeNearViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getNear(p, mLiveClassId, callback);
            }

            @Override
            public List<LiveBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), LiveBean.class);
            }

            @Override
            public void onRefreshSuccess(List<LiveBean> list, int count) {
                if (CommonAppConfig.LIVE_ROOM_SCROLL) {
                    LiveStorge.getInstance().put(Constants.LIVE_NEAR, list);
                }
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<LiveBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }


    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Override
    public void onItemClick(LiveBean bean, int position) {
        watchLive(bean, Constants.LIVE_NEAR, position);
    }


    @Override
    public void release() {
        MainHttpUtil.cancel(MainHttpConsts.GET_NEAR);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

}
