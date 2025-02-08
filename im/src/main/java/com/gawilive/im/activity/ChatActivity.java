package com.gawilive.im.activity;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.utils.RouteUtil;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.im.R;
import com.gawilive.im.bean.ImUserBean;
import com.gawilive.im.views.ChatListViewHolder;

/**
 * Created by cxf on 2018/10/24.
 */

public class ChatActivity extends AbsActivity {

    private ChatListViewHolder mChatListViewHolder;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, ChatActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_list;
    }

    @Override
    protected void main() {
        mChatListViewHolder = new ChatListViewHolder(mContext, findViewById(R.id.root), ChatListViewHolder.TYPE_ACTIVITY);
        mChatListViewHolder.setActionListener(new ChatListViewHolder.ActionListener() {
            @Override
            public void onCloseClick() {
                onBackPressed();
            }

            @Override
            public void onItemClick(ImUserBean bean) {
                if (Constants.MALL_IM_ADMIN.equals(bean.getId())) {
                    if (CommonAppConfig.getInstance().isTeenagerType()) {
                        ToastUtil.show(com.gawilive.common.R.string.a_137);
                    }else{
                        RouteUtil.forward(mContext,"com.gawilive.mall.activity.OrderMessageActivity");
                    }
                } else {
                    ChatRoomActivity.forward(mContext, bean, bean.getAttent() == 1, false);
                }
            }
        });
        mChatListViewHolder.addToParent();
        mChatListViewHolder.loadData();
    }

    @Override
    protected void onDestroy() {
        if (mChatListViewHolder != null) {
            mChatListViewHolder.release();
        }
        super.onDestroy();
    }
}
