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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import me.zhanshi123.VipSystem.caches.MainCache;
import me.zhanshi123.VipSystem.managers.MessageManager;

public class DataBase
{
	String type = "SQLite", addr = "jdbc:mysql://127.0.0.1:3306/vipsystem";
	String pwd, user;
	Connection conn = null;
	Statement statement = null;
	PreparedStatement pst = null;
	Plugin p = null;
	public HashMap<String, Info> data = new HashMap<String, Info>();
	MainCache ca;

	public MainCache getMainCache()
	{
		return ca;
	}

	public void getCache()
	{
		long start = System.currentTimeMillis();
		Utils.saveCache(conn, data);
		long end = System.currentTimeMillis();
		Long time = end - start;
		Bukkit.getConsoleSender().sendMessage(MessageManager.gCacheSaved.replace("%time%", String.valueOf(time)));
	}

	public DataBase(Plugin p, String type)
	{
		this.p = p;
		this.type = type;
	}

	public void MySQL(String addr, String user, String pwd)
	{
		this.addr = addr;
		this.user = user;
		this.pwd = pwd;
	}

	public void executeUpdate(String sql)
	{
		try
		{
			statement = conn.createStatement();
			statement.executeUpdate(sql);
			statement.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

	public boolean init()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			if (type.equalsIgnoreCase("sqlite"))
			{
				conn = DriverManager.getConnection("jdbc:sqlite:plugins/VipSystem/database.db");
			}
			else if (type.equalsIgnoreCase("mysql"))
			{
				conn = DriverManager.getConnection(addr, user, pwd);
			}
			statement = conn.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + Main.getConfigManager().getPrefix()
					+ "players` (`player` varchar(64) NOT NULL,`time` varchar(50) NOT NULL,`left` varchar(50) NOT NULL,`vipg` varchar(50) NOT NULL,`expired` varchar(3) NOT NULL,PRIMARY KEY (`player`));");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + Main.getConfigManager().getPrefix()
					+ "vipkeys` (`key` varchar(64) NOT NULL,`vipg` varchar(50) NOT NULL,`day` varchar(50) NOT NULL,PRIMARY KEY (`key`));");
			statement.close();
			ca = new MainCache(conn);
			return true;
		}
		catch (ClassNotFoundException | SQLException e)
		{
			e.printStackTrace();
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lVipSystem &7>>> &c&l无法使用数据库"));
			return false;
		}
	}

	// String time=getDate(name).get(0);
	// String left=getDate(name).get(1);
	public List<String> getDate(String name)
	{
		List<String> date = new ArrayList<String>();
		if (data.containsKey(name))
		{
			Info info = data.get(name);
			date.add(String.valueOf(info.getTime()));
			date.add(String.valueOf(info.getLeft()));
		}
		else
		{
			data = null;
		}
		return date;
	}

	public Date getActiveDate(String name)
	{
		List<String> date = getDate(name);
		long time = Long.valueOf(date.get(0));
		Date now = new Date(time);
		return now;
	}

	public String getGroup(String name)
	{
		String group = data.get(name).getGroup();
		String currentGroup;
		if (group.contains("#"))
		{
			int hashmark = group.indexOf('#');
			currentGroup = group.substring(0, hashmark);
		}
		else
		{
			currentGroup = group;
		}
		return currentGroup;
	}

	public void addVip(String name, String group, String day)
	{
		int old = 0;
		if (exists(name))
		{
			old = Integer.valueOf(getDate(name).get(1));
		}
		if (day.equalsIgnoreCase("-1"))
		{
			setDate(name, System.currentTimeMillis(), day);
			setGroup(name, group);
			setExpired(name, 0);
		}
		else
		{
			setDate(name, System.currentTimeMillis(), String.valueOf(old + Integer.valueOf(day)));
			setGroup(name, group);
			setExpired(name, 0);
		}

	}

	public String getLastGroup(String name)
	{
		String group = data.get(name).getGroup();
		int hashmark = group.indexOf('#');
		String lastGroup = group.substring(hashmark + 1, group.length());
		return lastGroup;
	}

	public void mkdir(String name)
	{
		data.put(name, new Info(name, 0L, "0", 0, 1));
	}

	public int getExpired(String name)
	{
		if (exists(name))
		{
			Info info = data.get(name);
			return info.getExpired();
		}
		else
		{
			return 1;
		}
		// 1过期 0没过期
	}

	public void setExpired(String name, int expired)
	{
		Info info = data.get(name);
		info.setExpired(expired);
		data.remove(name);
		data.put(name, info);
	}

	public boolean exists(String name)
	{
		boolean ex = false;
		ex = data.containsKey(name);
		return ex;
	}

	public void setDate(String name, long time, String left)
	{
		if (!exists(name))
		{
			mkdir(name);
		}
		Info info = data.get(name);
		info.setTime(time);
		info.setLeft(Integer.valueOf(left));
		data.remove(name);
		data.put(name, info);
	}

	public void removeVip(String name)
	{
		Info info = data.get(name);
		info.setLeft(0);
		info.setExpired(1);
		data.remove(name);
		Utils.removeFromDatabase(conn, name);
	}

	public void setGroup(String name, String group)
	{
		Info info = data.get(name);
		info.setGroup(group);
		data.remove(name);
		data.put(name, info);
	}

	// Return true if is passed
	public boolean isPassed(String name)
	{
		if (exists(name))
		{
			List<String> date = getDate(name);
			long time = Long.valueOf(date.get(0));
			String left = date.get(1);
			Date now = new Date(time);
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(now);
			gc.add(Calendar.SECOND, Integer.valueOf(left));
			Date passed = gc.getTime();
			if (passed.before(new Date()))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	public HashMap<String, Info> getDatas()
	{
		HashMap<String, Info> data = new HashMap<String, Info>();
		try
		{
			statement = conn.createStatement();
			ResultSet rs = statement
					.executeQuery("SELECT * FROM `" + Main.getConfigManager().getPrefix() + "players`;");
			while (rs.next())
			{
				data.put(rs.getString("player"), new Info(rs.getString("player"), rs.getLong("time"),
						rs.getString("vipg"), rs.getInt("left"), rs.getInt("expired")));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return data;
	}

	/*
	 * Key部分
	 */
	public boolean checkKey(String key)
	{
		try
		{
			statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(
					"select * from " + Main.getConfigManager().getPrefix() + "vipkeys where `key` = '" + key + "';");
			if (rs.next())
			{
				statement.close();
				return true;
			}
			else
			{
				statement.close();
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public void removeKey(String key)
	{
		try
		{
			statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(
					"select * from " + Main.getConfigManager().getPrefix() + "vipkeys where `key` = '" + key + "';");
			if (rs.next())
			{
				statement.executeUpdate(
						"delete from " + Main.getConfigManager().getPrefix() + "vipkeys where `key` = '" + key + "';");
			}
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void insertKey(List<String> key, String group, String day)
	{
		try
		{
			statement = conn.createStatement();
			for (String x : key)
			{
				statement.executeUpdate("insert into `" + Main.getConfigManager().getPrefix() + "vipkeys` values ('" + x
						+ "','" + group + "','" + day + "');");
			}
			statement.close();
		}
		catch (Exception e)
		{
			if (!e.getMessage().startsWith("Duplicate entry"))
				e.printStackTrace();
		}
	}

	public Key getKey(String key)
	{
		Key k = null;
		try
		{
			statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(
					"select * from `" + Main.getConfigManager().getPrefix() + "vipkeys` where `key` = '" + key + "';");
			if (rs.next())
			{
				String group = rs.getString("vipg");
				String days = rs.getString("day");
				k = new Key(key, group, days);
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
		List<String> keys = new ArrayList<String>();
		try
		{
			statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("select * from `" + Main.getConfigManager().getPrefix()
					+ "vipkeys` where `vipg` = '" + group + "';");
			while (rs.next())
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

	//
	public HashMap<String, InfoOld> getOldData()
	{
		HashMap<String, InfoOld> data = new HashMap<String, InfoOld>();
		try
		{
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select * from `" + Main.getConfigManager().getPrefix() + "players`");
			while (rs.next())
			{
				InfoOld info = new InfoOld(rs.getString("player"), rs.getInt("year"), rs.getInt("month"),
						rs.getInt("day"), rs.getString("vipg"), rs.getInt("left"), rs.getInt("expired"));
				data.put(rs.getString("player"), info);
			}
			st.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return data;
	}

	public void insertNewData(HashMap<String, InfoOld> old)
	{
		HashMap<String, Info> newdata = new HashMap<String, Info>();
		for (Entry<String, InfoOld> e : old.entrySet())
		{
			InfoOld oi = e.getValue();
			String player = oi.getPlayer();
			String group = oi.getGroup();
			int expired = oi.getExpired();
			long left = oi.getLeft();
			String year = String.valueOf(oi.getYear());
			String month = String.valueOf(oi.getMonth());
			String day = String.valueOf(oi.getDay());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date now = null;
			try
			{
				now = format.parse(year + "-" + month + "-" + day);
			}
			catch (ParseException ex)
			{
				ex.printStackTrace();
			}
			long time = now.getTime();

			newdata.put(player, new Info(player, time, group, left * 86400, expired));
		}

		try
		{
			Statement st = conn.createStatement();
			for (Entry<String, Info> entry : newdata.entrySet())
			{
				Info info = entry.getValue();
				st.executeUpdate("insert into `" + Main.getConfigManager().getPrefix() + "players` values ('"
						+ info.getPlayer() + "','" + info.getTime() + "','" + info.getLeft() + "','" + info.getGroup()
						+ "','" + info.getExpired() + "');");
			}
			st.close();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}
	}
}
