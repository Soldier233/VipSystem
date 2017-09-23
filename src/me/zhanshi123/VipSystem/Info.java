package me.zhanshi123.VipSystem;

public class Info
{
	long time, left;
	int expired;
	String player, group;

	public Info(String player, long time, String group, long left, int expired)
	{
		this.player = player;
		this.time = time;
		this.group = group;
		this.left = left;
		this.expired = expired;
	}

	public int getExpired()
	{
		return expired;
	}

	public void setExpired(int expired)
	{
		this.expired = expired;
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

	public Long getLeft()
	{
		return left;
	}

	public void setLeft(long left)
	{
		this.left = left;
	}

	public String getGroup()
	{
		return group;
	}

	public void setGroup(String group)
	{
		this.group = group;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(player).append(" ").append(time).append(" ").append(group).append(" ").append(left);
		return sb.toString();
	}
}
