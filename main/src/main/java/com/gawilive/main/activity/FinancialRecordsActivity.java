package com.gawilive.main.activity;

import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.utils.MMKVUtils;
import com.gawilive.common.utils.OkHttp;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;
import com.gawilive.main.adapter.FinancialRecordsAdapter;
import com.gawilive.main.bean.TransferListModel;
import okhttp3.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 资金记录
public class FinancialRecordsActivity extends AbsActivity {

    private FinancialRecordsAdapter adapter;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayout mLlNotData;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_financial_records;
    }

    @Override
    protected void main() {
        super.main();
        initView();
    }

    private void initView() {
        setTitle(WordUtil.getString(R.string.string_zzjl));
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FinancialRecordsAdapter(this);
        mRecyclerView.setAdapter(adapter);
        mLlNotData = findViewById(R.id.llNotData);

        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page++;
                getData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 0;
                getData();
            }
        });
        getData();


    }

    private int page = 0;

    private final int size = 10;

    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("uid", MMKVUtils.getPayUid());
        map.put("limit", String.valueOf(size));
        map.put("offset", String.valueOf(page));

        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=transfer_list", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 1) {
                    JsonArray array = object.getAsJsonArray("data");
                    List<TransferListModel> list = new Gson().fromJson(array.toString(), new TypeToken<List<TransferListModel>>() {
                    }.getType());

                    if (page == 0) {
                        adapter.setData(list);
                    } else {
                        adapter.addData(list);
                    }
                    if (adapter.getData().size() > 0) {
                        mLlNotData.setVisibility(View.VISIBLE);
                    } else {
                        mLlNotData.setVisibility(View.GONE);
                    }
                }

                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();

            }

            @Override
            public void requestFailure(Request request, IOException e) {
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
            }

        });
    }
}
