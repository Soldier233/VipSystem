package me.zhanshi123.VipSystem;

public class Info {
	long time;
	int left,expired;
	String player,group;
	public Info(String player,long time,String group,int left,int expired)
	{
		this.player=player;
		this.time=time;
		this.group=group;
		this.left=left;
		this.expired=expired;
	}
	public int getExpired()
	{
		return expired;
	}
	public void setExpired(int expired)
	{
		this.expired=expired;
	}
	public String getPlayer()
	{
		return player;
	}
	public void setPlayer(String player)
	{
		this.player = player;
	}
	public long getTime()
	{
		return time;
	}
	public void setTime(long time)
	{
		this.time = time;
	}
	public int getLeft()
	{
		return left;
	}
	public void setLeft(int left)
	{
		this.left=left;
	}
	public String getGroup()
	{
		return group;
	}
	public void setGroup(String group)
	{
		this.group = group;
	}
}
