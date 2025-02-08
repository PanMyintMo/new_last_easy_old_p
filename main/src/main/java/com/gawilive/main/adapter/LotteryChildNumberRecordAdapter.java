package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.hjq.shape.view.ShapeTextView;
import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.main.R;

/**
 * 开奖记录适配器 数字
 */
public class LotteryChildNumberRecordAdapter extends AppAdapter<String> {


    private ShapeTextView mTvNumber;

    public LotteryChildNumberRecordAdapter(Context context) {
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


        private ViewHolder() {
            super(R.layout.item_number);
            mTvNumber = findViewById(R.id.tvNumber);
        }

        @Override
        public void onBindView(int position) {
            mTvNumber.setText(getItem(position));
        }
    }
}
