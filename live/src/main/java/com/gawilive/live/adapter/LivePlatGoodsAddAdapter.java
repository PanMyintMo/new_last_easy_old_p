package com.gawilive.live.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.common.Constants;
import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.bean.GoodsBean;
import com.gawilive.common.custom.MyRadioButton;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.StringUtil;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.live.R;
import com.gawilive.live.http.LiveHttpUtil;

import java.util.List;

/**
 * Created by cxf on 2019/8/29.
 */

public class LivePlatGoodsAddAdapter extends RefreshAdapter<GoodsBean> {

    private final View.OnClickListener mAddClickListener;
    private final String mMoneySymbol;
    private final String mAddString;
    private final String mAddedString;
    private final String mStringYong;


    public LivePlatGoodsAddAdapter(Context context) {
        super(context);
        mAddClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                final int position = (int) v.getTag();
                final GoodsBean bean = mList.get(position);
                if(bean.getIssale() == 1){
                    return;
                }
                final int isSale = bean.getIssale() == 1 ? 0 : 1;
                LiveHttpUtil.setPlatGoodsSale(bean.getId(), isSale, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            bean.setIssale(isSale);
                            notifyItemChanged(position, Constants.PAYLOAD);
                        }
                        ToastUtil.show(msg);
                    }

                });
            }
        };
        mMoneySymbol = WordUtil.getString(R.string.money_symbol);
        mAddString = WordUtil.getString(R.string.add);
        mAddedString = WordUtil.getString(R.string.added);
        mStringYong = WordUtil.getString(R.string.mall_408);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_plat_goods_add, parent, false));
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
        TextView mTitle;
        TextView mPrice;
        TextView mPriceYong;
        MyRadioButton mBtnAdd;

        public Vh(View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mTitle = itemView.findViewById(R.id.title);
            mPrice = itemView.findViewById(R.id.price);
            mPriceYong = itemView.findViewById(R.id.price_yong);
            mBtnAdd = itemView.findViewById(R.id.btn_add);
            mBtnAdd.setOnClickListener(mAddClickListener);
        }

        void setData(GoodsBean bean, int position, Object payload) {
            if (payload == null) {
                mBtnAdd.setTag(position);
                ImgLoader.display(mContext, bean.getThumb(), mThumb);
                mPrice.setText(StringUtil.contact(mMoneySymbol, bean.getPriceNow()));
                mPriceYong.setText(StringUtil.contact(mStringYong, mMoneySymbol, bean.getPriceYong()));
                mTitle.setText(bean.getName());
            }

            if (bean.getIssale() == 1) {
                mBtnAdd.setText(mAddedString);
                mBtnAdd.doChecked(true);

            } else {
                mBtnAdd.setText(mAddString);
                mBtnAdd.doChecked(false);
            }
        }
    }
}
