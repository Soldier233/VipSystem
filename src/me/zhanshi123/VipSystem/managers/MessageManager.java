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
					"&3&l- &b/vipsys viptime &9��ѯʣ��VIP����",
					"&3&l- &b/vipsys key <CDK> &9ʹ��cdk"
			}));
			config.set("commands.help.admin", Arrays.asList(new String[]{
					"&3&l- &b/vipsys look <�����> &9��ѯָ�����VIP",
					"&3&l- &b/vipsys give <�����> <VIP��> <����> &9��Ŀ�����ָ��������VIP",
					"&3&l- &b/vipsys remove <�����> &9�Ƴ�Ŀ����ҵ�VIP",
					"&3&l- &b/vipsys createkey <����> <VIP��> <����> &9����cdk",
					"&3&l- &b/vipsys export <VIP��> &9����ĳ���������δʹ�õ�cdk",
					"&3&l- &b/vipsys save &9�������滺��(Ĭ��5����һ��)"
			}));
			config.set("commands.error.PlayerNotFound", "&c��Ҳ�����!");
			config.set("commands.error.AlreadyHaveVip", "&c�������ӵ��VIP");
			config.set("commands.error.HaveNoVip", "&c�����û��VIP");
			config.set("commands.error.YouHaveNoVip", "&c��û��VIP");
			config.set("commands.error.ConsoleForbid", "&c��̨��ֹʹ�ø�����");
			config.set("commands.error.CodeNotFound", "&cδ�ҵ��ü�����");
			config.set("commands.error.YouAlreadyHaveVip", "&c���Ѿ�ӵ��������VIP��");
			config.set("commands.error.CodeExportFailed", "&c����ʧ��!ԭ��:%reason%");
			
			config.set("commands.success.VipGave", "&a�ɹ�����VIP��ָ�����");
			config.set("commands.success.VipRemoved", "&aVIP�ɹ��Ƴ�");
			config.set("commands.success.QueriedByAdmin", "&a%player%��%group%,ʣ��ʱ��%left%��");
			config.set("commands.success.QueriedBySelf", "&a����%group%,ʣ��ʱ��%left%��");
			config.set("commands.success.CacheSaved", "&a�������");
			config.set("commands.success.CodeActivated", "&a�ɹ�����!��õ���%vip% %left%��");
			config.set("commands.success.ThreadCreated", "&a�����봴���߳��ѷ�����ȴ���̨��ʾ��Ϣ!");
			config.set("commands.success.CodeExport", "&a�������Ѿ�������ϣ���ʱ%time%ms��·��Ϊ%path%");
			
			config.set("general.console.CacheSaved", "[VipSystem����ϵͳ] ��a��l�Ի������ݽ��б�����ɣ������ˡ�c%time%��a��lms");
			config.set("general.console.CodeCreated", "��6��lVipSystem ��7>>> ��a�������!����б�������ϸ��������!��ʱ%time%ms");
			config.set("general.console.Loading", "��6��lVipSystem ��7>>> ��a�����ʼ����");
			config.set("general.console.LoadingComplete", "��6��lVipSystem ��7>>> ��a���������� ��ʱ%time%ms ����QQ 1224954468 ��������Ⱥ563012939");
			config.set("general.console.DatabaseInitError", "��6��lVipSystem ��7>>> ��c���ݿ��ʼ��ʧ�ܣ�ֹͣ����");
			config.set("general.console.FailedToGetOnlinePlayers", "��6��lVipSystem ��7>>> ��a�޷���ȡ������ң�����ϵ����");
			
			config.set("general.updater.StartDetecting", "��6��lVipSystem ��7>>> ��a��������");
			config.set("general.updater.UpdateFailed", "��6��lVipSystem ��7>>> ��a��ȡ����ʧ��");
			config.set("general.updater.LatestVersion", "��6��lVipSystem ��7>>> ��a��Ŀǰʹ�õİ汾�����°�");
			config.set("general.updater.NewUpdate", "��6��lVipSystem ��7>>> ��a�°汾%version%������Ŷ���Ͽ�ȥ���ذ�!");
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
