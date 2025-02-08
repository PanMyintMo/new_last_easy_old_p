package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.common.utils.GlideUtils;
import com.gawilive.main.R;
import com.gawilive.main.bean.LotteryRecordChildModel;

/**
 * 开奖记录适配器
 */
public class LotteryChildRecordAdapter extends AppAdapter<LotteryRecordChildModel> {


    private ImageView mImgUserHead;
    private TextView mTvNickName;
    private RecyclerView mRecyclerViewNum;

    public LotteryChildRecordAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }


    private final class ViewHolder extends AppAdapter<?>.ViewHolder {


        private ViewHolder() {
            super(R.layout.item_lottery_child);
            mImgUserHead = findViewById(R.id.imgUserHead);
            mTvNickName = findViewById(R.id.tvNickName);
            mRecyclerViewNum = findViewById(R.id.recyclerViewNum);
        }

        @Override
        public void onBindView(int position) {
            LotteryRecordChildModel model = getItem(position);
            mTvNickName.setText(model.getUser_nickname());
            GlideUtils.setUserHead(getItemView().getContext(), model.getTurntable_thumb(), mImgUserHead);

            String[] str = model.getNumber_string().split(",");
            LotteryChildNumberRecordAdapter adapter = new LotteryChildNumberRecordAdapter(getItemView().getContext());
            mRecyclerViewNum.setAdapter(adapter);
            for (int i = 0; i < str.length; i++) {
                adapter.addItem(str[i]);
            }
        }
    }
}
