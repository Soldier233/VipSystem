package me.zhanshi123.VipSystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor
{
	public void sendHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l==========&a&lVipSystem&6&l=========="));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l- &b/vipsys viptime &9查询剩余VIP天数"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l- &b/vipsys key <CDK> &9使用cdk"));
		if(sender.isOp())
		{
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l- &b/vipsys look <玩家名> &9查询指定玩家VIP"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l- &b/vipsys give <玩家名> <VIP组> <天数> &9给目标玩家指定天数的VIP"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l- &b/vipsys remove <玩家名> &9移除目标玩家的VIP"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l- &b/vipsys createkey <数量> <VIP组> <天数> &9创建cdk"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l- &b/vipsys export <VIP组> &9导出某个组的所有未使用的cdk"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l- &b/vipsys save &9立即保存缓存(默认5分钟一次)"));
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
					Player p=Bukkit.getPlayer(name);
					if(p!=null)
					{
						name=Utils.getPlayerName(p);
					}
					else
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c玩家不在线!"));
						return true;
					}
					String group=args[2];
					String day=args[3];
					if(Main.getDataBase().exists(name))
					{
						if(Main.getDataBase().getGroup(name).equals(group)||Main.getDataBase().getGroup(name).equals("0"))
						{
							Utils.addVip(name, group, String.valueOf(day),Main.getConfigManager().getUUIDMode());
							Main.getPlaceholderCache().flushData(name);
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l成功发送VIP到指定玩家"));
						}
						else
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c&l该玩家还有别的VIP，请先移除那个VIP再进行操作"));
						}
					}
					else
					{
						Utils.addVip(name, group, day,Main.getConfigManager().getUUIDMode());
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l成功发送VIP到指定玩家"));
					}
				}
				else if(args[0].equalsIgnoreCase("remove")&&sender.isOp())
				{
					String name=args[1];
					Player p=Bukkit.getPlayer(name);
					if(p!=null)
					{
						name=Utils.getPlayerName(p);
					}
					else
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c玩家不在线!"));
						return true;
					}
					if(Main.getDataBase().getExpired(name)==0)
					{
						Utils.removeVip(p);
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
					Player p=Bukkit.getPlayer(name);
					if(p!=null)
					{
						name=Utils.getPlayerName(p);
					}
					else
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c玩家不在线!"));
						return true;
					}
					String group=null;
					float days=0;
					if(Main.getDataBase().getExpired(name)==0)
					{
						List<String> date=Main.getDataBase().getDate(name);
						String year=date.get(0);
						String month=date.get(1);
						String day=date.get(2);
						String left=date.get(3);
						SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
						try {
							Date now=format.parse(year+"-"+month+"-"+day);
							days=Utils.calculateLeftDays(now, left);
						} catch (ParseException e) {
							e.printStackTrace();
						}	
						group=Main.getDataBase().getGroup(name);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l"+p.getName()+"是"+group+"，剩余时间"+String.valueOf(days)+"天"));	
					}
					else
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l"+p.getName()+"没有VIP"));	
					}
				}
				else if(args[0].equalsIgnoreCase("save")&&sender.isOp())
				{
					Main.getDataBase().getCache();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l保存完成!"));	
				}
				else if(args[0].equalsIgnoreCase("viptime"))
				{
					if(sender instanceof Player)
					{
						Player p=(Player) sender;
						String name=Utils.getPlayerName(p);
						float days=0;
						if(Main.getDataBase().getExpired(name)==0)
						{
							List<String> date=Main.getDataBase().getDate(name);
							String year=date.get(0);
							String month=date.get(1);
							String day=date.get(2);
							String left=date.get(3);
							SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
							try {
								Date now=format.parse(year+"-"+month+"-"+day);
								days=Utils.calculateLeftDays(now, left);
							} catch (ParseException e) {
								e.printStackTrace();
							}	
							String group=Main.getDataBase().getGroup(name);
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
					Player p;
					if(sender instanceof Player)
					{
						p=(Player) sender;
					}
					else
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c&l后台不能运行该命令"));
						return true;
					}
					String name=Utils.getPlayerName(p);
					String cdk=args[1];
					
					if(!Main.getKeyManager().exists(cdk))
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c&l该激活码不存在!"));
					}
					else
					{
						Key key=Main.getKeyManager().getKey(cdk);
						if(Main.getDataBase().exists(name))
						{
							if(Main.getDataBase().getGroup(name).equals(key.getGroup()))
							{
								Utils.addVip(name, key.getGroup(), String.valueOf(key.getDays()), Main.getConfigManager().getUUIDMode());
								Main.getKeyManager().removeKey(key.getKey());
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l成功使用激活码，你获得了"+key.getGroup()+" "+key.getDays()+"天"));
							}
							else
							{
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c&l对不起，你还有其他VIP，无法使用!"));
							}
						}
						else
						{
							Utils.addVip(name, key.getGroup(), key.getDays(), Main.getConfigManager().getUUIDMode());
							Main.getKeyManager().removeKey(key.getKey());
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l成功使用激活码，你获得了"+key.getGroup()+" "+key.getDays()+"天"));
						}
					}
				}
				else if(args[0].equalsIgnoreCase("createkey")&&sender.isOp())
				{
					int amount=Integer.valueOf(args[1]);
					String group=args[2];
					String days=args[3];
					Main.getKeyManager().create(amount, group, days);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l创建线程已发起!完成后会在后台提示"));
				}
				else if(args[0].equalsIgnoreCase("export")&&sender.isOp())
				{
					String group=args[1];
					long start=System.currentTimeMillis();
					List<String> list=Main.getDataBase().getKeys(group);
					File f=new File(Main.getInstance().getDataFolder(),"Keys-"+group+"-"+System.currentTimeMillis()+".txt");
					try
					{
						BufferedWriter bw=new BufferedWriter(new FileWriter(f));
						for(String x:list)
						{
							bw.write(x);
							bw.newLine();
						}
						bw.close();
						long end=System.currentTimeMillis();
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a导出成功，用时"+(end-start)+"ms。见"+f.getAbsolutePath()));
					}
					catch(Exception e)
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c导出失败!原因:"+e.getMessage()));
					}
				}
			}
		}
		return true;
	}
}
