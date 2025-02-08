package com.gawilive.im.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.common.Constants;
import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.bean.UserBean;
import com.gawilive.common.custom.MyRadioButton;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.CommonHttpUtil;
import com.gawilive.common.utils.RouteUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.im.R;
import com.gawilive.im.bean.VideoImConcatBean;

import java.util.List;

/**
 * Created by 云豹科技 on 2022/1/18.
 */
public class ImMsgConcatAdapter extends RefreshAdapter<VideoImConcatBean> {

    private final String mTip;
    private final int mColor;
    private final View.OnClickListener mFollowClickListener;
    private final View.OnClickListener mOnClickListener;
    private final String mFollow;
    private final String mFollowing;

    public ImMsgConcatAdapter(Context context) {
        super(context);
        mFollow = WordUtil.getString(R.string.follow);
        mFollowing = WordUtil.getString(R.string.following);
        mTip = WordUtil.getString(R.string.a_096);
        mColor = ContextCompat.getColor(context, R.color.textColor);
        mFollowClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null) {
                    VideoImConcatBean bean = (VideoImConcatBean) tag;
                    CommonHttpUtil.setAttention(bean.getFromUid(), null);
                }
            }
        };
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null) {
                    VideoImConcatBean bean = (VideoImConcatBean) tag;
                    RouteUtil.forwardUserHome(mContext, bean.getFromUid());
                }
            }
        };
    }

    public void updateItem(String id, int attention) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        for (int i = 0, size = mList.size(); i < size; i++) {
            VideoImConcatBean bean = mList.get(i);
            if (bean != null && id.equals(bean.getFromUid())) {
                bean.setIsAttent(attention);
                notifyItemChanged(i, Constants.PAYLOAD);
                break;
            }
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_im_msg_concat, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int i) {
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), payload);
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mTitle;
        TextView mAddTime;
        ImageView mAvatar;
        MyRadioButton mBtnFollow;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mAddTime = itemView.findViewById(R.id.add_time);
            mAvatar = itemView.findViewById(R.id.avatar);
            mBtnFollow = itemView.findViewById(R.id.btn_follow);
            mBtnFollow.setOnClickListener(mFollowClickListener);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(VideoImConcatBean bean, Object payload) {
            if (payload == null) {
                mBtnFollow.setTag(bean);
                itemView.setTag(bean);
                mAddTime.setText(bean.getAddTime());
                UserBean userBean = bean.getUserBean();
                if (userBean != null) {
                    ImgLoader.displayAvatar(mContext, userBean.getAvatar(), mAvatar);
                    String userName = userBean.getUserNiceName();
                    if (userName == null) {
                        userName = "";
                    }
                    String title = String.format(mTip, userName);
                    SpannableString span = new SpannableString(title);
                    span.setSpan(new ForegroundColorSpan(mColor), 0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mTitle.setText(span);
                }
            }
            if (bean.getIsAttent() == 1) {
                mBtnFollow.doChecked(true);
                mBtnFollow.setText(mFollowing);
            } else {
                mBtnFollow.doChecked(false);
                mBtnFollow.setText(mFollow);
            }

        }
    }
}
