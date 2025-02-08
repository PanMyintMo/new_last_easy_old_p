package com.gawilive.main.views;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.bean.ConfigBean;
import com.gawilive.common.custom.HomeIndicatorTitle;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.common.utils.ScreenDimenUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;
import com.gawilive.main.dialog.LanguageDialogFragment;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 首页
 */

public class MainHomeViewHolder extends AbsMainHomeParentViewHolder {

    private MainHomeLiveViewHolder mLiveViewHolder;
    private MainHomeVideoViewHolder mVideoViewHolder;
    private MainHomeNearViewHolder mNearViewHolder;
    private int mStatusBarHeight;

    private View mCover;
    private int mChangeHeight;
    private View mBtnRank;


    public MainHomeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_new;
    }

    @Override
    public void init() {
        setStatusHeight();
        super.init();
        mStatusBarHeight = ScreenDimenUtil.getInstance().getStatusBarHeight();
        mCover = findViewById(R.id.cover);
//        mAppBarLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                mChangeHeight = mAppBarLayout.getHeight() - mStatusBarHeight;
//            }
//        });
        mBtnRank = findViewById(R.id.btn_rank);
        onRankVisibleChanged();
        initListener();
    }

    private void initListener() {
        findViewById(R.id.btn_select_language).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonAppConfig.getInstance().setConfig(null);
                LanguageDialogFragment fragment = new LanguageDialogFragment();
                fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "LanguageDialogFragment");
            }
        });
    }

    public void onAppBarOffsetChanged(float verticalOffset) {
        if (mChangeHeight != 0 && mCover != null) {
            float rate = verticalOffset / mChangeHeight;
            mCover.setAlpha(rate);
        }
    }

    @Override
    protected void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        AbsMainHomeChildViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mLiveViewHolder = new MainHomeLiveViewHolder(mContext, parent);
                    vh = mLiveViewHolder;
                } else if (position == 1) {
                    mVideoViewHolder = new MainHomeVideoViewHolder(mContext, parent);
                    vh = mVideoViewHolder;
                } else if (position == 2) {
                    mNearViewHolder = new MainHomeNearViewHolder(mContext, parent);
                    vh = mNearViewHolder;
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
            }
        }
        if (vh != null) {
            vh.loadData();
        }
    }

    @Override
    protected int getPageCount() {
        return 3;
    }

    @Override
    protected String[] getTitles() {
        return new String[]{
                WordUtil.getString(R.string.live),
                WordUtil.getString(R.string.video),
                WordUtil.getString(R.string.near)
        };
    }


    @Override
    protected IPagerTitleView getIndicatorTitleView(Context context, String[] titles, final int index) {
        HomeIndicatorTitle indicatorTitle = new HomeIndicatorTitle(mContext);
        SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
        if (index == 0) {
            simplePagerTitleView.setPadding(0, 0, 0, 0);
        } else {
            simplePagerTitleView.setPadding(DpUtil.dp2px(15), 0, 0, 0);
        }
        simplePagerTitleView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.tabs_unselect));
        simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.white));
        simplePagerTitleView.setText(titles[index]);
        simplePagerTitleView.setTextSize(18);
        simplePagerTitleView.getPaint().setFakeBoldText(true);
        indicatorTitle.addView(simplePagerTitleView);
        indicatorTitle.setTitleView(simplePagerTitleView);
        indicatorTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(index);
                }
            }
        });

        return indicatorTitle;
    }


    private void onRankVisibleChanged() {
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            if (configBean.getLeaderboard_switch() == 1) {
                if (CommonAppConfig.getInstance().isTeenagerType()) {
                    if (mBtnRank != null && mBtnRank.getVisibility() != View.GONE) {
                        mBtnRank.setVisibility(View.GONE);
                    }
                } else {
                    if (mBtnRank != null && mBtnRank.getVisibility() != View.VISIBLE) {
                        mBtnRank.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (mBtnRank != null && mBtnRank.getVisibility() != View.GONE) {
                    mBtnRank.setVisibility(View.GONE);
                }
            }
        }
    }


}
