package com.gawilive.common.bean;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class AddressModel {


    private String code;
    private String province;
    private String city;
    private String area;

    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    private List<AddressModel> children;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
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

    public List<AddressModel> getChildren() {
        return children == null ? new ArrayList<>() : children;
    }

    public void setChildren(List<AddressModel> children) {
        this.children = children;
    }
}
