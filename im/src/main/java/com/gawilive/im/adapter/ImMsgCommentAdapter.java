package com.gawilive.im.adapter;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.DialogUitl;
import com.gawilive.common.utils.FaceTextUtil;
import com.gawilive.common.utils.RouteUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.im.R;
import com.gawilive.im.bean.VideoImMsgBean;
import com.gawilive.video.activity.VideoPlayActivity;
import com.gawilive.video.bean.VideoBean;
import com.gawilive.video.http.VideoHttpUtil;

/**
 * Created by 云豹科技 on 2022/1/18.
 */
public class ImMsgCommentAdapter extends RefreshAdapter<VideoImMsgBean> {

    private final String mTip;
    private final String mTip2;
    private final int mColor;
    private final View.OnClickListener mAvatarClickListener;
    private final View.OnClickListener mOnClickListener;

    public ImMsgCommentAdapter(Context context) {
        super(context);
        mTip = WordUtil.getString(R.string.a_094);
        mTip2 = WordUtil.getString(R.string.a_098);
        mColor = ContextCompat.getColor(context, R.color.textColor);
        mAvatarClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag(R.id.avatar);
                if (tag != null) {
                    VideoImMsgBean bean = (VideoImMsgBean) tag;
                    RouteUtil.forwardUserHome(mContext, bean.getFromUid());
                }
            }
        };
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    VideoImMsgBean bean = (VideoImMsgBean) tag;
                    VideoHttpUtil.getVideoInfo(bean.getVideoId(), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                VideoBean videoBean = JSON.parseObject(info[0], VideoBean.class);
                                VideoPlayActivity.forwardSingle(mContext, videoBean);
                            }
                        }

                        @Override
                        public boolean showLoadingDialog() {
                            return true;
                        }

                        @Override
                        public Dialog createLoadingDialog() {
                            return DialogUitl.loadingDialog(mContext);
                        }
                    });
                }
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_im_msg_comment, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int i) {
        ((Vh) vh).setData(mList.get(i));
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mTitle;
        TextView mAddTime;
        ImageView mAvatar;
        ImageView mThumb;
        TextView mContent;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mAddTime = itemView.findViewById(R.id.add_time);
            mAvatar = itemView.findViewById(R.id.avatar);
            mThumb = itemView.findViewById(R.id.thumb);
            mContent = itemView.findViewById(R.id.content);
            mAvatar.setOnClickListener(mAvatarClickListener);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(VideoImMsgBean bean) {
            mAvatar.setTag(R.id.avatar, bean);
            itemView.setTag(bean);
            String userName = bean.getUserName();
            String title = String.format(bean.getType() == 0 ? mTip : mTip2, userName);
            SpannableString span = new SpannableString(title);
            span.setSpan(new ForegroundColorSpan(mColor), 0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTitle.setText(span);
            mContent.setText(FaceTextUtil.renderChatMessage(bean.getContent()));
            mAddTime.setText(bean.getAddTime());
            ImgLoader.displayAvatar(mContext, bean.getAvatar(), mAvatar);
            ImgLoader.display(mContext, bean.getThumb(), mThumb);
        }
    }
}
