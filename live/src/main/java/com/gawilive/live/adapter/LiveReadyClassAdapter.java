package com.gawilive.live.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.common.bean.LiveClassBean;
import com.gawilive.common.custom.MyRadioButton;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.interfaces.OnItemClickListener;
import com.gawilive.live.R;

import java.util.List;

/**
 * Created by cxf on 2018/10/7.
 */

public class LiveReadyClassAdapter extends RecyclerView.Adapter<LiveReadyClassAdapter.Vh> {

    private final Context mContext;
    private final List<LiveClassBean> mList;
    private final LayoutInflater mInflater;
    private final View.OnClickListener mOnClickListener;
    private OnItemClickListener<LiveClassBean> mOnItemClickListener;

    public LiveReadyClassAdapter(Context context, List<LiveClassBean> list) {
        mContext=context;
        mList = list;
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick((LiveClassBean) tag, 0);
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<LiveClassBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_ready_class, parent, false));
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

        ImageView mThumb;
        TextView mName;
        TextView mDes;
        MyRadioButton mRadioButton;

        public Vh(View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mName = itemView.findViewById(R.id.name);
            mDes = itemView.findViewById(R.id.des);
            mRadioButton = itemView.findViewById(R.id.radioButton);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveClassBean bean) {
            itemView.setTag(bean);
            ImgLoader.display(mContext,bean.getThumb(), mThumb);
            mName.setText(bean.getName());
            mDes.setText(bean.getDes());
            mRadioButton.doChecked(bean.isChecked());
        }
    }
}
