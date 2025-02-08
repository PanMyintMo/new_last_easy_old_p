package com.gawilive.mall.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.StringUtil;
import com.gawilive.mall.R;
import com.gawilive.mall.adapter.LogisticsDetailsAdapter;
import com.gawilive.mall.bean.FindOrderStatusModel;
import com.gawilive.mall.bean.LogisticsModel;
import com.gawilive.mall.http.MallHttpUtil;

import java.util.Arrays;
import java.util.List;

public class LogisticsDetailsActivity extends AbsActivity {

    private RecyclerView recyclerView;

    private LogisticsDetailsAdapter adapter;

    private String billCode;

    public static void start(Context context, String billCode) {
        Intent intent = new Intent(context, LogisticsDetailsActivity.class);
        intent.putExtra("billCode", billCode);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_logistics_details;
    }

    @Override
    protected void main() {
        super.main();
        setTitle("物流轨迹");
        billCode = getIntent().getStringExtra("billCode");
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new LogisticsDetailsAdapter(this);
        recyclerView.setAdapter(adapter);
        openFindOrderStatus();
    }

    // 查询轨迹
    private void openFindTrack() {
        MallHttpUtil.openFindTrack(billCode, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    Log.d("返回数据", info.toString());
                    List<LogisticsModel> list = JSON.parseArray(Arrays.toString(info), LogisticsModel.class);
                    adapter.addData(list);
                }
            }
        });
    }

    // 查询订单状态
    private void openFindOrderStatus() {
        MallHttpUtil.openFindOrderStatus(billCode, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    Log.d("返回数据", obj.toJSONString());
                    FindOrderStatusModel model = JSON.parseObject(obj.toJSONString(), FindOrderStatusModel.class);
                    LogisticsModel logisticsModel = new LogisticsModel();
                    logisticsModel.setRemark(model.getOrderStatusName());
                    logisticsModel.setAcceptAddress(model.getOrderStatusName());
                    logisticsModel.setAcceptTime(model.getOrderTime());
                    if ("2".equals(model.getOrderStatus())) {
                        openFindTrack();
                    }else {
                        adapter.addItem(logisticsModel);
                    }

                }
            }
        });
    }


}
