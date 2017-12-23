package me.zhanshi123.VipSystem.tasks;

import java.util.Collection;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.zhanshi123.VipSystem.Main;
import me.zhanshi123.VipSystem.Utils;
import me.zhanshi123.VipSystem.managers.MessageManager;

public class AllVipCheckTask extends BukkitRunnable {
    public void run() {
	Collection<Player> players = Utils.getOnlinePlayers();
	if (players == null) {
	    Bukkit.getConsoleSender().sendMessage(MessageManager.FailedToGetOnlinePlayers);
	    return;
	}
	for (final Player x : players) {
	    new VipCheckTask(x).runTask(Main.getInstance());
	}
    }
}
