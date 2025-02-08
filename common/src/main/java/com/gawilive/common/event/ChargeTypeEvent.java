package com.gawilive.common.event;

public class ChargeTypeEvent {
    private final String mPayType;

    public ChargeTypeEvent(String payType) {
        mPayType = payType;
    }

    public String getPayType() {
        return mPayType;
    }
}
