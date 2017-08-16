package me.zhanshi123.VipSystem;

import java.util.List;

public class CustomCommand {
	String vip;
	List<String> a,e;
	public CustomCommand(String vip,List<String> a,List<String> e)
	{
		this.vip=vip;
		this.a=a;
		this.e=e;
	}
	public void setVip(String vip)
	{
		this.vip=vip;
	}
	public String getVip()
	{
		return vip;
	}
	public void setActivate(List<String> a)
	{
		this.a=a;
	}
	public List<String> getActivate()
	{
		return a;
	}
	public void setExpire(List<String> e)
	{
		this.e=e;
    }
	public List<String> getExpire()
	{
		return e;
	}
}
