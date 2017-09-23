package me.zhanshi123.VipSystem.listeners;

import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.zhanshi123.VipSystem.Info;
import me.zhanshi123.VipSystem.Main;
import me.zhanshi123.VipSystem.Utils;

public class PlayerListener implements Listener
{
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		final Player x = e.getPlayer();
		String name = Utils.getPlayerName(x);
		Info info = Main.getDataBase().getMainCache().getData(name);
		if (info == null)
		{
			return;
		}
		else
		{
			Main.getDataBase().data.put(name, info);
		}
		if (Main.getDataBase().exists(name))
		{
			if (!Main.getDataBase().getGroup(name).equalsIgnoreCase(Main.getPermission().getPrimaryGroup(x)))
			{
				Main.getPermission().playerRemoveGroup(x, Main.getPermission().getPrimaryGroup(x));
				Main.getPermission().playerAddGroup(x, Main.getDataBase().getGroup(name));
			}
			String left = Main.getDataBase().getDate(name).get(1);
			if (Long.valueOf(left) == -1L)
				return;
			Date expired = Utils.getExpriedDate(Main.getDataBase().getActiveDate(name), left);
			long millis = expired.getTime() - (new Date().getTime());
			if (millis < 0)
			{
				Utils.removeVip(x.getName());
			}
			else if (millis < 60000)
			{
				new BukkitRunnable()
				{
					public void run()
					{
						if (x.isOnline())
							Utils.removeVip(x.getName());
					}
				}.runTaskLater(Main.getInstance(), (millis / 1000) * 20L);
			}
		}
		Main.getPlaceholderCache().flushData(name);
	}
}
