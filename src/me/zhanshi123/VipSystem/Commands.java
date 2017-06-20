package me.zhanshi123.VipSystem;

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
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l- /vipsys viptime &9&l��ѯʣ��VIP����"));
		//sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l- /vipsys key <CDK> &9&lʹ��cdk"));
		if(sender.isOp())
		{
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l- /vipsys look <�����> &9&l��ѯָ�����VIP"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l- /vipsys give <�����> <VIP��> <����> &9&l��Ŀ�����ָ��������VIP"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l- /vipsys remove <�����> &9&l�Ƴ�Ŀ����ҵ�VIP"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l- /vipsys save &9&l�������滺��(Ĭ��5����һ��)"));
			//sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l- /vipsys createkey <����> <VIP��> <����> &9&lʹ��cdk"));
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
					if(Main.getDataBase().exists(name))
					{
						if(Main.getDataBase().getGroup(name).equals(group)||Main.getDataBase().getGroup(name).equals("0"))
						{
							Utils.addVip(name, group, day);
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l�ɹ�����VIP��ָ�����"));
						}
						else
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c&l����һ��б��VIP�������Ƴ��Ǹ�VIP�ٽ��в���"));
						}
					}
					else
					{
						Utils.addVip(name, group, day);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l�ɹ�����VIP��ָ�����"));
					}
				}
				else if(args[0].equalsIgnoreCase("remove")&&sender.isOp())
				{
					String name=args[1];
					if(Main.getDataBase().getExpired(name)==0)
					{
						Utils.removeVip(Bukkit.getPlayer(name));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l�ɹ��Ƴ�ָ����ҵ�VIP"));	
					}
					else
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c&l����Ҳ���VIP"));	
					}
				}
				else if(args[0].equalsIgnoreCase("look")&&sender.isOp())
				{
					String name=args[1];
					String group=null;
					long days=0;
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
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l"+name+"��"+group+"��ʣ��ʱ��"+String.valueOf(days)+"��"));	
					}
					else
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l"+name+"û��VIP"));	
					}
				}
				else if(args[0].equalsIgnoreCase("save")&&sender.isOp())
				{
					Main.getDataBase().getCache();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l�������!"));	
				}
				else if(args[0].equalsIgnoreCase("viptime"))
				{
					if(sender instanceof Player)
					{
						Player p=(Player) sender;
						String name=p.getName();
						long days=0;
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
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l����"+group+"��ʣ��ʱ��"+String.valueOf(days)+"��"));	
						}
						else
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &a&l��û��VIPŶ~"));	
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
}
