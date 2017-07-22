package me.zhanshi123.VipSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.zhanshi123.VipSystem.caches.PlaceholderCache;
import me.zhanshi123.VipSystem.hook.placeholders.Papi;
import me.zhanshi123.VipSystem.hook.vault.VaultHook;
import me.zhanshi123.VipSystem.listeners.PlayerListener;
import me.zhanshi123.VipSystem.managers.ConfigManager;
import me.zhanshi123.VipSystem.managers.KeyManager;
import me.zhanshi123.VipSystem.managers.MessageManager;
import me.zhanshi123.VipSystem.metrics.Metrics;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin
{
	private static Main instance;
	private static PlaceholderCache pc;
	private double updateDetect()
	{
		double version=0.0D;
		Bukkit.getConsoleSender().sendMessage(MessageManager.StartDetecting);
		try {
			URL url=new URL("http://www.mcmhsj.net/VipSystem/config.yml");
			InputStream in=url.openStream();
			BufferedReader br=new BufferedReader(new InputStreamReader(in,"GBK"));
			FileConfiguration f = new YamlConfiguration();
			f.load(br);
			version=f.getDouble("Version");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}
	public static KeyManager getKeyManager()
	{
		return km;
	}
	public static Main getInstance()
	{
		return instance;
	}
	public static PlaceholderCache getPlaceholderCache()
	{
		return pc;
	}
	public static Permission getPermission()
	{
		return perm;
	}
	public static DataBase getDataBase()
	{
		return db;
	}
	public static ConfigManager getConfigManager()
	{
		return cm;
	}
    private static Permission perm = null;
    private static KeyManager km = null;
    Plugin plugin=this;
    private static DataBase db=null;
    private static ConfigManager cm=null;
	public void onDisable()
	{
		db.getCache();
		Bukkit.getScheduler().cancelTasks(plugin);
	}
	public void initConfig()
	{
		File f=new File(getDataFolder(),"config.yml");
		if(!f.exists())
		{
			saveDefaultConfig();
		}
		cm=new ConfigManager(plugin);
	}
	public boolean initDatabase()
	{
		List<String> info=cm.getMySQL();
		String type=cm.getType();
		db=new DataBase(plugin,type);
		String url="jdbc:mysql://"+info.get(0)+":"+info.get(1)+"/"+info.get(2);
		if(type.equals("mysql"))
		{
			db.MySQL(url, info.get(3), info.get(4));
		}
		if(!db.init())
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	public void onEnable()
	{
		long start=System.currentTimeMillis();
		instance=this;
		new MessageManager();
		initConfig();
		double version=Double.valueOf(cm.getVersion());
		cm.setVersion(getDescription().getVersion());
		saveConfig();
		if(initDatabase())
		{
			
		}
		else
		{
			Bukkit.getConsoleSender().sendMessage(MessageManager.DatabaseInitError);
			setEnabled(false);
			return;
		}
		String strver=String.valueOf(version);
		String[] array=strver.split("\\.");
		int firstVersion=Integer.valueOf(array[0]);
		int secondVersion=Integer.valueOf(array[1]);
		if(secondVersion<7)
		{
			db.executeUpdate("ALTER TABLE `players` MODIFY COLUMN `vipg`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `left`;");
		}
		if(secondVersion<8)
		{
			db.executeUpdate("ALTER TABLE `vipkeys` MODIFY COLUMN `vipg`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `key`;");
		}
		pc=new PlaceholderCache();
		perm = new VaultHook(instance).getPermission();
		Bukkit.getPluginCommand("vipsys").setExecutor(new Commands());
		RegisterTasks();
		km=new KeyManager();
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), instance);
		Metrics metrics = new Metrics(this);
		boolean b=false;
		if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
		{
			b=new Papi(this).hook();
		}
		long end=System.currentTimeMillis();
		Bukkit.getConsoleSender().sendMessage(MessageManager.LoadingComplete.replace("%time%", String.valueOf(end-start)));
	}
	
	public void RegisterTasks()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				double version=updateDetect();
				String strver=String.valueOf(version);
				String[] array=strver.split("\\.");
				int secondVersion=Integer.valueOf(array[1]);
				String strver1=String.valueOf(cm.getVersion());
				String[] array1=strver1.split("\\.");
				int secondVersion1=Integer.valueOf(array1[1]);
				if(version==Double.valueOf(cm.getVersion()))
				{
					Bukkit.getConsoleSender().sendMessage(MessageManager.LatestVersion);
				}
				else if(version==0.0D)
				{
					Bukkit.getConsoleSender().sendMessage(MessageManager.UpdateFailed);
				}
				else if(secondVersion>secondVersion1)
				{
					Bukkit.getConsoleSender().sendMessage(MessageManager.NewUpdate.replace("%version%", String.valueOf(version)));
				}
				else
				{
					Bukkit.getConsoleSender().sendMessage(MessageManager.UpdateFailed);
				}
			}
		}.runTaskAsynchronously(plugin);
		new BukkitRunnable()
		{
			public void run()
			{
				db.getCache();
			}
		}.runTaskTimer(plugin, 0L, 20*300L);
		new BukkitRunnable()
		{
			public void run()
			{
				Collection<Player> players=Utils.getOnlinePlayers();
				if(players==null)
				{
					Bukkit.getConsoleSender().sendMessage(MessageManager.FailedToGetOnlinePlayers);
					return;
				}
				for(Player x:players)
				{
					String name=Utils.getPlayerName(x);
					if(db.exists(name))
					{
						if(!db.getGroup(name).equalsIgnoreCase(perm.getPrimaryGroup(x)))
						{
							perm.playerRemoveGroup(x, perm.getPrimaryGroup(x));
							perm.playerAddGroup(x, db.getGroup(name));
						}
						if(db.isPassed(name))
						{
							Utils.removeVip(x);
						}
					}
				}
			}
		}.runTaskTimer(plugin, 20*60L, 20*60L);
	}
}