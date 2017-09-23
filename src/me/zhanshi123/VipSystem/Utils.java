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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.zhanshi123.VipSystem.managers.CustomCommandManager;

public class Utils
{

	public static long getTimeFromString(String a)
	{
		String dMatches = "[0-9]+d";
		String hMatches = "[0-9]+h";
		String mMatches = "[0-9]+m";
		String sMatches = "[0-9]+s";
		long d = 0L;
		long h = 0L;
		long m = 0L;
		long s = 0L;
		Pattern p = Pattern.compile(dMatches);
		Matcher ma = p.matcher(a);
		if (ma.find())
		{
			d = getTime(ma.group());
		}
		p = Pattern.compile(hMatches);
		ma = p.matcher(a);
		if (ma.find())
		{
			h = getTime(ma.group());
		}
		p = Pattern.compile(mMatches);
		ma = p.matcher(a);
		if (ma.find())
		{
			m = getTime(ma.group());
		}
		p = Pattern.compile(sMatches);
		ma = p.matcher(a);
		if (ma.find())
		{
			s = getTime(ma.group());
		}
		long time = d * 86400L;
		time += h * 3600L;
		time += m * 60L;
		time += s * 1L;
		return time;
	}

	private static long getTime(String a)
	{
		Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(a);
		if (m.find())
		{
			return Long.parseLong(m.group());
		}
		return 0L;
	}

	public static Collection<Player> getOnlinePlayers()
	{
		Collection<Player> players = null;
		try
		{
			Class<?> clazz = Class.forName("org.bukkit.Bukkit");
			Method method = clazz.getMethod("getOnlinePlayers");
			if (method.getReturnType().equals(Collection.class))
			{
				players = (Collection<Player>) method.invoke(Bukkit.getServer());
			}
			else
			{
				players = Arrays.asList((Player[]) method.invoke(Bukkit.getServer()));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return players;
	}

	public static String getExpiredDate(Date now, String left)
	{
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(now);
		gc.add(Calendar.SECOND, Integer.valueOf(left));
		Date passed = gc.getTime();
		SimpleDateFormat format = new SimpleDateFormat(Main.getConfigManager().getDateFormat());
		return format.format(passed);
	}

	public static Date getExpriedDate(Date now, String left)
	{
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(now);
		gc.add(Calendar.SECOND, Integer.valueOf(left));
		Date passed = gc.getTime();
		return passed;
	}

	public static float calculateLeftDays(Date now, String left)
	{
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(now);
		gc.add(Calendar.SECOND, Integer.valueOf(left));
		Date passed = gc.getTime();
		float days = Float.valueOf(passed.getTime() - new Date().getTime()) / Float.valueOf(1000 * 3600 * 24);
		float day = (float) (Math.round(days * 100)) / 100;
		return day;
	}

	public static void addVip(String name, String group, String secs)
	{
		Player p;
		if (Main.getConfigManager().getUUIDMode())
		{
			p = Bukkit.getPlayer(UUID.fromString(name));
		}
		else
		{
			p = Bukkit.getPlayer(name);
		}
		final Player player = p;
		String last = Main.getPermission().getPrimaryGroup(p);
		String olast = null;
		if (Main.getDataBase().exists(name))
		{
			olast = Main.getDataBase().getLastGroup(name);
		}
		if (!secs.equalsIgnoreCase("-1"))
		{
			if (Integer.valueOf(secs) < 60 && Integer.valueOf(secs) >= 0)
			{
				List<String> date = Main.getDataBase().getDate(name);
				if (date == null)
				{
					// 新开的情况下
					int sec = Integer.valueOf(secs);
					long delay = sec * 20L;
					new BukkitRunnable()
					{
						public void run()
						{
							if (player.isOnline())
								removeVip(player.getName());
						}
					}.runTaskLater(Main.getInstance(), delay);
				}
				else
				{
					// 续费的情况下
					if ((Integer.valueOf(secs) + Integer.valueOf(date.get(1))) < 60)
					{
						int sec = Integer.valueOf(secs);
						long delay = sec * 20L;
						new BukkitRunnable()
						{
							public void run()
							{
								if (player.isOnline())
									removeVip(player.getName());
							}
						}.runTaskLater(Main.getInstance(), delay);
					}
				}

			}
		}
		Main.getPermission().playerRemoveGroup(p, Main.getPermission().getPrimaryGroup(p));
		Main.getPermission().playerAddGroup(p, group);
		Main.getPlaceholderCache().flushData(name);
		if (last.equalsIgnoreCase(group))
		{
			Main.getDataBase().addVip(name, group + "#" + olast, secs);
		}
		else
		{
			Main.getDataBase().addVip(name, group + "#" + last, secs);
		}
		CustomCommand cc = CustomCommandManager.getInstance().getCustomCommand(group);
		if (cc != null)
		{
			for (String x : cc.getActivate())
			{
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), x.replace("%player%", p.getName()));
			}
		}
	}

