package me.zhanshi123.VipSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import me.zhanshi123.VipSystem.caches.MainCache;

public class DataBase {
	String type="SQLite",addr="jdbc:mysql://127.0.0.1:3306/vipsystem";
	String pwd,user;
	Connection conn=null;
	Statement statement=null;
	PreparedStatement pst=null;
	Plugin p=null;
	public HashMap<String,Info> data=new HashMap<String,Info>();
	MainCache ca;
	public MainCache getMainCache()
	{
		return ca; 
	}
	public void getCache()
	{
		long start=System.currentTimeMillis();
		Utils.saveCache(conn, data);
		long end=System.currentTimeMillis();
		Long time=end-start;
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[VipSystem缓存系统] &a&l对缓存数据进行保存完成，花费了&c"+String.valueOf(time)+"&a&lms"));
	}
	public DataBase(Plugin p,String type)
	{
		this.p=p;
		this.type=type;
	}
	public void MySQL(String addr,String user,String pwd)
	{
		this.addr=addr;
		this.user=user;
		this.pwd=pwd;
	}
	public void executeUpdate(String sql)
	{
		try {
			statement=conn.createStatement();
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	public boolean init()
	{
		try {
			Class.forName("org.sqlite.JDBC");
			if(type.equalsIgnoreCase("sqlite"))
			{
				conn = DriverManager.getConnection("jdbc:sqlite:plugins/VipSystem/database.db");
			}
			else if(type.equalsIgnoreCase("mysql"))
			{
				conn = DriverManager.getConnection(addr,user,pwd);
			}
			statement = conn.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS `players` (`player` varchar(64) NOT NULL,`year` varchar(5) NOT NULL,`month` varchar(5) NOT NULL,`day` varchar(5) NOT NULL,`left` varchar(5) NOT NULL,`vipg` varchar(50) NOT NULL,`expired` varchar(3) NOT NULL,PRIMARY KEY (`player`));");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS `vipkeys` (`key` varchar(64) NOT NULL,`vipg` varchar(50) NOT NULL,`day` varchar(5) NOT NULL,PRIMARY KEY (`key`));");
			statement.close();
			ca=new MainCache(conn);
			return true;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c&l无法使用数据库"));
			return false;
		}
	}
	//String year=getDate(name).get(0);
	//String month=getDate(name).get(1);
	//String day=getDate(name).get(2);
	//String left=getDate(name).get(3);
	public List<String> getDate(String name)
	{
		List<String> date=new ArrayList<String>();
		if(data.containsKey(name))
		{
			Info info=data.get(name);
			date.add(String.valueOf(info.getYear()));
			date.add(String.valueOf(info.getMonth()));
			date.add(String.valueOf(info.getDay()));
			date.add(String.valueOf(info.getLeft()));
		}
		else
		{
			data=null;
		}
		return date;
	}
	public String getGroup(String name)
	{
		String group=data.get(name).getGroup();
		String currentGroup;
		if(group.contains("#"))
		{
			int hashmark=group.indexOf('#');
			currentGroup=group.substring(0,hashmark);
		}
		else
		{
			currentGroup=group;
		}
		return currentGroup;
	}
	public void addVip(String name,String group,String day)
	{
		Date date=new Date();
		int old=0;
		if(exists(name))
		{
			old=Integer.valueOf(getDate(name).get(3));
		}
		setDate(name, String.valueOf(date.getYear()+1900), String.valueOf(date.getMonth()+1), String.valueOf(date.getDate()), String.valueOf(old+Integer.valueOf(day)));
		setGroup(name,group);
		setExpired(name,0);
	}
	public String getLastGroup(String name)
	{
		String group=data.get(name).getGroup();
		int hashmark=group.indexOf('#');
		String lastGroup=group.substring(hashmark+1,group.length());
		return lastGroup;
	}
	public void mkdir(String name)
	{
		data.put(name, new Info(name,0,0,0,"0",0,1));
	}
	public int getExpired(String name)
	{
		if(exists(name))
		{
			Info info=data.get(name);
			return info.getExpired();
		}
		else
		{
			return 1;
		}
		//1过期 0没过期
	}
	public void setExpired(String name,int expired)
	{
		Info info=data.get(name);
		info.setExpired(expired);
		data.remove(name);
		data.put(name, info);
	}
	public boolean exists(String name)
	{
		boolean ex=false;
		ex=data.containsKey(name);
		return ex;
	}
	public void setDate(String name,String year,String month,String day,String left)
	{
		if(!exists(name))
		{
			mkdir(name);
		}
		Info info=data.get(name);
		info.setYear(Integer.valueOf(year));
		info.setMonth(Integer.valueOf(month));
		info.setDay(Integer.valueOf(day));
		info.setLeft(Integer.valueOf(left));
		data.remove(name);
		data.put(name, info);
	}
	public void removeVip(String name)
	{
		Info info=data.get(name);
		info.setLeft(0);
		info.setExpired(1);
		data.remove(name);
		Utils.removeFromDatabase(conn, name);
	}
	public void setGroup(String name,String group)
	{
		Info info=data.get(name);
		info.setGroup(group);
		data.remove(name);
		data.put(name, info);
	}
	//Return true if is passed
	public boolean isPassed(String name)
	{
		if(exists(name))
		{
			List<String> date=getDate(name);
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
				if(passed.before(new Date()))
				{
					return true;
				}
				else
				{
					return false;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	/*
	 * Key部分
	 */
	public boolean checkKey(String key)
	{
		try {
			statement=conn.createStatement();
			ResultSet rs=statement.executeQuery("select * from vipkeys where `key` = '"+key+"';");
			if(rs.next())
			{
				statement.close();
				return true; 
			}
			else
			{
				statement.close();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public void removeKey(String key)
	{
		try {
			statement=conn.createStatement();
			ResultSet rs=statement.executeQuery("select * from vipkeys where `key` = '"+key+"';");
			if(rs.next())
			{
				statement.executeUpdate("delete from vipkeys where `key` = '"+key+"';");
			}
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void insertKey(List<String> key,String group,String day)
	{
		try {
			statement=conn.createStatement();
			for(String x:key)
			{
				statement.executeUpdate("insert into vipkeys values ('"+x+"','"+group+"','"+day+"');");
			}
			statement.close();
		} catch (Exception e) {
			if(!e.getMessage().startsWith("Duplicate entry"))
				e.printStackTrace();
		}
	}
	public Key getKey(String key)
	{
		Key k=null;
		try
		{
			statement=conn.createStatement();
			ResultSet rs=statement.executeQuery("select * from vipkeys where `key` = '"+key+"';");
			if(rs.next())
			{
				String group=rs.getString("vipg");
				String days=rs.getString("day");
				k=new Key(key,group,days);
			}
			statement.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return k;
	}
	public List<String> getKeys(String group)
	{
		List<String> keys=new ArrayList<String>();
		try
		{
			statement=conn.createStatement();
			ResultSet rs=statement.executeQuery("select * from vipkeys where `vipg` = '"+group+"';");
			while(rs.next())
			{
				keys.add(rs.getString("key"));
			}
			statement.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return keys;
	}
}
