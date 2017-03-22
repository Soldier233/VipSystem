package me.zhanshi123.VipSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class Utils {
	
	public static long saveCache(Connection conn,HashMap<String,Info> data)
	{
		long starttime=System.currentTimeMillis();
		List<String> players=new ArrayList<String>();
		Iterator<Entry<String,Info>> i=data.entrySet().iterator();
		while(i.hasNext())
		{
			Entry<String,Info> e=i.next();
			String name=(String) e.getKey();
			players.add(name);
		}
		
		for(String name:players)
		{
			try
			{
				Info info=data.get(name);
				PreparedStatement pst=conn.prepareStatement("select * from players where player = '"+name+"';");
				ResultSet rs=pst.executeQuery();
				pst.close();
				if(rs.next())
				{
					PreparedStatement st=conn.prepareStatement("update players set year = '"+info.getYear()+"' , month = '"+info.getMonth()+"' , day = '"+info.getDay()+"' , left = '"+info.getLeft()+"' , vipg = '"+info.getYear()+"' where player = '"+name+"';");
					st.executeUpdate();
					st.close();
				}
				else
				{
					PreparedStatement pst1=conn.prepareStatement("insert into players values ('"+name+"','"+info.getYear()+"','"+info.getMonth()+"','"+info.getDay()+"','"+info.getLeft()+"','"+info.getGroup()+");");
					pst1.executeUpdate();
					pst1.close();
				}
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
