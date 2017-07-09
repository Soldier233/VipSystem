package me.zhanshi123.VipSystem.managers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import me.zhanshi123.VipSystem.Key;
import me.zhanshi123.VipSystem.Main;

public class KeyManager {
	public KeyManager()
	{
		
	}
	public void create(int number,String group,String day)
	{
		String list=Main.getConfigManager().getKeyWords();
		List<String> keys=new ArrayList<String>();
		new BukkitRunnable()
		{
			public void run()
			{
				long start=System.currentTimeMillis();
				for(int i=0;i<number;i++)
				{
					StringBuilder sb=new StringBuilder();
					for(int j=0;j<16;j++)
					{
						Random r=new Random();
						char word=list.charAt(r.nextInt(list.length()-1));
						sb.append(word);
					}
					String key=sb.toString();
					keys.add(key);
				}
				Main.getDataBase().insertKey(keys, group, day);
				long end=System.currentTimeMillis();
				Bukkit.getConsoleSender().sendMessage("§6§lVipSystem §7>>> §a创建完成!如果有报错请仔细查阅问题!用时"+(end-start)+"ms");
			}
		}.runTaskAsynchronously(Main.getInstance());
	}
	public boolean exists(String key)
	{
		return Main.getDataBase().checkKey(key);
	}
	public void removeKey(String key)
	{
		Main.getDataBase().removeKey(key);
	}
	public Key getKey(String key)
	{
		return Main.getDataBase().getKey(key);
	}
}
