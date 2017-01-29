package me.zhanshi123.VipSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class DataBase {
	String type="SQLite",addr="jdbc:mysql://127.0.0.1:3306/vipsystem";
	String pwd,user;
	Connection conn=null;
	Statement statement=null;
	Plugin p=null;
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
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS `players` (`player` varchar(64) NOT NULL,`year` varchar(5) NOT NULL,`month` varchar(5) NOT NULL,`day` varchar(5) NOT NULL,`left` varchar(5) NOT NULL,`vipg` varchar(10) NOT NULL,PRIMARY KEY (`player`));");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS `vipkeys` (`key` varchar(64) NOT NULL,`vipg` varchar(5) NOT NULL,`day` varchar(5) NOT NULL,PRIMARY KEY (`player`));");
			statement.close();
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
		try {
			statement=conn.createStatement();
			ResultSet result=statement.executeQuery("select * from players where player='"+name+"'");
			if(!result.next())
			{
				date=null;
			}
			else
			{
				date.add(result.getString("year"));
				date.add(result.getString("month"));
				date.add(result.getString("day"));
				date.add(result.getString("left"));
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			date=null;
		}
		return date;
	}
	public String getGroup(String name)
	{
		String group=null;
		try {
			statement=conn.createStatement();
			ResultSet result=statement.executeQuery("select * from players where player='"+name+"'");
			if(result.next())
			{
				group=result.getString("vipg");
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return group;
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
	}
	public void mkdir(String name)
	{
		try {
			statement=conn.createStatement();
			statement.executeUpdate("INSERT INTO players VALUES ('" +name+ "', '0','0','0','0','0')");
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public boolean exists(String name)
	{
		boolean ex=false;
		try {
			statement=conn.createStatement();
			ResultSet result=statement.executeQuery("select * from players where player='"+name+"'");
			if(result.next())
			{
				ex=true;
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ex;
	}
	public void setDate(String name,String year,String month,String day,String left)
	{
		if(!exists(name))
		{
			mkdir(name);
		}
		try {
			statement=conn.createStatement();
			statement.executeUpdate("UPDATE players SET year = '"+year+"' WHERE player = '"+name+"';");
			statement.executeUpdate("UPDATE players SET month = '"+month+"' WHERE player = '"+name+"';");
			statement.executeUpdate("UPDATE players SET day = '"+day+"' WHERE player = '"+name+"';");
			statement.executeUpdate("UPDATE players SET left = '"+left+"' WHERE player = '"+name+"';");
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void removeVip(String name)
	{
		try {
		statement=conn.createStatement();
		statement.executeUpdate("DELETE FROM players WHERE player = '"+name+"'");
		statement.close();
	} catch (SQLException e) {
		e.printStackTrace();
	}
	}
	public void insertKey(String key,String group,String day)
	{
		try {
			ResultSet rs=statement.executeQuery("select * from vipkeys where key = '"+key+"'");
			if(!rs.next())
			{
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void setGroup(String name,String group)
	{
		try {
			statement=conn.createStatement();
			statement.executeUpdate("UPDATE players SET vipg = '"+group+"' WHERE player = '"+name+"';");
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
}
