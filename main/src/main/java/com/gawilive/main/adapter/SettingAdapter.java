package com.gawilive.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.common.Constants;
import com.gawilive.main.R;
import com.gawilive.main.bean.SettingBean;

import java.util.List;

/**
 * Created by cxf on 2018/9/30.
 */

public class SettingAdapter extends RecyclerView.Adapter {

    private static final int NORMAL = 0;
    private static final int VERSION = 1;
    private static final int LAST = 2;
    private static final int CHECK = 3;
    private final Context mContext;
    private final List<SettingBean> mList;
    private final LayoutInflater mInflater;
    private final View.OnClickListener mOnClickListener;
    private ActionListener mActionListener;
    private final String mVersionString;
    private String mCacheString;
    private final Drawable mRadioCheckDrawable;
    private final Drawable mRadioUnCheckDrawable;
    private final View.OnClickListener mOnRadioBtnClickListener;


    public SettingAdapter(Context context, List<SettingBean> list, String version, String cache) {
        mContext = context;
        mList = list;
        mVersionString = version;
        mCacheString = cache;
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    SettingBean bean = mList.get(position);
                    if (mActionListener != null) {
                        mActionListener.onItemClick(bean, position);
                    }
                }
            }
        };
        mRadioCheckDrawable = ContextCompat.getDrawable(context, R.mipmap.icon_btn_radio_1);
        mRadioUnCheckDrawable = ContextCompat.getDrawable(context, R.mipmap.icon_btn_radio_0);
        mOnRadioBtnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                SettingBean settingBean = mList.get(position);
                settingBean.setChecked(!settingBean.isChecked());
                notifyItemChanged(position, Constants.PAYLOAD);
                if(mActionListener!=null){
                    mActionListener.onCheckChanged(settingBean);
                }
            }
        };

    }

    public void setCacheString(String cacheString) {
        mCacheString = cacheString;
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public int getItemViewType(int position) {
        SettingBean bean = mList.get(position);
        if (bean.getId() == Constants.SETTING_UPDATE_ID || bean.getId() == Constants.SETTING_CLEAR_CACHE) {
            return VERSION;
        } else if (bean.getId() == -1||bean.getId() == -2) {
            return CHECK;
        } else if (bean.isLast()) {
            return LAST;
        } else {
            return NORMAL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VERSION) {
            return new Vh2(mInflater.inflate(R.layout.item_setting_1, parent, false));
        } else if (viewType == CHECK) {
            return new RadioButtonVh(mInflater.inflate(R.layout.item_setting_3, parent, false));
        } else if (viewType == LAST) {
            return new Vh(mInflater.inflate(R.layout.item_setting_2, parent, false));
        } else {
            return new Vh(mInflater.inflate(R.layout.item_setting, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position), position);
        } else if (vh instanceof RadioButtonVh) {
            Object payload = payloads.size() > 0 ? payloads.get(0) : null;
            ((RadioButtonVh) vh).setData(mList.get(position), position, payload);
        } else {
            ((Vh2) vh).setData(mList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mName;

        public Vh(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(SettingBean bean, int position) {
            itemView.setTag(position);
            mName.setText(bean.getName());
        }
    }

    class Vh2 extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mText;

        public Vh2(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mText = itemView.findViewById(R.id.text);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(SettingBean bean, int position) {
            itemView.setTag(position);
            mName.setText(bean.getName());
            if (bean.getId() == Constants.SETTING_CLEAR_CACHE) {
                mText.setText(mCacheString);
            } else {
                mText.setText(mVersionString);
            }
        }
    }


    class RadioButtonVh extends RecyclerView.ViewHolder {

        TextView mName;
        ImageView mBtnRadio;

        public RadioButtonVh(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mBtnRadio = itemView.findViewById(R.id.btn_radio);
            mBtnRadio.setOnClickListener(mOnRadioBtnClickListener);
        }

        void setData(SettingBean bean, int position, Object payload) {
            if (payload == null) {
                mName.setText(bean.getName());
                mBtnRadio.setTag(position);
            }
            mBtnRadio.setImageDrawable(bean.isChecked() ? mRadioCheckDrawable : mRadioUnCheckDrawable);
        }
    }


    public interface ActionListener {
        void onItemClick(SettingBean bean, int position);

        void onCheckChanged(SettingBean bean);
    }


}
