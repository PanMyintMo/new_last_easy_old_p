package com.gawilive.mall.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.utils.CityUtil;
import com.gawilive.common.utils.DialogUitl;
import com.gawilive.common.utils.StringUtil;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.mall.R;
import com.gawilive.mall.bean.SelectAreaModel;
import com.gawilive.mall.http.MallHttpUtil;

import java.util.ArrayList;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import com.google.gson.Gson;

/**
 * 卖家 编辑退货地址
 */
public class SellerAddressEditActivity extends AbsActivity implements View.OnClickListener {

    private EditText mRefundName;//退货人名字
    private EditText mRefundPhoneNum;//退货人手机号
    private TextView mRefundArea;//退货人所在地区
    private EditText mRefundAddress;//退货人详细地址

    private String mRefundProvinceVal;//退货人所在省
    private String mRefundCityVal;//退货人所在市
    private String mRefundZoneVal;//退货人所在区


    @Override
    protected int getLayoutId() {
        return R.layout.activity_seller_address_edit;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.mall_149));
        mRefundName = findViewById(R.id.refund_name);//退货人名字
        mRefundPhoneNum = findViewById(R.id.refund_phone_num);//退货人手机号
        mRefundArea = findViewById(R.id.refund_area);//退货人所在地区
        mRefundAddress = findViewById(R.id.refund_address);//退货人详细地址
        findViewById(R.id.btn_refund_area).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        Intent intent = getIntent();
        mRefundName.setText(intent.getStringExtra(Constants.MALL_REFUND_NAME));
        mRefundPhoneNum.setText(intent.getStringExtra(Constants.MALL_REFUND_PHONE));
        mRefundAddress.setText(intent.getStringExtra(Constants.MALL_REFUND_ADDRESS));
        mRefundProvinceVal = intent.getStringExtra(Constants.MALL_REFUND_PROVINCE);
        mRefundCityVal = intent.getStringExtra(Constants.MALL_REFUND_CITY);
        mRefundZoneVal = intent.getStringExtra(Constants.MALL_REFUND_ZONE);
        mRefundArea.setText(StringUtil.contact(mRefundProvinceVal, " ", mRefundCityVal, " ", mRefundZoneVal));


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_refund_area) {
            chooseCity();
        } else if (id == R.id.btn_submit) {
            submit();
        }

    }


    /**
     * 选择地区
     */
    private void chooseCity() {

        AreaSelectActivity.start(this, selectResult);
    }

    private String selectResult;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 999) {
            selectResult = data.getStringExtra("selectResult");
            SelectAreaModel model = new Gson().fromJson(selectResult, SelectAreaModel.class);
            mRefundProvinceVal = model.getProvince();
            mRefundCityVal = model.getCity();
            mRefundZoneVal = model.getArea();
            mRefundArea.setText(StringUtil.contact(model.getProvince(), " ", model.getCity(), " ", model.getArea()));
        }
    }



    private void submit() {
        String name = mRefundName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.show(R.string.mall_150);
            return;
        }
        String phoneNum = mRefundPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.show(R.string.mall_151);
            return;
        }
        String area = mRefundArea.getText().toString().trim();
        if (TextUtils.isEmpty(area)) {
            ToastUtil.show(R.string.mall_152);
            return;
        }
        String address = mRefundAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            ToastUtil.show(R.string.mall_153);
            return;
        }

        MallHttpUtil.modifyRefundAddress(
                name,
                phoneNum,
                mRefundProvinceVal,
                mRefundCityVal,
                mRefundZoneVal,
                address,
                new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            setResult(RESULT_OK);
                            finish();
                        }
                        ToastUtil.show(msg);
                    }
                }
        );

    }
}
