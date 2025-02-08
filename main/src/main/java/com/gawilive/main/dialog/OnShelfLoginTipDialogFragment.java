package com.gawilive.main.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.activity.WebViewActivity;
import com.gawilive.common.dialog.AbsDialogFragment;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;

/**
 * 上架提示弹窗
 */
    public class OnShelfLoginTipDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private TextView mTitle;
    private TextView mContent;
    private Runnable mOnConfirmClick;

    private ImageView mLoginCheckBox;
    private boolean mChecked=true;
    private Drawable mCheckedDrawable;
    private Drawable mUnCheckedDrawable;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_login_tip_shelf;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(280);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            });
        }
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        mTitle = findViewById(R.id.title);
        mContent = findViewById(R.id.content);

        if (mTitle != null) {
            mTitle.setText(WordUtil.getString(R.string.login_tip_001));
        }
        String content = WordUtil.getString(R.string.login_tip_002);
        String loginTip = WordUtil.getString(R.string.login_tip_003);

        SpannableString spannableContent = new SpannableString(content);
        SpannableString spannableTip = new SpannableString(loginTip);
        JSONArray msgArray = new JSONArray();
        JSONObject obj0 = new JSONObject();
        obj0.put("title", WordUtil.getString(R.string.login_tip_004));
        obj0.put("url", "https://yszc.ezwel.live/");
        JSONObject obj1 = new JSONObject();
        obj1.put("title", WordUtil.getString(R.string.login_tip_005));
        obj1.put("url", "https://yszc.ezwel.live/");
        msgArray.add(obj0);
        msgArray.add(obj1);

        for (int i = 0, size = msgArray.size(); i < size; i++) {

            final JSONObject msgItem = msgArray.getJSONObject(i);
            String title = msgItem.getString("title");
            ClickableSpan clickableSpan = new ClickableSpan() {

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(0xff3399ee);
                    ds.setUnderlineText(false);
                }

                @Override
                public void onClick(View widget) {
                    WebViewActivity.forward(mContext, "https://yszc.ezwel.live/");
                }
            };



            int startIndex = content.indexOf(title);
            if (startIndex >= 0) {
                int endIndex = startIndex + title.length();
                spannableContent.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }


            int startIndex2 = loginTip.indexOf(title);
            if (startIndex2 >= 0) {
                int endIndex2 = startIndex2 + title.length();
                spannableTip.setSpan(clickableSpan, startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }


        mContent.setText(spannableContent);
        mContent.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
        mContent.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明

        TextView loginTipTextView = findViewById(R.id.login_tip);
        loginTipTextView.setText(spannableTip);
        loginTipTextView.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
        loginTipTextView.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明

        mCheckedDrawable = ContextCompat.getDrawable(mContext, R.mipmap.bg_login_check_1);
        mUnCheckedDrawable = ContextCompat.getDrawable(mContext, R.mipmap.bg_login_check_0);
        mLoginCheckBox = findViewById(R.id.btn_login_check);
        mLoginCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChecked = !mChecked;
                mLoginCheckBox.setImageDrawable(mChecked ? mCheckedDrawable : mUnCheckedDrawable);
            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cancel) {
            dismiss();
            if (mContext != null) {
                ((AppCompatActivity) mContext).finish();
            }
        } else if (id == R.id.btn_confirm) {
            if (!mChecked) {
                ToastUtil.show(R.string.login_check_tip);
                return;
            }
            dismiss();
            if (mOnConfirmClick != null) {
                mOnConfirmClick.run();
            }
        }
    }



    public void setOnConfirmClick(Runnable runnable) {
        mOnConfirmClick = runnable;
    }
}
