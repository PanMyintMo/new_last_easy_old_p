package com.gawilive.mall.views;

import android.app.Dialog;
import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.gawilive.common.Constants;
import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.custom.CommonRefreshView;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.DialogUitl;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.common.views.AbsCommonViewHolder;
import com.gawilive.mall.R;
import com.gawilive.mall.activity.GoodsAddOutSideActivity;
import com.gawilive.mall.activity.GoodsDetailActivity;
import com.gawilive.mall.activity.SellerAddGoodsActivity;
import com.gawilive.mall.activity.SellerManageGoodsActivity;
import com.gawilive.mall.adapter.SellerXiaJiaAdapter;
import com.gawilive.mall.bean.GoodsManageBean;
import com.gawilive.mall.http.MallHttpUtil;

import java.util.Arrays;
import java.util.List;

public class SellerXiaJiaViewHolder extends AbsCommonViewHolder implements SellerXiaJiaAdapter.ActionListener {

    private CommonRefreshView mRefreshView;
    private SellerXiaJiaAdapter mAdapter;

    public SellerXiaJiaViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_seller_manage_goods;
    }

    @Override
    public void init() {
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_goods_seller);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<GoodsManageBean>() {
            @Override
            public RefreshAdapter<GoodsManageBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new SellerXiaJiaAdapter(mContext);
                    mAdapter.setActionListener(SellerXiaJiaViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MallHttpUtil.getManageGoodsList("remove_shelves", p, callback);
            }

            @Override
            public List<GoodsManageBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), GoodsManageBean.class);
            }

            @Override
            public void onRefreshSuccess(List<GoodsManageBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<GoodsManageBean> loadItemList, int loadItemCount) {

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
    public void onItemClick(GoodsManageBean bean) {
        GoodsDetailActivity.forward(mContext, bean.getId(), bean.getType());
    }

    @Override
    public void onEditClick(GoodsManageBean bean) {
        if (bean.getType() == Constants.GOODS_TYPE_OUT) {
            GoodsAddOutSideActivity.forward(mContext, bean.getId());
        } else {
            SellerAddGoodsActivity.forward(mContext, bean.getId());
        }
    }

    @Override
    public void onDeleteClick(final GoodsManageBean bean) {
        new DialogUitl.Builder(mContext)
                .setContent(WordUtil.getString(R.string.mall_384))
                .setCancelable(true)
                .setBackgroundDimEnabled(true)
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        MallHttpUtil.goodsDelete(bean.getId(), new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0) {
                                    if (mRefreshView != null) {
                                        mRefreshView.initData();
                                    }
                                    ((SellerManageGoodsActivity) mContext).getGoodsNum();
                                } else {
                                    ToastUtil.show(msg);
                                }
                            }
                        });
                    }
                })
                .build()
                .show();
    }

    @Override
    public void onShangJiaClick(final GoodsManageBean bean) {
        new DialogUitl.Builder(mContext)
                .setContent(WordUtil.getString(R.string.mall_383))
                .setCancelable(true)
                .setBackgroundDimEnabled(true)
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        MallHttpUtil.goodsUpStatus(bean.getId(), 1, new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0) {
                                    if (mRefreshView != null) {
                                        mRefreshView.initData();
                                    }
                                    ((SellerManageGoodsActivity) mContext).getGoodsNum();
                                } else {
                                    ToastUtil.show(msg);
                                }
                            }
                        });
                    }
                })
                .build()
                .show();

    }
}
