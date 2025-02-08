package com.gawilive.main.views;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.activity.WebViewActivity;
import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.bean.UserBean;
import com.gawilive.common.custom.CommonRefreshView;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.OnItemClickListener;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.main.R;
import com.gawilive.main.activity.MallSearchActivity;
import com.gawilive.main.activity.TeenagerActivity;
import com.gawilive.main.adapter.MainMallAdapter;
import com.gawilive.main.adapter.MainMallClassAdapter;
import com.gawilive.main.bean.BannerBean;
import com.gawilive.main.http.MainHttpConsts;
import com.gawilive.main.http.MainHttpUtil;
import com.gawilive.mall.activity.BuyerActivity;
import com.gawilive.mall.activity.GoodsCollectActivity;
import com.gawilive.mall.activity.GoodsDetailActivity;
import com.gawilive.mall.activity.SellerActivity;
import com.gawilive.mall.bean.GoodsHomeClassBean;
import com.gawilive.mall.bean.GoodsSimpleBean;

import java.util.List;

/**
 * 首页 商城
 */
public class MainMallViewHolder extends AbsMainViewHolder implements OnItemClickListener<GoodsSimpleBean>, View.OnClickListener {

    private CommonRefreshView mRefreshView;
    private MainMallAdapter mAdapter;
    private Banner mBanner;
    private View mBannerWrap;
    private boolean mBannerNeedUpdate;
    private List<BannerBean> mBannerList;
    private List<GoodsHomeClassBean> mClassList;
    private RecyclerView mRecyclerViewClass;
    private boolean mClassShowed;
    private View mScrollIndicator;
    private int mDp25;
    private ImageView mBtnMyShop;
    private View mTeenagerWrap;


