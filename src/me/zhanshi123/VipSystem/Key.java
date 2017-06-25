package me.zhanshi123.VipSystem;

import java.util.Random;

import org.bukkit.scheduler.BukkitRunnable;

import me.zhanshi123.VipSystem.managers.ConfigManager;

public class Key {
	DataBase db=null;
	ConfigManager cm=null;
	public Key(DataBase db,ConfigManager cm)
	{
		this.db=db;
		this.cm=cm;
	}
	public boolean create(int number,String group,String day)
	{
		boolean success=false;
		String list=cm.getKeyWords();
		new BukkitRunnable()
		{
			public void run()
			{
				for(int i=0;i<number;i++)
				{
					StringBuilder sb=new StringBuilder();
					for(int j=0;j<12;j++)
					{
						Random r=new Random(System.currentTimeMillis());
						char word=list.charAt(r.nextInt(list.length()-1));
						sb.append(word);
					}
					String key=sb.toString();
					db.insertKey(key, group, day);
				}
			}
		}.runTask(new Main());

		return success;
	}
}
