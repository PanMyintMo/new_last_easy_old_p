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

// 实名认证
public class RealNameActivity extends AbsActivity {
    private EditText mEdRealName;
    private EditText mEdIdNo;
    private ShapeTextView mAddBank;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_real_name;
    }

    @Override
    protected void main() {
        super.main();
        initView();
    }

    private void initView() {
        setTitle(WordUtil.getString(R.string.string_smrz));
        mEdRealName = findViewById(R.id.edRealName);
        mEdIdNo = findViewById(R.id.edIdNo);
        mAddBank = findViewById(R.id.addBank);
        mAddBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
    }

    private void commit() {
        String realName = mEdRealName.getText().toString().trim();
        String edNo = mEdIdNo.getText().toString().trim();
        if (TextUtils.isEmpty(realName)) {
            ToastUtil.show(mEdRealName.getHint());
            return;
        }
        if (TextUtils.isEmpty(edNo)) {
            ToastUtil.show(mEdIdNo.getHint());
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("pid", MMKVUtils.getPayUid());
        map.put("key", MMKVUtils.getKey());
        map.put("cert", "1");
        map.put("certtype", "0");
        map.put("certmethod", "0");
        map.put("certname", realName);
        map.put("certno", edNo);
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=realName", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                String msg = object.get("msg").getAsString();
                if (code == 0) {
                    finish();
                }
                ToastUtil.show(msg);
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }
}
