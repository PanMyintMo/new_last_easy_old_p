package com.gawilive.main.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.common.utils.ScreenDimenUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;
import com.gawilive.mall.bean.GoodsSimpleBean;

import java.util.HashMap;


public class MainMallAdapter extends RefreshAdapter<GoodsSimpleBean> {

    private static final int HEAD = -1;
    private final View.OnClickListener mOnClickListener;
    private final View mHeadView;
    private final String mSaleString;
    //    private String mMoneySymbol;
    private final int mThumbWidth;
    private final int mThumbHeightMax;
    private final HashMap<String, Integer> mMap;
    private boolean mNeedRefreshData;


    public MainMallAdapter(Context context) {
        super(context);
        mSaleString = WordUtil.getString(R.string.mall_114);
//        mMoneySymbol = WordUtil.getString(R.string.money_symbol);
        mHeadView = mInflater.inflate(R.layout.item_main_mall_head, null, false);
        mHeadView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick((GoodsSimpleBean) tag, 0);
                }
            }
        };
        mThumbWidth = (ScreenDimenUtil.getInstance().getScreenWdith() - DpUtil.dp2px(30)) / 2;
        mThumbHeightMax = DpUtil.dp2px(250);
        mMap = new HashMap<>();
    }

    public View getHeadView() {
        return mHeadView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == HEAD) {
            ViewParent viewParent = mHeadView.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(mHeadView);
            }
            HeadVh headVh = new HeadVh(mHeadView);
            headVh.setIsRecyclable(false);
            return headVh;
        }
        return new Vh(mInflater.inflate(R.layout.item_shop_home, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof Vh) {
            ((Vh) vh).setData(position, mList.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
//        L.e("MainMallAdapter", "---onViewAttachedToWindow---->" + position);
        if (position == 0) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (null != lp && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);//占满一行
            }
        } else {
            GoodsSimpleBean bean = mList.get(position - 1);
            if (holder instanceof Vh) {
                ((Vh) holder).changeHeight(bean);
            }
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
//                if (layoutManager != null) {
//                    layoutManager.invalidateSpanAssignments(); //防止第一行到顶部有空白区域
//                }
//            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int[] firstVisibleItem = null;
                    firstVisibleItem = layoutManager.findFirstVisibleItemPositions(firstVisibleItem);
                    if (firstVisibleItem != null) {
                        if (firstVisibleItem[0] <= 2) {
                            if (mNeedRefreshData) {
                                mNeedRefreshData = false;
                                layoutManager.invalidateSpanAssignments();
                            }
                        } else {
                            mNeedRefreshData = true;
                        }

                    }
                }

            }

        });


    }

    class HeadVh extends RecyclerView.ViewHolder {
        public HeadVh(View itemView) {
            super(itemView);
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mThumb;
        TextView mName;
        TextView mPirce;
        TextView mSaleNum;
        TextView mOriginPrice;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mName = itemView.findViewById(R.id.name);
            mPirce = itemView.findViewById(R.id.price);
            mSaleNum = itemView.findViewById(R.id.sale_num);
            mOriginPrice = itemView.findViewById(R.id.origin_price);
            mOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(int position, final GoodsSimpleBean bean) {
            itemView.setTag(bean);
//            L.e("MainMallAdapter", "---onBindViewHolder---->" + position);
            Integer height = mMap.get(bean.getThumb());
            if (height == null) {
                ImgLoader.displayDrawable(mContext, bean.getThumb(), new ImgLoader.DrawableCallback() {
                    @Override
                    public void onLoadSuccess(Drawable drawable) {
                        float w = drawable.getIntrinsicWidth();
                        float h = drawable.getIntrinsicHeight();
                        ViewGroup.LayoutParams lp = mThumb.getLayoutParams();
                        int height = (int) (h / w * mThumbWidth);
                        if (height > mThumbHeightMax) {
                            height = mThumbHeightMax;
                        }
                        mMap.put(bean.getThumb(), height);
                        if (lp.height != height) {
                            lp.height = height;
                            mThumb.requestLayout();
                        }
                        mThumb.setImageDrawable(drawable);
                    }

                    @Override
                    public void onLoadFailed() {
                        ViewGroup.LayoutParams lp = mThumb.getLayoutParams();
                        lp.height = mThumbWidth;
                        mThumb.requestLayout();
                        mThumb.setImageDrawable(null);
                    }
                });
            } else {
                ViewGroup.LayoutParams lp = mThumb.getLayoutParams();
                if (lp.height != height) {
                    lp.height = height;
                    mThumb.requestLayout();
                }
                ImgLoader.display(mContext, bean.getThumb(), mThumb);
            }

            mName.setText(bean.getName());
            mPirce.setText(bean.getPrice());
            if (bean.getType() == 1) {
                mSaleNum.setText(null);
                mOriginPrice.setText(bean.getOriginPrice());
            } else {
//                Log.d("Sunday","bean.getSaleNum():"+bean.getSaleNum());
                mSaleNum.setText(String.format(mSaleString, bean.getSaleNum()));
                mOriginPrice.setText(null);
            }
        }


        public void changeHeight(GoodsSimpleBean bean) {
            Integer height = mMap.get(bean.getThumb());
            if (height != null) {
                ViewGroup.LayoutParams lp = mThumb.getLayoutParams();
                if (lp.height != height) {
                    lp.height = height;
                    mThumb.requestLayout();
                }
            }
        }

    }
}