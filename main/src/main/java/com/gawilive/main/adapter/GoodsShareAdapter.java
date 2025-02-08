package com.gawilive.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.bean.LevelBean;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.utils.CommonIconUtil;
import com.gawilive.main.R;
import com.gawilive.main.bean.GoodsShareUserBean;

import java.util.List;

/**
 * Created by cxf on 2018/9/29.
 */

public class GoodsShareAdapter extends RefreshAdapter<GoodsShareUserBean> {

    private final View.OnClickListener mClickListener;
    private final Drawable mCheckedDrawable;
    private final Drawable mUnCheckedDrawable;


    public GoodsShareAdapter(Context context) {
        super(context);
        mCheckedDrawable = ContextCompat.getDrawable(context, R.mipmap.ic_check_1);
        mUnCheckedDrawable = ContextCompat.getDrawable(context, R.mipmap.ic_check_0);
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                GoodsShareUserBean bean = mList.get(position);
                bean.setChecked(!bean.isChecked());
                notifyItemChanged(position, Constants.PAYLOAD);
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_goods_share, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImgCheck;
        ImageView mAvatar;
        TextView mName;
        TextView mSign;
        ImageView mSex;
        ImageView mLevelAnchor;
        ImageView mLevel;

        public Vh(View itemView) {
            super(itemView);
            mImgCheck = itemView.findViewById(R.id.img_check);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mSign = itemView.findViewById(R.id.sign);
            mSex = itemView.findViewById(R.id.sex);
            mLevelAnchor = itemView.findViewById(R.id.level_anchor);
            mLevel = itemView.findViewById(R.id.level);
            itemView.setOnClickListener(mClickListener);
        }

        void setData(GoodsShareUserBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                ImgLoader.displayAvatar(mContext, bean.getAvatar(), mAvatar);
                mName.setText(bean.getUserNiceName());
                mSign.setText(bean.getSignature());
                mSex.setImageResource(CommonIconUtil.getSexIcon(bean.getSex()));
                LevelBean anchorLevelBean = CommonAppConfig.getInstance().getAnchorLevel(bean.getLevelAnchor());
                if (anchorLevelBean != null) {
                    ImgLoader.display(mContext, anchorLevelBean.getThumb(), mLevelAnchor);
                }
                LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
                if (levelBean != null) {
                    ImgLoader.display(mContext, levelBean.getThumb(), mLevel);
                }
            }
            mImgCheck.setImageDrawable(bean.isChecked() ? mCheckedDrawable : mUnCheckedDrawable);
        }

    }
}
