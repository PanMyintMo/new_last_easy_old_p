package com.gawilive.main.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hjq.shape.view.ShapeTextView;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.utils.MMKVUtils;
import com.gawilive.common.utils.OkHttp;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.main.R;
import okhttp3.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PayPassActivity extends AbsActivity {
    private FrameLayout mFlTop;
    private TextView mTitleView;
    private ImageView mBtnBack;
    private EditText mEdPayPass;
    private EditText mEdConfirmPayPass;
    private ShapeTextView mBtnConfirm;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay_pass;
    }

    @Override
    protected void main() {
        super.main();
        initView();
    }

    private void initView() {
        mFlTop = findViewById(R.id.fl_top);
        mTitleView = findViewById(R.id.titleView);
        mBtnBack = findViewById(R.id.btn_back);
        mEdPayPass = findViewById(R.id.edPayPass);
        mEdConfirmPayPass = findViewById(R.id.edConfirmPayPass);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mTitleView.setText("Set payment password");

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
    }

    private void confirm() {
        String pass1 = mEdPayPass.getText().toString().trim();
        final String pass2 = mEdConfirmPayPass.getText().toString().trim();
        if (TextUtils.isEmpty(pass1)) {
            ToastUtil.show(mEdPayPass.getHint());
            return;
        }
        if (TextUtils.isEmpty(pass2)) {
            ToastUtil.show(mEdConfirmPayPass.getHint());
            return;
        }
        if (!pass2.equals(pass1)) {
            ToastUtil.show("The two password inputs are inconsistent, please re-enter.");
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("uid", MMKVUtils.getPayUid());
        map.put("key", MMKVUtils.getKey());
        map.put("paypwd", pass2);
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=paypwd", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) { // 进行信息同步
                    MMKVUtils.setPayPwd(pass2);
                    ToastUtil.show("Setup successful");
                    finish();
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });

    }
}
