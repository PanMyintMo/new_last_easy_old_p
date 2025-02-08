package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.manager.ActivityManager;
import com.gawilive.main.R;
import com.gawilive.main.bean.TransferUserBean;

public class UserListAdapter extends AppAdapter<TransferUserBean> {

    public UserListAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        return getData() == null ? 0 : getData().size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private LinearLayout llSelectUser;
        private ImageView userHead;
        private TextView tvNickName;
        private TextView tvPhone;

        private ViewHolder() {
            super(R.layout.item_user_item);
        }

        @Override
        public void onBindView(int position) {
            initView();
            ImgLoader.displayAvatar(ActivityManager.getInstance().getTopActivity(), getItem(position).getAvatar(), userHead);
            tvNickName.setText(getItem(position).getUsername());
            tvPhone.setText(getItem(position).getAccount());
        }

        private void initView() {
            llSelectUser = findViewById(R.id.llSelectUser);
            userHead = findViewById(R.id.user_head);
            tvNickName = findViewById(R.id.tvNickName);
            tvPhone = findViewById(R.id.tvPhone);
        }
    }
}
