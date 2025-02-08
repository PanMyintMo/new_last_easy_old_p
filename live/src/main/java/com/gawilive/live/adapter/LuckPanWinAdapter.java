package com.gawilive.live.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.live.bean.TurntableGiftBean;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.live.R;

import java.util.List;

/**
 * Created by cxf on 2019/8/27.
 */

public class LuckPanWinAdapter extends RecyclerView.Adapter<LuckPanWinAdapter.Vh> {

    private final Context mContext;
    private final List<TurntableGiftBean> mList;
    private final LayoutInflater mInflater;


    public LuckPanWinAdapter(Context context, List<TurntableGiftBean> list) {
        mInflater = LayoutInflater.from(context);
        this.mContext=context;
        mList = list;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_luck_pan_win, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull LuckPanWinAdapter.Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        TextView mText;

        public Vh(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mText = itemView.findViewById(R.id.text);
        }

        public void setData(TurntableGiftBean turntableGiftBean, int position, Object payload) {
            if(payload==null){
                ImgLoader.display(mContext,turntableGiftBean.getThumb(),mImg);
                mText.setText(turntableGiftBean.getName()+"x"+turntableGiftBean.getNums());
            }

        }
    }
}
