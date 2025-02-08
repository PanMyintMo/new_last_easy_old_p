package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.main.R;
import com.gawilive.main.bean.PaymentChildRecordsModel;

/**
 * 缴费记录适配器
 */
public class PaymentRecordsChildAdapter extends AppAdapter<PaymentChildRecordsModel> {


    public PaymentRecordsChildAdapter(Context context) {
        super(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }


    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mTvNumber;

        private ViewHolder() {
            super(R.layout.item_payment_record_child);
            mTvNumber = findViewById(R.id.tvNumber);
        }

        @Override
        public void onBindView(int position) {
            PaymentChildRecordsModel model = getItem(position);
            mTvNumber.setText(model.getNumber().replace(",", " "));
        }
    }
}
