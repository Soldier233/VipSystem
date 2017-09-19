package me.zhanshi123.VipSystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.zhanshi123.VipSystem.managers.ConfigManager;
import me.zhanshi123.VipSystem.managers.CustomCommandManager;
import me.zhanshi123.VipSystem.managers.MessageManager;

public class Commands implements CommandExecutor
{
	public void sendHelp(CommandSender sender)
	{
		for(int i=0;i<MessageManager.PlayerHelp.size();i++)
		{
			sender.sendMessage(MessageManager.PlayerHelp.get(i).replace('&', '¡ì'));
		}
		if(sender.isOp())
		{
			for(int i=0;i<MessageManager.AdminHelp.size();i++)
			{
				sender.sendMessage(MessageManager.AdminHelp.get(i).replace('&', '¡ì'));
			}
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
						sender.sendMessage((MessageManager.prefix+MessageManager.PlayerNotFound).replace('&', '¡ì'));
						return false;
					}
					String group=args[2];
					String day=args[3];
					long time=-1L;
					if(!day.equalsIgnoreCase("-1"))
						time=Utils.getTimeFromString(day);
					if(Main.getDataBase().exists(name))
					{
						if(Main.getDataBase().getGroup(name).equals(group)||Main.getDataBase().getGroup(name).equals("0"))
						{
							Utils.addVip(name, group, String.valueOf(time));
							Main.getPlaceholderCache().flushData(name);
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.VipGave));
							return true;
						}
						else
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.AlreadyHaveVip));
							return false;
						}
					}
					else
					{
						Utils.addVip(name, group, String.valueOf(time));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.VipGave));
						return true;
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
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.PlayerNotFound));
						return false;
					}
					if(Main.getDataBase().getExpired(name)==0)
					{
						Utils.removeVip(p);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.VipRemoved));
					}
					else
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.HaveNoVip));	
					}
				}
				else if(args[0].equalsIgnoreCase("list")&&sender.isOp())
				{
					sender.sendMessage((MessageManager.prefix+MessageManager.Showed).replace("&", "¡ì"));
					HashMap<String,Info> data=Main.getDataBase().getDatas();
					for(Entry<String,Info> e:data.entrySet())
					{
						sender.sendMessage(e.getValue().toString());
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
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.PlayerNotFound));
						return true;
					}
					String group=null;
					float days=0;
					if(Main.getDataBase().getExpired(name)==0)
					{
						List<String> date=Main.getDataBase().getDate(name);
						long time=Long.valueOf(date.get(0));
						String left=date.get(1);
						group=Main.getDataBase().getGroup(name);
						if(!left.equals("-1"))
						{
							Date now=new Date(time);
							days=Utils.calculateLeftDays(now, left);
							String dates=Utils.getExpiredDate(now, left);
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.QueriedByAdmin.replace("%player%", p.getName())
							.replace("%group%", group).replace("%date%", dates).replace("%left%", String.valueOf(days))));	
						}
						else
						{
							sender.sendMessage((MessageManager.prefix+MessageManager.QueriedByAdminPermanent).replace("%group%",group).replace("&", "¡ì")
									.replace("%player%", p.getName()));
						}
					}
					else
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.HaveNoVip));	
					}
				}
				else if(args[0].equalsIgnoreCase("save")&&sender.isOp())
				{
					Main.getDataBase().getCache();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.CacheSaved));	
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
							long time=Long.valueOf(date.get(0));
							String left=date.get(1);
							String group=Main.getDataBase().getGroup(name);
							if(!left.equals("-1"))
							{
								Date now=new Date(time);
								days=Utils.calculateLeftDays(now, left);
								String dates=Utils.getExpiredDate(now, left);
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.QueriedBySelf)
										.replace("%group%", group).replace("%left%", String.valueOf(days)).replace("%date%", dates));	
							}
							else
							{
								sender.sendMessage((MessageManager.prefix+MessageManager.QueriedBySelfPermanent).replace("&","¡ì").replace("%group%",group));
							}
						}
						else
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.YouHaveNoVip));	
						}
					}
				}
				else if(args[0].equalsIgnoreCase("reload")&&sender.isOp())
				{
					Main.cm=new ConfigManager(Main.getInstance());
					CustomCommandManager.getInstance().reload();
					sender.sendMessage((MessageManager.prefix+"¡ìaOK!").replace("&", "¡ì"));
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
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.ConsoleForbid));
						return true;
					}
					String name=Utils.getPlayerName(p);
					String cdk=args[1];
					
					if(!Main.getKeyManager().exists(cdk))
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.CodeNotFound));
					}
					else
					{
						Key key=Main.getKeyManager().getKey(cdk);
						if(Main.getDataBase().exists(name))
						{
							if(Main.getDataBase().getGroup(name).equals(key.getGroup()))
							{
								Utils.addVip(name, key.getGroup(), String.valueOf(key.getDays()));
								Main.getKeyManager().removeKey(key.getKey());
								String days=key.getDays();
								if(days.equals("-1"))
									days=MessageManager.permanent;
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.CodeActivated)
										.replace("%vip%", key.getGroup()).replace("%left%",days));
							}
							else
							{
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.YouAlreadyHaveVip));
							}
						}
						else
						{
							Utils.addVip(name, key.getGroup(), key.getDays());
							Main.getKeyManager().removeKey(key.getKey());
							String days=key.getDays();
							if(days.equals("-1"))
								days=MessageManager.permanent;
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.CodeActivated)
									.replace("%vip%", key.getGroup()).replace("%left%",days));
						}
					}
				}
				else if(args[0].equalsIgnoreCase("createkey")&&sender.isOp())
				{
					int amount=Integer.valueOf(args[1]);
					String group=args[2];
					String days=args[3];
					long time=Utils.getTimeFromString(days);
					String stime;
					if(days.equalsIgnoreCase("-1"))
					{
						stime=days;
					}
					else
					{
						stime=String.valueOf(time);
					}
					Main.getKeyManager().create(amount, group, stime);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.ThreadCreated));
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
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.CodeExport)
								.replace("%time%", String.valueOf(end-start)).replace("%path%", f.getAbsolutePath()));
					}
					catch(Exception e)
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.CodeExportFailed)
								.replace("%reason%", e.getMessage()));
					}
				}
			}
		}
		return true;
	}
}
