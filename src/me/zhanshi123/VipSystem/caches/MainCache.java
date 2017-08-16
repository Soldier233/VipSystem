package me.zhanshi123.VipSystem.caches;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.bukkit.Bukkit;

import me.zhanshi123.VipSystem.Info;
import me.zhanshi123.VipSystem.Main;

public class MainCache extends Cache
{
	boolean debug=false;
	Connection conn=null;
	Statement st=null;
	Statement st1=null;
	public MainCache(Connection conn)
	{
		try
		{
			this.conn=conn;
			st=conn.createStatement();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	@Override
	public HashMap<String,Info> getData()
	{
		HashMap<String,Info> data=new HashMap<String,Info>();
		try
		{
			ResultSet rs=st.executeQuery("select * from `"+Main.getConfigManager().getPrefix()+"players`");
			while(rs.next())
			{
				Info info=new Info(rs.getString("player"),rs.getLong("time"),rs.getString("vipg"),rs.getInt("left"),rs.getInt("expired"));
				if(rs.getInt("expired")==1)
				{
					st1=conn.createStatement();
					st1.executeUpdate("delete from `"+Main.getConfigManager().getPrefix()+"players` where player = '"+info.getPlayer()+"';");
					st1.close();
				}
				else
				{
					data.put(rs.getString("player"),info);
				}
			}
			st.close();
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return data;
	}
	public Info getData(String name)
	{
		try {
			st=conn.createStatement();
			ResultSet rs=st.executeQuery("SELECT * from `"+Main.getConfigManager().getPrefix()+"players` where `player` = '"+name+"'");
			if(rs.next())
			{
				Info info=new Info(rs.getString("player"),rs.getLong("time"),rs.getString("vipg"),rs.getInt("left"),rs.getInt("expired"));
				return info;
			}
			else
			{
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
