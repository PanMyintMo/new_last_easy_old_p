package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.common.utils.GlideUtils;
import com.gawilive.main.R;
import com.gawilive.main.bean.PrizeListModel;

/**
 * 抽奖列表适配器
 */
public class PrizeListShowAdapter extends AppAdapter<PrizeListModel> {


    public PrizeListShowAdapter(Context context) {
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

        private ViewHolder() {
            super(R.layout.item_prize_show);
            mImg = findViewById(R.id.img);
        }

        @Override
        public void onBindView(int position) {
            PrizeListModel model = getItem(position);
            GlideUtils.setImages(getItemView().getContext(), model.getThumb_str(), mImg, 6);
        }
    }
}
