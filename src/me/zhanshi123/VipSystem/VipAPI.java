package me.zhanshi123.VipSystem;

import java.util.List;

public class VipAPI {
    private static VipAPI instance = null;

    public VipAPI() {
	instance = this;
    }

    public static VipAPI getInstance() {
	return instance;
    }

    /**
     * @author Soldier
     * 
     * @param name
     *            玩家名<br>
     * 	   Player name
     * 
     * @return 返回该玩家所在的VIP组，如果没有则返回null<br>
     * 	Return the vip group of the player.<br>
     *         Return null if it doesn't exist.
     * 
     * 
     **/
    public String getVipGroup(String name) {
	if (Main.getDataBase().exists(name)) {
	    return Main.getDataBase().getGroup(name);
	} else {
	    return null;
	}
    }

    /**
     * @author Soldier
     * 
     * @param name
     *            玩家名<br>
     * 	   Player name
     * 
     * @return 返回该玩家开通VIP前所在的组，如果没有则返回null<br>
     * 	Return the group which the player is in before he get a VIP<br>
     *         Return null if it doesn't exist.
     * 
     * 
     **/
    public String getLastGroup(String name) {
	if (Main.getDataBase().exists(name)) {
	    return Main.getDataBase().getLastGroup(name);
	} else {
	    return null;
	}
    }

    /**
     * @author Soldier
     * 
     * @param name
     *            玩家名<br>
     * 	   Player name
     * 
     * @return 返回玩家是否是VIP的boolean值<br>
     *         Return the boolean value of player's VIP status
     * 
     **/
    public boolean isVip(String name) {
	return Main.getDataBase().exists(name);
    }

    /**
     * @author Soldier
     * 
     * @param name
     *            玩家名<br>
     * 	   Player name
     * 
     * @return 返回玩家开通Vip时的时间情况<br>
     *         Return the date at which the vip starts in List<br>
     *         getDate(name).get(0) 获取开通的MilliMiles时间(to get MilliMiles when
     *         starts)<br>
     *         getDate(name).get(1) 获取剩余描述(to get left seconds)
     * 
     **/
    public List<String> getDate(String name) {
	return Main.getDataBase().getDate(name);
    }

    /**
     * @author Soldier
     * 
     * @param name
     *            玩家名<br>
     * 	   Player name
     * @param group
     *            组名<br>
     * 	   Group name
     * @param secs
     *            秒数<br>
     * 	   Seconds
     * 
     **/
    public void addVip(String name, String group, String secs) {
	Utils.addVip(name, group, secs);
    }

    /**
     * @author Soldier
     * 
     * @param name
     *            玩家名<br>
     * 	   Player name
     * 
     **/
    public void removeVip(String name) {
	Utils.removeVip(name);
    }

}
