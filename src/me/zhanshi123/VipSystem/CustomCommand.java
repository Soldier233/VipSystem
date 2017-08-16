package me.zhanshi123.VipSystem;

import java.util.List;

public class CustomCommand {
	String vip;
	List<String> cmds;
	public CustomCommand(String vip,List<String> cmds)
	{
		this.vip=vip;
		this.cmds=cmds;
	}
	public void setVip(String vip)
	{
		this.vip=vip;
	}
	public String getVip()
	{
		return vip;
	}
	public void setCmds(List<String> cmds)
	{
		this.cmds=cmds;
	}
	public List<String> getCmds()
	{
		return cmds;
	}
}
