package com.gawilive.main.activity;

import android.content.Intent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.hjq.shape.view.ShapeTextView;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.adapter.BaseAdapter;
import com.gawilive.common.utils.OkHttp;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;
import com.gawilive.main.adapter.MyBankListAdapter;
import com.gawilive.main.bean.BankModel;
import okhttp3.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 我的银行卡
public class MyBankListActivity extends AbsActivity {
    private RecyclerView mRecyclerView;


    private MyBankListAdapter adapter;

    private ShapeTextView tvAddBank;

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
        setTitle(WordUtil.getString(R.string.string_my_bank));
        mRecyclerView = findViewById(R.id.recyclerView);
        tvAddBank = findViewById(R.id.tvAddBank);
        adapter = new MyBankListAdapter(this);
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                Intent intent=new Intent();
                intent.putExtra("bankData",new Gson().toJson(adapter.getItem(position)));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        mRecyclerView.setAdapter(adapter);

        tvAddBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddBankActivity.class);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBank();
    }

    private void getBank() {
        Map<String, String> map = new HashMap<>();
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=banklist", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 1) {
                    JsonArray array = object.getAsJsonArray("data");
                    List<BankModel> list = new Gson().fromJson(array.toString(), new TypeToken<List<BankModel>>() {
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
