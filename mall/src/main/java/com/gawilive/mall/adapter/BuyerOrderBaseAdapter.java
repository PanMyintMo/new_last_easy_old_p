package com.gawilive.mall.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.utils.StringUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.mall.R;
import com.gawilive.mall.bean.BuyerOrderBean;

public abstract class BuyerOrderBaseAdapter extends RefreshAdapter<BuyerOrderBean> {

    private final String mTotalTipString;
    private final String mMoneySymbol;
    private final View.OnClickListener mShopClickListener;
    private final View.OnClickListener mItemClickListener;
    protected ActionListener mActionListener;

    public BuyerOrderBaseAdapter(Context context) {
        super(context);
        mTotalTipString = WordUtil.getString(R.string.mall_195);
        mMoneySymbol = WordUtil.getString(R.string.money_symbol);
        mShopClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    mActionListener.onShopClick((BuyerOrderBean) tag);
                }
            }
        };
        mItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    mActionListener.onItemClick((BuyerOrderBean) tag);
                }
            }
        };
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }


    public class BaseVh extends RecyclerView.ViewHolder {

        TextView mShopName;
        TextView mStatusTip;
        ImageView mGoodsThumb;
        TextView mGoodsName;
        TextView mGoodsPrice;
        TextView mGoodsSpecName;
        TextView mGoodsNum;
        TextView mTotalTip;
        TextView mTotalPrice;

        public BaseVh(@NonNull View itemView) {
            super(itemView);
            mShopName = itemView.findViewById(R.id.shop_name);
            mStatusTip = itemView.findViewById(R.id.status_tip);
            mGoodsThumb = itemView.findViewById(R.id.goods_thumb);
            mGoodsName = itemView.findViewById(R.id.goods_name);
            mGoodsPrice = itemView.findViewById(R.id.goods_price);
            mGoodsSpecName = itemView.findViewById(R.id.goods_spec_name);
            mGoodsNum = itemView.findViewById(R.id.goods_num);
            mTotalTip = itemView.findViewById(R.id.total_tip);
            mTotalPrice = itemView.findViewById(R.id.total_price);
            mShopName.setOnClickListener(mShopClickListener);
            itemView.setOnClickListener(mItemClickListener);
        }


        public void setData(BuyerOrderBean bean) {
            mShopName.setTag(bean);
            itemView.setTag(bean);
            mShopName.setText(bean.getShopName());
            mStatusTip.setText(bean.getStatusTip());
            ImgLoader.display(mContext, bean.getGoodsSpecThumb(), mGoodsThumb);
            mGoodsName.setText(bean.getGoodsName());
            mGoodsPrice.setText(StringUtil.contact(mMoneySymbol, bean.getGoodsPrice()));  // Correct
            mGoodsSpecName.setText(bean.getGoodsSpecName());
            mGoodsNum.setText(StringUtil.contact("x", bean.getGoodsNum()));

            // Use the format string directly in String.format()
            mTotalTip.setText(String.format("%1$s items in total", String.valueOf(bean.getGoodsNum())));

            mTotalPrice.setText(StringUtil.contact(mMoneySymbol, bean.getTotalPrice()));  // Correct
        }

    }

    public interface ActionListener {
        /**
         * 点击商店名字
         */
        void onShopClick(BuyerOrderBean bean);

        /**
         * 点击item
         */
        void onItemClick(BuyerOrderBean bean);

        /**
         * 取消订单
         */
        void onCancelOrderClick(BuyerOrderBean bean);

        /**
         * 付款
         */
        void onPayClick(BuyerOrderBean bean);

        /**
         * 删除订单
         */
        void onDeleteClick(BuyerOrderBean bean);

        /**
         * 查看物流
         */
        void onWuLiuClick(BuyerOrderBean bean);

        /**
         * 确认收货
         */
        void onConfirmClick(BuyerOrderBean bean);

        /**
         * 评价
         */
        void onCommentClick(BuyerOrderBean bean);

        /**
         * 发表追评
         */
        void onAppendCommentClick(BuyerOrderBean bean);

        /**
         * 退款详情
         */
        void onRefundClick(BuyerOrderBean bean);

    }

}
