package me.zhanshi123.VipSystem;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin
{
	public static DataBase getDataBase()
	{
		return db;
	}
	public static ConfigManager getConfigManager()
	{
		return cm;
	}
    Permission perm = null;
    Plugin plugin=this;
    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
        	perm = permissionProvider.getProvider();
        }
        return (perm != null);
    }
    private static DataBase db=null;
    private static ConfigManager cm=null;
	public void onDisable()
	{
		db.getCache();
		Bukkit.getScheduler().cancelTasks(plugin);
	}
	public void onEnable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l插件加载中..."));
		File f=new File(getDataFolder(),"config.yml");
		if(!f.exists())
		{
			saveDefaultConfig();
		}
		cm=new ConfigManager(plugin);
		setupPermissions();
		List<String> info=cm.getMySQL();
		String type=cm.getType();
		db=new DataBase(plugin,type);
		String url="jdbc:mysql://"+info.get(0)+":"+info.get(1)+"/"+info.get(2);
		if(type.equals("mysql"))
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[VipSystem] &a&l尝试向"+url+"建立MySQL连接，使用用户"+info.get(3)));
			db.MySQL(url, info.get(3), info.get(4));
		}
		if(!db.init())
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c&l插件发生致命错误，停止加载!"));
			setEnabled(false);
		}
		else
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[VipSystem] &a&l数据库连接成功"));
		}
		RegisterTasks();
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l插件加载完毕 作者QQ 1224954468 技术交流群563012939"));
	}
	public void sendHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l==========&a&lVipSystem&6&l=========="));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l- /vipsys viptime &9&l查询剩余VIP天数"));
		//sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l- /vipsys key <CDK> &9&l使用cdk"));
		if(sender.isOp())
		{
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l- /vipsys look <玩家名> &9&l查询指定玩家VIP"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l- /vipsys give <玩家名> <VIP组> <天数> &9&l给目标玩家指定天数的VIP"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l- /vipsys remove <玩家名> &9&l移除目标玩家的VIP"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l- /vipsys save &9&l立即保存缓存(默认5分钟一次)"));
			//sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l- /vipsys createkey <数量> <VIP组> <天数> &9&l使用cdk"));
		}	
	}
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("vipsys"))
		{
			if(args.length==0)
			{
				sendHelp(sender);
			}
			else
			{
				if(args[0].equalsIgnoreCase("give")&&sender.isOp())
				{
					String name=args[1];
					String group=args[2];
					String day=args[3];
					if(db.exists(name))
					{
						if(db.getGroup(name).equals(group)||db.getGroup(name).equals("0"))
						{
							addVip(name, group, day);
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l成功发送VIP到指定玩家"));
						}
						else
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c&l该玩家还有别的VIP，请先移除那个VIP再进行操作"));
						}
					}
					else
					{
						addVip(name, group, day);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l成功发送VIP到指定玩家"));
					}
				}
				else if(args[0].equalsIgnoreCase("remove")&&sender.isOp())
				{
					String name=args[1];
					if(db.getExpired(name)==0)
					{
						removeVip(Bukkit.getPlayer(name));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l成功移除指定玩家的VIP"));	
					}
					else
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c&l该玩家不是VIP"));	
					}
				}
				else if(args[0].equalsIgnoreCase("look")&&sender.isOp())
				{
					String name=args[1];
					String group=null;
					long days=0;
					if(db.getExpired(name)==0)
					{
						List<String> date=db.getDate(name);
						String year=date.get(0);
						String month=date.get(1);
						String day=date.get(2);
						String left=date.get(3);
						SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
						try {
							Date now=format.parse(year+"-"+month+"-"+day);
							GregorianCalendar gc=new GregorianCalendar(); 
							gc.setTime(now);
							gc.add(5,Integer.valueOf(left));
							Date passed=gc.getTime();
							days=(passed.getTime()-new Date().getTime())/(1000*3600*24);  
							
						} catch (ParseException e) {
							e.printStackTrace();
						}	
						group=db.getGroup(name);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l"+name+"是"+group+"，剩余时间"+String.valueOf(days)+"天"));	
					}
					else
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l"+name+"没有VIP"));	
					}
				}
				else if(args[0].equalsIgnoreCase("save")&&sender.isOp())
				{
					db.getCache();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l保存完成!"));	
				}
				else if(args[0].equalsIgnoreCase("viptime"))
				{
					if(sender instanceof Player)
					{
						Player p=(Player) sender;
						String name=p.getName();
						long days=0;
						if(db.getExpired(name)==0)
						{
							List<String> date=db.getDate(name);
							String year=date.get(0);
							String month=date.get(1);
							String day=date.get(2);
							String left=date.get(3);
							SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
							try {
								Date now=format.parse(year+"-"+month+"-"+day);
								GregorianCalendar gc=new GregorianCalendar(); 
								gc.setTime(now);
								gc.add(5,Integer.valueOf(left));
								Date passed=gc.getTime();
								days=(passed.getTime()-new Date().getTime())/(1000*3600*24);  
								
							} catch (ParseException e) {
								e.printStackTrace();
							}	
							String group=db.getGroup(name);
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l你是"+group+"，剩余时间"+String.valueOf(days)+"天"));	
						}
						else
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l你没有VIP哦~"));	
						}
					}
				}
				else if(args[0].equalsIgnoreCase("key"))
				{
					String cdk=args[1];
					
				}
			}
		}
		return true;
	}
	public void RegisterTasks()
	{
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
				List<Player> players=(List<Player>) Bukkit.getOnlinePlayers();
				for(Player x:players)
				{
					String name=x.getName();
					if(db.exists(name))
					{
						if(!db.getGroup(name).equalsIgnoreCase(perm.getPrimaryGroup(x)))
						{
							perm.playerRemoveGroup(x, perm.getPrimaryGroup(x));
							perm.playerAddGroup(x, db.getGroup(name));
						}
						if(db.isPassed(name))
						{
							removeVip(x);
						}
					}
				}
			}
		}.runTaskTimer(plugin, 20*60L, 20*60L);
	}
	public void addVip(String name,String group,String day)
	{
		Player p=Bukkit.getPlayer(name);
		String last=perm.getPrimaryGroup(p);
		perm.playerRemoveGroup(p, perm.getPrimaryGroup(p));
		perm.playerAddGroup(p, group);
		db.addVip(name, group+"#"+last, day);
	}
	public void removeVip(Player p)
	{
		perm.playerRemoveGroup(p, db.getGroup(p.getName()));
		if(cm.getDefault().equalsIgnoreCase("#last"))
			perm.playerAddGroup(p, db.getLastGroup(p.getName()));
		else
			perm.playerAddGroup(p, cm.getDefault());
		db.removeVip(p.getName());
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player x=e.getPlayer();
		String name=x.getName();
		if(db.exists(name))
		{
			if(!db.getGroup(name).equalsIgnoreCase(perm.getPrimaryGroup(x)))
			{
				perm.playerRemoveGroup(x, perm.getPrimaryGroup(x));
				perm.playerAddGroup(x, db.getGroup(name));
			}
			if(db.isPassed(name))
			{
				removeVip(x);
			}
		}
	}
}