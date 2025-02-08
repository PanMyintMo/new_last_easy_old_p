package com.gawilive.mall.views;

import android.content.Context;
import android.view.ViewGroup;

import com.gawilive.mall.adapter.BuyerOrderBaseAdapter;
import com.gawilive.mall.adapter.BuyerOrderRefundAdapter;
import com.gawilive.mall.bean.BuyerOrderBean;

/**
 * 买家 订单列表 退款
 */
public class BuyerOrderRefundViewHolder extends AbsBuyerOrderViewHolder {

    public BuyerOrderRefundViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public String getOrderType() {
        return "refund";
    }

    @Override
    public BuyerOrderBaseAdapter getBuyerOrderAdapter() {
        return new BuyerOrderRefundAdapter(mContext);
    }

    @Override
    public void onItemClick(BuyerOrderBean bean) {
        onRefundClick(bean);
    }
}
