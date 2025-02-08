package com.gawilive.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;
import com.tencent.qcloud.tuikit.tuichat.bean.ChatInfo;
import com.tencent.qcloud.tuikit.tuichat.classicui.page.TUIC2CChatFragment;
import com.tencent.qcloud.tuikit.tuichat.presenter.C2CChatPresenter;
import com.gawilive.common.R;
import com.gawilive.common.bean.UserBean;
import com.gawilive.common.utils.SoftHideKeyBoardUtil;

public class ChatActivity extends AbsActivity {

    private String chatID = "";

    private String title = "";

    private UserBean userBean;

    public static void start(Context context, String chatId, String title) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    public static void start(Context context, UserBean userBean) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("userBean", userBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SoftHideKeyBoardUtil.assistActivity(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void main() {
        super.main();
        chatID = getIntent().getStringExtra("chatId");
        title = getIntent().getStringExtra("title");
        userBean = getIntent().getParcelableExtra("userBean");
        setTitle(TextUtils.isEmpty(title) ? userBean.getUserNiceName() : title);
        sendSession();
    }


    private void sendSession() {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setId(TextUtils.isEmpty(chatID) ? userBean.getId() : chatID);
        Bundle bundle = new Bundle();
        bundle.putSerializable(TUIChatConstants.CHAT_INFO, chatInfo);
        TUIC2CChatFragment tuic2CChatFragment = new TUIC2CChatFragment();
        tuic2CChatFragment.setArguments(bundle);
        C2CChatPresenter presenter = new C2CChatPresenter();
        presenter.initListener();
        tuic2CChatFragment.setPresenter(presenter);
        Fragment fragment = tuic2CChatFragment;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment).commitAllowingStateLoss();
    }
}
