package me.zhanshi123.VipSystem.managers;

import java.sql.SQLException;
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
		new BukkitRunnable()
		{
			public void run()
			{
				for(int i=0;i<number;i++)
				{
					StringBuilder sb=new StringBuilder();
					for(int j=0;j<12;j++)
					{
						Random r=new Random();
						char word=list.charAt(r.nextInt(list.length()-1));
						sb.append(word);
					}
					String key=sb.toString();
					Main.getDataBase().insertKey(key, group, day);
				}
				Bukkit.getConsoleSender().sendMessage("§6§lVipSystem §7>>> §a创建完成!如果有报错请仔细查阅问题!");
			}
		}.runTask(Main.getInstance());
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
