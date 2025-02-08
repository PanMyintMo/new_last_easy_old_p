package com.gawilive.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.main.R;
import com.gawilive.main.activity.ActiveTopicActivity;
import com.gawilive.main.bean.ActiveTopicBean;

import java.util.List;

public class ActiveHotTopicAdapter extends RefreshAdapter<ActiveTopicBean> {

    private final View.OnClickListener mOnClickListener;

    public ActiveHotTopicAdapter(Context context, List<ActiveTopicBean> list) {
        super(context, list);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiveTopicBean bean = (ActiveTopicBean) v.getTag();
                if (bean != null) {
                    ActiveTopicActivity.forward(mContext, bean.getId(), bean.getName());
                }
            }
        };

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_active_hot_topic_new, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int i) {
        ((Vh) vh).setData(mList.get(i));
    }


    class Vh extends RecyclerView.ViewHolder {

//        ImageView mThumb;
        TextView mName;

        TextView mDesc;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mDesc = itemView.findViewById(R.id.tvDesc);
            mName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(ActiveTopicBean bean) {
            itemView.setTag(bean);

            mName.setText(bean.getName());

        }
    }
}
