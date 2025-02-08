package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.main.R;
import com.gawilive.main.bean.PayTypeModel;

public class PayTypeAdapter extends AppAdapter<PayTypeModel> {

    public PayTypeAdapter(Context context) {
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

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private TextView tvName;
        private ImageView imgSelect;

        private ViewHolder() {
            super(R.layout.item_pay_type);
        }

        @Override
        public void onBindView(int position) {
            initView();
            PayTypeModel model = getItem(position);
            tvName.setText(model.getShowname());
            if (model.isSelect()){
                imgSelect.setImageResource(R.mipmap.ic_list_select);
            }else {
                imgSelect.setImageResource(R.mipmap.ic_list_unselect);

            }
        }

        private void initView() {
            tvName = findViewById(R.id.tvName);
            imgSelect = findViewById(R.id.imgSelect);
        }
    }
}
