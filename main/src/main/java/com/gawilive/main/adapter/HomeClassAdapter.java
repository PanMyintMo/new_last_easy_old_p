package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gawilive.common.bean.LiveClassBean;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.main.R;

public class HomeClassAdapter extends AppAdapter<LiveClassBean> {

    public HomeClassAdapter(Context context) {
        super(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final ImageView mImg;
        private final TextView mName;


        private ViewHolder() {
            super(R.layout.item_main_home_live_class);
            mImg = itemView.findViewById(R.id.img);
            mName = itemView.findViewById(R.id.name);
        }

        @Override
        public void onBindView(int position) {
            LiveClassBean bean = getItem(position);
            if (bean.getId() == -1) {
                mImg.setImageResource(R.mipmap.icon_main_class_all);
            } else {
                ImgLoader.display(getContext(), bean.getThumb(), mImg);
            }
            mName.setText(bean.getName());
        }
    }
}
