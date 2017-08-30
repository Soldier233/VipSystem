package me.zhanshi123.VipSystem.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import me.zhanshi123.VipSystem.CustomCommand;
import me.zhanshi123.VipSystem.Main;

public class CustomCommandManager {
	static CustomCommandManager ccm=null;
	public CustomCommandManager()
	{
		FileConfiguration config=Main.getConfigManager().getConfig();
		if(config.isConfigurationSection("Config.Commands"))
		{
			Set<String> keys=config.getConfigurationSection("Config.Commands").getKeys(false);
			for(String s:keys)
			{
				List<String> activate,expire;
				activate=config.getStringList("Config.Commands."+s+".activate");
				expire=config.getStringList("Config.Commands."+s+".expire");
				CustomCommand cc=new CustomCommand(s,activate,expire);
				register(cc);
			}
		}
		ccm=this;
	}
	public static CustomCommandManager getInstance()
	{
		return ccm;
	}
	public void reload()
	{
		data.clear();
		FileConfiguration config=Main.getConfigManager().getConfig();
		if(config.isConfigurationSection("Config.Commands"))
		{
			Set<String> keys=config.getConfigurationSection("Config.Commands").getKeys(false);
			for(String s:keys)
			{
				List<String> activate,expire;
				activate=config.getStringList("Config.Commands."+s+".activate");
				expire=config.getStringList("Config.Commands."+s+".expire");
				CustomCommand cc=new CustomCommand(s,activate,expire);
				register(cc);
			}
		}
		ccm=this;
	}
	//------·Ö¸î------
	private HashMap<String,CustomCommand> data=new HashMap<String,CustomCommand>();
	public void register(CustomCommand cc)
	{
		if(!data.containsKey(cc))
		{
			data.put(cc.getVip(), cc);
		}
	}
	public CustomCommand getCustomCommand(String vip)
	{
		CustomCommand cc=null;
		if(data.containsKey(vip))
		{
			cc=data.get(vip);
		}
		return cc;
	}
}
