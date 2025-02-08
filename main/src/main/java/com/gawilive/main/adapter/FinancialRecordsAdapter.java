package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.main.R;
import com.gawilive.main.bean.TransferListModel;

// 适配器
public class FinancialRecordsAdapter extends AppAdapter<TransferListModel> {


    public FinancialRecordsAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        return getData() == null ? 0 : getData().size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    public void initView() {
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {


        private final TextView mTvDesc;
        private final TextView mTvTime;
        private final TextView mTvMoney;

        private ViewHolder() {
            super(R.layout.item_financial_record);
            mTvDesc = findViewById(R.id.tvDesc);
            mTvTime = findViewById(R.id.tvTime);
            mTvMoney = findViewById(R.id.tvMoney);
        }

        @Override
        public void onBindView(int position) {
            TransferListModel model = getItem(position);
            mTvDesc.setText(model.getMemo());
            mTvMoney.setText(model.getStr_fh() + model.getMoney());
            mTvTime.setText(model.getAddtime());
        }
    }
}