package me.zhanshi123.VipSystem.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import me.zhanshi123.VipSystem.Main;

public class CacheSaveTask extends BukkitRunnable{

    @Override
    public void run() {
	Main.getDataBase().getCache();
    }

}
