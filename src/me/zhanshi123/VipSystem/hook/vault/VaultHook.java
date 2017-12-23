package me.zhanshi123.VipSystem.hook.vault;

import org.bukkit.plugin.RegisteredServiceProvider;

import me.zhanshi123.VipSystem.Main;
import net.milkbowl.vault.permission.Permission;

public class VaultHook {
    private Permission perm;
    private Main plugin;

    public VaultHook(Main plugin) {
	this.plugin = plugin;
	setupPermissions();
    }

    public Permission getPermission() {
	return perm;
    }

    private boolean setupPermissions() {
	RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager()
		.getRegistration(net.milkbowl.vault.permission.Permission.class);
	if (permissionProvider != null) {
	    perm = permissionProvider.getProvider();
	}
	return (perm != null);
    }
}
