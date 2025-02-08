package com.gawilive.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.common.bean.LiveClassBean;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.interfaces.OnItemClickListener;
import com.gawilive.main.R;

import java.util.List;

/**
 * Created by cxf on 2018/9/25.
 */

public class MainHomeLiveClassAdapter extends RecyclerView.Adapter<MainHomeLiveClassAdapter.Vh> {

    private final Context mContext;
    private List<LiveClassBean> mList;
    private final LayoutInflater mInflater;
    private final View.OnClickListener mOnClickListener;
    private OnItemClickListener<LiveClassBean> mOnItemClickListener;

    public MainHomeLiveClassAdapter(Context context) {
        mContext = context;

        mInflater = LayoutInflater.from(context);
        mOnClickListener = v -> {
            Object tag = v.getTag();
            if (tag != null) {
                int position = (int) tag;
                LiveClassBean bean = mList.get(position);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean, position);
                }
            }
        };
    }

    public MainHomeLiveClassAdapter setData(List<LiveClassBean> list) {
        mList = list;
        notifyDataSetChanged();
        return this;
    }


    public void setOnItemClickListener(OnItemClickListener<LiveClassBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_main_home_live_class, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView mImg;
        TextView mName;

        public Vh(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveClassBean bean, int position) {
            itemView.setTag(position);
            if (bean.getId() == -1) {
                mImg.setImageResource(R.mipmap.icon_main_class_all);
            } else {
                ImgLoader.display(mContext, bean.getThumb(), mImg);
            }
            mName.setText(bean.getName());
        }
    }
}
