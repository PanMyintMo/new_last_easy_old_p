package com.gawilive.main.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.custom.CommonRefreshView;
import com.gawilive.common.custom.ItemDecoration;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.OnItemClickListener;
import com.gawilive.common.utils.FloatWindowHelper;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.live.bean.LiveBean;
import com.gawilive.live.utils.LiveStorge;
import com.gawilive.main.R;
import com.gawilive.main.adapter.MainHomeFollowAdapter;
import com.gawilive.main.http.MainHttpConsts;
import com.gawilive.main.http.MainHttpUtil;
import com.gawilive.main.presenter.CheckLivePresenter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/10/27.
 */

public class LiveRecommendActivity extends AbsActivity implements OnItemClickListener<LiveBean> {

    private CommonRefreshView mRefreshView;
    private MainHomeFollowAdapter mAdapter;
    private CheckLivePresenter mCheckLivePresenter;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, LiveRecommendActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_class;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.main_live_recom));
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live_class);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveBean>() {
            @Override
            public RefreshAdapter<LiveBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeFollowAdapter(mContext);
                    mAdapter.setOnItemClickListener(LiveRecommendActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getRecommendLive(p, callback);
            }

            @Override
            public List<LiveBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), LiveBean.class);
            }

            @Override
            public void onRefreshSuccess(List<LiveBean> list, int listCount) {
                if (CommonAppConfig.LIVE_ROOM_SCROLL) {
                    LiveStorge.getInstance().put(Constants.LIVE_CLASS_RECOMMEND, list);
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
    public void onItemClick(LiveBean bean, int position) {
        watchLive(bean, position);
    }


    /**
     * 观看直播
     */
    public void watchLive(LiveBean liveBean, int position) {
        if (!FloatWindowHelper.checkVoice(true)) {
            return;
        }
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            mCheckLivePresenter.watchLive(liveBean, Constants.LIVE_CLASS_RECOMMEND, position);
        } else {
            mCheckLivePresenter.watchLive(liveBean);
        }
    }

    @Override
    protected void onDestroy() {
        LiveStorge.getInstance().remove(Constants.LIVE_CLASS_RECOMMEND);
        MainHttpUtil.cancel(MainHttpConsts.GET_CLASS_LIVE);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRefreshView.initData();
    }

}
