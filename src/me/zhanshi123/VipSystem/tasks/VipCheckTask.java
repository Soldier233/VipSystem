package me.zhanshi123.VipSystem.tasks;

import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.zhanshi123.VipSystem.Main;
import me.zhanshi123.VipSystem.Utils;

public class VipCheckTask extends BukkitRunnable {

    private Player x;

    public VipCheckTask(Player player) {
	this.x = player;
    }

    @Override
    public void run() {
	String name = Utils.getPlayerName(x);
	if (Main.getDataBase().exists(name)) {
	    if (!Main.getDataBase().getGroup(name).equalsIgnoreCase(Main.getPermission().getPrimaryGroup(x))) {
		Main.getPermission().playerRemoveGroup(x, Main.getPermission().getPrimaryGroup(x));
		if (Main.getConfigManager().isGlobal())
		    Main.getPermission().playerAddGroup(null, x, Main.getDataBase().getGroup(name));
		else
		    Main.getPermission().playerAddGroup(x, Main.getDataBase().getGroup(name));
	    }
	    String left = Main.getDataBase().getDate(name).get(1);
	    if (Long.valueOf(left) == -1L)
		return;
	    Date expired = Utils.getExpriedDate(Main.getDataBase().getActiveDate(name), left);
	    long millis = expired.getTime() - System.currentTimeMillis();
	    if (millis < 0) {
		Utils.removeVip(x.getName());
	    } else if (millis < 60000) {
		new VipDelayedRemoveTask(x).runTaskLater(Main.getInstance(), (millis / 1000) * 20L);
	    }
	}
    }

}
