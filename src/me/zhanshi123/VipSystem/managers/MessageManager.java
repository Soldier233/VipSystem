package me.zhanshi123.VipSystem.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.zhanshi123.VipSystem.Main;

public class MessageManager {
	FileConfiguration config;
	public static List<String> PlayerHelp,AdminHelp;
	public MessageManager()
	{
		File f=new File(Main.getInstance().getDataFolder(),"messages.yml");
		config=new YamlConfiguration();
		boolean first=!f.exists();
		if(first)
		{
			try {
				f.createNewFile();
			} catch (Exception e) {e.printStackTrace();}
		}
		try {
			config.load(new InputStreamReader(new FileInputStream(f),"UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(first)
		{
			config.set("prefix", "&6&lVipSystem &7>>> &r");
			config.set("commands.help.player",Arrays.asList(new String[]{
					"&6&l==========&a&lVipSystem&6&l==========",
					"&3&l- &b/vipsys viptime &9查询剩余VIP天数",
					"&3&l- &b/vipsys key <CDK> &9使用cdk"
			}));
			config.set("commands.help.admin", Arrays.asList(new String[]{
					"&3&l- &b/vipsys look <玩家名> &9查询指定玩家VIP",
					"&3&l- &b/vipsys give <玩家名> <VIP组> <天数> &9给目标玩家指定天数的VIP",
					"&3&l- &b/vipsys remove <玩家名> &9移除目标玩家的VIP",
					"&3&l- &b/vipsys createkey <数量> <VIP组> <天数> &9创建cdk",
					"&3&l- &b/vipsys export <VIP组> &9导出某个组的所有未使用的cdk",
					"&3&l- &b/vipsys save &9立即保存缓存(默认5分钟一次)"
			}));
			config.set("commands.error.PlayerNotFound", "&c玩家不在线!");
			config.set("commands.error.AlreadyHaveVip", "&c该玩家已拥有VIP");
			config.set("commands.error.HaveNoVip", "&c该玩家没有VIP");
			config.set("commands.error.YouHaveNoVip", "&c你没有VIP");
			config.set("commands.error.ConsoleForbid", "&c后台禁止使用该命令");
			config.set("commands.error.CodeNotFound", "&c未找到该激活码");
			config.set("commands.error.YouAlreadyHaveVip", "&c你已经拥有其他的VIP了");
			config.set("commands.error.CodeExportFailed", "&c导出失败!原因:%reason%");
			
			config.set("commands.success.VipGave", "&a成功发送VIP到指定玩家");
			config.set("commands.success.VipRemoved", "&aVIP成功移除");
			config.set("commands.success.QueriedByAdmin", "&a%player%是%group%,剩余时间%left%天");
			config.set("commands.success.QueriedBySelf", "&a你是%group%,剩余时间%left%天");
			config.set("commands.success.CacheSaved", "&a保存完成");
			config.set("commands.success.CodeActivated", "&a成功激活!你得到了%vip% %left%天");
			config.set("commands.success.ThreadCreated", "&a激活码创建线程已发起，请等待后台提示信息!");
			config.set("commands.success.CodeExport", "&a激活码已经导出完毕，用时%time%ms，路径为%path%");
			
			config.set("general.console.CacheSaved", "[VipSystem缓存系统] §a§l对缓存数据进行保存完成，花费了§c%time%§a§lms");
			config.set("general.console.CodeCreated", "§6§lVipSystem §7>>> §a创建完成!如果有报错请仔细查阅问题!用时%time%ms");
			config.set("general.console.Loading", "§6§lVipSystem §7>>> §a插件开始加载");
			config.set("general.console.LoadingComplete", "§6§lVipSystem §7>>> §a插件加载完成 用时%time%ms 作者QQ 1224954468 技术交流群563012939");
			config.set("general.console.DatabaseInitError", "§6§lVipSystem §7>>> §c数据库初始化失败，停止加载");
			config.set("general.console.FailedToGetOnlinePlayers", "§6§lVipSystem §7>>> §a无法获取在线玩家，请联系作者");
			
			config.set("general.updater.StartDetecting", "§6§lVipSystem §7>>> §a检查更新中");
			config.set("general.updater.UpdateFailed", "§6§lVipSystem §7>>> §a获取更新失败");
			config.set("general.updater.LatestVersion", "§6§lVipSystem §7>>> §a你目前使用的版本是最新版");
			config.set("general.updater.NewUpdate", "§6§lVipSystem §7>>> §a新版本%version%发布了哦，赶快去下载吧!");
			try {
				config.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		prefix=config.getString("prefix");
		PlayerHelp=config.getStringList("commands.help.player");
		AdminHelp=config.getStringList("commands.help.admin");
		PlayerNotFound=config.getString("commands.error.PlayerNotFound");
		ConsoleForbid=config.getString("commands.error.ConsoleForbid");
		VipGave=config.getString("commands.success.VipGave");
		AlreadyHaveVip=config.getString("commands.error.AlreadyHaveVip");
		HaveNoVip=config.getString("commands.success.HaveNoVip");
		VipRemoved=config.getString("commands.success.VipRemoved");
		QueriedByAdmin=config.getString("commands.success.QueriedByAdmin");
		CacheSaved=config.getString("commands.success.CacheSaved");
		QueriedBySelf=config.getString("commands.success.QueriedBySelf");
		YouHaveNoVip=config.getString("commands.error.YouHaveNoVip");
		CodeNotFound=config.getString("commands.error.CodeNotFound");
		CodeActivated=config.getString("commands.success.CodeActivated");
		YouAlreadyHaveVip=config.getString("commands.error.YouAlreadyHaveVip");
		ThreadCreated=config.getString("commands.success.ThreadCreated");
		CodeExport=config.getString("commands.success.CodeExport");
		CodeExportFailed=config.getString("commands.error.CodeExportFailed");
		gCacheSaved=config.getString("general.console.CacheSaved");
		StartDetecting=config.getString("general.updater.StartDetecting");
		Loading=config.getString("general.console.Loading");
		DatabaseInitError=config.getString("general.console.DatabaseInitError");
		LoadingComplete=config.getString("general.console.LoadingComplete");
		FailedToGetOnlinePlayers=config.getString("general.console.FailedToGetOnlinePlayers");
		UpdateFailed=config.getString("general.updater.UpdateFailed");
		LatestVersion=config.getString("general.updater.LatestVersion");
		NewUpdate=config.getString("general.updater.NewUpdate");
		CodeCreated=config.getString("general.console.CodeCreated");
	}
	public static String prefix,CodeCreated,NewUpdate,LatestVersion,UpdateFailed,FailedToGetOnlinePlayers,LoadingComplete,DatabaseInitError,Loading,StartDetecting,gCacheSaved,CodeExportFailed,CodeExport,ThreadCreated,YouAlreadyHaveVip,CodeActivated,CodeNotFound,QueriedBySelf,ConsoleForbid,YouHaveNoVip,CacheSaved,PlayerNotFound,AlreadyHaveVip,HaveNoVip,VipGave,VipRemoved,QueriedByAdmin;

}
