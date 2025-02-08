package com.gawilive.game.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gawilive.game.R;

/**
 * Created by cxf on 2017/10/17.
 * 游戏下注记录
 */

public class GameBetCoinView extends LinearLayout {

    private final Context mContext;
    private TextView mNameTextView;
    private TextView mTotalBet;
    private TextView mMyBet;
    private final String mName;
    private final int mTextColor;
    private final float mTextSize;
    private final int mMarginTop1;
    private final int mMarginTop2;
    private final int mMarginTop3;
    private int mTotalBetVal;
    private int mMyBetVal;
    private final String mWan;

    public GameBetCoinView(Context context) {
        this(context, null);
    }

    public GameBetCoinView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameBetCoinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GameBetCoinView);
        mName = ta.getString(R.styleable.GameBetCoinView_gbc_name);
        mTextColor = ta.getColor(R.styleable.GameBetCoinView_gbc_textColor, 0xff333333);
        mTextSize = ta.getDimension(R.styleable.GameBetCoinView_gbc_textSize, 0);
        mMarginTop1 = (int) ta.getDimension(R.styleable.GameBetCoinView_gbc_marginTop1, 0);
        mMarginTop2 = (int) ta.getDimension(R.styleable.GameBetCoinView_gbc_marginTop2, 0);
        mMarginTop3 = (int) ta.getDimension(R.styleable.GameBetCoinView_gbc_marginTop3, 0);
        ta.recycle();
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        mWan = context.getString(R.string.game_wan);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mNameTextView = new TextView(mContext);
        LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0, mMarginTop1, 0, 0);
        mNameTextView.setLayoutParams(params1);
        mNameTextView.setText(mName);
        mNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mNameTextView.setTextColor(mTextColor);
        addView(mNameTextView);

        mTotalBet = new TextView(mContext);
        LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.setMargins(0, mMarginTop2, 0, 0);
        mTotalBet.setLayoutParams(params2);
        mTotalBet.setText(String.valueOf(mTotalBetVal));
        mTotalBet.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mTotalBet.setTextColor(mTextColor);
        addView(mTotalBet);

        mMyBet = new TextView(mContext);
        LayoutParams params3 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params3.setMargins(0, mMarginTop3, 0, 0);
        mMyBet.setLayoutParams(params3);
        mMyBet.setText(String.valueOf(mMyBetVal));
        mMyBet.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mMyBet.setTextColor(mTextColor);
        addView(mMyBet);
    }

    public void updateBetVal(int betVal, boolean isSelf) {
        mTotalBetVal += betVal;
        showTotalBet();
        if (isSelf) {
            mMyBetVal += betVal;
            showSelfBet();
        }
    }

    public void setBetVal(int totalBet, int myBet) {
        mTotalBetVal = totalBet;
        mMyBetVal = myBet;
        showTotalBet();
        showSelfBet();
    }

    /**
     * 显示下注总金额
     */
    private void showTotalBet() {
        String result;
        if (mTotalBetVal >= 10000) {
            if (mTotalBetVal % 10000 == 0) {
                result = mTotalBetVal / 10000 + mWan;
            } else {
                result = String.format("%.1f", mTotalBetVal / 10000f) + mWan;
            }
        } else {
            result = String.valueOf(mTotalBetVal);
        }
        mTotalBet.setText(result);
    }

    /**
     * 显示自己的下注总金额
     */
    private void showSelfBet() {
        String result;
        if (mMyBetVal >= 10000) {
            if (mMyBetVal % 10000 == 0) {
                result = mMyBetVal / 10000 + mWan;
            } else {
                result = String.format("%.1f", mTotalBetVal / 10000f) + mWan;
            }
        } else {
            result = String.valueOf(mMyBetVal);
        }
        mMyBet.setText(result);
    }

    public void reset() {
        mTotalBetVal = 0;
        mMyBetVal = 0;
        mTotalBet.setText("0");
        mMyBet.setText("0");
    }

}
