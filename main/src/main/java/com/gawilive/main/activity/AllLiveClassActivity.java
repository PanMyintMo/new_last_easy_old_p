package com.gawilive.main.activity;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.bean.ConfigBean;
import com.gawilive.common.bean.LiveClassBean;
import com.gawilive.common.interfaces.OnItemClickListener;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;
import com.gawilive.main.adapter.MainHomeLiveClassAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 云豹科技 on 2022/6/1.
 * 全部分类
 */
public class AllLiveClassActivity extends AbsActivity {

    public static void forward(Context context) {
        context.startActivity(new Intent(context, AllLiveClassActivity.class));
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_all_live_class;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.a_106));
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 5, GridLayoutManager.VERTICAL, false));
        List<LiveClassBean> classList = null;
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            classList = configBean.getLiveClass();
        }
        if (classList == null) {
            classList = new ArrayList<>();
        }
        MainHomeLiveClassAdapter classAdapter = new MainHomeLiveClassAdapter(mContext);
        classAdapter.setOnItemClickListener(new OnItemClickListener<LiveClassBean>() {
            @Override
            public void onItemClick(LiveClassBean bean, int position) {
                if (!canClick()) {
                    return;
                }
                LiveClassActivity.forward(mContext, bean.getId(), bean.getName());
            }
        });
        recyclerView.setAdapter(classAdapter);
        classAdapter.setData(classList);
    }


}
