package net.nathanlackie.Main;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ItemsReset extends TimerTask {
	public static Timer timer;
	public static boolean hasReset = false;

	public void run() {
		PlayerSet.sets.clear();
		timer = new Timer();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		timer.schedule(new ItemsReset(), (cal.getTimeInMillis() - System.currentTimeMillis())+1000);
		Bukkit.broadcastMessage(ChatColor.GREEN + "SuperSell items have been reset globally.");
		if (!hasReset) {
			hasReset = true;
		}
	}
}
