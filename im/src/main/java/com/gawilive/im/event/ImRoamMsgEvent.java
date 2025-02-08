package com.gawilive.im.event;


import com.gawilive.im.bean.ImUserBean;

/**
 * Created by cxf on 2018/7/20.
 * IM漫游消息 事件
 */

public class ImRoamMsgEvent {

    private final ImUserBean mBean;

    public ImRoamMsgEvent(ImUserBean bean){
        mBean=bean;
    }

}
