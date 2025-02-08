package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;

import android.widget.TextView;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;
import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.main.R;
import com.gawilive.main.bean.PaymentRecordsModel;

/**
 * 缴费记录适配器
 */
public class PaymentRecordsAdapter extends AppAdapter<PaymentRecordsModel> {


    public PaymentRecordsAdapter(Context context) {
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


        private final TextView mTvQs;
        private final TextView mTvCount;
        private final RecyclerView mRecyclerView;


        private ViewHolder() {
            super(R.layout.item_payment_records);
            mTvQs = findViewById(R.id.tvQs);
            mTvCount = findViewById(R.id.tvCount);
            mRecyclerView = findViewById(R.id.recyclerView);

        }

        @Override
        public void onBindView(int position) {
            PaymentRecordsModel model = getItem(position);
            mTvQs.setText(model.getCycle() + "期");
            mTvCount.setText(model.getTotal() + "票");
            PaymentRecordsChildAdapter adapter = new PaymentRecordsChildAdapter(getItemView().getContext());
            mRecyclerView.setAdapter(adapter);
            adapter.setData(model.getData());
        }
    }
}
