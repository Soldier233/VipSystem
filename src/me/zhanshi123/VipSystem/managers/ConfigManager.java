package me.zhanshi123.VipSystem.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
	Plugin p=null;
	private FileConfiguration config= new YamlConfiguration();
	File f;
	public ConfigManager(Plugin p)
	{
		this.p=p;
		f=new File(p.getDataFolder(),"config.yml");
		try {
			config.load(new InputStreamReader(new FileInputStream(f),"UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void saveConfig()
	{
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public FileConfiguration getConfig()
	{
		return config;
	}
	public String getDateFormat()
	{
		return config.getString("Config.DateFormat");
	}
	public String getLanguage()
	{
		if(config.isString("Config.language"))
		{
			return config.getString("Config.language");
		}
		else
		{
			return "en";
		}
	}
	public String getDisplayString()
	{
		return config.getString("Config.DisplayString");
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
	public String getPrefix()
	{
		return config.getString("Config.DataBase.prefix");
	}
	public void setPreifx(String s)
	{
		config.set("Config.DataBase.prefix", s);
	}
	public void setDateFormat(String s)
	{
		config.set("Config.DateFormat", s);
	}
	
}
