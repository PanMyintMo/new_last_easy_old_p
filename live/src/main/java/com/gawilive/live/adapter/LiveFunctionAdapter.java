package com.gawilive.live.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.live.R;
import com.gawilive.live.activity.LiveActivity;
import com.gawilive.live.activity.LiveAnchorActivity;
import com.gawilive.live.bean.LiveFunctionBean;
import com.gawilive.common.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/9.
 */

public class LiveFunctionAdapter extends RecyclerView.Adapter<LiveFunctionAdapter.Vh> {

    private final List<LiveFunctionBean> mList;
    private final LayoutInflater mInflater;
    private final View.OnClickListener mOnClickListener;
    private OnItemClickListener<Integer> mOnItemClickListener;

    public LiveFunctionAdapter(Context context, boolean hasGame, boolean openFlash, boolean taskSwitch,boolean luckPanSwitch) {



        mList = new ArrayList<>();
        if (((LiveActivity) context).isVoiceChatRoom()) {
            if (context instanceof LiveAnchorActivity) {
                //mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_MUSIC, R.mipmap.icon_live_func_music, R.string.live_music));
                mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_SHARE, R.mipmap.icon_live_func_share, R.string.live_share));
                mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_RED_PACK, R.mipmap.icon_live_func_rp, R.string.live_red_pack));
                if (taskSwitch) {
                    mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_TASK, R.mipmap.icon_live_func_task, R.string.daily_task));
                }
//                mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_LUCK, R.mipmap.icon_live_func_luck, R.string.a_002));
            } else {
                mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_SHARE, R.mipmap.icon_live_func_share, R.string.live_share));
                if(!CommonAppConfig.getInstance().isTeenagerType()){
                    if(luckPanSwitch){
                        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_PAN, R.mipmap.icon_live_luck_pan, R.string.a_003));
                    }
                    mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_RED_PACK, R.mipmap.icon_live_func_rp, R.string.live_red_pack));
                    if (taskSwitch) {
                        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_TASK, R.mipmap.icon_live_func_task, R.string.daily_task));
                    }
                }
//                mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_LUCK, R.mipmap.icon_live_func_luck, R.string.a_002));
            }
        } else {

            mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_BEAUTY, R.mipmap.icon_live_func_beauty, R.string.live_beauty));
            mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_CAMERA, R.mipmap.icon_live_func_camera, R.string.live_camera));
           mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_FLASH, openFlash ? R.mipmap.icon_live_func_flash : R.mipmap.icon_live_func_flash_1, R.string.live_flash));
            mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_MUSIC, R.mipmap.icon_live_func_music, R.string.live_music));
            mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_SHARE, R.mipmap.icon_live_func_share, R.string.live_share));
            if (hasGame) {//含有游戏
                mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_GAME, R.mipmap.icon_live_func_game, R.string.live_game));
            }
          mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_RED_PACK, R.mipmap.icon_live_func_rp, R.string.live_red_pack));
           mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_LINK_MIC, R.mipmap.icon_live_func_lm, R.string.live_link_mic));
            mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_MIRROR, R.mipmap.icon_live_func_jx, R.string.live_mirror));
        }
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int functionID = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(functionID, 0);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<Integer> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_function, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mIcon;
        TextView mName;


        public Vh(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.icon);
            mName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveFunctionBean bean) {
            itemView.setTag(bean.getID());
            mIcon.setImageResource(bean.getIcon());
            mName.setText(bean.getName());
        }
    }
}
