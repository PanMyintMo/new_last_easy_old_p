package com.gawilive.main.bean;

public class PaySuccessModel {


    private String money;
    private String order_sn;
    private String skr;
    private String paytime;
    private String paytype;

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getSkr() {
        return skr;
    }

    public void setSkr(String skr) {
        this.skr = skr;
    }

    public String getPaytime() {
        return paytime;
    }

    public void setPaytime(String paytime) {
        this.paytime = paytime;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }
}
