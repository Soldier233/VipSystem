package me.zhanshi123.VipSystem.hook;

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
			List<String> date=Main.getDataBase().getDate(p.getName());
			String year=date.get(0);
			String month=date.get(1);
			String day=date.get(2);
			String left=date.get(3);
			long days=0;
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date now=format.parse(year+"-"+month+"-"+day);
				days=Utils.calculateLeftDays(now, left);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return String.valueOf(days);
		}
		else if(str.equalsIgnoreCase("group"))
		{
			return Main.getDataBase().getGroup(p.getName());
		}
		else if(str.equalsIgnoreCase("lastgroup"))
		{
			return Main.getDataBase().getLastGroup(p.getName());
		}
		else
		{
			return null;
		}
	}
}
