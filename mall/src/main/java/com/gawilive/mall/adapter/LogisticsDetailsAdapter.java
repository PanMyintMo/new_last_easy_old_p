package com.gawilive.mall.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.mall.R;
import com.gawilive.mall.bean.LogisticsModel;
import com.hjq.shape.view.ShapeTextView;
import com.hjq.shape.view.ShapeView;

/**
 * 物流展示
 */
public class LogisticsDetailsAdapter extends AppAdapter<LogisticsModel> {


    public LogisticsDetailsAdapter(Context context) {
        super(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }


    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private ShapeTextView mIcon;
        private ShapeTextView mLine;
        private TextView mTvRemark;
        private TextView mTvAcceptTime;
        private TextView mTvAcceptAddress;


        private ViewHolder() {
            super(R.layout.item_logistics_details);
            mIcon = findViewById(R.id.icon);
            mLine = findViewById(R.id.line);
            mTvRemark = findViewById(R.id.tvRemark);
            mTvAcceptTime = findViewById(R.id.tvAcceptTime);
            mTvAcceptAddress = findViewById(R.id.tvAcceptAddress);
        }

        @Override
        public void onBindView(int position) {
            LogisticsModel model = getItem(position);
            if (position == 0) {
                mIcon.getShapeDrawableBuilder().setSolidColor(getContext().getResources().getColor(R.color.colorAccent)).intoBackground();
                mTvRemark.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
                mTvAcceptTime.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
                mTvAcceptAddress.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            } else {
                mIcon.getShapeDrawableBuilder().setSolidColor(Color.parseColor("#999999")).intoBackground();
                mTvRemark.setTextColor(Color.parseColor("#000000"));
                mTvAcceptTime.setTextColor(Color.parseColor("#333333"));
                mTvAcceptAddress.setTextColor(Color.parseColor("#333333"));
            }
            mTvRemark.setText(model.getRemark());
            mTvAcceptTime.setText(model.getAcceptTime());
            mTvAcceptAddress.setText(Html.fromHtml(model.getAcceptAddress()));


        }
    }
}