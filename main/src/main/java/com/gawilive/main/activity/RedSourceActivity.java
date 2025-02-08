package com.gawilive.main.activity;

import android.view.View;
import android.widget.LinearLayout;

import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;
import com.gawilive.main.adapter.RedSourceAdapter;
import com.gawilive.main.bean.RedSourceModel;
import com.gawilive.main.http.MainHttpUtil;
import com.gawilive.main.http.MainHttpUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.Arrays;
import java.util.List;

/**
 * 红积分
 */
public class RedSourceActivity extends AbsActivity {

    private SmartRefreshLayout refreshLayout;

    private LinearLayout llNotData;

    private RecyclerView recyclerView;

    private RedSourceAdapter adapter;

    private TextView mRightView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_red_source;
    }

    @Override
    protected void main() {
        super.main();
        initView();
    }

    private void initView() {
        setTitle(WordUtil.getString(R.string.new_69));
        refreshLayout = findViewById(R.id.refreshView);
        llNotData = findViewById(R.id.llNotData);
        recyclerView = findViewById(R.id.recyclerView);
        mRightView = findViewById(R.id.tvRightTitle);
        adapter = new RedSourceAdapter(this);
        recyclerView.setAdapter(adapter);
        getData();

        mRightView.setOnClickListener(v -> {
            RuleActivity.start(this, 0);
        });
    }

    private void getData() {
        MainHttpUtil.getRedScore(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<RedSourceModel> list = JSONArray.parseArray(Arrays.toString(info), RedSourceModel.class);
                    adapter.setData(list);
                    if (adapter.getCount() > 0) {
                        llNotData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        llNotData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    llNotData.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
