package com.gawilive.main.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.hjq.shape.view.ShapeTextView;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.main.R;

// 转账成功
public class TransferSuccessfulActivity extends AbsActivity {


    private TextView mTvTile;
    private ShapeTextView mTvBack;

    private String title;

    public static void start(Context context, String title) {
        Intent intent = new Intent(context, TransferSuccessfulActivity.class);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_successful;
    }

    @Override
    protected void main() {
        super.main();
        initView();
    }

    private void initView() {
        title = getIntent().getStringExtra("title");
        mTvTile = findViewById(R.id.tvTile);
        mTvBack = findViewById(R.id.tvBack);
        mTvTile.setText(title);
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
