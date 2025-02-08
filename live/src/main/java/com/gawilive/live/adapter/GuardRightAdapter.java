package com.gawilive.live.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.live.R;
import com.gawilive.live.bean.GuardRightBean;
import com.gawilive.common.glide.ImgLoader;

import java.util.List;

/**
 * Created by cxf on 2018/11/6.
 */

public class GuardRightAdapter extends RecyclerView.Adapter<GuardRightAdapter.Vh> {

    private final Context mContext;
    private final List<GuardRightBean> mList;
    private final LayoutInflater mInflater;
//    private int mColor1;
//    private int mColor2;

    public GuardRightAdapter(Context context, List<GuardRightBean> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
//        mColor1 = ContextCompat.getColor(context, R.color.textColor);
//        mColor2 = ContextCompat.getColor(context, R.color.gray3);
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new Vh(mInflater.inflate(R.layout.guard_right, parent, false));
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

        ImageView mIcon;
        TextView mTitle;
        TextView mDes;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.icon);
            mTitle = itemView.findViewById(R.id.title);
            mDes = itemView.findViewById(R.id.des);
        }

        void setData(GuardRightBean bean) {
            mTitle.setText(bean.getTitle());
            mDes.setText(bean.getDes());
            if (bean.isChecked()) {
                ImgLoader.display(mContext, bean.getIcon1(), mIcon);
            } else {
                ImgLoader.display(mContext, bean.getIcon0(), mIcon);
            }
        }
    }
}
