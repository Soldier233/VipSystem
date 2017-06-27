package me.zhanshi123.VipSystem.hook.placeholders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.zhanshi123.VipSystem.Main;
import me.zhanshi123.VipSystem.Utils;

public class Papi extends EZPlaceholderHook
{
	  public Papi(Main plugin)
	  {
	    super(plugin, "VipSystem");
	  }

	@Override
	public String onPlaceholderRequest(Player p, String str) 
	{
		if(str.equalsIgnoreCase("leftdays"))
		{
			if(Main.getPlaceholderCache().getData().containsKey(p.getName()))
				return String.valueOf(Main.getPlaceholderCache().getData().get(p.getName()).getLeftDays());
			else
				return "0";
		}
		else if(str.equalsIgnoreCase("group"))
		{
			if(Main.getPlaceholderCache().getData().containsKey(p.getName()))
				return String.valueOf(Main.getPlaceholderCache().getData().get(p.getName()).getVipGroup());
			else
				return null;
		}
		else if(str.equalsIgnoreCase("lastgroup"))
		{
			if(Main.getPlaceholderCache().getData().containsKey(p.getName()))
				return String.valueOf(Main.getPlaceholderCache().getData().get(p.getName()).getLastGroup());
			else
				return null;
		}
		else
		{
			return null;
		}
	}
}
