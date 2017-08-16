package me.zhanshi123.VipSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Utils {
	public static Collection<Player> getOnlinePlayers()
	{
		Collection<Player> players=null;
		try {
			Class<?> clazz=Class.forName("org.bukkit.Bukkit");
			Method method=clazz.getMethod("getOnlinePlayers");
			if(method.getReturnType().equals(Collection.class))
			{
				players=(Collection<Player>) method.invoke(Bukkit.getServer());
			}
			else
			{
				players=Arrays.asList((Player[]) method.invoke(Bukkit.getServer()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return players;
	}
	public static String getExpiredDate(Date now,String left)
	{
		GregorianCalendar gc=new GregorianCalendar(); 
		gc.setTime(now);
		gc.add(Calendar.SECOND,Integer.valueOf(left));
		Date passed=gc.getTime();
		SimpleDateFormat format=new SimpleDateFormat(Main.getConfigManager().getDateFormat());
		return format.format(passed);
	}
	public static Date getExpriedDate(Date now,String left)
	{
		GregorianCalendar gc=new GregorianCalendar(); 
		gc.setTime(now);
		gc.add(Calendar.SECOND,Integer.valueOf(left));
		Date passed=gc.getTime();
		return passed;
	}
	public static float calculateLeftDays(Date now,String left)
	{
		GregorianCalendar gc=new GregorianCalendar(); 
		gc.setTime(now);
		gc.add(Calendar.SECOND,Integer.valueOf(left));
		Date passed=gc.getTime();
		float days=Float.valueOf(passed.getTime()-new Date().getTime())/Float.valueOf(1000*3600*24);
		float day=(float)(Math.round(days*100))/100;
		return day;
	}
	public static void addVip(String name,String group,String secs)
	{
		Player p;
		if(Main.getConfigManager().getUUIDMode())
		{
			p=Bukkit.getPlayer(UUID.fromString(name));
		}
		else
		{
			p=Bukkit.getPlayer(name);
		}
		String last=Main.getPermission().getPrimaryGroup(p);
		if(Integer.valueOf(secs)<=60)
		{
			int sec=Integer.valueOf(secs);
			long delay=sec*20L;
			new BukkitRunnable()
			{
				public void run()
				{
					if(p.isOnline())
						removeVip(p);
				}
			}.runTaskLater(Main.getInstance(), delay);
		}
		Main.getPermission().playerRemoveGroup(p, Main.getPermission().getPrimaryGroup(p));
		Main.getPermission().playerAddGroup(p, group);
		Main.getPlaceholderCache().flushData(name);
		Main.getDataBase().addVip(name, group+"#"+last, secs);
	}
	public static void removeVip(Player p)
	{
		Main.getPermission().playerRemoveGroup(p, Main.getDataBase().getGroup(Utils.getPlayerName(p)));
		if(Main.getConfigManager().getDefault().equalsIgnoreCase("#last"))
			Main.getPermission().playerAddGroup(p, Main.getDataBase().getLastGroup(Utils.getPlayerName(p)));
		else
			Main.getPermission().playerAddGroup(p, Main.getConfigManager().getDefault());
		Main.getPlaceholderCache().flushData(Utils.getPlayerName(p));
		Main.getDataBase().removeVip(Utils.getPlayerName(p));
	}
	
	public static void removeFromDatabase(Connection conn,String name)
	{
		try 
		{
			Statement st=conn.createStatement();
			st.executeUpdate("delete from `"+Main.getConfigManager().getPrefix()+"players` where player = '"+name+"';");
			st.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	static boolean debug=false;
	public static long saveCache(Connection conn,HashMap<String,Info> data)
	{
		long starttime=System.currentTimeMillis();
		List<String> players=new ArrayList<>();
		Iterator<Entry<String,Info>> i=data.entrySet().iterator();
		while(i.hasNext())
		{
			Entry<String,Info> e=i.next();
			String name=e.getKey();
			players.add(name);
		}
		
		for(String name:players)
		{
			try
			{
				if(debug)
				{
					Bukkit.getConsoleSender().sendMessage("±£´æÍæ¼Ò "+name);
				}
				Info info=data.get(name);
				PreparedStatement pst=conn.prepareStatement("select * from `"+Main.getConfigManager().getPrefix()+"players` where player = '"+name+"';");
				ResultSet rs=pst.executeQuery();
				if(rs.next())
				{
					Statement statement = conn.createStatement();
					statement.execute("UPDATE `"+Main.getConfigManager().getPrefix()+"players` SET `time`='"+info.getTime()+"',`left`='"+info.getLeft()+"',vipg='"+info.getGroup()+"',expired='"+info.getExpired()+"' WHERE player='"+name+"';");
					statement.close();
				}
				else
				{
					PreparedStatement pst1=conn.prepareStatement("insert into `"+Main.getConfigManager().getPrefix()+"players` values ('"+name+"','"+info.getTime()+"','"+info.getLeft()+"','"+info.getGroup()+"','"+info.getExpired()+"');");
					pst1.executeUpdate();
					pst1.close();
				}
				pst.close();
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		
		long stoptime=System.currentTimeMillis();
		return stoptime-starttime;
	}
	
	public static String getPlayerName(Player p)
	{
		if(Main.getConfigManager().getUUIDMode())
		{
			return p.getUniqueId().toString();
		}
		else
		{
			return p.getName();
		}
	}
	
	public static Player getPlayer(String uuid)
	{
		return Bukkit.getPlayer(UUID.fromString(uuid));
	}
}
