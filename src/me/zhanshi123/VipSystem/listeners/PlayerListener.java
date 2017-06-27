package me.zhanshi123.VipSystem.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.zhanshi123.VipSystem.Main;
import me.zhanshi123.VipSystem.Utils;

public class PlayerListener implements Listener
{
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player x=e.getPlayer();
		String name=x.getName();
		if(Main.getDataBase().exists(name))
		{
			if(!Main.getDataBase().getGroup(name).equalsIgnoreCase(Main.getPermission().getPrimaryGroup(x)))
			{
				Main.getPermission().playerRemoveGroup(x, Main.getPermission().getPrimaryGroup(x));
				Main.getPermission().playerAddGroup(x, Main.getDataBase().getGroup(name));
			}
			if(Main.getDataBase().isPassed(name))
			{
				Utils.removeVip(x);
			}
		}
		Main.getPlaceholderCache().flushData(name);
	}
}
