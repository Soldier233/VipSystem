package me.zhanshi123.VipSystem.caches;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.zhanshi123.VipSystem.Main;
import me.zhanshi123.VipSystem.Utils;
import me.zhanshi123.VipSystem.hook.placeholders.PlaceholderInfo;

public class PlaceholderCache implements Cache {
    HashMap<String, PlaceholderInfo> data = new HashMap<String, PlaceholderInfo>();

    @Override
    public HashMap<String, PlaceholderInfo> getData() {
	return data;
    }

    public void flushData(String name) {
	if (!Main.getDataBase().exists(name)) {
	    return;
	}
	List<String> date = Main.getDataBase().getDate(name);
	long time = Long.valueOf(date.get(0));
	String left = date.get(1);
	Date now = new Date(time);
	float days = Utils.calculateLeftDays(now, left);
	if (data.containsKey(name)) {
	    data.remove(name);
	}
	String vipGroup = Main.getDataBase().getGroup(name);
	String lastGroup = Main.getDataBase().getLastGroup(name);
	data.put(name, new PlaceholderInfo(vipGroup, lastGroup, days));
    }
}
