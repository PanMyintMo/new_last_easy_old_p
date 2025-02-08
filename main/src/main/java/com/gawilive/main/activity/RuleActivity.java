package com.gawilive.main.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.main.R;
import com.gawilive.main.bean.RuleBean;
import com.gawilive.main.http.MainHttpUtil;

// 积分规则
public class RuleActivity extends AbsActivity {

    private int type = 0;

    private TextView tvContent;


    public static void start(Context context, int type) {
        Intent intent = new Intent(context, RuleActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rule;
    }

    @Override
    protected void main() {
        super.main();
        tvContent = findViewById(R.id.tvContent);
        type = getIntent().getIntExtra("type", 0);

        if (type == 1) {
            getGreenRule();
        } else {
            getRedRule();
        }
    }

    // 红色规则
    private void getRedRule() {
        MainHttpUtil.getRedScoreRule(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    RuleBean bean = JSONObject.parseObject(info[0], RuleBean.class);
                    setTitle(bean.getPost_title());
                    tvContent.setText(bean.getPost_content());
                }
            }
        });
    }

    // 绿色规则
    private void getGreenRule() {
        MainHttpUtil.getGreenScoreRule(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    RuleBean bean = JSONObject.parseObject(info[0], RuleBean.class);
                    setTitle(bean.getPost_title());
                    String bulletPointContent =  bean.getPost_content().replace("111", "") + "\n"
                            + "• EZLIVE တွင် 7 ရက် ပြည့်တိုင်း မဲတစ်ခါနှိုက်မည်။ \n"
                            + "• Diamond 49 ပေးပြီး မဲတစ်ကြိမ် နှိုက်နိုင်မည်။ \n"
                            + "• နေ့စဉ် Login ၀င်ပြီးစိန်တလုံးရယူပါ။\n"
                            + "• live သုံးမိနစ်ကြည့်ပီး စိန်တလုံး ရယူပါ။ စုစုပေါင်းစိန်သုံးလုံးရယူပါ။\n"
                            + "• user များသည် 7 ရက်လျှင် စိန် 49 လုံး ရရှိန်ုင်ပါသည်။ \n"

                            + "• ပါ၀င်သူ အရေအတွက်များလာသည်နှင့်အမျှ ဆုလက်ဆောင်များလည်း တိုးလာန်ုင်သည်။\n"

                            + "* စည်းကမ်းမှတ်ချက် : Free Diamond ကို Account register လုပ်ပြီး မှ ရယူနိုင်ပါသည်။ ";



                    tvContent.setText(bulletPointContent);
                }
            }
        });
    }
}
