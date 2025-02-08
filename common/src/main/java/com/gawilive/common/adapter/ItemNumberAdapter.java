package com.gawilive.common.adapter;

import android.content.Context;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.gawilive.common.R;

/**
 * 中奖号码适配器
 */
public class ItemNumberAdapter extends AppAdapter<String> {

    public ItemNumberAdapter(Context context) {
        super(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private ViewHolder() {
            super(R.layout.item_number);
        }

        @Override
        public void onBindView(int position) {

        }
    }
}
