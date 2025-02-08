package com.gawilive.main.bean;

/**
 * 抽奖基础信息获取
 */
public class DrawWinLotteryModel {

    private String id;
    private String title;
    private String number_one;
    private String number_two;
    private String number_three;
    private String number_four;
    private String number_five;
    private String number_string;
    private String cycle;
    private String is_finish;
    private String status;
    private String people;
    private String turntable_id;
    private String draw_time;
    private String addtime;
    private String uptime;

    private String already_purchased_count;

    public String getAlready_purchased_count() {
        return already_purchased_count;
    }

    public void setAlready_purchased_count(String already_purchased_count) {
        this.already_purchased_count = already_purchased_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumber_one() {
        return number_one;
    }

    public void setNumber_one(String number_one) {
        this.number_one = number_one;
    }

    public String getNumber_two() {
        return number_two;
    }

    public void setNumber_two(String number_two) {
        this.number_two = number_two;
    }

    public String getNumber_three() {
        return number_three;
    }

    public void setNumber_three(String number_three) {
        this.number_three = number_three;
    }

    public String getNumber_four() {
        return number_four;
    }

    public void setNumber_four(String number_four) {
        this.number_four = number_four;
    }

    public String getNumber_five() {
        return number_five;
    }

    public void setNumber_five(String number_five) {
        this.number_five = number_five;
    }

    public String getNumber_string() {
        return number_string;
    }

    public void setNumber_string(String number_string) {
        this.number_string = number_string;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getIs_finish() {
        return is_finish;
    }

    public void setIs_finish(String is_finish) {
        this.is_finish = is_finish;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getTurntable_id() {
        return turntable_id;
    }

    public void setTurntable_id(String turntable_id) {
        this.turntable_id = turntable_id;
    }

    public String getDraw_time() {
        return draw_time;
    }

    public void setDraw_time(String draw_time) {
        this.draw_time = draw_time;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }
}
