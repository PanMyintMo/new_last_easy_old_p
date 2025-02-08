package com.gawilive.common.dialog;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.R;
import com.gawilive.common.adapter.ChatChargePayAdapter;
import com.gawilive.common.bean.CoinBean;
import com.gawilive.common.bean.CoinPayBean;
import com.gawilive.common.event.ChargeTypeEvent;
import com.gawilive.common.interfaces.OnItemClickListener;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.common.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by cxf on 2019/4/22.
 */

public class LiveChargePayDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private List<CoinPayBean> mPayList;
    private ChatChargePayAdapter mAdapter;
    private ActionListener mActionListener;
    private CoinBean mCoinBean;
    private TextView mTvCoin;
    private TextView mTvMoneySymbol;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_chat_charge_pay;
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
        params.width = DpUtil.dp2px(310);
        params.height = DpUtil.dp2px(350);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewById(R.id.btn_charge).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        mTvCoin = findViewById(R.id.coin);
        mTvMoneySymbol = findViewById(R.id.money_symbol);
        TextView money = findViewById(R.id.money);
        if (mCoinBean != null) {
            money.setText(mCoinBean.getMoney());
        }
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        if (mPayList != null) {
            mAdapter = new ChatChargePayAdapter(mContext, mPayList);
            mAdapter.setOnItemClickListener(new OnItemClickListener<CoinPayBean>() {
                @Override
                public void onItemClick(CoinPayBean bean, int position) {
                    boolean isPayPal = Constants.PAY_TYPE_PAYPAL.equals(bean.getId());
                    if (mTvMoneySymbol != null) {
                        mTvMoneySymbol.setText(isPayPal ? "$" : "Ks");
                    }
                    if (mTvCoin != null) {
                        String coin = isPayPal ? mCoinBean.getCoinPaypal() : mCoinBean.getCoin();
                        mTvCoin.setText(StringUtil.contact(coin, CommonAppConfig.getInstance().getCoinName()));
                    }
                    EventBus.getDefault().post(new ChargeTypeEvent(bean.getId()));
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            CoinPayBean coinPayBean = mAdapter.getCheckedPayBean();
            if (coinPayBean != null) {
                boolean isPayPal = Constants.PAY_TYPE_PAYPAL.equals(coinPayBean.getId());
                if (mTvMoneySymbol != null) {
                    mTvMoneySymbol.setText(isPayPal ? "$" : "Ks");
                }
                if (mTvCoin != null) {
                    String coin = isPayPal ? mCoinBean.getCoinPaypal() : mCoinBean.getCoin();
                    mTvCoin.setText(StringUtil.contact(coin, CommonAppConfig.getInstance().getCoinName()));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_close) {
            dismiss();
        } else if (i == R.id.btn_charge) {
            charge();
        }
    }

    private void charge() {
        if (mAdapter != null && mActionListener != null) {
            mActionListener.onChargeClick(mAdapter.getCheckedPayBean());
            dismiss();
        }
    }


    public void setPayList(List<CoinPayBean> payList) {
        for (int i = 0, size = payList.size(); i < size; i++) {
            payList.get(i).setChecked(i == 0);
        }
        mPayList = payList;
    }

    public void setCoinBean(CoinBean bean) {
        mCoinBean = bean;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().post(new ChargeTypeEvent(""));
        mActionListener = null;
        super.onDestroy();
    }

    public interface ActionListener {
        void onChargeClick(CoinPayBean coinPayBean);
    }


    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
