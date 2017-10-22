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
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin
{
	private static Main instance;
	private static PlaceholderCache pc;

	private double updateDetect()
	{
		double version = 0.0D;
		Bukkit.getConsoleSender().sendMessage(MessageManager.StartDetecting);
		try
		{
			URL url = new URL("http://www.mcmhsj.net/VipSystem/config.yml");
			InputStream in = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "GBK"));
			FileConfiguration f = new YamlConfiguration();
			f.load(br);
			version = f.getDouble("Version");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return version;
	}

	public static KeyManager getKeyManager()
	{
		return km;
	}

	public static Main getInstance()
	{
		return instance;
	}

	public static PlaceholderCache getPlaceholderCache()
	{
		return pc;
	}

	public static Permission getPermission()
	{
		return perm;
	}

	public static DataBase getDataBase()
	{
		return db;
	}

	public static ConfigManager getConfigManager()
	{
		return cm;
	}

	private static Permission perm = null;
	private static KeyManager km = null;
	Plugin plugin = this;
	private static DataBase db = null;
	static ConfigManager cm = null;

	public void onDisable()
	{
		db.getCache(true);
		Bukkit.getScheduler().cancelTasks(plugin);
	}

	public void initConfig()
	{
		File f = new File(getDataFolder(), "config.yml");
		if (!f.exists())
		{
			saveDefaultConfig();
		}

		cm = new ConfigManager(plugin);
	}

	public boolean initDatabase()
	{
		Utils.debug("��ȡ������Ϣ");
		List<String> info = cm.getMySQL();
		String type = cm.getType();
		Utils.debug("ʵ�������ݿ�");
		db = new DataBase(plugin, type);
		String url = "jdbc:mysql://" + info.get(0) + ":" + info.get(1) + "/" + info.get(2);
		if (type.equals("mysql"))
		{
			Utils.debug("����MySQL������Ϣ");
			db.MySQL(url, info.get(3), info.get(4));
		}
		Utils.debug("�������ӵ����ݿ�");
		if (!db.init())
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private void saveMessages()
	{
		saveResource("messages/zh_CN.yml", false);
		saveResource("messages/en.yml", false);
		saveResource("messages/zh_TW.yml", false);
	}

	@SuppressWarnings("unused")
	public void onEnable()
	{
		long start = System.currentTimeMillis();
		instance = this;
		saveMessages();
		initConfig();
		Utils.debug("��ʼ���汾����");
		new VersionManager(this);
		Utils.debug("��ʼ�����Թ���");
		new MessageManager();
		Utils.debug("ͬ���汾��Ϣ");
		double version = Double.valueOf(VersionManager.getInstance().getVersion());
		VersionManager.getInstance().setVersion(getDescription().getVersion());
		Utils.debug("��ʼ�����ݿ�");
		if (initDatabase())
		{
			Utils.debug("���ݿ��ʼ���ɹ�");
		}
		else
		{
			Bukkit.getConsoleSender().sendMessage(MessageManager.DatabaseInitError);
			setEnabled(false);
			Utils.debug("���ݿ��ʼ��ʧ��");
			return;
		}
		Utils.debug("�ж���һ���汾�͸ð汾�����������������");
		String strver = String.valueOf(version);
		String[] array = strver.split("\\.");
		int firstVersion = Integer.valueOf(array[0]);
		int secondVersion = Integer.valueOf(array[1]);
		if (secondVersion < 7 && firstVersion == 1)
		{
			db.executeUpdate("ALTER TABLE `" + cm.getPrefix()
					+ "players` MODIFY COLUMN `vipg`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `left`;");
		}
		if (secondVersion < 8 && firstVersion == 1)
		{
			db.executeUpdate("ALTER TABLE `" + cm.getPrefix()
					+ "vipkeys` MODIFY COLUMN `vipg`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `key`;");
		}
		if (secondVersion < 15 && firstVersion == 1)
		{
			cm.setPreifx("");
			cm.setDateFormat("yyyy-MM-dd");
		}
		if (firstVersion < 2)
		{
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
					MessageManager.prefix.replace("&", "��") + "��aData convert complete! From:" + strver + " To: 2.x");
		}
		Utils.debug("�汾�������(���û�������򲻴���)");
		Utils.debug("��ʼ����������");
		pc = new PlaceholderCache();
		Utils.debug("��ʼ��Vault����");
		perm = new VaultHook(instance).getPermission();
		Utils.debug("ע������");
		Bukkit.getPluginCommand("vipsys").setExecutor(new Commands());
		Utils.debug("ע������");
		RegisterTasks();
		Utils.debug("��ʼ�������������");
		km = new KeyManager();
		Utils.debug("��ʼ��API");
		new VipAPI();
		Utils.debug("��ʼ���Զ������������");
		new CustomCommandManager();
		Utils.debug("ע�������");
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), instance);
		Utils.debug("��ʼ������ͳ��");
		Metrics metrics = new Metrics(this);
		Utils.debug("����ע��PlaceholderAPI����");
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
		{
			if(new Papi(this).hook()){
				Utils.debug("PlaceholderAPI���� ע��ɹ�");
			}
		}
		Utils.debug("����������");
		for (final Player x : Utils.getOnlinePlayers())
		{
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
					if(Main.getConfigManager().isGlobal())
						Main.getPermission().playerAddGroup(null, x, Main.getDataBase().getGroup(name));
					else
						Main.getPermission().playerAddGroup(x, Main.getDataBase().getGroup(name));
				}
				String left = db.getDate(name).get(1);
				if (Long.valueOf(left) == -1L)
					continue;
				Date expired = Utils.getExpriedDate(db.getActiveDate(name), left);
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
					}.runTaskLater(instance, (millis / 1000) * 20L);
				}
			}
			Main.getPlaceholderCache().flushData(name);
		}
		long end = System.currentTimeMillis();
		Bukkit.getConsoleSender()
				.sendMessage(MessageManager.LoadingComplete.replace("%time%", String.valueOf(end - start)));
	}

	public void RegisterTasks()
	{
		Utils.debug("ִ�и��¼������");
		new BukkitRunnable()
		{
			public void run()
			{
				double version = updateDetect();
				String strver = String.valueOf(version);
				String strver1 = String.valueOf(VersionManager.getInstance().getVersion());
				VersionComprator compare = new VersionComprator();
				// �˴��汾���ж�ʹ�õ�tapas4java�Ŀ�Դ��Ŀ
				// ��ַ https://github.com/tapas4java/VersionComprator
				if (version == Double.valueOf(VersionManager.getInstance().getVersion()))
				{
					Bukkit.getConsoleSender().sendMessage(MessageManager.LatestVersion);
				}
				else if (version == 0.0D)
				{
					Bukkit.getConsoleSender().sendMessage(MessageManager.UpdateFailed);
				}
				else if (compare.compare(strver, strver1) == 1)
				{
					Bukkit.getConsoleSender()
							.sendMessage(MessageManager.NewUpdate.replace("%version%", String.valueOf(version)));
				}
				else
				{
					Bukkit.getConsoleSender().sendMessage(MessageManager.UpdateFailed);
				}
			}
		}.runTaskAsynchronously(plugin);
		Utils.debug("ע�Ỻ�汣������");
		new BukkitRunnable()
		{
			public void run()
			{
				db.getCache();
			}
		}.runTaskTimer(plugin, 0L, 20 * 300L);
		Utils.debug("ע��VIP���ڼ������");
		new BukkitRunnable()
		{
			public void run()
			{
				Collection<Player> players = Utils.getOnlinePlayers();
				if (players == null)
				{
					Bukkit.getConsoleSender().sendMessage(MessageManager.FailedToGetOnlinePlayers);
					return;
				}
				for (final Player x : players)
				{
					String name = Utils.getPlayerName(x);
					if (db.exists(name))
					{
						if (!db.getGroup(name).equalsIgnoreCase(perm.getPrimaryGroup(x)))
						{
							perm.playerRemoveGroup(x, perm.getPrimaryGroup(x));
							if(Main.getConfigManager().isGlobal())
								Main.getPermission().playerAddGroup(null, x, Main.getDataBase().getGroup(name));
							else
								Main.getPermission().playerAddGroup(x, Main.getDataBase().getGroup(name));
						}
						String left = db.getDate(name).get(1);
						if (Long.valueOf(left) == -1L)
							continue;
						Date expired = Utils.getExpriedDate(db.getActiveDate(name), left);
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
							}.runTaskLater(instance, (millis / 1000) * 20L);
						}
					}
				}
			}
		}.runTaskTimer(plugin, 20 * 60L, 20 * 60L);
	}
}