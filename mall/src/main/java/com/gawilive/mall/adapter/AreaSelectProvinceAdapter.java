package com.gawilive.mall.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.common.bean.AddressModel;
import com.gawilive.mall.R;

public class AreaSelectProvinceAdapter extends AppAdapter<AddressModel> {

    public AreaSelectProvinceAdapter(Context context) {
        super(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView tvCity;

        private ViewHolder() {
            super(R.layout.item_area_select);
            tvCity = findViewById(R.id.tvCity);
        }

        @Override
        public void onBindView(int position) {
            AddressModel model = getItem(position);
            tvCity.setText(model.getProvince());
            if (model.isSelect()){
                tvCity.setTextColor(getResources().getColor(R.color.colorAccent));
            }else {
                tvCity.setTextColor(getResources().getColor(R.color.textColor));
            }
        }
    }
}
