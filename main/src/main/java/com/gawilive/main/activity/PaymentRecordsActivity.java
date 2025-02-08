package com.gawilive.main.activity;

import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSONArray;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;
import com.gawilive.main.adapter.PaymentRecordsAdapter;
import com.gawilive.main.bean.PaymentRecordsModel;
import com.gawilive.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 缴费记录
 */
public class PaymentRecordsActivity extends AbsActivity {
    private SmartRefreshLayout mRefreshView;
    private RecyclerView mRecyclerView;

    private PaymentRecordsAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_prize_details;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.new_36));
        initView();
        initData();
    }

    private void initView() {
        mRefreshView = findViewById(R.id.refreshView);
        mRecyclerView = findViewById(R.id.recyclerView);
        adapter = new PaymentRecordsAdapter(this);
        mRecyclerView.setAdapter(adapter);
    }

    private void initData() {
        MainHttpUtil.getDrawWinTrunLog(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<PaymentRecordsModel> list = JSONArray.parseArray(Arrays.toString(info), PaymentRecordsModel.class);
                    adapter.setData(list);
                }
            }
        });
    }


}
