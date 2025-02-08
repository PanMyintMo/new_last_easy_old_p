package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;

import android.widget.TextView;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;
import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.main.R;
import com.gawilive.main.bean.LotteryRecordModel;

import java.util.ArrayList;

/**
 * 开奖记录适配器
 */
public class LotteryRecordAdapter extends AppAdapter<LotteryRecordModel> {


    public LotteryRecordAdapter(Context context) {
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


        private final TextView mTvDesignated;
        private final RecyclerView mRecyclerView;

        private ViewHolder() {
            super(R.layout.item_lottery_record);
            mTvDesignated = findViewById(R.id.tvDesignated);
            mRecyclerView = findViewById(R.id.recyclerView);
        }

        @Override
        public void onBindView(int position) {
            LotteryRecordModel model = getItem(position);
            mTvDesignated.setText(model.getCycle() + "期");
            LotteryChildRecordAdapter adapter = new LotteryChildRecordAdapter(getItemView().getContext());
            mRecyclerView.setAdapter(adapter);
            adapter.setData(model.getData() == null ? new ArrayList<>() : model.getData());

        }
    }
}
