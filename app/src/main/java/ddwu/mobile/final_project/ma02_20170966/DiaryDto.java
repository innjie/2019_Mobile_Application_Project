package ddwu.mobile.final_project.ma02_20170966;

import java.io.Serializable;
/*
날짜별 할 일을 저장하기 위한 DTO
Intent 에 저장 가능하게 하기 위하여
Serializable 인터페이스를 구현함
*/

public class DiaryDto implements Serializable {

    public long _id;
    private String month;
    private String day;
    private String title;
    private String comment;
    private String feel;
    private String location;
    private String weather;
    private String map;

    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
    }

    public long get_id() {return _id;}
    public void set_id(long _id) { this._id = _id; }

    public String getDay() {
        return day;
    }
    public void setDay(String day) {
        this.day = day;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String type) {
        this.comment = type;
    }

    public String getFeel() {
        return feel;
    }
    public void setFeel(String shop) {
        this.feel = shop;
    }

    @Override
    public String toString() {
        return month + " / " + day + title+ feel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public String getMap() {
        return map;
    }
    public void setMap(String map) {
        this.map = map;
    }
}