package me.zhanshi123.VipSystem.hook.placeholders;

public class PlaceholderInfo {
	String vipGroup,lastGroup;
	long leftDays;
	public PlaceholderInfo(String vipGroup,String lastGroup,long leftDays)
	{
		this.vipGroup=vipGroup;
		this.lastGroup=lastGroup;
		this.leftDays=leftDays;
	}
	public void setVipGroup(String vipGroup)
	{
		this.vipGroup=vipGroup;
	}
	public void setlastGroup(String lastGroup)
	{
		this.lastGroup=lastGroup;
	}
	public void setleftDays(long leftDays)
	{
		this.leftDays=leftDays;
	}
	public String getVipGroup()
	{
		return vipGroup;
	}
	public String getLastGroup()
	{
		return lastGroup;
	}
	public long getLeftDays()
	{
		return leftDays;
	}
	@Override
	public String toString()
	{
		return new StringBuilder().append(vipGroup).append(" ").append(lastGroup).append(" ").append(leftDays).toString();
	}
}
