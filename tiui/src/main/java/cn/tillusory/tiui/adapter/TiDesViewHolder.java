package cn.tillusory.tiui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import cn.tillusory.tiui.R;

/**
 * Created by Anko on 2018/11/22.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiDesViewHolder extends RecyclerView.ViewHolder {

    public TextView tiTextTV;
    public ImageView tiImageIV;
    public View tiCover;
    public View tiShadow;

    public TiDesViewHolder(View itemView) {
        super(itemView);
        tiTextTV = itemView.findViewById(R.id.tiTextTV);
        tiImageIV = itemView.findViewById(R.id.tiImageIV);
        tiCover = itemView.findViewById(R.id.tiCover);
        tiShadow = itemView.findViewById(R.id.tiShadow);
    }

}