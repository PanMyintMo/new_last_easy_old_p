package com.gawilive.main.dialog;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gawilive.common.Constants;
import com.gawilive.common.dialog.AbsDialogFragment;
import com.gawilive.common.http.HttpClient;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.common.utils.SpUtil;
import com.gawilive.main.R;
import com.gawilive.main.activity.MainActivity;

/**
 * 语言切换
 *
 * @Author: Sunday
 * @Date: 2023/3/6 10:53
 */
public class LanguageDialogFragment extends AbsDialogFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_language;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(300);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListener();
    }

    private void initListener() {
        findViewById(R.id.zh_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpUtil.getInstance().setStringValue(SpUtil.LANGUAGE, Constants.CHINESE_CODE);
                restart();
                dismiss();
            }
        });

        findViewById(R.id.en_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpUtil.getInstance().setStringValue(SpUtil.LANGUAGE, Constants.ENGLISH_CODE);
                restart();
                dismiss();
            }
        });

        findViewById(R.id.arabic_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtil.getInstance().setStringValue(SpUtil.LANGUAGE, Constants.AR_CODE);
                restart();
                dismiss();
            }
        });
        findViewById(R.id.burmese_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtil.getInstance().setStringValue(SpUtil.LANGUAGE, Constants.MY_CODE);
                restart();
                dismiss();
            }
        });
        findViewById(R.id.thai_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtil.getInstance().setStringValue(SpUtil.LANGUAGE, Constants.TH_CODE);
                restart();
                dismiss();
            }
        });
    }

    private void restart() {
        setLanguageCode();
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out);
        this.dismiss();
//        try {
//
//           // intent.setClass(getActivity(), Class.forName("com.gawilive.phonelive.activity.LauncherActivity"));
//
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }

    }

    // 设置请求语言的标识
    private void setLanguageCode() {
        String language = SpUtil.getInstance().getStringValue(SpUtil.LANGUAGE);
        if (language.contains(Constants.CHINESE_CODE)) {
            HttpClient.getInstance().setLanguage(Constants.CHINESE_CODE);
        } else if (language.contains(Constants.AR_CODE)) {
            HttpClient.getInstance().setLanguage(Constants.AR_CODE);
        } else if (language.contains(Constants.TH_CODE)) {
            HttpClient.getInstance().setLanguage(Constants.TH_CODE);
        } else if (language.contains(Constants.MY_CODE)) {
            HttpClient.getInstance().setLanguage(Constants.MY_CODE);
        } else {
            HttpClient.getInstance().setLanguage(Constants.ENGLISH_CODE);
        }
    }
}
