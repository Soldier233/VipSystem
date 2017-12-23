package me.zhanshi123.VipSystem;

public class Key {
    String key, group, days;

    public Key(String key, String group, String days) {
	this.key = key;
	this.group = group;
	this.days = days;
    }

    public String getKey() {
	return key;
    }

    public String getGroup() {
	return group;
    }

    public String getDays() {
	return days;
    }

    public void setKey(String key) {
	this.key = key;
    }

    public void setGroup(String group) {
	this.group = group;
    }

    public void setDays(String days) {
	this.days = days;
    }
}
