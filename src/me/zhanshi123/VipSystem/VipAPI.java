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
	 *  @param name 玩家名<br>PlayerName
	 *  
	 *  @return 返回该玩家所在的VIP组，如果没有则返回null<br>Return the vip group of the player.<br>
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
	 *  @param name 玩家名<br>PlayerName
	 *  
	 *  @return 返回该玩家开通VIP前所在的组，如果没有则返回null<br>Return the group which the player is in before he get a VIP<br>
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
	 *  @param name 玩家名<br>PlayerName
	 *  
	 *  @return 返回玩家是否是VIP的boolean值<br>
	 *  Return the boolean value of player's VIP status
	 * 
	 **/
	public boolean isVip(String name)
	{
		return Main.getDataBase().exists(name);
	}
	
}
