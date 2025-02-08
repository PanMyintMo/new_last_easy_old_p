package com.gawilive.live.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gawilive.live.R;
import com.gawilive.live.activity.LiveAudienceActivity;
import com.gawilive.live.bean.LiveVoiceFaceBean;

import java.util.List;

public class LiveVoiceFaceAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final List<LiveVoiceFaceBean> mList;
    private final View.OnClickListener mOnClickListener;

    public LiveVoiceFaceAdapter(Context context, LayoutInflater inflater, List<LiveVoiceFaceBean> list) {
        mContext = context;
        mInflater = inflater;
        mList = list;
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();
                if (mContext != null) {
                    ((LiveAudienceActivity) mContext).voiceRoomSendFace(index);
                }

            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_live_voice_face, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int i) {
        ((Vh) vh).setData(mList.get(i));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveVoiceFaceBean bean) {
            mImg.setImageResource(bean.getImageRes());
            itemView.setTag(bean.getIndex());
        }
    }
}
