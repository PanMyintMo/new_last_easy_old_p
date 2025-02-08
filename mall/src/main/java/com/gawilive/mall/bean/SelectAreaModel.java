package com.gawilive.mall.bean;

import android.text.TextUtils;

public class SelectAreaModel {

    private String code;
    private String province;
    private String city;
    private String area;

    private int checkProvincePosition;

    private int checkCityPosition;

    private int checkAreaPosition;

    public int getCheckProvincePosition() {
        return checkProvincePosition;
    }

    public void setCheckProvincePosition(int checkProvincePosition) {
        this.checkProvincePosition = checkProvincePosition;
    }

    public int getCheckCityPosition() {
        return checkCityPosition;
    }

    public void setCheckCityPosition(int checkCityPosition) {
        this.checkCityPosition = checkCityPosition;
    }

    public int getCheckAreaPosition() {
        return checkAreaPosition;
    }

    public void setCheckAreaPosition(int checkAreaPosition) {
        this.checkAreaPosition = checkAreaPosition;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvince() {
        return TextUtils.isEmpty(province) ? "" : province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return TextUtils.isEmpty(city) ? "" : city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return TextUtils.isEmpty(area) ? "" : area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
