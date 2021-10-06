package net.nathanlackie.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener {

	private Economy econ;
	Timer timer = new Timer();
	HashMap<String, String> langItem = new HashMap<String, String>();
	HashMap<String, String> langBlock = new HashMap<String, String>();

	@Override
	public void onEnable() {
		if (!setupEconomy()) {
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		getServer().getPluginManager().registerEvents(new MenuFunctionListener(), this);
		this.saveDefaultConfig();
		this.getCommand("sellitems").setExecutor(new sellitems(this));
		this.getCommand("SuperSell").setExecutor(new SuperSell(this));
		if (new File(this.getDataFolder() + "\\players.data").exists()) {
			PlayerSet.sets.clear();
			List<PlayerSet> grabbedData = Data.loadData(this.getDataFolder() + "\\players.data").currentSaved;
			for (int i = 0; i < grabbedData.size(); i++) {
				PlayerSet.sets.add(grabbedData.get(i));
			}
			Bukkit.getLogger().info("SuperSell Saved Data Loaded");
		}
		if (!new File(this.getDataFolder() + "\\lang.json").exists()) {
			try {
				Files.copy(getClass().getResourceAsStream("/lang.json"),Paths.get(this.getDataFolder() + "\\lang.json"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			File lang = new File(this.getDataFolder() + "\\lang.json");
			Scanner scan = new Scanner(lang);
			while (scan.hasNextLine()) {
				String data = scan.nextLine().replaceAll("[\",]", "").replace(": ", ":").trim();
				if (data.length() > 5) {
					int amt = 0;
					int secondP = 0;
					boolean t = false;
					for (int i = 0; i < data.length(); i++) {
						if (data.charAt(i) == '.') {
							amt++;
						}
						if (amt == 2 && t == false) {
							secondP = i+1;
							t = true;
						}
					}
					if (amt == 2 && data.substring(0, 5).equals("block")) {
						langBlock.put(data.substring(secondP).split(":")[0], data.substring(secondP).split(":")[1]);
					} else if (amt == 2 && data.substring(0, 4).equals("item")) {
						langItem.put(data.substring(secondP).split(":")[0], data.substring(secondP).split(":")[1]);
					}
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ItemSell.getFromConfig(this);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		timer.schedule(new ItemsReset(), (cal.getTimeInMillis() - System.currentTimeMillis())+1000);
	}

	@Override
	public void onDisable() {
		new Data().saveData(this.getDataFolder() + "\\players.data");
		if (ItemsReset.hasReset) {
			ItemsReset.timer.cancel();
		} else {
			timer.cancel();
		}
	}

	public boolean setupEconomy() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			this.getLogger().severe("Error 1: Disabled due to no Vault dependency found!");
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			this.getLogger().severe("Error 2: Disabled due to no Vault dependency found!");
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public Economy getEconomy() {
		return econ;
	}
}