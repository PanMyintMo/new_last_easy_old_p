package com.gawilive.main.bean;

import com.gawilive.common.bean.UserBean;

public class GoodsShareUserBean extends UserBean {

    private boolean mChecked;

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
