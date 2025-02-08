package com.gawilive.main.views;

import android.content.Context;
import android.view.ViewGroup;
import com.gawilive.main.R;

// 动态
public class MainActiveViewNewViewHolder extends AbsMainHomeParentViewHolder{

    public MainActiveViewNewViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_active_new;
    }

    @Override
    protected void loadPageData(int position) {

    }

    @Override
    protected int getPageCount() {
        return 0;
    }

    @Override
    protected String[] getTitles() {
        return new String[0];
    }
}