	public static void removeVip(String name)
	{
		Player p = Bukkit.getPlayer(name);
		String group = Main.getDataBase().getGroup(Utils.getPlayerName(p));
		Main.getPermission().playerRemoveGroup(p, group);
		if (Main.getConfigManager().getDefault().equalsIgnoreCase("#last"))
			Main.getPermission().playerAddGroup(p, Main.getDataBase().getLastGroup(Utils.getPlayerName(p)));
		else
			Main.getPermission().playerAddGroup(p, Main.getConfigManager().getDefault());
		Main.getPlaceholderCache().flushData(Utils.getPlayerName(p));
		Main.getDataBase().removeVip(Utils.getPlayerName(p));
		CustomCommand cc = CustomCommandManager.getInstance().getCustomCommand(group);
		if (cc != null)
		{
			for (String x : cc.getExpire())
			{
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), x.replace("%player%", p.getName()));
			}
		}
	}

	public static void removeFromDatabase(Connection conn, String name)
	{
		try
		{
			Statement st = conn.createStatement();
			st.executeUpdate(
					"delete from `" + Main.getConfigManager().getPrefix() + "players` where player = '" + name + "';");
			st.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void saveCacheSync(final Connection conn, final HashMap<String, Info> data)
	{
		List<String> players = new ArrayList<>();
		Iterator<Entry<String, Info>> i = data.entrySet().iterator();
		while (i.hasNext())
		{
			Entry<String, Info> e = i.next();
			String name = e.getKey();
			players.add(name);
		}
		try
		{
			PreparedStatement query = conn.prepareStatement(
					"SELECT * FROM `" + Main.getConfigManager().getPrefix() + "players` WHERE `player` = ?;");
			PreparedStatement update = conn.prepareStatement(
					"UPDATE `" + Main.getConfigManager().getPrefix() + "players` SET `time` = ?,"
							+ " `left` = ?, `vipg`= ?, `expired` = ? WHERE `player` = ?;");
			PreparedStatement insert = conn.prepareStatement(
					"INSERT INTO `" + Main.getConfigManager().getPrefix() + "players` VALUES(?,?,?,?,?);");
			for (String name : players)
			{
				Info info = data.get(name);
				query.setString(1, name);
				ResultSet rs = query.executeQuery();
				if (rs.next())
				{
					update.setString(1, String.valueOf(info.getTime()));
					update.setString(2, String.valueOf(info.getLeft()));
					update.setString(3, info.getGroup());
					update.setString(4, String.valueOf(info.getExpired()));
					update.setString(5, name);
					update.executeUpdate();
				}
				else
				{
					insert.setString(1, name);
					insert.setString(2, String.valueOf(info.getTime()));
					insert.setString(3, String.valueOf(info.getLeft()));
					insert.setString(4, info.getGroup());
					insert.setString(5, String.valueOf(info.getExpired()));
					insert.executeUpdate();
				}
			}
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}
	}
	
	public static void saveCache(final Connection conn, final HashMap<String, Info> data)
	{
		new BukkitRunnable()
		{
			public void run()
			{
				List<String> players = new ArrayList<>();
				Iterator<Entry<String, Info>> i = data.entrySet().iterator();
				while (i.hasNext())
				{
					Entry<String, Info> e = i.next();
					String name = e.getKey();
					players.add(name);
				}
				try
				{
					PreparedStatement query = conn.prepareStatement(
							"SELECT * FROM `" + Main.getConfigManager().getPrefix() + "players` WHERE `player` = ?;");
					PreparedStatement update = conn.prepareStatement(
							"UPDATE `" + Main.getConfigManager().getPrefix() + "players` SET `time` = ?,"
									+ " `left` = ?, `vipg`= ?, `expired` = ? WHERE `player` = ?;");
					PreparedStatement insert = conn.prepareStatement(
							"INSERT INTO `" + Main.getConfigManager().getPrefix() + "players` VALUES(?,?,?,?,?);");
					for (String name : players)
					{
						Info info = data.get(name);
						query.setString(1, name);
						ResultSet rs = query.executeQuery();
						if (rs.next())
						{
							update.setString(1, String.valueOf(info.getTime()));
							update.setString(2, String.valueOf(info.getLeft()));
							update.setString(3, info.getGroup());
							update.setString(4, String.valueOf(info.getExpired()));
							update.setString(5, name);
							update.executeUpdate();
						}
						else
						{
							insert.setString(1, name);
							insert.setString(2, String.valueOf(info.getTime()));
							insert.setString(3, String.valueOf(info.getLeft()));
							insert.setString(4, info.getGroup());
							insert.setString(5, String.valueOf(info.getExpired()));
							insert.executeUpdate();
						}
					}
				}
				catch (SQLException e1)
				{
					e1.printStackTrace();
				}
			}
		}.runTaskAsynchronously(Main.getInstance());
	}

	public static String getPlayerName(Player p)
	{
		if (Main.getConfigManager().getUUIDMode())
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
