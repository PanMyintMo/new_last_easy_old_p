package com.gawilive.main.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.main.bean.PayUserBean;
import com.gawilive.main.dialog.DiamondTransferFragment;
import com.gawilive.main.interfaces.DiamondTransferListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hjq.shape.view.ShapeTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.HtmlConfig;
import com.gawilive.common.activity.WebViewActivity;
import com.gawilive.common.bean.*;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.CommonHttpUtil;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.utils.*;
import com.gawilive.im.activity.ChatActivity;
import com.gawilive.live.activity.LiveRecordActivity;
import com.gawilive.live.activity.LuckPanRecordActivity;
import com.gawilive.live.activity.RoomManageActivity;
import com.gawilive.main.R;
import com.gawilive.main.activity.*;
import com.gawilive.main.bean.BannerBean;
import com.gawilive.main.http.MainHttpUtil;
import com.gawilive.mall.activity.*;

import okhttp3.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// New version of personal center
public class MainMeViewNewHolder extends AbsMainViewHolder  {

    private Context context;
    private FrameLayout mFlTop;
    private ImageView mBtnSetting;
    private ImageView mBtnMsg;
    private TextView mRedPoint;
    private RoundedImageView mAvatar;
    private TextView mName;
    private ShapeTextView mPersonData;
    private ImageView mImgLogo;
    private TextView mTvInputQb;
    private Banner mBanner;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView mFollowNum;

    private TextView mFansNum;

    private TextView mCollectNum;


    private boolean mPaused;

    private RelativeLayout mGroupVip;

    private List<BannerBean> mBannerList;

    private boolean mBannerNeedUpdate;

    private ImageView btn_wallet;

    private TextView mCollectionNum;
    private RelativeLayout mMGroupVip;

    private TextView mTvFjGl;
    private TextView mTvZbZx;
    private TextView mTvDj;
    private TextView mTvMrrw;
    private TextView mTvZbmx;
    private TextView mTvFxnr;
    private TextView mTvZJJL;
    private TextView mTvGxsz;
    private ImageView mBtnWallet;


    private TextView forwardProfit;

    private TextView tvWdRz;

    private TextView tvWdDj;

    private TextView tvWdSp;

    private TextView btn_vip;

    private TextView tvCoin, tvIntegral, tvBalance, tvJfCj, tvDp, tvJPXq;

    private LinearLayout llRedScore, llGreenScore, transfer;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;


    public MainMeViewNewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
        this.context = context;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_me_new;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();