    public MainMallViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_mall;
    }

    @Override
    public void init() {
        setStatusHeight();
        findViewById(R.id.btn_search).setOnClickListener(this);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_main_mall);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRefreshView.setLayoutManager(layoutManager);
        mAdapter = new MainMallAdapter(mContext);
        mAdapter.setOnItemClickListener(this);
        mRefreshView.setRecyclerViewAdapter(mAdapter);
        mRefreshView.getRecyclerView().setItemAnimator(null);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<GoodsSimpleBean>() {
            @Override
            public RefreshAdapter<GoodsSimpleBean> getAdapter() {
                return null;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getHomeGoodsList(p, callback);
            }

            @Override
            public List<GoodsSimpleBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                mBannerNeedUpdate = false;
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
                mClassList = JSON.parseArray(obj.getString("shoptwoclass"), GoodsHomeClassBean.class);
                showBanner();
                showClass();
               // Log.d("Sunday", "processData================");
                return JSON.parseArray(obj.getString("list"), GoodsSimpleBean.class);
            }

            @Override
            public void onRefreshSuccess(List<GoodsSimpleBean> list, int listCount) {
                //Log.d("Sunday", "onRefreshSuccess============");
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<GoodsSimpleBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        View headView = mAdapter.getHeadView();
        mScrollIndicator = headView.findViewById(R.id.scroll_indicator);
        mDp25 = DpUtil.dp2px(25);

        //edit by pp remove empty banner
     //  mBannerWrap = headView.findViewById(R.id.banner_wrap);
        mBanner = headView.findViewById(R.id.banner);
    /*    mBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                ImgLoader.display(mContext, ((BannerBean) path).getImageUrl(), imageView);
            }
        });*/
        /*mBanner.setOnBannerListener(new OnBannerListener() {
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
        });*/
        mRecyclerViewClass = headView.findViewById(R.id.recyclerView_class);
        mRecyclerViewClass.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (mScrollIndicator != null) {
                    mScrollIndicator.setTranslationX(mDp25 * computeScrollPercent());
                }
            }
        });

        mBtnMyShop = findViewById(R.id.btn_my_shop);
        mBtnMyShop.setOnClickListener(this);
        findViewById(R.id.btn_collect).setOnClickListener(this);
        showMyShopAvatar();
        mTeenagerWrap = findViewById(R.id.teenager_wrap);
        findViewById(R.id.btn_close_teenager).setOnClickListener(this);
        if (CommonAppConfig.getInstance().isTeenagerType()) {
            mTeenagerWrap.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 计算滑动的百分比
     */
    private float computeScrollPercent() {
        if (mRecyclerViewClass != null) {
            //当前RcyclerView显示区域的高度。水平列表屏幕从左侧到右侧显示范围
            int extent = mRecyclerViewClass.computeHorizontalScrollExtent();
            //整体的高度，注意是整体，包括在显示区域之外的
            int range = mRecyclerViewClass.computeHorizontalScrollRange();
            //已经向下滚动的距离，为0时表示已处于顶部
            float offset = mRecyclerViewClass.computeHorizontalScrollOffset();
            //已经滚动的百分比 0~1
            float percent = offset / (range - extent);
            if (percent > 1) {
                percent = 1;
            }
            return percent;
        }
        return 0;
    }


    private void showBanner() {
        if (mBanner == null || mBannerWrap == null) {
            return;
        }
        if (mBannerList == null || mBannerList.size() == 0) {
            mBannerWrap.setVisibility(View.GONE);
            return;
        }
        if (mBannerNeedUpdate) {
            mBanner.update(mBannerList);
        }
    }


    private void showClass() {
        //Log.d("Sunday", "showClass========");
        if (mRecyclerViewClass == null) {
            return;
        }
        if (mClassList == null || mClassList.size() == 0) {
            mRecyclerViewClass.setVisibility(View.GONE);
            return;
        }
        if (mClassShowed) {
            return;
        }
        mClassShowed = true;
        int size = mClassList.size();
       // Log.d("Sunday", "showClass========size:" + size);
        if (size <= 12) {
            mRecyclerViewClass.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        } else {
            mRecyclerViewClass.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.HORIZONTAL, false));
        }
        MainMallClassAdapter adapter = new MainMallClassAdapter(mContext, mClassList);
        mRecyclerViewClass.setAdapter(adapter);
    }

    @Override
    public void onItemClick(GoodsSimpleBean bean, int position) {
        GoodsDetailActivity.forward(mContext, bean.getId(), false, bean.getType());
    }


    @Override
    public void loadData() {
        if (isFirstLoadData() && mRefreshView != null) {
            mRefreshView.initData();
        }
    }


    @Override
    public void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_HOME_GOODS_LIST);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (!((AbsActivity) mContext).checkLogin()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.btn_search) {
            MallSearchActivity.forward(mContext);
        } else if (id == R.id.btn_my_shop) {
            forwardMall();
        } else if (id == R.id.btn_collect) {
            mContext.startActivity(new Intent(mContext, GoodsCollectActivity.class));
        } else if (id == R.id.btn_close_teenager) {
            TeenagerActivity.forward(mContext);
        }
    }


    /**
     * 我的小店 商城
     */
    /*private void forwardMall() {
        UserBean u = CommonAppConfig.getInstance().getUserBean();

        if (u != null) {
            Log.e("IN", "Information for user bean: " + u.toString()); // Ensure u has a proper toString() method.

            if (u.getIsOpenShop() == 0) {
                BuyerActivity.forward(mContext);
            } else {
                SellerActivity.forward(mContext);
            }
        }
        else {
            Log.e("IN", "UserBean is null.");
        }

    }*/


    private void forwardMall() {
        UserBean u = CommonAppConfig.getInstance().getUserBean();

        // Correct log statement using concatenation
        if (u != null) {
           // Log.e("IN", "Information for user bean: " + u.toString());
        } else {
           // Log.e("IN", "UserBean is null.");
        }

        // Navigation logic
        if (u != null) {
            if (u.getIsOpenShop() == 0) {
                BuyerActivity.forward(mContext); // Navigate to BuyerActivity
            } else {
                SellerActivity.forward(mContext); // Navigate to SellerActivity
            }
        }
    }

    public void showMyShopAvatar() {
        if (mBtnMyShop != null) {
            if (CommonAppConfig.getInstance().isLogin()) {
                UserBean u = CommonAppConfig.getInstance().getUserBean();
                if (u != null) {
                    ImgLoader.displayAvatar(mContext, u.getAvatar(), mBtnMyShop);
                }
            } else {
                mBtnMyShop.setImageResource(R.mipmap.icon_avatar_placeholder);
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        if (CommonAppConfig.getInstance().isTeenagerType()) {
            if (mTeenagerWrap != null && mTeenagerWrap.getVisibility() != View.VISIBLE) {
                mTeenagerWrap.setVisibility(View.VISIBLE);
            }
        } else {
            if (mTeenagerWrap != null && mTeenagerWrap.getVisibility() == View.VISIBLE) {
                mTeenagerWrap.setVisibility(View.INVISIBLE);
            }
        }
    }
}
