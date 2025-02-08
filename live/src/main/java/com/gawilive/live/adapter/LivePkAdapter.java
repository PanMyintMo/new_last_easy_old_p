package com.gawilive.live.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
import com.gawilive.live.bean.LivePkBean;

/**
 * Created by cxf on 2018/11/15.
 */

public class LivePkAdapter extends RefreshAdapter<LivePkBean> {

    private final View.OnClickListener mOnClickListener;
    private final String mLivePkInviteString;//邀请连麦
    private final String mLivePkInviteString2;//已邀请

    public LivePkAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick((LivePkBean) tag, 0);
                }
            }
        };
        mLivePkInviteString = WordUtil.getString(R.string.live_pk_invite);
        mLivePkInviteString2 = WordUtil.getString(R.string.live_pk_invite_2);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_pk, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position));
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mSex;
        ImageView mLevel;
        TextView mBtnInvite;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mSex = itemView.findViewById(R.id.sex);
            mLevel = itemView.findViewById(R.id.level);
            mBtnInvite = itemView.findViewById(R.id.btn_invite);
            mBtnInvite.setOnClickListener(mOnClickListener);
        }

        void setData(LivePkBean bean) {
            mBtnInvite.setTag(bean);
            ImgLoader.display(mContext,bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            mSex.setImageResource(CommonIconUtil.getSexIcon(bean.getSex()));
            LevelBean levelBean = CommonAppConfig.getInstance().getAnchorLevel(bean.getLevelAnchor());
            if (levelBean != null) {
                ImgLoader.display(mContext,levelBean.getThumb(), mLevel);
            }
            if (bean.isLinkMic()) {
                mBtnInvite.setText(mLivePkInviteString2);
                mBtnInvite.setEnabled(false);
            } else {
                mBtnInvite.setText(mLivePkInviteString);
                mBtnInvite.setEnabled(true);
            }
        }
    }
}
