package me.zhanshi123.VipSystem;

public class VipAPI 
{
	private static VipAPI instance=null;
	public VipAPI()
	{
		instance=this;
	}
	public static VipAPI getInstance()
	{
		return instance;
	}
	
	/**
	 *  @author Soldier
	 *  
	 *  @param name �����<br>PlayerName
	 *  
	 *  @return ���ظ�������ڵ�VIP�飬���û���򷵻�null<br>Return the vip group of the player.<br>
	 *  Return null if it doesn't exist.
	 *              
	 * 
	 **/
	public String getVipGroup(String name)
	{
		if(Main.getDataBase().exists(name))
		{
			return Main.getDataBase().getGroup(name);
		}
		else
		{
			return null;
		}
	}
	/**
	 *  @author Soldier
	 *  
	 *  @param name �����<br>PlayerName
	 *  
	 *  @return ���ظ���ҿ�ͨVIPǰ���ڵ��飬���û���򷵻�null<br>Return the group which the player is in before he get a VIP<br>
	 *  Return null if it doesn't exist.
	 *              
	 * 
	 **/
	public String getLastGroup(String name)
	{
		if(Main.getDataBase().exists(name))
		{
			return Main.getDataBase().getLastGroup(name);
		}
		else
		{
			return null;
		}
	}
	/**
	 *  @author Soldier
	 *  
	 *  @param name �����<br>PlayerName
	 *  
	 *  @return ��������Ƿ���VIP��booleanֵ<br>
	 *  Return the boolean value of player's VIP status
	 * 
	 **/
	public boolean isVip(String name)
	{
		return Main.getDataBase().exists(name);
	}
	
}
