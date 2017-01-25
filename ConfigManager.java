package me.zhanshi123.VipSystem;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
	Plugin p=null;
	FileConfiguration config=null;
	public ConfigManager(Plugin p)
	{
		this.p=p;
		config=p.getConfig();
	}
	public String getDefault()
	{
		return config.getString("Config.Default");
	}
	public List<String> getRanks()
	{
		return config.getStringList("Config.Ranks");
	}
	public String getMessage(String path)
	{
		String text=config.getString("Message."+path);
		text=ChatColor.translateAlternateColorCodes('&', text);
		return text;
	}
	public String getType()
	{
		if(config.getString("Config.DataBase.Type").equalsIgnoreCase("mysql"))
		{
			return "mysql";
		}
		else if(config.getString("Config.DataBase.Type").equalsIgnoreCase("sqlite"))
		{
			return "sqlite";
		}
		else
		{
			return "sqlite";
		}
	}
	public List<String> getMySQL()
	{
		List<String> text=new ArrayList<String>();
		text.add(config.getString("Config.Database.MySQL.addr"));
		text.add(config.getString("Config.Database.MySQL.user"));
		text.add(config.getString("Config.Database.MySQL.pwd"));
		return text;
	}
}
