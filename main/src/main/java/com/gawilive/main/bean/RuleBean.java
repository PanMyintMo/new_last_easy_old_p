package com.gawilive.main.bean;

public class RuleBean {

    private String id;
    private String user_id;
    private String create_time;
    private String update_time;
    private String post_title;
    private String post_title_en;
    private String post_title_ar;
    private String post_content;
    private Object post_content_filtered;
    private String type;
    private String list_order;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_title_en() {
        return post_title_en;
    }

    public void setPost_title_en(String post_title_en) {
        this.post_title_en = post_title_en;
    }

    public String getPost_title_ar() {
        return post_title_ar;
    }

    public void setPost_title_ar(String post_title_ar) {
        this.post_title_ar = post_title_ar;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public Object getPost_content_filtered() {
        return post_content_filtered;
    }

    public void setPost_content_filtered(Object post_content_filtered) {
        this.post_content_filtered = post_content_filtered;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getList_order() {
        return list_order;
    }

    public void setList_order(String list_order) {
        this.list_order = list_order;
    }
}
