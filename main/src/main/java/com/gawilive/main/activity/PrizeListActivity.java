package com.gawilive.main.activity;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;
import com.gawilive.main.adapter.PrizeListAdapter;
import com.gawilive.main.bean.PrizeListModel;
import com.gawilive.main.http.MainHttpUtil;

import java.util.List;

/**
 * 奖品列表
 */
public class PrizeListActivity extends AbsActivity {
    private RecyclerView mRecyclerView;

    private PrizeListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_prize_list;
    }

    @Override
    protected void main() {
        super.main();
        initView();
        initData();
    }

    private void initView() {
        setTitle(WordUtil.getString(R.string.new_37));
        mRecyclerView = findViewById(R.id.recyclerView);
        mAdapter = new PrizeListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        getData();
    }

    private void getData() {
        MainHttpUtil.getTurntable(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    JSONArray array = obj.getJSONArray("list");
                    List<PrizeListModel> list = JSON.parseArray(array.toJSONString(), PrizeListModel.class);
                    mAdapter.setData(list);
                }
            }
        });
    }
}
