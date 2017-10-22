package me.zhanshi123.VipSystem.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class VersionManager
{
	Plugin p = null;
	private FileConfiguration config = new YamlConfiguration();
	File f;
	
	private static VersionManager instance;
	
	public VersionManager(Plugin p)
	{
		this.p = p;
		f = new File(p.getDataFolder(), "version.yml");
		if(!f.exists())
			p.saveResource("version.yml", false);
		try
		{
			config.load(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		instance=this;
	}
	
	public static VersionManager getInstance(){
		return instance;
	}
	
	public String getVersion(){
		return config.getString("version");
	}
	
	public void setVersion(String version){
		config.set("version", version);
		try
		{
			config.save(f);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
