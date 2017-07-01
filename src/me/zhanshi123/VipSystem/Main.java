package me.zhanshi123.VipSystem;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
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
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin
{
	private static Main instance;
	private static PlaceholderCache pc;
	@SuppressWarnings("deprecation")
	private double updateDetect()
	{
		double version=0.0D;
		Bukkit.getConsoleSender().sendMessage("§6§lVipSystem §7>>> §a开始检查VipSystem的更新");
		try {
			URL url=new URL("http://www.mcmhsj.net/VipSystem/config.yml");
			InputStream in=url.openStream();
			FileConfiguration f = new YamlConfiguration();
			f.load(in);
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
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l尝试向"+url+"建立MySQL连接，使用用户"+info.get(3)));
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
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l插件加载中..."));
		long start=System.currentTimeMillis();
		instance=this;
		initConfig();
		double version=Double.valueOf(cm.getVersion());
		cm.setVersion(getDescription().getVersion());
		if(initDatabase())
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l数据库连接成功"));
		}
		else
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c&l插件发生致命错误，停止加载!"));
			setEnabled(false);
			return;
		}
		if(version<1.7D)
		{
			db.executeUpdate("ALTER TABLE `players` MODIFY COLUMN `vipg`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `left`;");
		}
		if(version<1.8D)
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
		if(b==false)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l未找到PlaceholderAPI或链接失败!"));
		}
		long end=System.currentTimeMillis();
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l插件加载完成 用时"+(end-start)+"ms 作者QQ 1224954468 技术交流群563012939"));
	}
	
	public void RegisterTasks()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				double version=updateDetect();
				if(version==Double.valueOf(cm.getVersion()))
				{
					Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l你目前使用的是最新版的插件哦"));
				}
				else if(version==0.0D)
				{
					Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c&l无法获取最新版本！"));;
				}
				else if(version>Double.valueOf(cm.getVersion()))
				{
					Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l最新版本"+version+"已经发布了！快去更新吧 http://www.mcbbs.net/thread-666924-1-1.html"));
				}
				else
				{
					Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l版本获取异常！"));
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
				for(Player x:Bukkit.getOnlinePlayers())
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