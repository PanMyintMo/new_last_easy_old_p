package com.gawilive.main.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.utils.WordUtil;
import com.hjq.toast.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.bean.UserBean;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.utils.BigDecimalUtils;
import com.gawilive.common.utils.GlideUtils;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.views.NumberDanceTextView;
import com.gawilive.main.R;
import com.gawilive.main.adapter.LotteryChildNumberRecordAdapter;
import com.gawilive.main.adapter.PrizeListShowAdapter;
import com.gawilive.main.bean.*;
import com.gawilive.main.http.MainHttpUtil;
import com.gawilive.main.popup.PurchaseLotteryTicketsPopupWindow;
import com.gawilive.main.popup.RulePopupWindow;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

// 新版积分手机首页
public class DrawALotteryOrRaffleActivity extends AbsActivity {
    private TextView mTitleView;
    private ImageView mBtnBack;
    private ImageView mImgBt;
    private TextView mTvDjs;
    private ImageView mBtnCj;
    private NumberDanceTextView mTvNo1;
    private NumberDanceTextView mTvNo2;
    private NumberDanceTextView mTvNo3;
    private NumberDanceTextView mTvNo4;
    private NumberDanceTextView mTvNo5;
    private TextView mTvMoreJp;
    private RecyclerView mRecyclerViewOne;
    private TextView mTvKjJdTitle;
    private TextView mTvSyJf;
    private TextView mTvGmps;
    private RelativeLayout mLlHead;
    private ImageView mImgUserHead;
    private RecyclerView mRecyclerViewNum;
    private TextView mTvCjgz;
    private TextView mTvJPXq;
    private TextView mTvCjjl;
    private TextView mTvFxtj;
    private TextView mTvLjgd;

    // 奖品适配器
    private PrizeListShowAdapter prizeListShowAdapter;

    // 单价
    private String unitCoin;

    private SeekBar seek_bar;

    private TextView tvOpenCount;

    private TextView tvPercentage;

    private TextView tvMoreRecord;

