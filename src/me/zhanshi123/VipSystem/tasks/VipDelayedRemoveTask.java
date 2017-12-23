package me.zhanshi123.VipSystem.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.zhanshi123.VipSystem.Utils;

public class VipDelayedRemoveTask extends BukkitRunnable{
    private Player player;
    public VipDelayedRemoveTask(Player player){
	this.player=player;
    }
    public void run() {
	    if (player.isOnline())
		Utils.removeVip(player.getName());
	}

}
