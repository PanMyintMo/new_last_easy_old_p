package com.gawilive.beauty.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gawilive.beauty.R;
import com.gawilive.beauty.bean.MeiYanTypeBean;
import com.gawilive.beauty.constant.Constants;
import com.gawilive.beauty.interfaces.OnItemClickListener;
import com.meihu.beautylibrary.MHSDK;
import com.gawilive.common.utils.WordUtil;

import java.util.List;

public class MeiYanTitleAdapter extends RecyclerView.Adapter {

    private final LayoutInflater mInflater;
    private final List<MeiYanTypeBean> mList;
    private final int mColor0;
    private final int mColor1;
    private int mCheckedPosition;
    private final View.OnClickListener mOnClickListener;
    private OnItemClickListener<MeiYanTypeBean> mOnItemClickListener;

    public MeiYanTitleAdapter(Context context, List<MeiYanTypeBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mColor0 = ContextCompat.getColor(context, R.color.mh_textColor3);
        mColor1 = ContextCompat.getColor(context, R.color.mh_global);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mList.get(position), position);
                }
            }
        };
    }


    public void setCheckedPosition(int position) {
        if (position != mCheckedPosition) {
            mList.get(position).setChecked(true);
            mList.get(mCheckedPosition).setChecked(false);
            notifyItemChanged(position, Constants.PAYLOAD);
            notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
            mCheckedPosition = position;
        }
    }

    public void setOnItemClickListener(OnItemClickListener<MeiYanTypeBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_meiyan_title, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class Vh extends RecyclerView.ViewHolder {

        TextView mName;
        View mView;
        public Vh(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.tv_title);
            mView = itemView.findViewById(R.id.view_title_buttom);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(MeiYanTypeBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                mName.setText(WordUtil.getString(bean.getName()));
            }
            mName.setTextColor(bean.isChecked() ? mColor1 : mColor0);
            if (MHSDK.isEngLish && bean.isChecked()){
                mView.setVisibility(View.VISIBLE);
            }else {
                mView.setVisibility(View.GONE);
            }
        }
    }

}
