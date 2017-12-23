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
     *            �����<br>
     * 	   Player name
     * 
     * @return ���ظ�������ڵ�VIP�飬���û���򷵻�null<br>
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
     *            �����<br>
     * 	   Player name
     * 
     * @return ���ظ���ҿ�ͨVIPǰ���ڵ��飬���û���򷵻�null<br>
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
     *            �����<br>
     * 	   Player name
     * 
     * @return ��������Ƿ���VIP��booleanֵ<br>
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
     *            �����<br>
     * 	   Player name
     * 
     * @return ������ҿ�ͨVipʱ��ʱ�����<br>
     *         Return the date at which the vip starts in List<br>
     *         getDate(name).get(0) ��ȡ��ͨ��MilliMilesʱ��(to get MilliMiles when
     *         starts)<br>
     *         getDate(name).get(1) ��ȡʣ������(to get left seconds)
     * 
     **/
    public List<String> getDate(String name) {
	return Main.getDataBase().getDate(name);
    }

    /**
     * @author Soldier
     * 
     * @param name
     *            �����<br>
     * 	   Player name
     * @param group
     *            ����<br>
     * 	   Group name
     * @param secs
     *            ����<br>
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
     *            �����<br>
     * 	   Player name
     * 
     **/
    public void removeVip(String name) {
	Utils.removeVip(name);
    }

}