    private TextView tvChildNickName;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_draw_a_lottery;
    }

    @Override
    protected void main() {
        super.main();
        initView();
        initData();
    }

    private void initView() {

        mTitleView = findViewById(R.id.titleView);
        mBtnBack = findViewById(R.id.btn_back);
        mImgBt = findViewById(R.id.img_bt);
        mTvDjs = findViewById(R.id.tvDjs);
        mBtnCj = findViewById(R.id.btn_cj);
        mTvNo1 = findViewById(R.id.tvNo1);
        mTvNo2 = findViewById(R.id.tvNo2);
        mTvNo3 = findViewById(R.id.tvNo3);
        mTvNo4 = findViewById(R.id.tvNo4);
        mTvNo5 = findViewById(R.id.tvNo5);
        mTvMoreJp = findViewById(R.id.tvMoreJp);
        mRecyclerViewOne = findViewById(R.id.recyclerViewOne);
        mTvKjJdTitle = findViewById(R.id.tvKjJdTitle);
        mTvSyJf = findViewById(R.id.tvSyJf);
        mTvGmps = findViewById(R.id.tvGmps);
        mLlHead = findViewById(R.id.llHead);
        mImgUserHead = findViewById(R.id.imgUserHead);
        mRecyclerViewNum = findViewById(R.id.recyclerViewNum);
        mTvCjgz = findViewById(R.id.tvCjgz);
        mTvJPXq = findViewById(R.id.tvJPXq);
        mTvCjjl = findViewById(R.id.tvCjjl);
        mTvFxtj = findViewById(R.id.tvFxtj);
        mTvLjgd = findViewById(R.id.tvLjgd);
        seek_bar = findViewById(R.id.seek_bar);
        tvOpenCount = findViewById(R.id.tvOpenCount);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvMoreRecord = findViewById(R.id.tvMoreRecord);
        tvChildNickName = findViewById(R.id.tvChildNickName);

        // 禁止拖动
        seek_bar.setOnTouchListener((v, event) -> true);

        // 更多奖品
        mTvMoreJp.setOnClickListener(v -> {
            startActivity(PrizeListActivity.class);
        });
        // 抽奖规则
        mTvCjgz.setOnClickListener(v -> showRuleDialog());

        prizeListShowAdapter = new PrizeListShowAdapter(this);
        mRecyclerViewOne.setAdapter(prizeListShowAdapter);

        // 抽奖
        mBtnCj.setOnClickListener(v -> {
            showPurchaseLotteryDialog();
//    if (isOpenTheLottery) {
//
//                showPurchaseLotteryDialog();
//            } else {
//
//                ToastUtils.show("This issue has ended");
//            }

        });

        mTvLjgd.setOnClickListener(v -> {
            String url = "https://yszc.ezwel.live/index.html";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            v.getContext().startActivity(intent);
        });

        // 缴费记录
        mTvCjjl.setOnClickListener(v -> {
            startActivity(PaymentRecordsActivity.class);
        });
        // 中奖记录
        tvMoreRecord.setOnClickListener(v -> startActivity(LotteryRecordActivity.class));

        // 奖品详情
        mTvJPXq.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, PrizeDetailsActivity.class));
        });

        /**
         * TextView 跳动的动画 数字从 startValue 到 endValue
         *
         * @param targetValue     结果数字
         * @param duration        动画时间
         * @param isHaveInitValue 是否设置初始值 如果 否 则取textView 当前的 数字为 初始值
         * @param startNum        如果有初始值设置初始值
         * @param scale           保留小数位数
         */
    }

    // 设置动画
    public void setAnimation(NumberDanceTextView view, String value) {
        view.setNumberText(value, 500, false, "0", 0);
    }

    private void initData() {
        getShowPrizeListShow();
        getUserInfo();
        getBasic();
        getDrawAPrizeRecord();
    }

    // 查询奖品列表
    private void getShowPrizeListShow() {
        MainHttpUtil.getTurntable(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    JSONArray array = obj.getJSONArray("list");
                    JSONArray configArray = obj.getJSONArray("config");
                    ConfigModel model = JSON.parseArray(configArray.toJSONString(), ConfigModel.class).get(0);
                    winNumScoreId = model.getId();
                    unitCoin = model.getCoin();
                    List<PrizeListModel> list = JSON.parseArray(array.toJSONString(), PrizeListModel.class);
                    prizeListShowAdapter.setData(list);
                }
            }
        });
    }

    // 抽奖规则
    private void showRuleDialog() {
        new XPopup.Builder(this)
                .asCustom(new RulePopupWindow(this))
                .show();
    }

    private String winNumScoreId;
    private String myCoin;

    // 购买奖券dialog
    private void showPurchaseLotteryDialog() {
        new XPopup.Builder(this)
                .asCustom(new PurchaseLotteryTicketsPopupWindow(this, unitCoin, myCoin, number -> {
                    raffle(number);
                }))
                .show();
    }

    // 历史开奖记录
    private void getDrawAPrizeRecord() {
        MainHttpUtil.getUserOpenWinLog(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<LotteryRecordModel> list = JSONArray.parseArray(Arrays.toString(info), LotteryRecordModel.class);
                    if (list.size() > 0) {
                        LotteryRecordModel model = list.get(0);
                        GlideUtils.setUserHead(DrawALotteryOrRaffleActivity.this, model.getData().get(0).getAvatar(), mImgUserHead);
                        tvChildNickName.setText(model.getData().get(0).getUser_nickname());
                        String[] str = model.getData().get(0).getNumber_string().split(",");
                        LotteryChildNumberRecordAdapter adapter = new LotteryChildNumberRecordAdapter(DrawALotteryOrRaffleActivity.this);
                        mRecyclerViewNum.setAdapter(adapter);
                        for (int i = 0; i < str.length; i++) {
                            adapter.addItem(str[i]);
                        }
                    }
                }
            }
        });
    }

    /**
     * 抽奖
     */
    private void raffle(String number) {
        MainHttpUtil.winningNumbers(number, winNumScoreId, basicModel.getId(), basicModel.getCycle(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    getBasic2();
                }
                ToastUtil.show(msg);
            }
        });
    }

    private DrawWinLotteryModel basicModel;


    // 是否开启抽奖
    private boolean isOpenTheLottery;


    private void getBasic() {
        MainHttpUtil.drawWinLottery(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<DrawWinLotteryModel> list = JSONArray.parseArray(Arrays.toString(info), DrawWinLotteryModel.class);
                    if (list.size() > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        basicModel = JSON.parseObject(obj.toJSONString(), DrawWinLotteryModel.class);
                        tvOpenCount.setText(TextUtils.isEmpty(basicModel.getPeople()) ? "0" : basicModel.getPeople());
                        if (!TextUtils.isEmpty(basicModel.getAlready_purchased_count()) && !"0".equals(basicModel.getAlready_purchased_count())) {
                            String number = BigDecimalUtils.div(TextUtils.isEmpty(basicModel.getAlready_purchased_count()) ? "0" : basicModel.getAlready_purchased_count(), TextUtils.isEmpty(basicModel.getPeople()) ? "0" : basicModel.getPeople(), 2);
                            String rate = BigDecimalUtils.mul(number, "100", 0);
                            tvPercentage.setText(rate + "%");
                            seek_bar.setProgress(Integer.parseInt(rate));
                            seek_bar.setSecondaryProgress(Integer.parseInt(rate));
                        } else {
                            String number = "0";
                            String rate = BigDecimalUtils.mul(number, "100", 0);
                            tvPercentage.setText(rate + "%");
                            seek_bar.setProgress(Integer.parseInt(rate));
                            seek_bar.setSecondaryProgress(Integer.parseInt(rate));
                        }
                        mTvGmps.setText(basicModel.getAlready_purchased_count());
                        setAnimation(mTvNo1, TextUtils.isEmpty(basicModel.getNumber_one()) ? "0" : basicModel.getNumber_one());
                        setAnimation(mTvNo2, TextUtils.isEmpty(basicModel.getNumber_two()) ? "0" : basicModel.getNumber_two());
                        setAnimation(mTvNo3, TextUtils.isEmpty(basicModel.getNumber_three()) ? "0" : basicModel.getNumber_three());
                        setAnimation(mTvNo4, TextUtils.isEmpty(basicModel.getNumber_four()) ? "0" : basicModel.getNumber_four());
                        setAnimation(mTvNo5, TextUtils.isEmpty(basicModel.getNumber_five()) ? "0" : basicModel.getNumber_five());
                        // 启动倒计时
                        setCountdown(basicModel.getDraw_time());
                    } else {
                        mTvDjs.setText(WordUtil.getString(R.string.new_57));
                    }

                }
            }
        });
    }

    private void getBasic2() {
        MainHttpUtil.drawWinLottery(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    basicModel = JSON.parseObject(obj.toJSONString(), DrawWinLotteryModel.class);
                    tvOpenCount.setText(basicModel.getPeople());
                    String number = BigDecimalUtils.div(basicModel.getAlready_purchased_count(), basicModel.getPeople(), 2);
                    String rate = BigDecimalUtils.mul(number, "100", 0);
                    tvPercentage.setText(rate + "%");
                    seek_bar.setProgress(Integer.parseInt(rate));
                    seek_bar.setSecondaryProgress(Integer.parseInt(rate));
                    mTvGmps.setText(basicModel.getAlready_purchased_count());
                }
            }
        });
    }

    private CountDownTimer countDownTimer;

    // 设置倒计时
    @SuppressLint("DefaultLocale")
    private void setCountdown(String value) {
        try {
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = format.parse(currentTime);
            Date date2 = format.parse(value);
            long startTime = date1.getTime();
            long endTime = date2.getTime();
            long timeDiff = endTime - startTime; // 计算时间差
            if (timeDiff < 1) {
                isOpenTheLottery = false;
                mTvDjs.setText(WordUtil.getString(R.string.new_55));
            } else {
                isOpenTheLottery = true;
                countDownTimer = new CountDownTimer(timeDiff, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // 这里你可以更新UI，显示剩余时间
                        // 例如："00:00:00"的格式
                        String hms = String.format("%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                        // 更新UI

                        mTvDjs.setText(String.format(getString(R.string.new_21), hms));
                    }

                    @Override
                    public void onFinish() {
                        // 倒计时完成时的操作
                        countDownTimer.cancel();
                        if (countDownTimer != null) {
                            countDownTimer = null;
                        }
                        // 获取新数据 ，进行倒计时
                        getBasic();
                    }
                }.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    // 获取用户信息
    private void getUserInfo() {
        MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                mTvSyJf.setText(BigDecimalUtils.add(TextUtils.isEmpty(bean.getGreen_score()) ? "0" : bean.getGreen_score(), "0", 0));
                myCoin = BigDecimalUtils.add(TextUtils.isEmpty(bean.getGreen_score()) ? "0" : bean.getGreen_score(), "0", 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
