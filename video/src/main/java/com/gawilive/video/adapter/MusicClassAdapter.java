package com.gawilive.video.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.video.R;
import com.gawilive.video.bean.MusicClassBean;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by cxf on 2018/7/26.
 */

public class MusicClassAdapter extends RecyclerView.Adapter<MusicClassAdapter.Vh> {

    private final Context mContext;
    private final List<MusicClassBean> mList;
    private final LayoutInflater mInflater;
    private final View.OnClickListener mOnClickListener;
    private OnItemClickListener<MusicClassBean> mOnItemClickListener;

    public MusicClassAdapter(Context context, List<MusicClassBean> list) {
        mContext=context;
        mList = list;
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick((MusicClassBean) tag, 0);
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<MusicClassBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_music_class, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        TextView mText;

        public Vh(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mText = itemView.findViewById(R.id.text);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(MusicClassBean bean) {
            itemView.setTag(bean);
            ImgLoader.display(mContext,bean.getImg_url(), mImg);
            mText.setText(bean.getTitle());
        }
    }
}
