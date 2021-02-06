package net.nathanlackie.Main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class ItemSell implements Serializable {
	private static final long serialVersionUID = 4119548018971168164L;
	public static List<ItemSell> fromConfig = new ArrayList<ItemSell>();
	public int rTop = 0;
	public int rBot = 0;
	public int weight = 0;
	public Material itemName;
	public int sellPrice;
	public int maxSell;
	public int currentSold;

	ItemSell(Material item, int price, int max, int top, int bot, int weight, boolean cond) {
		this.itemName = item;
		this.sellPrice = price;
		this.maxSell = max;
		this.currentSold = 0;
		this.rTop = top;
		this.rBot = bot;
		this.weight = weight;

		if (cond) {
			fromConfig.add(this);
		}
	}

	public static void getFromConfig(Main plugin) {
		FileConfiguration config = plugin.getConfig();
		fromConfig.clear();
		for (String i : config.getConfigurationSection("Items").getKeys(true)) {
			if (i.contains(".") == false) {
				new ItemSell(Material.matchMaterial(i), 0, 0, 0, 0, 0, true);
			}
		}
		for (String i : config.getConfigurationSection("Items").getKeys(true)) {
			if (i.contains(".") == true) {
				for (ItemSell item : fromConfig) {
					String[] path = i.split("\\.");
					if (item.itemName == Material.matchMaterial(path[0]) && path[1].equals("worth")) {
						item.sellPrice = config.getInt("Items." + i);
					} else if (item.itemName == Material.matchMaterial(path[0]) && path[1].equals("max")) {
						item.maxSell = config.getInt("Items." + i);
					} else if (item.itemName == Material.matchMaterial(path[0]) && path[1].equals("weight")) {
						item.weight = config.getInt("Items." + i);
					}
				}
			}
		}
	}
}
