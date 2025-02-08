package com.gawilive.main.activity;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.dialog.NotLoginDialogFragment;
import com.gawilive.main.R;
import com.gawilive.main.views.MainListViewHolder;

/**
 * 排行榜
 */
public class RankActivity extends AbsActivity {

    public static void forward(Context context, int position) {
        Intent intent = new Intent(context, RankActivity.class);
        intent.putExtra(Constants.LIVE_POSITION, position);
        context.startActivity(intent);
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rank;
    }

    @Override
    protected void main() {
        int position = getIntent().getIntExtra(Constants.LIVE_POSITION, 0);
        MainListViewHolder viewHolder = new MainListViewHolder(mContext, findViewById(R.id.container));
        viewHolder.addToParent();
        viewHolder.subscribeActivityLifeCycle();
        viewHolder.loadData(position);
    }

    /**
     * 未登录的弹窗
     */
    @Override
    public void showNotLoginDialog() {
        NotLoginDialogFragment fragment = new NotLoginDialogFragment();
        fragment.setActionListener(new NotLoginDialogFragment.ActionListener() {
            @Override
            public void beforeForwardLogin() {
                finish();
            }
        });
        fragment.show(getSupportFragmentManager(), "NotLoginDialogFragment");
    }
}
