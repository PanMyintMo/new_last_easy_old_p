package com.gawilive.live.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.bean.LevelBean;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.utils.CommonIconUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.live.R;
import com.gawilive.live.bean.GuardUserBean;

/**
 * Created by cxf on 2018/11/6.
 */

public class GuardAdapter extends RefreshAdapter<GuardUserBean> {

    private static final int HEAD = 1;
    private static final int NORMAL = 0;
    private final String mVotesName;
    private final String mWeekContributeString;//本周贡献
    private final boolean mDialog;
    private final Drawable mGuardDrawable0;
    private final Drawable mGuardDrawable1;

    public GuardAdapter(Context context, boolean dialog) {
        super(context);
        mDialog = dialog;
        mVotesName = CommonAppConfig.getInstance().getVotesName();
        mWeekContributeString = WordUtil.getString(R.string.guard_week_con);
        mGuardDrawable0 = ContextCompat.getDrawable(context, R.mipmap.icon_live_chat_guard_1);
        mGuardDrawable1 = ContextCompat.getDrawable(context, R.mipmap.icon_live_chat_guard_2);
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        }
        return NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            return new HeadVh(mInflater.inflate(mDialog ? R.layout.guard_list_head : R.layout.guard_list_head_2, parent, false));
        }
        return new Vh(mInflater.inflate(mDialog ? R.layout.guard_list : R.layout.guard_list_2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof HeadVh) {
            ((HeadVh) vh).setData(mList.get(position));
        } else {
            ((Vh) vh).setData(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class HeadVh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mSex;
        ImageView mLevel;
        TextView mVotes;


        public HeadVh(@NonNull View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mSex = itemView.findViewById(R.id.sex);
            mLevel = itemView.findViewById(R.id.level);
            mVotes = itemView.findViewById(R.id.votes);
        }

        void setData(GuardUserBean bean) {
            ImgLoader.displayAvatar(mContext, bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            mSex.setImageResource(CommonIconUtil.getSexIcon(bean.getSex()));
            LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
            if (levelBean != null) {
                ImgLoader.display(mContext, levelBean.getThumb(), mLevel);
            }
            mVotes.setText(Html.fromHtml(mWeekContributeString + "  <font color='#ff5878'>" + bean.getContribute() + "</font>  " + mVotesName));
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mIcon;
        ImageView mAvatar;
        TextView mName;
        ImageView mSex;
        ImageView mLevel;
        TextView mVotes;


        public Vh(@NonNull View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.icon);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mSex = itemView.findViewById(R.id.sex);
            mLevel = itemView.findViewById(R.id.level);
            mVotes = itemView.findViewById(R.id.votes);
        }

        void setData(GuardUserBean bean) {
            ImgLoader.displayAvatar(mContext, bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            mSex.setImageResource(CommonIconUtil.getSexIcon(bean.getSex()));
            LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
            if (levelBean != null) {
                ImgLoader.display(mContext, levelBean.getThumb(), mLevel);
            }
            mVotes.setText(bean.getContribute() + " " + mVotesName);
            if (bean.getType() == 1) {
                mIcon.setImageDrawable(mGuardDrawable0);
            } else {
                mIcon.setImageDrawable(mGuardDrawable1);
            }
        }
    }
}