        Log.d("REACH","Reach on Create");

//       manager.setFragmentResultListener("coin_balance", this, new FragmentResultListener() {
//            @Override
//            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
//                // Retrieve the new coin balance from the bundle
//                String newBalance = bundle.getString("coin_balance");
//                if (newBalance != null) {
//                    // Update the UI or perform necessary actions with the new balance
//                    tvCoin.setText(newBalance);
//                    // Optionally, update SharedPreferences or other storage
//                    SpUtil.getInstance().setDiamondBalance("coin_balance", newBalance);
//                }
//            }
//            });

    }

    @Override
    public void init() {
        setStatusHeight();
        initView();
        initData();
    }


    private void initView() {
        mFlTop = findViewById(R.id.fl_top);
        mBtnSetting = findViewById(R.id.btn_setting);
        mBtnMsg = findViewById(R.id.btn_msg);
        mRedPoint = findViewById(R.id.red_point);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        mPersonData = findViewById(R.id.personData);
        mImgLogo = findViewById(R.id.imgLogo);
        mTvInputQb = findViewById(R.id.tvInputQb);
        mBanner = findViewById(R.id.banner);
        mFansNum = findViewById(R.id.fans_num);
        mFollowNum = findViewById(R.id.follow_num);
        mCollectNum = findViewById(R.id.collection_num);
        mGroupVip = findViewById(R.id.mGroupVip);
        btn_wallet = findViewById(R.id.btn_wallet);
        mTvInputQb = findViewById(R.id.tvInputQb);
        mTvFjGl = findViewById(R.id.tvFjGl);
        mTvZbZx = findViewById(R.id.tvZbZx);
        mTvDj = findViewById(R.id.tvDj);
        mTvMrrw = findViewById(R.id.tvMrrw);
        mTvZbmx = findViewById(R.id.tvZbmx);
        mTvFxnr = findViewById(R.id.tvFxnr);
        mTvZJJL = findViewById(R.id.tvZJJL);
        mTvGxsz = findViewById(R.id.tvGxsz);
        forwardProfit = findViewById(R.id.forwardProfit);
        tvWdRz = findViewById(R.id.tvWdRz);
        tvWdDj = findViewById(R.id.tvWdDj);
        tvWdSp = findViewById(R.id.tvWdSp);
        btn_vip = findViewById(R.id.btn_vip);
        tvCoin = findViewById(R.id.tvCoin);
        transfer = findViewById(R.id.transfer);
        //  tvBalance = findViewById(R.id.tvBalance);
        tvIntegral = findViewById(R.id.tvIntegral);
        tvJfCj = findViewById(R.id.tvJfCj);
        tvDp = findViewById(R.id.tvDp);
        // llRedScore = findViewById(R.id.llRedScore);
        llGreenScore = findViewById(R.id.llGreenScore);




        mBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                ImgLoader.display(mContext, ((BannerBean) path).getImageUrl(), imageView);
            }
        });
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int p) {
                if (mBannerList != null) {
                    if (p >= 0 && p < mBannerList.size()) {
                        BannerBean bean = mBannerList.get(p);
                        if (bean != null) {
                            String link = bean.getLink();
                            if (!TextUtils.isEmpty(link)) {
                                WebViewActivity.forward(mContext, link, false);
                            }
                        }
                    }
                }
            }
        });

        // Enter wallet
        mTvInputQb.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, MyCoinActivity.class));
        });

        // set up
        mBtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardSetting();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        String storedBalance = SpUtil.getInstance().getDiamondBalance("coin_balance");

                        if (storedBalance != null) {
                            initData();
                        }
                        //      swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        //transfer dialog fragment

        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkPin = SpUtil.getInstance().getBooleanValue(SpUtil.PIN);

                if (checkPin) {
                    if (context instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) context;

                        // Initialize and show the DiamondTransferFragment
                        DiamondTransferFragment dialog = DiamondTransferFragment.newInstance();


                        dialog.setDiamondTransferListener(navigateToParent -> {
                            if (navigateToParent) {
                                Log.d("SETDIAMONDTRANSFERLISTENER","SET DIAMOND TRANSFER LISTENER");
                              initData();
                            }
                        });

                        dialog.show(activity.getSupportFragmentManager(), "DiamondTransferFragment");
                    } else {
                        throw new IllegalStateException("Context must be an instance of AppCompatActivity");
                    }
                } else {
                    // If PIN is not set, navigate to the PinActivity
                    context.startActivity(new Intent(context, PinActivity.class));
                }
            }
        });

        // Live broadcast details
        mTvZbmx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.forward(mContext, CommonAppConfig.HOST + "/appapi/Detail/index");
            }
        });

        // my certification
        tvWdRz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.forward(mContext, HtmlConfig.AUTH);
            }
        });

        // Openvip
        btn_vip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.forward(mContext, HtmlConfig.SHOP);
//                //  mContext.startActivity(new Intent(mContext,DrawALotteryOrRaffleActivity));
//                RouteUtil.forwardPrize(mContext);
            }
        });

        // earnings
        forwardProfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardProfit();
            }
        });
        //Level
        tvWdDj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.forward(mContext, CommonAppConfig.HOST + "/appapi/Level/index");
            }
        });
        // Video
        tvWdSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardMyVideo();
            }
        });


        // Fan
        mFansNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardFans();
            }
        });

        // concern
        mFollowNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardFollow();
            }
        });

        // Comment
        mCollectNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonAppConfig.getInstance().isTeenagerType()) {
                    ToastUtil.show(com.gawilive.common.R.string.a_137);
                } else {
                    mContext.startActivity(new Intent(mContext, GoodsCollectActivity.class));
                }

            }
        });

        mBtnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.forward(mContext);
            }
        });

        btn_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteUtil.forwardMyCoin(mContext);
            }
        });

        // room management
        mTvFjGl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardRoomManage();
            }
        });
        //Equipment center
        mTvZbZx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.forward(mContext, CommonAppConfig.HOST + "/appapi/Equipment/index");
            }
        });
        // Props
        mTvDj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.forward(mContext, CommonAppConfig.HOST + "/appapi/Mall/index");
            }
        });
        //daily tasks
        mTvMrrw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, DailyTaskActivity.class));
            }
        });

        // Paid content
        mTvFxnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardPayContent();
            }
        });
        // Winning incentives
        mTvZJJL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                luckPanRecord();
            }
        });
        //Personalization
        mTvGxsz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardSetting();
            }
        });

        // Points lottery
        tvJfCj.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, DrawALotteryOrRaffleActivity.class));
        });

        // personal information
        mPersonData.setOnClickListener(v -> RouteUtil.forwardUserHome(mContext, CommonAppConfig.getInstance().getUid()));

        // Source of red points
