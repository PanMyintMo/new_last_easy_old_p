package com.gawilive.main.bean;

import java.util.List;

public class PaymentRecordsModel {

    private String cycle;

    private List<PaymentChildRecordsModel> data;


    private String total;

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public List<PaymentChildRecordsModel> getData() {
        return data;
    }

    public void setData(List<PaymentChildRecordsModel> data) {
        this.data = data;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}

