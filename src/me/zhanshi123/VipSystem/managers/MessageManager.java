package me.zhanshi123.VipSystem.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.zhanshi123.VipSystem.Main;

public class MessageManager
{
	FileConfiguration config;
	public static List<String> PlayerHelp, AdminHelp;

	public MessageManager()
	{
		File f = new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/messages/"
				+ Main.getConfigManager().getLanguage() + ".yml");
		config = new YamlConfiguration();
		boolean first = !f.exists();
		if (first)
		{
			try
			{
				f.createNewFile();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			config.load(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		prefix = config.getString("prefix");
		PlayerHelp = config.getStringList("commands.help.player");
		AdminHelp = config.getStringList("commands.help.admin");
		PlayerNotFound = config.getString("commands.error.PlayerNotFound");
		ConsoleForbid = config.getString("commands.error.ConsoleForbid");
		VipGave = config.getString("commands.success.VipGave");
		AlreadyHaveVip = config.getString("commands.error.AlreadyHaveVip");
		HaveNoVip = config.getString("commands.success.HaveNoVip");
		VipRemoved = config.getString("commands.success.VipRemoved");
		QueriedByAdmin = config.getString("commands.success.QueriedByAdmin");
		CacheSaved = config.getString("commands.success.CacheSaved");
		QueriedBySelf = config.getString("commands.success.QueriedBySelf");
		YouHaveNoVip = config.getString("commands.error.YouHaveNoVip");
		CodeNotFound = config.getString("commands.error.CodeNotFound");
		CodeActivated = config.getString("commands.success.CodeActivated");
		YouAlreadyHaveVip = config.getString("commands.error.YouAlreadyHaveVip");
		ThreadCreated = config.getString("commands.success.ThreadCreated");
		CodeExport = config.getString("commands.success.CodeExport");
		CodeExportFailed = config.getString("commands.error.CodeExportFailed");
		gCacheSaved = config.getString("general.console.CacheSaved");
		StartDetecting = config.getString("general.updater.StartDetecting");
		Loading = config.getString("general.console.Loading");
		DatabaseInitError = config.getString("general.console.DatabaseInitError");
		LoadingComplete = config.getString("general.console.LoadingComplete");
		FailedToGetOnlinePlayers = config.getString("general.console.FailedToGetOnlinePlayers");
		UpdateFailed = config.getString("general.updater.UpdateFailed");
		LatestVersion = config.getString("general.updater.LatestVersion");
		NewUpdate = config.getString("general.updater.NewUpdate");
		CodeCreated = config.getString("general.console.CodeCreated");
		Showed = config.getString("commands.success.Showed");
		permanent = config.getString("commands.permanent");
		QueriedByAdminPermanent = config.getString("commands.success.QueriedByAdminPermanent");
		QueriedBySelfPermanent = config.getString("commands.success.QueriedBySelfPermanent");
	}

	public static String prefix, QueriedBySelfPermanent, QueriedByAdminPermanent, permanent, Showed, CodeCreated,
			NewUpdate, LatestVersion, UpdateFailed, FailedToGetOnlinePlayers, LoadingComplete, DatabaseInitError,
			Loading, StartDetecting, gCacheSaved, CodeExportFailed, CodeExport, ThreadCreated, YouAlreadyHaveVip,
			CodeActivated, CodeNotFound, QueriedBySelf, ConsoleForbid, YouHaveNoVip, CacheSaved, PlayerNotFound,
			AlreadyHaveVip, HaveNoVip, VipGave, VipRemoved, QueriedByAdmin;

}
