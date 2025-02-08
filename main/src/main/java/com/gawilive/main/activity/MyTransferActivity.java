package com.gawilive.main.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.lzj.pass.dialog.PayPassDialog;
import com.lzj.pass.dialog.PayPassView;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.adapter.BaseAdapter;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.utils.MMKVUtils;
import com.gawilive.common.utils.MoneyWatcher;
import com.gawilive.common.utils.OkHttp;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;
import com.gawilive.main.adapter.PayTypeAdapter;
import com.gawilive.main.bean.PayTypeModel;
import com.gawilive.main.bean.TransferUserBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class MyTransferActivity extends AbsActivity {
    private android.widget.FrameLayout flTop;
    private android.widget.TextView titleView;
    private android.widget.ImageView btnBack;
    private android.widget.TextView tvSelectPeople;
    private android.widget.ImageView userHead;
    private android.widget.LinearLayout llSelectUser;
    private android.widget.TextView tvNickName;
    private android.widget.TextView tvPhone;
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private com.hjq.shape.view.ShapeTextView tvLijCz;

    private PayTypeAdapter adapter;

    private EditText editText;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_transfer;
    }

    @Override
    protected void main() {
        super.main();
        initView();
    }

    private void initView() {
        flTop = findViewById(R.id.fl_top);
        titleView = findViewById(R.id.titleView);
        btnBack = findViewById(R.id.btn_back);
        tvSelectPeople = findViewById(R.id.tvSelectPeople);
        userHead = findViewById(R.id.user_head);
        llSelectUser = findViewById(R.id.llSelectUser);
        tvNickName = findViewById(R.id.tvNickName);
        tvPhone = findViewById(R.id.tvPhone);
        recyclerView = findViewById(R.id.recyclerView);
        tvLijCz = findViewById(R.id.tvLijCz);
        editText = findViewById(R.id.edMoney);

        tvSelectPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyTransferActivity.this, UserListActivity.class);
                startActivityForResult(intent, 999);
            }
        });

        llSelectUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyTransferActivity.this, UserListActivity.class);
                startActivityForResult(intent, 999);
            }
        });

        adapter = new PayTypeAdapter(this);
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    adapter.getItem(i).setSelect(i == position);
                }
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);
        editText.addTextChangedListener(new MoneyWatcher());
        tvLijCz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = editText.getText().toString().trim();
                if (TextUtils.isEmpty(money)) {
                    ToastUtil.show(editText.getHint());
                    return;
                }
                if (TextUtils.isEmpty(certName)) {
                    ToastUtil.show(WordUtil.getString(R.string.string_select_zzr));
                    return;
                }
                payDialog(money);

            }
        });
    }

    private void payDialog(final String money) {
        final PayPassDialog dialog = new PayPassDialog(this);
        dialog.getPayViewPass()
                .setRandomNumber(true)
                .setPayClickListener(new PayPassView.OnPayClickListener() {
                    @Override
                    public void onPassFinish(String passContent) {
                        //6位输入完成回调
                        transfer(passContent, money);
                    }

                    @Override
                    public void onPayClose() {
                        dialog.dismiss();
                        //关闭弹框
                    }

                    @Override
                    public void onPayForget() {
                        dialog.dismiss();
                        //点击忘记密码回调
                        startActivity(new Intent(MyTransferActivity.this, PayPassActivity.class));
                    }
                });
    }

    private String certName;
    private String certNo;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == RESULT_OK) {
            String userData = data.getStringExtra("userData");
            if (!TextUtils.isEmpty(userData)) {
                TransferUserBean bean = new Gson().fromJson(userData, TransferUserBean.class);
                tvSelectPeople.setVisibility(View.GONE);
                llSelectUser.setVisibility(View.VISIBLE);
                ImgLoader.displayAvatar(mContext, bean.getAvatar(), userHead);
                tvNickName.setText(bean.getUsername());
                tvPhone.setText(bean.getAccount());
                certName = bean.getCertname();
                certNo = bean.getCertno();
            }
        }
    }

    // 支付方式
    private void payType() {
        OkHttp.getAsync(CommonAppConfig.HOST2 + "?act=paytype", new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 1) {
                    JsonArray array = object.getAsJsonArray("data");
                    List<PayTypeModel> list = new Gson().fromJson(array.toString(), new TypeToken<List<PayTypeModel>>() {
                    }.getType());
                    adapter.setData(list);
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    private void transfer(String paypass, String money) {
        Map<String, String> map = new HashMap<>();
        map.put("uid", MMKVUtils.getPayUid());
        map.put("key", MMKVUtils.getKey());
        map.put("money", money);
        map.put("certname", certName);
        map.put("certno", certNo);
        map.put("paypwd", paypass);
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=transfer", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                String msg = object.get("msg").getAsString();
                if (code == 0) {
                    TransferSuccessfulActivity.start(MyTransferActivity.this, WordUtil.getString(R.string.string_zzcg));
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
