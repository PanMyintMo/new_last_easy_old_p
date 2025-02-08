package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.common.utils.GlideUtils;
import com.gawilive.main.R;
import com.gawilive.main.bean.PrizeListModel;

/**
 * 抽奖列表适配器
 */
public class PrizeListAdapter extends AppAdapter<PrizeListModel> {


    public PrizeListAdapter(Context context) {
        super(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    public void initView() {
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final ImageView mImg;
        private final TextView mTvDesc;

        private ViewHolder() {
            super(R.layout.item_prize_list);
            mImg = findViewById(R.id.img);
            mTvDesc = findViewById(R.id.tvDesc);
        }

        @Override
        public void onBindView(int position) {
            PrizeListModel model = getItem(position);
            mTvDesc.setText(model.getTitle());
            GlideUtils.setImages(getItemView().getContext(), model.getThumb_str(), mImg, 6);
        }
    }
}
