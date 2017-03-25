package me.zhanshi123.VipSystem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.bukkit.Bukkit;

public class Cache
{
	boolean debug=false;
	Connection conn=null;
	Statement st=null;
	public Cache(Connection conn)
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
	public HashMap<String,Info> getData()
	{
		HashMap<String,Info> data=new HashMap<String,Info>();
		try
		{
			ResultSet rs=st.executeQuery("select * from players");
			while(rs.next())
			{
				Info info=new Info(rs.getString("player"),rs.getInt("year"),rs.getInt("month"),rs.getInt("day"),rs.getString("vipg"),rs.getInt("left"),rs.getInt("expired"));
				data.put(rs.getString("player"),info);
				if(debug)
				{
					Bukkit.getConsoleSender().sendMessage(info.getInfoString());
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
}
