package me.zhanshi123.VipSystem.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.anitech.versioncomprator.VersionComprator;
import com.google.gson.Gson;

import me.zhanshi123.VipSystem.Main;
import me.zhanshi123.VipSystem.Update;
import me.zhanshi123.VipSystem.managers.MessageManager;
import me.zhanshi123.VipSystem.managers.VersionManager;

public class UpdateCheckTask extends BukkitRunnable {

    @Override
    public void run() {
	Update update = updateDetect();
	if(update==null){
	    return;
	}
	Main.setUpdate(update);
	double version=update.getVersion();
	String strver = String.valueOf(version);
	String strver1 = String.valueOf(VersionManager.getInstance().getVersion());
	VersionComprator compare = new VersionComprator();
	// 此处版本号判断使用的tapas4java的开源项目
	// 地址 https://github.com/tapas4java/VersionComprator
	if (version == Double.valueOf(VersionManager.getInstance().getVersion())) {
	    Bukkit.getConsoleSender().sendMessage(MessageManager.LatestVersion);
	} else if (version == 0.0D) {
	    Bukkit.getConsoleSender().sendMessage(MessageManager.UpdateFailed);
	} else if (compare.compare(strver, strver1) == 1) {
	    Bukkit.getConsoleSender()
		    .sendMessage(MessageManager.NewUpdate.replace("%version%", String.valueOf(version)).replace(
			    "%msg%", update.getMessage()));
	} else {
	    Bukkit.getConsoleSender().sendMessage(MessageManager.UpdateFailed);
	}
    }

    private Update updateDetect() {
	Update update=null;
	Bukkit.getConsoleSender().sendMessage(MessageManager.StartDetecting);
	try {
	    URL url = new URL("http://www.zhanshi123.me/update/?name=VipSystem");
	    InputStream in = url.openStream();
	    BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
	    String json=br.readLine();
	    update=new Gson().fromJson(json, Update.class);
	    } catch (Exception e) {
	    e.printStackTrace();
	}
	return update;
    }
}
