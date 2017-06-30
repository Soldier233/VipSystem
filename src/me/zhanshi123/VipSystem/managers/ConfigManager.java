package me.zhanshi123.VipSystem.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
	Plugin p=null;
	private FileConfiguration config;
	public ConfigManager(Plugin p)
	{
		this.p=p;
		config=p.getConfig();
	}
	public boolean getUUIDMode()
	{
		
		return config.getBoolean("Config.UUID-Mode",false);
	}
	
	public String getVersion()
	{
		return config.getString("Version");
	}
	public void setVersion(String ver)
	{
		config.set("Version", ver);
	}
	public String getKeyWords()
	{
		return config.getString("Config.KeyWord");
	}
	public String getDefault()
	{
		return config.getString("Config.Default");
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
		text.add(config.getString("Config.DataBase.MySQL.addr"));//0
		text.add(config.getString("Config.DataBase.MySQL.port"));//1
		text.add(config.getString("Config.DataBase.MySQL.base"));//2
		text.add(config.getString("Config.DataBase.MySQL.user"));//3
		text.add(config.getString("Config.DataBase.MySQL.pwd"));//4
		return text;
	}
}
