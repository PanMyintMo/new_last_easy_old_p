package com.gawilive.mall.views;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.custom.CommonRefreshView;
import com.gawilive.common.custom.ItemDecoration;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.OnItemClickListener;
import com.gawilive.common.views.AbsCommonViewHolder;
import com.gawilive.mall.R;
import com.gawilive.mall.activity.GoodsDetailActivity;
import com.gawilive.mall.activity.ShopHomeActivity;
import com.gawilive.mall.adapter.ShopHomePlatAdapter;
import com.gawilive.mall.bean.GoodsSimpleBean;
import com.gawilive.mall.http.MallHttpConsts;
import com.gawilive.mall.http.MallHttpUtil;

import java.util.List;

/**
 * 店铺 平台自营商品
 */
public class ShopHomePlatViewHolder extends AbsCommonViewHolder implements OnItemClickListener<GoodsSimpleBean> {

    private CommonRefreshView mRefreshView;
    private ShopHomePlatAdapter mAdapter;
    private String mToUid;

    public ShopHomePlatViewHolder(Context context, ViewGroup parentView, String toUid) {
        super(context, parentView, toUid);
    }

    @Override
    protected void processArguments(Object... args) {
        if (args.length > 0) {
            mToUid = (String) args[0];
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_shop_home_my;
    }

    @Override
    public void init() {
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_goods_seller_2);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 10, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mAdapter = new ShopHomePlatAdapter(mContext, !TextUtils.isEmpty(mToUid) && mToUid.equals(CommonAppConfig.getInstance().getUid()));
        mAdapter.setOnItemClickListener(this);
        mRefreshView.setRecyclerViewAdapter(mAdapter);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<GoodsSimpleBean>() {
            @Override
            public RefreshAdapter<GoodsSimpleBean> getAdapter() {
                return null;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MallHttpUtil.getPlatShop(mToUid, p, callback);
            }

            @Override
            public List<GoodsSimpleBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                ((ShopHomeActivity) mContext).showShopInfo(obj.getJSONObject("shop_info"));
                return JSON.parseArray(obj.getString("list"), GoodsSimpleBean.class);
            }

            @Override
            public void onRefreshSuccess(List<GoodsSimpleBean> list, int listCount) {
                ((ShopHomeActivity) mContext).finishRefresh();
            }

            @Override
            public void onRefreshFailure() {
                ((ShopHomeActivity) mContext).finishRefresh();
            }

            @Override
            public void onLoadMoreSuccess(List<GoodsSimpleBean> loadItemList, int loadItemCount) {

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
    public void onItemClick(GoodsSimpleBean bean, int position) {
        GoodsDetailActivity.forward(mContext, bean.getId(), true, bean.getType(), mToUid);
    }

    @Override
    public void onDestroy() {
        MallHttpUtil.cancel(MallHttpConsts.GET_PLAT_SHOP);
        super.onDestroy();
    }
}
