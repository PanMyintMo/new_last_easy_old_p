package com.gawilive.main.activity;

import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSONArray;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.main.R;
import com.gawilive.main.adapter.PrizeDetailsAdapter;
import com.gawilive.main.bean.PrizeDetailsModel;
import com.gawilive.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

// 奖品详情
public class PrizeDetailsActivity extends AbsActivity {
    private SmartRefreshLayout mRefreshView;
    private RecyclerView mRecyclerView;

    private PrizeDetailsAdapter adapter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_prize_details;
    }

    @Override
    protected void main() {
        super.main();
        initView();
        getData();
    }


    private void getData() {
        MainHttpUtil.getUserWinTrue(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<PrizeDetailsModel> list = JSONArray.parseArray(Arrays.toString(info), PrizeDetailsModel.class);
                    adapter.setData(list);
                }
            }
        });
    }

    private void initView() {
        mRefreshView = findViewById(R.id.refreshView);
        mRecyclerView = findViewById(R.id.recyclerView);
        adapter = new PrizeDetailsAdapter(this);
        mRecyclerView.setAdapter(adapter);
    }
}
