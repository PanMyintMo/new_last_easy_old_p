package com.gawilive.main.bean;


public class PayUserBean {

    private String uid;
    private String key;
    private String pwd;
    private String account;
    private String username;
    private String paypwd;
    private String phone;

    private String avatar;

    private String money;

    private String certno;

    private String certname;

    public String getCertno() {
        return certno;
    }

    public PayUserBean setCertno(String certno) {
        this.certno = certno;
        return this;
    }

    public String getCertname() {
        return certname;
    }

    public PayUserBean setCertname(String certname) {
        this.certname = certname;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getUid() {
        return uid;
    }

    public PayUserBean setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getKey() {
        return key;
    }

    public PayUserBean setKey(String key) {
        this.key = key;
        return this;
    }

    public String getPwd() {
        return pwd;
    }

    public PayUserBean setPwd(String pwd) {
        this.pwd = pwd;
        return this;
    }

    public String getAccount() {
        return account;
    }

    public PayUserBean setAccount(String account) {
        this.account = account;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public PayUserBean setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPaypwd() {
        return paypwd;
    }

    public PayUserBean setPaypwd(String paypwd) {
        this.paypwd = paypwd;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public PayUserBean setPhone(String phone) {
        this.phone = phone;
        return this;
    }
}
