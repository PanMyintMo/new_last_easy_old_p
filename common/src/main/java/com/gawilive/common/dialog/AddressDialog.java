package com.gawilive.common.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.gawilive.common.R;
import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.common.adapter.TabAdapter;
import com.gawilive.common.bean.AddressModel;

import java.util.List;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE;
import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/02/12
 * desc   : 省市区选择对话框
 * doc    : https://baijiahao.baidu.com/s?id=1615894776741007967
 */
public final class AddressDialog {

    public static final class Builder
            extends BaseDialog.Builder<Builder>
            implements TabAdapter.OnTabListener,
            Runnable, RecyclerViewAdapter.OnSelectListener,
            BaseDialog.OnShowListener, BaseDialog.OnDismissListener {

        private final TextView mTitleView;
        private final ImageView mCloseView;
        private final RecyclerView mTabView;

        private final ViewPager2 mViewPager;

        private final TabAdapter mTabAdapter;
        private final RecyclerViewAdapter mAdapter;
        private final ViewPager2.OnPageChangeCallback mCallback;

        @Nullable
        private OnListener mListener;

        private String mProvince = null;
        private String mCity = null;
        private String mArea = null;

        private boolean mIgnoreArea;

        private List<AddressModel> list;

        @SuppressWarnings("all")
        public Builder(Context context) {
            super(context);
            setContentView(R.layout.address_dialog);
            setHeight(getResources().getDisplayMetrics().heightPixels / 2);

            mViewPager = findViewById(R.id.vp_address_pager);
            mAdapter = new RecyclerViewAdapter(context);
            mAdapter.setOnSelectListener(this);
            mViewPager.setAdapter(mAdapter);

            mTitleView = findViewById(R.id.tv_address_title);
            mCloseView = findViewById(R.id.iv_address_closer);
            mTabView = findViewById(R.id.rv_address_tab);
            setOnClickListener(mCloseView);

            mTabAdapter = new TabAdapter(context, TabAdapter.TAB_MODE_SLIDING, false);
            mTabAdapter.addItem("请选择");
            mTabAdapter.setOnTabListener(this);
            mTabView.setAdapter(mTabAdapter);

            mCallback = new ViewPager2.OnPageChangeCallback() {

                private int mPreviousScrollState, mScrollState = SCROLL_STATE_IDLE;

                @Override
                public void onPageScrollStateChanged(int state) {
                    mPreviousScrollState = mScrollState;
                    mScrollState = state;
                    if (state == ViewPager2.SCROLL_STATE_IDLE && mTabAdapter.getSelectedPosition() != mViewPager.getCurrentItem()) {
                        final boolean updateIndicator = mScrollState == SCROLL_STATE_IDLE || (mScrollState == SCROLL_STATE_SETTLING && mPreviousScrollState == SCROLL_STATE_IDLE);
                        onTabSelected(mTabView, mViewPager.getCurrentItem());
                    }
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
            };


            addOnShowListener(this);
            addOnDismissListener(this);
        }

        public Builder setData(List<AddressModel> list) {
            this.list = list;
            // 显示省份列表
            mAdapter.addItem(this.list);
            return this;
        }

        public Builder setTitle(@StringRes int id) {
            return setTitle(getString(id));
        }

        public Builder setTitle(CharSequence text) {
            mTitleView.setText(text);
            return this;
        }

        /**
         * 设置默认省份
         */
        public Builder setProvince(String province) {
            if (TextUtils.isEmpty(province)) {
                return this;
            }

            List<AddressModel> data = mAdapter.getItem(0);
            if (data == null || data.isEmpty()) {
                return this;
            }

            for (int i = 0; i < data.size(); i++) {
                if (!province.equals(data.get(i).getProvince())) {
                    continue;
                }
                selectedAddress(0, i, false);
                break;
            }
            return this;
        }

        /**
         * 设置默认城市
         */
        public Builder setCity(String city) {
            if (mIgnoreArea) {
                // 已经忽略了县级区域的选择，不能选定指定的城市
                throw new IllegalStateException("The selection of county-level regions has been ignored. The designated city cannot be selected");
            }
            if (TextUtils.isEmpty(city)) {
                return this;
            }

            List<AddressModel> data = mAdapter.getItem(1);
            if (data == null || data.isEmpty()) {
                return this;
            }

            for (int i = 0; i < data.size(); i++) {
                if (!city.equals(data.get(i).getCity())) {
                    continue;
                }
                // 避开直辖市，因为选择省的时候已经自动跳过市区了
                if (mAdapter.getItem(1).size() > 1) {
                    selectedAddress(1, i, false);
                }
                break;
            }
            return this;
        }

        /**
         * 不选择县级区域
         */
        public Builder setIgnoreArea() {
            if (mAdapter.getCount() == 3) {
                // 已经指定了城市，则不能忽略县级区域
                throw new IllegalStateException("Cities have been designated and county-level areas can no longer be ignored");
            }
            mIgnoreArea = true;
            return this;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * {@link RecyclerViewAdapter.OnSelectListener}
         */
        @Override
        public void onSelected(int recyclerViewPosition, int clickItemPosition) {
            selectedAddress(recyclerViewPosition, clickItemPosition, true);
        }

        /**
         * 选择地区
         *
         * @param type         类型（省、市、区）
         * @param position     点击的位置
         * @param smoothScroll 是否需要平滑滚动
         */
        @SuppressWarnings("all")
        private void selectedAddress(int type, int position, boolean smoothScroll) {
            switch (type) {
                case 0:
                    // 记录当前选择的省份
                    mProvince = mAdapter.getItem(type).get(position).getProvince();
                    mTabAdapter.setItem(type, mProvince);

                    mTabAdapter.addItem("请选择");
                    mTabAdapter.setSelectedPosition(type + 1);
                    mAdapter.addItem(mAdapter.getItem(type).get(position).getChildren());
                    // mAdapter.addItem(AddressManager.getCityList(mAdapter.getItem(type).get(position).getNext()));
                    mViewPager.setCurrentItem(type + 1, smoothScroll);

                    // 如果当前选择的是直辖市，就直接跳过选择城市，直接选择区域
                    if (mAdapter.getItem(type + 1).size() == 1) {
                        selectedAddress(type + 1, 0, false);
                    }
                    break;
                case 1:
                    // 记录当前选择的城市
                    mCity = mAdapter.getItem(type).get(position).getCity();
                    mTabAdapter.setItem(type, mCity);

                    if (mIgnoreArea) {

                        if (mListener != null) {
                            mListener.onSelected(getDialog(), mProvince, mCity, mArea);
                        }

                        // 延迟关闭
                        postDelayed(this::dismiss, 300);

                    } else {
                        mTabAdapter.addItem("请选择");
                        mTabAdapter.setSelectedPosition(type + 1);
                        mAdapter.addItem(mAdapter.getItem(type).get(position).getChildren());
                        mViewPager.setCurrentItem(type + 1, smoothScroll);
                    }

                    break;
                case 2:
                    // 记录当前选择的区域
                    mArea = mAdapter.getItem(type).get(position).getArea();
                    mTabAdapter.setItem(type, mArea);

                    if (mListener != null) {
                        mListener.onSelected(getDialog(), mProvince, mCity, mArea);
                    }

                    // 延迟关闭
                    postDelayed(this::dismiss, 300);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void run() {
            if (isShowing()) {
                dismiss();
            }
        }


        @Override
        public void onClick(View view) {
            if (view == mCloseView) {
                dismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onCancel(getDialog());
            }
        }

        /**
         * {@link TabAdapter.OnTabListener}
         */

        @Override
        public boolean onTabSelected(RecyclerView recyclerView, int position) {
            synchronized (this) {
                if (mViewPager.getCurrentItem() != position) {
                    mViewPager.setCurrentItem(position);
                }

                mTabAdapter.setItem(position, "请选择");
                switch (position) {
                    case 0:
                        mProvince = mCity = mArea = null;
                        if (mTabAdapter.getCount() > 2) {
                            mTabAdapter.removeItem(2);
                            mAdapter.removeItem(2);
                        }

                        if (mTabAdapter.getCount() > 1) {
                            mTabAdapter.removeItem(1);
                            mAdapter.removeItem(1);
                        }
                        break;
                    case 1:
                        mCity = mArea = null;
                        if (mTabAdapter.getCount() > 2) {
                            mTabAdapter.removeItem(2);
                            mAdapter.removeItem(2);
                        }
                        break;
                    case 2:
                        mArea = null;
                        break;
                    default:
                        break;
                }
            }
            return true;
        }

        /**
         * {@link BaseDialog.OnShowListener}
         */

        @Override
        public void onShow(BaseDialog dialog) {
            // 注册 ViewPager 滑动监听
            mViewPager.registerOnPageChangeCallback(mCallback);
        }

        /**
         * {@link BaseDialog.OnDismissListener}
         */

        @Override
        public void onDismiss(BaseDialog dialog) {
            // 反注册 ViewPager 滑动监听
            mViewPager.unregisterOnPageChangeCallback(mCallback);
        }
    }

    private final static class RecyclerViewAdapter extends AppAdapter<List<AddressModel>> {

        @Nullable
        private OnSelectListener mListener;

        private RecyclerViewAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        private final class ViewHolder extends AppAdapter<?>.ViewHolder implements OnItemClickListener {

            private final AddressAdapter mAdapter;

            ViewHolder() {
                super(new RecyclerView(getContext()));
                RecyclerView recyclerView = (RecyclerView) getItemView();
                recyclerView.setNestedScrollingEnabled(true);
                recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mAdapter = new AddressAdapter(getContext());
                mAdapter.setOnItemClickListener(this);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onBindView(int position) {
                mAdapter.setData(getItem(position));
            }

            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                if (mListener == null) {
                    return;
                }
                mListener.onSelected(getViewHolderPosition(), position);
            }
        }

        private void setOnSelectListener(@Nullable OnSelectListener listener) {
            mListener = listener;
        }

        private interface OnSelectListener {

            void onSelected(int recyclerViewPosition, int clickItemPosition);
        }
    }

    private static final class AddressAdapter extends AppAdapter<AddressModel> {

        private AddressAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            TextView textView = new TextView(parent.getContext());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setBackgroundResource(R.drawable.transparent_selector);
            textView.setTextColor(0xFF222222);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.sp_14));
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding((int) getResources().getDimension(R.dimen.dp_20),
                    (int) getResources().getDimension(R.dimen.dp_10),
                    (int) getResources().getDimension(R.dimen.dp_20),
                    (int) getResources().getDimension(R.dimen.dp_10));
            return new ViewHolder(textView);
        }

        private final class ViewHolder extends AppAdapter<?>.ViewHolder {

            private final TextView mTextView;

            private ViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) getItemView();
            }

            @Override
            public void onBindView(int position) {
                mTextView.setText(getItem(position).getProvince());
            }
        }
    }




    public interface OnListener {

        /**
         * 选择完成后回调
         *
         * @param province 省
         * @param city     市
         * @param area     区
         */
        void onSelected(BaseDialog dialog, String province, String city, String area);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {
        }
    }
}