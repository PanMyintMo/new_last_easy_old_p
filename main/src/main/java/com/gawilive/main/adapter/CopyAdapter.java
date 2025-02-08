package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.main.R;


/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/05
 *    desc   : 可进行拷贝的副本
 */
public final class CopyAdapter extends AppAdapter<String> {

    public CopyAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private ViewHolder() {
            super(R.layout.item_active_vh_text);
        }

        @Override
        public void onBindView(int position) {

        }
    }
}