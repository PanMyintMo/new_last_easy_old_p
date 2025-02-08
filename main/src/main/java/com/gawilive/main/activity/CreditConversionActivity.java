package com.gawilive.main.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.bean.UserBean;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.utils.MMKVUtils;
import com.gawilive.common.utils.OkHttp;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;
import com.gawilive.main.bean.PayUserBean;
import com.gawilive.main.http.MainHttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hjq.shape.view.ShapeTextView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import okhttp3.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 额度转换
 */
public class CreditConversionActivity extends AbsActivity {


    private TextView mTvMallBalance;
    private TextView mTvWattleBalance;
    private RelativeLayout mLlConversionType;
    private TextView mTvConversionType;
    private EditText mEdMoney;
    private ShapeTextView mTvLijCz;

    private int type;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_credit_conversion;
    }

    @Override
    protected void main() {
        super.main();
        initView();
    }

    private void showTypeDialog() {
        String[] options = {WordUtil.getString(R.string.new_61), WordUtil.getString(R.string.new_62)};
        new XPopup.Builder(this)
                .asBottomList(WordUtil.getString(R.string.new_59), options, (position, text) -> {
                    mTvConversionType.setText(text);
                    type = (position + 1);
                }).show();
    }

    private void initView() {
        mTvMallBalance = findViewById(R.id.tvMallBalance);
        mTvWattleBalance = findViewById(R.id.tvWattleBalance);
        mLlConversionType = findViewById(R.id.llConversionType);
        mTvConversionType = findViewById(R.id.tvConversionType);
        mEdMoney = findViewById(R.id.edMoney);
        mTvLijCz = findViewById(R.id.tvLijCz);
        mLlConversionType.setOnClickListener(v -> {
            showTypeDialog();
        });
        mTvLijCz.setOnClickListener(v -> {
            commitData();
        });

        getBaseInfo();
        getWattleInfo();
    }

    // 数据提交
    private void commitData() {
        String money = mEdMoney.getText().toString().trim();
        if (type < 1) {
            ToastUtil.show(WordUtil.getString(R.string.new_59));
            return;
        }
        if (TextUtils.isEmpty(money)) {
            ToastUtil.show(mEdMoney.getHint());
            return;
        }
        convert(money);
    }

    private void getBaseInfo() {
        MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                mTvMallBalance.setText(bean.getBalance());
            }
        });
    }

    // 获取钱包信息
    private void getWattleInfo() {
        OkHttp.getAsync(CommonAppConfig.HOST2 + "?act=findUser&phone=" + CommonAppConfig.getInstance().getUserBean().getMobile(), new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                JsonObject dataObject = object.getAsJsonObject("data");
                PayUserBean bean = new Gson().fromJson(dataObject.toString(), PayUserBean.class);
                mTvWattleBalance.setText(bean.getMoney());
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    private void convert(String money) {
        Map<String, String> map = new HashMap<>();
        map.put("live_user_id", CommonAppConfig.getInstance().getUid());
        map.put("purse_uid", MMKVUtils.getPayUid());
        map.put("money", money);
        map.put("type", String.valueOf(type));
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=syncShopBalance", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                String msg = object.get("msg").getAsString();
                ToastUtil.show(msg);
                if (code == 0) {
                    mTvWattleBalance.setText("");
                    getBaseInfo();
                    getWattleInfo();
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }
}
