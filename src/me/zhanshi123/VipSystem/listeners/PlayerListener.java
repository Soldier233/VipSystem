package me.zhanshi123.VipSystem.listeners;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.zhanshi123.VipSystem.Info;
import me.zhanshi123.VipSystem.Main;
import me.zhanshi123.VipSystem.Update;
import me.zhanshi123.VipSystem.Utils;
import me.zhanshi123.VipSystem.managers.MessageManager;
import me.zhanshi123.VipSystem.tasks.VipCheckTask;

public class PlayerListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
	final Player x = e.getPlayer();
	Utils.debug("��Ҽ�����Ϸ������黺�����Ƿ��и��������");
	String name = Utils.getPlayerName(x);
	Info info = Main.getDataBase().getMainCache().getData(name);
	if (info == null) {
	    return;
	} else {
	    Utils.debug("������û�и�������ݣ���ȡ����");
	    Main.getDataBase().data.put(name, info);
	}
	new VipCheckTask(x).runTask(Main.getInstance());
	Main.getPlaceholderCache().flushData(name);
    }
    @EventHandler
    public void onOpJoin(PlayerJoinEvent e){
	if(e.getPlayer().isOp()){
	    Player player=e.getPlayer();
	    Update update=Main.getUpdate();
	    player.sendMessage(MessageManager.NewUpdate.replace("%version%", String.valueOf(update.getVersion())).replace(
		    "%msg%", update.getMessage()));
	}
    }
}
