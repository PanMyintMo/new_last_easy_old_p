package com.gawilive.mall.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.common.Constants;
import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.utils.StringUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.mall.R;
import com.gawilive.mall.activity.GoodsDetailActivity;
import com.gawilive.mall.bean.GoodsPlatBean;
import com.gawilive.mall.http.MallHttpUtil;

import java.util.List;

public class SellerAddPlatAdapter extends RefreshAdapter<GoodsPlatBean> {

    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private final View.OnClickListener mOnClickListener;
    private final View.OnClickListener mAddOnClickListener;
    private final String mSaleString;
    private final String mMoneySymbol;
    private final String mStringYong;
    private final int mAddColor;
    private final int mUnAddColor;
    private final Drawable mAddBg;
    private final Drawable mUnAddBg;
    private final String mAddString;
    private final String mUnAddString;


    public SellerAddPlatAdapter(Context context) {
        super(context);
        mSaleString = WordUtil.getString(R.string.mall_114);
        mMoneySymbol = WordUtil.getString(R.string.money_symbol);
        mStringYong = WordUtil.getString(R.string.mall_408);
        mAddColor = ContextCompat.getColor(mContext, R.color.gray1);
        mUnAddColor = ContextCompat.getColor(mContext, R.color.white);
        mAddBg = ContextCompat.getDrawable(mContext, R.drawable.seller_11);
        mUnAddBg = ContextCompat.getDrawable(mContext, R.drawable.seller_10);
        mAddString = WordUtil.getString(R.string.added);
        mUnAddString = WordUtil.getString(R.string.add);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                GoodsPlatBean bean = (GoodsPlatBean) v.getTag();
                if (bean != null) {
                    GoodsDetailActivity.forward(mContext, bean.getId(), bean.getType());
                }
            }
        };
        mAddOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                GoodsPlatBean bean = (GoodsPlatBean) v.getTag();
                MallHttpUtil.setPlatGoods(bean.getId());
            }
        };
    }


    public void goodsStatusChanged(String goodsId, int status) {
        if (TextUtils.isEmpty(goodsId)) {
            return;
        }
        if (mList == null) {
            return;
        }
        for (int i = 0, size = mList.size(); i < size; i++) {
            GoodsPlatBean bean = mList.get(i);
            if (goodsId.equals(bean.getId())) {
                bean.setAdd(status);
                notifyItemChanged(i, Constants.PAYLOAD);
                break;
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return LEFT;
        }
        return RIGHT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == LEFT) {
            return new Vh(mInflater.inflate(R.layout.item_seller_plat_left, viewGroup, false));
        }
        return new Vh(mInflater.inflate(R.layout.item_seller_plat_right, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mThumb;
        TextView mName;
        TextView mPirce;
        TextView mSaleNum;
        TextView mPirceYong;
        TextView mBtnAdd;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mName = itemView.findViewById(R.id.name);
            mPirce = itemView.findViewById(R.id.price);
            mSaleNum = itemView.findViewById(R.id.sale_num);
            mPirceYong = itemView.findViewById(R.id.price_yong);
            mBtnAdd = itemView.findViewById(R.id.btn_add);
            mBtnAdd.setOnClickListener(mAddOnClickListener);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(GoodsPlatBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(bean);
                mBtnAdd.setTag(bean);
                ImgLoader.display(mContext, bean.getThumb(), mThumb);
                mName.setText(bean.getName());
                mPirce.setText(StringUtil.contact(mMoneySymbol, bean.getPrice()));
                mSaleNum.setText(String.format(mSaleString, bean.getSaleNum()));
                mPirceYong.setText(StringUtil.contact(mStringYong, mMoneySymbol, bean.getPriceYong()));
            }
            if (bean.getAdd() == 1) {
                mBtnAdd.setText(mAddString);
                mBtnAdd.setTextColor(mAddColor);
                mBtnAdd.setBackground(mAddBg);
            } else {
                mBtnAdd.setText(mUnAddString);
                mBtnAdd.setTextColor(mUnAddColor);
                mBtnAdd.setBackground(mUnAddBg);
            }
        }

    }
}
