package com.gawilive.mall.activity;


import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.bean.AddressModel;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.mall.R;
import com.gawilive.mall.adapter.AreaSelectAreaAdapter;
import com.gawilive.mall.adapter.AreaSelectCityAdapter;
import com.gawilive.mall.adapter.AreaSelectProvinceAdapter;
import com.gawilive.mall.bean.SelectAreaModel;
import com.gawilive.mall.http.MallHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 地区选择
 */
public class AreaSelectActivity extends AbsActivity {
    private RecyclerView mRecyclerViewProvince;
    private RecyclerView mRecyclerViewCity;
    private RecyclerView mRecyclerViewArea;

    private AreaSelectProvinceAdapter mProvinceAdapter;

    private AreaSelectCityAdapter mCityAdapter;

    private AreaSelectAreaAdapter mAreaAdapter;

    private int checkProvincePosition = 0;

    private int checkCityPosition = 0;

    private int checkAreaPosition = 0;

    private TextView tvConfirm;

    private String selectResult;


    public static void start(Activity context, String result) {
        Intent intent = new Intent(context, AreaSelectActivity.class);
        intent.putExtra("selectResult", result);
        context.startActivityForResult(intent, 999);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_area_select;
    }


    @Override
    protected void main() {
        super.main();
        selectResult = getIntent().getStringExtra("selectResult");
        if (!TextUtils.isEmpty(selectResult)) {
            SelectAreaModel model = new Gson().fromJson(selectResult, SelectAreaModel.class);
            checkAreaPosition = model.getCheckAreaPosition();
            checkCityPosition = model.getCheckCityPosition();
            checkProvincePosition = model.getCheckProvincePosition();
        }
        initView();
        getData();
    }

    private void getData() {
        MallHttpUtil.getAddress(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<AddressModel> list = JSONArray.parseArray(Arrays.toString(info), AddressModel.class);
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setSelect(i == checkProvincePosition);
                    }
                    mProvinceAdapter.setData(list);

                    for (int i = 0; i < list.get(checkProvincePosition).getChildren().size(); i++) {
                        list.get(checkProvincePosition).getChildren().get(i).setSelect(i == checkCityPosition);
                    }
                    mCityAdapter.setData(list.get(checkProvincePosition).getChildren());

                    for (int i = 0; i < list.get(checkProvincePosition).getChildren().get(checkCityPosition).getChildren().size(); i++) {
                        list.get(checkProvincePosition).getChildren().get(checkCityPosition).getChildren().get(i).setSelect(i == checkCityPosition);
                    }
                    mAreaAdapter.setData(list.get(checkProvincePosition).getChildren().get(checkCityPosition).getChildren());
                }
            }
        });
    }

    private void initView() {
        setTitle("地区选择");
        mRecyclerViewProvince = findViewById(R.id.recyclerViewProvince);
        mRecyclerViewCity = findViewById(R.id.recyclerViewCity);
        mRecyclerViewArea = findViewById(R.id.recyclerViewArea);
        mProvinceAdapter = new AreaSelectProvinceAdapter(this);
        mProvinceAdapter.setOnItemClickListener((recyclerView, itemView, position) -> {
            for (int i = 0; i < mProvinceAdapter.getData().size(); i++) {
                if (i == position) {
                    mProvinceAdapter.getData().get(i).setSelect(true);
                    checkProvincePosition = position;
                } else {
                    mProvinceAdapter.getData().get(i).setSelect(false);
                }
            }
            mProvinceAdapter.notifyDataSetChanged();
            checkCityPosition = 0;
            List<AddressModel> cityList = mProvinceAdapter.getData().get(checkProvincePosition).getChildren();
            List<AddressModel> areaList = mProvinceAdapter.getData().get(checkProvincePosition).getChildren().get(checkCityPosition).getChildren();
            for (int i = 0; i < cityList.size(); i++) {
                cityList.get(i).setSelect(i == 0);
            }
            mCityAdapter.setData(cityList);
            for (int i = 0; i < areaList.size(); i++) {
                areaList.get(i).setSelect(i == 0);
            }
            mAreaAdapter.setData(areaList);
        });
        mCityAdapter = new AreaSelectCityAdapter(this);
        // 城市点击事件
        mCityAdapter.setOnItemClickListener((recyclerView, itemView, position) -> {
            for (int i = 0; i < mCityAdapter.getData().size(); i++) {
                if (i == position) {
                    mCityAdapter.getData().get(i).setSelect(true);
                    checkCityPosition = position;
                } else {
                    mCityAdapter.getData().get(i).setSelect(false);
                }
                mCityAdapter.notifyDataSetChanged();
            }
            List<AddressModel> areaList = mCityAdapter.getData().get(checkCityPosition).getChildren();
            for (int i = 0; i < areaList.size(); i++) {
                areaList.get(i).setSelect(i == 0);
            }
            mAreaAdapter.setData(areaList);

        });
        mAreaAdapter = new AreaSelectAreaAdapter(this);
        // 地区点击事件
        mAreaAdapter.setOnItemClickListener((recyclerView, itemView, position) -> {
            for (int i = 0; i < mAreaAdapter.getData().size(); i++) {
                if (i == position) {
                    mAreaAdapter.getData().get(i).setSelect(true);
                    checkAreaPosition = position;
                } else {
                    mAreaAdapter.getData().get(i).setSelect(false);
                }
                mAreaAdapter.notifyDataSetChanged();
            }

        });
        mRecyclerViewProvince.setAdapter(mProvinceAdapter);
        mRecyclerViewCity.setAdapter(mCityAdapter);
        mRecyclerViewArea.setAdapter(mAreaAdapter);

        tvConfirm = findViewById(R.id.tv_confirm);

        // 确认
        tvConfirm.setOnClickListener(v -> {
            SelectAreaModel model = new SelectAreaModel();
            model.setProvince(mProvinceAdapter.getData().get(checkProvincePosition).getProvince());
            model.setCity(mCityAdapter.getData().get(checkCityPosition).getCity());
            model.setArea(mAreaAdapter.getData().get(checkAreaPosition).getArea());
            model.setCode(mProvinceAdapter.getData().get(checkProvincePosition).getCode());
            model.setCheckAreaPosition(checkAreaPosition);
            model.setCheckCityPosition(checkCityPosition);
            model.setCheckProvincePosition(checkProvincePosition);
            String result = new Gson().toJson(model);
            Intent intent = new Intent();
            intent.putExtra("selectResult", result);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}
