package net.nathanlackie.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SuperSell implements CommandExecutor {
	Main plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("Improper arguments\n/supersell [arguement]\nArguments: reload, reset, playerreset");
		} else if (args.length == 1 && args[0].equals("reload")) {
			plugin.reloadConfig();
			Bukkit.getLogger().info("SuperSell Config Reloaded");
			sender.sendMessage("SuperSell Config Reloaded");
			ItemSell.getFromConfig(plugin);
		} else if (args.length == 1 && args[0].equals("reset")) {
			PlayerSet.sets.clear();
			Bukkit.broadcastMessage(ChatColor.GREEN + "SuperSell items have been reset globally.");
			new Data().saveData(plugin.getDataFolder() + "\\players.data");
		} else if (args.length == 2 && args[0].equals("reset")) {
			boolean found = false;
			for (int i = 0; i < PlayerSet.sets.size(); i++) {
				PlayerSet currentSet = PlayerSet.sets.get(i);
				if (Bukkit.getPlayer(currentSet.player) == Bukkit.getPlayer(args[1])) {
					PlayerSet.sets.remove(i);
					sender.sendMessage("Data removed for player " + currentSet.player);
					found = true;
					break;
				}
			}
			if (found == false) {
				sender.sendMessage("Data for player could not be found, either player has no data or is invalid.");
			}
			new Data().saveData(plugin.getDataFolder() + "\\players.data");
		}
		return true;
	}

	SuperSell(Main instance) {
		plugin = instance;
	}
}
