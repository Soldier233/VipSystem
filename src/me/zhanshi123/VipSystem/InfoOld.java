package me.zhanshi123.VipSystem;

public class InfoOld {
    int year, month, day, left, expired;
    String player, group;

    public InfoOld(String player, int year, int month, int day, String group, int left, int expired) {
	this.player = player;
	this.year = year;
	this.month = month;
	this.day = day;
	this.group = group;
	this.left = left;
	this.expired = expired;
    }

    public int getExpired() {
	return expired;
    }

    public void setExpired(int expired) {
	this.expired = expired;
    }

    public String getPlayer() {
	return player;
    }

    public void setPlayer(String player) {
	this.player = player;
    }

    public int getYear() {
	return year;
    }

    public void setYear(int year) {
	this.year = year;
    }

    public int getMonth() {
	return month;
    }

    public void setMonth(int month) {
	this.month = month;
    }

    public int getDay() {
	return day;
    }

    public void setDay(int day) {
	this.day = day;
    }

    public int getLeft() {
	return left;
    }

    public void setLeft(int left) {
	this.left = left;
    }

    public String getGroup() {
	return group;
    }

    public void setGroup(String group) {
	this.group = group;
    }

    public String getInfoString() {
	StringBuilder sb = new StringBuilder();
	sb.append(player).append(",").append(year).append(",").append(month).append(",").append(day).append(",")
		.append(left).append(",").append(group).append(",").append(expired);
	return sb.toString();
    }
}