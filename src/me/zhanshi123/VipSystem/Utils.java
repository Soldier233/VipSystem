package me.zhanshi123.VipSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

public class Utils {
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
					Bukkit.getConsoleSender().sendMessage("保存玩家 "+name);
				}
				Info info=data.get(name);
				PreparedStatement pst=conn.prepareStatement("select * from players where player = '"+name+"';");
				ResultSet rs=pst.executeQuery();
				if(rs.next())
				{
					if(debug)
					{
						String sql="UPDATE players SET year='"+info.getYear()+"',month='"+info.getMonth()+"',day='"+info.getDay()+"',`left`='"+info.getLeft()+"',vipg='"+info.getGroup()+"',expired='"+info.getExpired()+"' WHERE player='"+name+"';";
						Bukkit.getConsoleSender().sendMessage("更新玩家语句 "+sql);
					}
					Statement statement = conn.createStatement();
					statement.execute("UPDATE players SET year='"+info.getYear()+"',month='"+info.getMonth()+"',day='"+info.getDay()+"',`left`='"+info.getLeft()+"',vipg='"+info.getGroup()+"',expired='"+info.getExpired()+"' WHERE player='"+name+"';");
					statement.close();
					//PreparedStatement st=conn.prepareStatement("UPDATE players SET year = '"+info.getYear()+"' , month = '"+info.getMonth()+"' , day = '"+info.getDay()+"' , left = '"+info.getLeft()+"' , vipg = '"+info.getGroup()+"' where player = '"+name+"';");
					Statement statement = conn.createStatement();
					statement.execute("UPDATE players SET year='"+info.getYear()+"',month='"+info.getMonth()+"',day='"+info.getDay()+"',left='"+info.getLeft()+"',vipg='"+info.getGroup()+"',expired='' WHERE player='"+name+"';");
					statement.close();
					//st.executeUpdate();
					//st.close();
				}
				else
				{
					PreparedStatement pst1=conn.prepareStatement("insert into players values ('"+name+"','"+info.getYear()+"','"+info.getMonth()+"','"+info.getDay()+"','"+info.getLeft()+"','"+info.getGroup()+"','"+info.getExpired()+"');");
					if(debug)
					{
						Bukkit.getConsoleSender().sendMessage(info.getInfoString()+" 对象不存在于数据库中");
					}
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
}