//        llRedScore.setOnClickListener(v -> {
//            mContext.startActivity(new Intent(mContext, RedSourceActivity.class));
//        });

        //Source of lottery diamonds
        llGreenScore.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, GreenSourceActivity.class));
        });

    }

    private void initData() {

        getBalance();
    }


    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    // Get certification information
    private void getAuthData() {
        MainHttpUtil.getAuth(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    AuthModel model = JSONObject.parseObject(info[0], AuthModel.class);
                    if ("1".equals(model.getStatus())) {
                        mContext.startActivity(new Intent(mContext, MyProfitActivity.class));
                    } else if ("0".equals(model.getStatus())) {
                        ToastUtil.show(WordUtil.getString(R.string.mall_063));
                    } else if ("2".equals(model.getStatus())) {
                        ToastUtil.show(WordUtil.getString(R.string.mall_064));
                        WebViewActivity.forward(mContext, HtmlConfig.AUTH);
                    } else {
                        ToastUtil.show(WordUtil.getString(R.string.string_smrz_tips));
                        WebViewActivity.forward(mContext, HtmlConfig.AUTH);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowed() && mPaused) {
            loadData();

        }
        mPaused = false;


    }

    private void getBanner() {
        MainHttpUtil.getHot(1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                List<BannerBean> bannerList = JSON.parseArray(obj.getString("slide"), BannerBean.class);
                if (bannerList != null && bannerList.size() > 0) {
                    if (mBannerList == null || mBannerList.size() != bannerList.size()) {
                        mBannerNeedUpdate = true;
                    } else {
                        for (int i = 0; i < mBannerList.size(); i++) {
                            BannerBean bean = mBannerList.get(i);
                            if (bean == null || !bean.isEqual(bannerList.get(i))) {
                                mBannerNeedUpdate = true;
                                break;
                            }
                        }
                    }
                }
                mBannerList = bannerList;
                showBanner();
            }
        });


    }


    private void showBanner() {
        if (mBanner == null) {
            return;
        }
        if (mBannerList != null && mBannerList.size() > 0) {
            if (mBanner.getVisibility() != View.VISIBLE) {
                mBanner.setVisibility(View.VISIBLE);
            }
            if (mBannerNeedUpdate) {
                mBanner.update(mBannerList);
            }
        } else {
            if (mBanner.getVisibility() != View.GONE) {
                mBanner.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void loadData() {
        if (CommonAppConfig.getInstance().isLogin()) {
            if (isFirstLoadData()) {
                showData1();
                getBalance();
            }
            MainHttpUtil.getBaseInfo(mCallback);
        } else {
            if (mContext != null) {
                ((MainActivity) mContext).setCurrentPage(0);
            }
        }
    }


    private final CommonCallback<UserBean> mCallback = new CommonCallback<UserBean>() {
        @Override
        public void callback(UserBean bean) {
            queryWallet(bean);
            showData();
        }
    };

    private void showData1() {
        String userBeanJson = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
        if (TextUtils.isEmpty(userBeanJson)) {
            return;
        }
        JSONObject obj = JSON.parseObject(userBeanJson);
        UserBean u = JSON.toJavaObject(obj, UserBean.class);
        ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
        mName.setText(u.getUserNiceName());
        CommonAppConfig appConfig = CommonAppConfig.getInstance();

        mFollowNum.setText(WordUtil.getString(R.string.follow) + "\t" + StringUtil.toWan(u.getFollows()));
        mFansNum.setText(WordUtil.getString(R.string.fans) + "\t" + StringUtil.toWan(u.getFans()));
        mCollectNum.setText(WordUtil.getString(R.string.mall_393) + "\t" + StringUtil.toWan(obj.getIntValue("goods_collect_nums")));
    }

    private void showData() {
        String userBeanJson = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
        if (TextUtils.isEmpty(userBeanJson)) {
            return;
        }
        JSONObject obj = JSON.parseObject(userBeanJson);
        UserBean u = JSON.toJavaObject(obj, UserBean.class);
        ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
        mName.setText(u.getUserNiceName());
        CommonAppConfig appConfig = CommonAppConfig.getInstance();

        mFollowNum.setText(WordUtil.getString(R.string.follow) + "\t" + StringUtil.toWan(u.getFollows()));
        mFansNum.setText(WordUtil.getString(R.string.fans) + "\t" + StringUtil.toWan(u.getFans()));
        mCollectNum.setText(WordUtil.getString(R.string.mall_393) + "\t" + StringUtil.toWan(obj.getIntValue("goods_collect_nums")));

        tvIntegral.setText(BigDecimalUtils.add(TextUtils.isEmpty(u.getGreen_score()) ? "0" : u.getGreen_score(), "0", 0));
//        tvBalance.setText(BigDecimalUtils.add(TextUtils.isEmpty(u.getRed_score()) ? "0" : u.getRed_score(), "0", 0));


        if (CommonAppConfig.getInstance().isTeenagerType()) {
            if (mGroupVip.getVisibility() != View.GONE) {
                mGroupVip.setVisibility(View.GONE);
            }

        } else {
            if (mGroupVip.getVisibility() != View.VISIBLE) {
                mGroupVip.setVisibility(View.VISIBLE);
            }
        }
        //  tvDp.setText("直播小店");

        // 店铺
        tvDp.setOnClickListener(v -> {
            if (u.getIsOpenShop() == 0) {

                BuyerActivity.forward(mContext);
            } else {
                SellerActivity.forward(mContext);
            }
        });

    }


    // Sync wallet information
    private void queryWallet(UserBean bean) {
        if (bean == null) {
            //Log.e("queryWallet", "UserBean is null, unable to query wallet.");
            return;
        }

        String mobile = bean.getMobile();
        if (mobile == null || mobile.isEmpty()) {
            //  Log.e("queryWallet", "UserBean mobile is null or empty, unable to query wallet.");
            return;
        }

        OkHttp.getAsync(CommonAppConfig.HOST2 + "?act=findUser&phone=" + mobile, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == -3) { // Synchronize information
                    Map<String, String> map = new HashMap<>();
                    map.put("phone", mobile);
                    map.put("email", TextUtils.isEmpty(bean.getUser_email()) ? mobile : bean.getUser_email());
                    map.put("pwd", bean.getUser_pass());
                    map.put("account", mobile);
                    map.put("username", bean.getUserNiceName());
                    map.put("avatar", bean.getAvatarThumb());
                    map.put("live_user_id", bean.getId());
                    syncData(map);
                } else {
                    JsonObject dataObject = object.getAsJsonObject("data");
                    PayUserBean payUserBean = new Gson().fromJson(dataObject.toString(), PayUserBean.class);
                    MMKVUtils.setPayUid(payUserBean.getUid());
                    MMKVUtils.setKey(payUserBean.getKey());
                    MMKVUtils.setPayPwd(payUserBean.getPaypwd());
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                //Log.e("queryWallet", "Error fetching user information", e);
            }
        });
    }


    // 同步信息
    private void syncData(Map<String, String> map) {
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=addUser", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                syncData(map);
            }

            @Override
            public void requestFailure(Request request, IOException e) {
            }
        });
    }


    /**
     * 我的关注
     */
    private void forwardFollow() {
        FollowActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }

    /**
     * 我的粉丝
     */
    private void forwardFans() {
        FansActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }

    /**
     * 直播记录
     */
    private void forwardLiveRecord() {
        LiveRecordActivity.forward(mContext, CommonAppConfig.getInstance().getUserBean());
    }

    /**
     * 我的收益
     */
    private void forwardProfit() {
        getAuthData();
    }

    /**
     * 我的钻石
     */
    private void forwardCoin() {
        RouteUtil.forwardMyCoin(mContext);
    }

    /**
     * 设置
     */
    private void forwardSetting() {
        mContext.startActivity(new Intent(mContext, SettingActivity.class));
    }

    /**
     * 我的视频
     */
    private void forwardMyVideo() {
        mContext.startActivity(new Intent(mContext, MyVideoActivity.class));
    }

    /**
     * 房间管理
     */
    private void forwardRoomManage() {
        mContext.startActivity(new Intent(mContext, RoomManageActivity.class));
    }

    /**
     * 转盘中奖记录
     */
    private void luckPanRecord() {
        mContext.startActivity(new Intent(mContext, LuckPanRecordActivity.class));
    }

    /**
     * 付费内容
     */
    private void forwardPayContent() {
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u != null) {
            if (u.getIsOpenPayContent() == 0) {
                PayContentActivity1.forward(mContext);
            } else {
                PayContentActivity2.forward(mContext);
            }
        }
    }

    // Check balance
    private void getBalance() {
        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    tvCoin.setText(coin);
                    SpUtil.getInstance().setDiamondBalance("coin_balance", coin);
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }


        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}