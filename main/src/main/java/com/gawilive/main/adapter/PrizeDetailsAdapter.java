package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.common.utils.GlideUtils;
import com.gawilive.main.R;
import com.gawilive.main.bean.PrizeDetailsModel;

/**
 * 奖品详情适配器
 */
public class PrizeDetailsAdapter extends AppAdapter<PrizeDetailsModel> {


    public PrizeDetailsAdapter(Context context) {
        super(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }


    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private final ImageView mImgIcon;
        private final TextView mTvTitle;
        private final TextView mTvNumber;
        private final TextView mTvStatus;
        private final TextView mTvTime;

        private ViewHolder() {
            super(R.layout.item_prize_details);
            mImgIcon = findViewById(R.id.imgIcon);
            mTvTitle = findViewById(R.id.tvTile);
            mTvNumber = findViewById(R.id.tvNumber);
            mTvStatus = findViewById(R.id.tv_status);
            mTvTime = findViewById(R.id.tvTime);
        }

        @Override
        public void onBindView(int position) {
            PrizeDetailsModel model = getItem(position);
            mTvTitle.setText(model.getTurntable_title());
            mTvNumber.setText("中奖号码" + model.getNumber_string().replace(",", " "));
            mTvStatus.setText(model.getExchange_status_name());
            GlideUtils.setImages(getItemView().getContext(), model.getTurntable_thumb_str(), mImgIcon, 6);
            mTvTime.setText(model.getWintime());
        }
    }
}
