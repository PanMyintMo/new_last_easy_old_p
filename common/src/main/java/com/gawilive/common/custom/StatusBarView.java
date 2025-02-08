package com.gawilive.common.custom;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.gawilive.common.utils.ScreenDimenUtil;


/**
 * Created by 云豹科技 on 2021/10/22.
 */
public class StatusBarView extends View {


    public StatusBarView(Context context) {
        super(context);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(ScreenDimenUtil.getInstance().getStatusBarHeight(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
