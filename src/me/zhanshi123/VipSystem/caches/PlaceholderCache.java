package me.zhanshi123.VipSystem.caches;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.zhanshi123.VipSystem.Main;
import me.zhanshi123.VipSystem.Utils;
import me.zhanshi123.VipSystem.hook.placeholders.PlaceholderInfo;

public class PlaceholderCache extends Cache{
	HashMap<String, PlaceholderInfo> data = new HashMap<String,PlaceholderInfo>();
	@Override
	public HashMap<String, PlaceholderInfo> getData() {
		return data;
	}
	public void flushData(String name)
	{
		if(!Main.getDataBase().exists(name))
		{
			return;
		}
		List<String> date=Main.getDataBase().getDate(name);
		String year=date.get(0);
		String month=date.get(1);
		String day=date.get(2);
		String left=date.get(3);
		long days=0;
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date now=format.parse(year+"-"+month+"-"+day);
			days=Utils.calculateLeftDays(now, left);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(data.containsKey(name))
		{
			data.remove(name);
		}
		String vipGroup=Main.getDataBase().getGroup(name);
		String lastGroup=Main.getDataBase().getLastGroup(name);
		data.put(name, new PlaceholderInfo(vipGroup,lastGroup,days));
	}
}
