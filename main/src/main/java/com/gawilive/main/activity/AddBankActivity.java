package com.gawilive.main.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hjq.shape.view.ShapeTextView;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.utils.MMKVUtils;
import com.gawilive.common.utils.OkHttp;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;
import okhttp3.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddBankActivity extends AbsActivity {
    private EditText mEdBankName;
    private EditText mEdBankCode;
    private ShapeTextView mAddBank;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_bank;
    }

    @Override
    protected void main() {
        super.main();
        initView();
        setTitle(WordUtil.getString(R.string.string_add_bank));
    }

    private void initView() {
        mEdBankName = findViewById(R.id.edBankName);
        mEdBankCode = findViewById(R.id.edBankCode);
        mAddBank = findViewById(R.id.addBank);
        mAddBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBank();
            }
        });
    }

    private void addBank() {
        String bankName = mEdBankName.getText().toString().trim();
        String bankNo = mEdBankCode.getText().toString().trim();
        if (TextUtils.isEmpty(bankName)) {
            ToastUtil.show(mEdBankName.getHint());
            return;
        }
        if (TextUtils.isEmpty(bankNo)) {
            ToastUtil.show(mEdBankCode.getHint());
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("uid", MMKVUtils.getPayUid());
        map.put("name", bankName);
        map.put("card_no", bankNo);
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=addbank", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) {
                    ToastUtil.show(WordUtil.getString(R.string.string_add_success));
                    finish();
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }
}
