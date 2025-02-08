package com.gawilive.main.activity;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSONArray;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.main.R;
import com.gawilive.main.adapter.LotteryRecordAdapter;
import com.gawilive.main.bean.LotteryRecordModel;
import com.gawilive.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 开奖记录
 */
public class LotteryRecordActivity extends AbsActivity {
    private FrameLayout mFlTop;
    private TextView mTitleView;
    private ImageView mBtnBack;
    private SmartRefreshLayout mRefreshView;
    private RecyclerView mRecyclerView;

    private LotteryRecordAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lottery_record;
    }

    @Override
    protected void main() {
        super.main();
        initView();
        initData();
    }

    private void initView() {
        setTitle("Historical lottery records");
        mFlTop = findViewById(R.id.fl_top);
        mTitleView = findViewById(R.id.titleView);
        mBtnBack = findViewById(R.id.btn_back);
        mRefreshView = findViewById(R.id.refreshView);
        mRecyclerView = findViewById(R.id.recyclerView);
        adapter = new LotteryRecordAdapter(this);
        mRecyclerView.setAdapter(adapter);
    }

    private void initData() {
        getData();
    }

    private void getData() {
        MainHttpUtil.getUserOpenWinLog(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<LotteryRecordModel> list = JSONArray.parseArray(Arrays.toString(info), LotteryRecordModel.class);
                    adapter.setData(list);
                }
            }
        });
    }
}
