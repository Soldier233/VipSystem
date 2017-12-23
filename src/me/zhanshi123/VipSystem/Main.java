package me.zhanshi123.VipSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.anitech.versioncomprator.VersionComprator;

import me.zhanshi123.VipSystem.caches.PlaceholderCache;
import me.zhanshi123.VipSystem.hook.placeholders.Papi;
import me.zhanshi123.VipSystem.hook.vault.VaultHook;
import me.zhanshi123.VipSystem.listeners.PlayerListener;
import me.zhanshi123.VipSystem.managers.ConfigManager;
import me.zhanshi123.VipSystem.managers.CustomCommandManager;
import me.zhanshi123.VipSystem.managers.KeyManager;
import me.zhanshi123.VipSystem.managers.MessageManager;
import me.zhanshi123.VipSystem.managers.VersionManager;
import me.zhanshi123.VipSystem.metrics.Metrics;
import me.zhanshi123.VipSystem.tasks.CacheSaveTask;
import me.zhanshi123.VipSystem.tasks.UpdateCheckTask;
import me.zhanshi123.VipSystem.tasks.AllVipCheckTask;
import me.zhanshi123.VipSystem.tasks.VipDelayedRemoveTask;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {
    private static Main instance;
    private static PlaceholderCache pc;
    private static Update update;

    public static Update getUpdate() {
	return update;
    }

    public static void setUpdate(Update update) {
	Main.update = update;
    }

    public static KeyManager getKeyManager() {
	return km;
    }

    public static Main getInstance() {
	return instance;
    }

    public static PlaceholderCache getPlaceholderCache() {
	return pc;
    }

    public static Permission getPermission() {
	return perm;
    }

    public static DataBase getDataBase() {
	return db;
    }

    public static ConfigManager getConfigManager() {
	return cm;
    }

    private static Permission perm = null;
    private static KeyManager km = null;
    Plugin plugin = this;
    private static DataBase db = null;
    static ConfigManager cm = null;

    public void onDisable() {
	db.getCache(true);
	Bukkit.getScheduler().cancelTasks(plugin);
    }

    public void initConfig() {
	File f = new File(getDataFolder(), "config.yml");
	if (!f.exists()) {
	    saveDefaultConfig();
	}

	cm = new ConfigManager(plugin);
    }

    public boolean initDatabase() {
	Utils.debug("获取连接信息");
	List<String> info = cm.getMySQL();
	String type = cm.getType();
	Utils.debug("实例化数据库");
	db = new DataBase(plugin, type);
	String url = "jdbc:mysql://" + info.get(0) + ":" + info.get(1) + "/" + info.get(2);
	if (type.equals("mysql")) {
	    Utils.debug("配置MySQL连接信息");
	    db.MySQL(url, info.get(3), info.get(4));
	}
	Utils.debug("尝试连接到数据库");
	if (!db.init()) {
	    return false;
	} else {
	    return true;
	}
    }

    private void saveMessages() {
	saveResource("messages/zh_CN.yml", false);
	saveResource("messages/en.yml", false);
	saveResource("messages/zh_TW.yml", false);
    }

    @SuppressWarnings("unused")
    public void onEnable() {
	long start = System.currentTimeMillis();
	instance = this;
	saveMessages();
	initConfig();
	Utils.debug("初始化版本管理");
	new VersionManager(this);
	Utils.debug("初始化语言管理");
	new MessageManager();
	Utils.debug("同步版本信息");
	double version = Double.valueOf(VersionManager.getInstance().getVersion());
	VersionManager.getInstance().setVersion(getDescription().getVersion());
	Utils.debug("初始化数据库");
	if (initDatabase()) {
	    Utils.debug("数据库初始化成功");
	} else {
	    Bukkit.getConsoleSender().sendMessage(MessageManager.DatabaseInitError);
	    setEnabled(false);
	    Utils.debug("数据库初始化失败");
	    return;
	}
	Utils.debug("判断上一个版本和该版本并进行相关升级操作");
	String strver = String.valueOf(version);
	String[] array = strver.split("\\.");
	int firstVersion = Integer.valueOf(array[0]);
	int secondVersion = Integer.valueOf(array[1]);
	if (secondVersion < 7 && firstVersion == 1) {
	    db.executeUpdate("ALTER TABLE `" + cm.getPrefix()
		    + "players` MODIFY COLUMN `vipg`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `left`;");
	}
	if (secondVersion < 8 && firstVersion == 1) {
	    db.executeUpdate("ALTER TABLE `" + cm.getPrefix()
		    + "vipkeys` MODIFY COLUMN `vipg`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `key`;");
	}
	if (secondVersion < 15 && firstVersion == 1) {
	    cm.setPreifx("");
	    cm.setDateFormat("yyyy-MM-dd");
	}
	if (firstVersion < 2) {
	    HashMap<String, InfoOld> old = db.getOldData();
	    db.executeUpdate("DROP TABLE `" + cm.getPrefix() + "players`;");
	    db.executeUpdate("CREATE TABLE IF NOT EXISTS `" + Main.getConfigManager().getPrefix()
		    + "players` (`player` varchar(64) NOT NULL,`time` varchar(50) NOT NULL,`left` varchar(50) NOT NULL,`vipg` varchar(50) NOT NULL,`expired` varchar(3) NOT NULL,PRIMARY KEY (`player`));");
	    // db.executeUpdate("ALTER TABLE `"+cm.getPrefix()+"players` DROP
	    // COLUMN `year`, DROP COLUMN `month`, DROP COLUMN `day`, ADD COLUMN
	    // `time` varchar(50) NOT NULL AFTER `player`;");
	    // db.executeUpdate("ALTER TABLE `"+cm.getPrefix()+"players` MODIFY
	    // COLUMN `left` varchar(50) CHARACTER SET utf8 COLLATE
	    // utf8_general_ci NOT NULL AFTER `time`;");
	    db.executeUpdate("ALTER TABLE `" + cm.getPrefix()
		    + "vipkeys` MODIFY COLUMN `day`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `vipg`;");
	    db.insertNewData(old);
	    Bukkit.getConsoleSender().sendMessage(
		    MessageManager.prefix.replace("&", "§") + "§aData convert complete! From:" + strver + " To: 2.x");
	}
	Utils.debug("版本更新完成(如果没有升级则不处理)");
	Utils.debug("初始化变量缓存");
	pc = new PlaceholderCache();
	Utils.debug("初始化Vault链接");
	perm = new VaultHook(instance).getPermission();
	Utils.debug("注册命令");
	Bukkit.getPluginCommand("vipsys").setExecutor(new Commands());
	Utils.debug("注册任务");
	RegisterTasks();
	Utils.debug("初始化激活码管理器");
	km = new KeyManager();
	Utils.debug("初始化API");
	new VipAPI();
	Utils.debug("初始化自定义命令管理器");
	new CustomCommandManager();
	Utils.debug("注册监听器");
	Bukkit.getPluginManager().registerEvents(new PlayerListener(), instance);
	Utils.debug("初始化数据统计");
	Metrics metrics = new Metrics(this);
	Utils.debug("尝试注册PlaceholderAPI变量");
	if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
	    if (new Papi(this).hook()) {
		Utils.debug("PlaceholderAPI变量 注册成功");
	    }
	}
	Utils.debug("检查在线玩家");
	for (final Player x : Utils.getOnlinePlayers()) {
	    String name = Utils.getPlayerName(x);
	    Info info = Main.getDataBase().getMainCache().getData(name);
	    if (info == null) {
		return;
	    } else {
		Main.getDataBase().data.put(name, info);
	    }
	    new AllVipCheckTask().runTask(Main.getInstance());
	    Main.getPlaceholderCache().flushData(name);
	}
	long end = System.currentTimeMillis();
	Bukkit.getConsoleSender()
		.sendMessage(MessageManager.LoadingComplete.replace("%time%", String.valueOf(end - start)));
    }

    public void RegisterTasks() {
	Utils.debug("执行更新检查任务");
	new UpdateCheckTask().runTaskAsynchronously(plugin);
	Utils.debug("注册缓存保存任务");
	new CacheSaveTask().runTaskTimer(plugin, 0L, 20 * 300L);
	Utils.debug("注册VIP到期检查任务");
	new AllVipCheckTask().runTaskTimer(plugin, 20 * 60L, 20 * 60L);
    }
}