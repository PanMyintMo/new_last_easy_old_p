package com.gawilive.main.bean;

import java.util.List;

public class LotteryRecordModel {


    private String cycle;

    private List<LotteryRecordChildModel> data;

    public List<LotteryRecordChildModel> getData() {
        return data;
    }

    public void setData(List<LotteryRecordChildModel> data) {
        this.data = data;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }
}
