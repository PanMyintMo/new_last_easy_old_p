package com.gawilive.common.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

public class MoneyWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    private final int digitsLength = 2;

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            String text = s.toString().trim();
            int len  = s.toString().length();
            //删除“.”后面超过2位后的数据
            if (text.contains(".")) {
                if (len - 1 - text.indexOf(".") > digitsLength) {
                    s.delete(text.indexOf(".") + digitsLength + 1, len);
                }
            }
            //如果"."在起始位置,则起始位置自动补0
            if (text == ".") {
                s.insert(0, "0");
            }
            //如果起始位置为0,且第二位跟的不是".",则无法后续输入
            if (text.startsWith("0") && text.length() > 1) {
                if (s.toString().substring(1, 2) != ".") {
                    s.replace(0, len, "0.");
                }
            }
        }
    }
}
