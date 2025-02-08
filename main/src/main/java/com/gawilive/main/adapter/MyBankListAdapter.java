package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.common.utils.GlideUtils;
import com.gawilive.main.R;
import com.gawilive.main.bean.BankModel;


/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/11/05
 * desc   : 可进行拷贝的副本
 */
public final class MyBankListAdapter extends AppAdapter<BankModel> {


    public MyBankListAdapter(Context context) {
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

        private TextView mTvBank;

        private ImageView mImgBankLogo;

        private ViewHolder() {
            super(R.layout.item_my_bank);
        }

        @Override
        public void onBindView(int position) {
            mTvBank = findViewById(R.id.tvBank);
            mImgBankLogo = findViewById(R.id.imgBankLogo);
            BankModel model = getItem(position);
            mTvBank.setText(model.getName() + "（" + model.getCard_no().substring(model.getCard_no().length() - 4) + "\t\t" + model.getRealname() + "）");

            GlideUtils.setImages(getContext(), model.getUrl(), mImgBankLogo, 4);

        }
    }
}