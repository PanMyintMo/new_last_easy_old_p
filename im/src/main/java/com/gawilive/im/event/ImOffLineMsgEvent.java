package com.gawilive.im.event;

import com.gawilive.im.bean.ImUserBean;

/**
 * Created by cxf on 2018/7/20.
 * IM收到离线消息 事件
 */

public class ImOffLineMsgEvent {
    private final ImUserBean mBean;

    public ImOffLineMsgEvent(ImUserBean bean) {
        mBean = bean;
    }

}