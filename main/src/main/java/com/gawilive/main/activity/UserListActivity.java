package com.gawilive.main.activity;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.adapter.BaseAdapter;
import com.gawilive.common.utils.MMKVUtils;
import com.gawilive.common.utils.OkHttp;
import com.gawilive.main.R;
import com.gawilive.main.adapter.UserListAdapter;
import com.gawilive.main.bean.TransferUserBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class UserListActivity extends AbsActivity {
    private com.scwang.smartrefresh.layout.SmartRefreshLayout refreshView;
    private androidx.recyclerview.widget.RecyclerView recyclerView;

    private UserListAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_list;
    }

    @Override
    protected void main() {
        super.main();
        initView();
    }

    private void initView() {
        setTitle("用户列表");
        refreshView = findViewById(R.id.refreshView);
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new UserListAdapter(this);
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                TransferUserBean bean = adapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("userData", new Gson().toJson(bean));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        recyclerView.setAdapter(adapter);
        getList();

    }

    private void getList() {
        Map<String, String> map = new HashMap<>();
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=userlist&uid=" + MMKVUtils.getPayUid(), map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {

                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 1) {
                    int count = object.get("count").getAsInt();
                    JsonArray array = object.getAsJsonArray("data");
                    List<TransferUserBean> list = new Gson().fromJson(array.toString(), new TypeToken<List<TransferUserBean>>() {
                    }.getType());
                    adapter.setData(list);
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }


}
