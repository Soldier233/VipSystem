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
						return true;
					}
					String group=args[2];
					String day=args[3];
					if(Main.getDataBase().exists(name))
					{
						if(Main.getDataBase().getGroup(name).equals(group)||Main.getDataBase().getGroup(name).equals("0"))
						{
							Utils.addVip(name, group, String.valueOf(day));
							Main.getPlaceholderCache().flushData(name);
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.VipGave));
						}
						else
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.AlreadyHaveVip));
						}
					}
					else
					{
						Utils.addVip(name, group, day);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.VipGave));
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
						return true;
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
						String year=date.get(0);
						String month=date.get(1);
						String day=date.get(2);
						String left=date.get(3);
						SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
						Date now=null;
						try {
							now=format.parse(year+"-"+month+"-"+day);
							days=Utils.calculateLeftDays(now, left);
						} catch (ParseException e) {
							e.printStackTrace();
						}	
						group=Main.getDataBase().getGroup(name);
						String dates=Utils.getExpiredDate(now, left);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.QueriedByAdmin.replace("%player%", p.getName())
						.replace("%group%", group).replace("%date%", dates).replace("%left%", String.valueOf(days))));	
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
							String year=date.get(0);
							String month=date.get(1);
							String day=date.get(2);
							String left=date.get(3);
							SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
							Date now=null;
							try {
								now=format.parse(year+"-"+month+"-"+day);
								days=Utils.calculateLeftDays(now, left);
							} catch (ParseException e) {
								e.printStackTrace();
							}	
							String group=Main.getDataBase().getGroup(name);
							String dates=Utils.getExpiredDate(now, left);
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.QueriedBySelf)
									.replace("%group%", group).replace("%left%", String.valueOf(days)).replace("%date%", dates));	
						}
						else
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.YouHaveNoVip));	
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
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.CodeActivated)
										.replace("%vip%", key.getGroup()).replace("%left%", key.getDays()));
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
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageManager.prefix+MessageManager.CodeActivated)
									.replace("%vip%", key.getGroup()).replace("%left%", key.getDays()));
						}
					}
				}
				else if(args[0].equalsIgnoreCase("createkey")&&sender.isOp())
				{
					int amount=Integer.valueOf(args[1]);
					String group=args[2];
					String days=args[3];
					Main.getKeyManager().create(amount, group, days);
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
