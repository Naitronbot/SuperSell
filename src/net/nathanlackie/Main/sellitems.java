package net.nathanlackie.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.HopperMenu;

import net.milkbowl.vault.economy.Economy;

public class sellitems implements CommandExecutor {
	Random rand = new Random();
	Economy economy;
	Main plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player;
		if (sender instanceof Player && ItemSell.fromConfig.size() > 4 && args.length == 0) {
			player = (Player) sender;
			Menu menu = createMenu(ItemSell.fromConfig, player);
			menu.open(player);
			new Data().saveData(plugin.getDataFolder() + "\\players.data");
		} else if (sender instanceof Player && ItemSell.fromConfig.size() > 4 && args.length == 1) {
			String targetPlayer = args[0];
			boolean playerFound = false;
			int index = -1;
			for (int i = 0; i < PlayerSet.sets.size(); i++) {
				if (PlayerSet.sets.get(i).player.toLowerCase().equals(targetPlayer.toLowerCase())) {
					playerFound = true;
					index = i;
					break;
				}
			}
			player = (Player) sender;
			if (playerFound == true) {
				Menu menu = HopperMenu.builder().title(PlayerSet.sets.get(index).player + "'s Sell Items").build();
				for (int i = 0; i < 5; i++) {
					buildMenu(menu, index, i, true);
				}
				menu.open(player);
			} else {
				player.sendMessage("Specified player "+targetPlayer+" either does not exist, or has not checked sellitems menu today.");
			}
			new Data().saveData(plugin.getDataFolder() + "\\players.data");
		} else if (sender instanceof Player && ItemSell.fromConfig.size() > 4 && args.length > 1) {
			sender.sendMessage("Invalid arguments");
		} else if (sender instanceof Player && ItemSell.fromConfig.size() < 5) {
			sender.sendMessage("Config must have at least 5 items registered.");
		} else {
			Bukkit.getLogger().severe("Command must be executed by a player.");
		}
		return true;
	}

	public Menu createMenu(List<ItemSell> fromConfigCopy, Player executingPlayer) {
		Menu menu = HopperMenu.builder().title("Sell Items").build();
		menu.setCloseHandler((player, menu1) -> {
			new Data().saveData(plugin.getDataFolder() + "\\players.data");
		});
		List<ItemSell> currentItems = new ArrayList<ItemSell>();
		boolean playerFound = false;
		int index = -1;
		List<ItemSell> items = new ArrayList<ItemSell>();
		for (int i = 0; i < fromConfigCopy.size(); i++) {
			ItemSell current = fromConfigCopy.get(i);
			items.add(new ItemSell(current.itemName, current.sellPrice, current.maxSell, current.rTop, current.rBot,
					current.weight, false));
		}
		for (int i = 0; i < PlayerSet.sets.size(); i++) {
			if (executingPlayer == Bukkit.getPlayer(PlayerSet.sets.get(i).player)) {
				playerFound = true;
				index = i;
				break;
			}
		}

		if (playerFound == true) {
			for (int i = 0; i < 5; i++) {
				buildMenu(menu, index, i, false);
			}
		} else {
			currentItems.clear();
			for (int i = 0; i < 5; i++) {
				int foundIndex = 0;
				int currentWeight = 0;
				for (int j = 0; j < items.size(); j++) {
					ItemSell currenti = items.get(j);
					currenti.rBot = currentWeight;
					currenti.rTop = currentWeight + currenti.weight;
					currentWeight += currenti.weight + 1;
				}
				int randomSelected = rand.nextInt(currentWeight);
				for (int j = 0; j < items.size(); j++) {
					if (randomSelected <= items.get(j).rTop && items.get(j).rBot <= randomSelected) {
						foundIndex = j;
						break;
					}
				}
				currentItems.add(items.get(foundIndex));
				items.remove(foundIndex);
			}
			new PlayerSet(currentItems, executingPlayer.getName());
			for (int i = 0; i < PlayerSet.sets.size(); i++) {
				if (executingPlayer == Bukkit.getPlayer(PlayerSet.sets.get(i).player)) {
					playerFound = true;
					index = i;
					break;
				}
			}
			for (int i = 0; i < PlayerSet.sets.size(); i++) {
				if (executingPlayer == Bukkit.getPlayer(PlayerSet.sets.get(i).player)) {
					for (int ii = 0; ii < 5; ii++) {
						buildMenu(menu, i, ii, false);
					}
					break;
				}
			}
		}
		return menu;
	}

	public void addClickHandler(Slot slot, ItemSell selected) {
		slot.setClickOptions(ClickOptions.builder().allow(ClickType.LEFT, ClickType.RIGHT).build());
		slot.setClickHandler((player, info) -> {
			boolean pFound = false;
			int pIndex = -1;
			for (int i = 0; i < PlayerSet.sets.size(); i++) {
				if (player == Bukkit.getPlayer(PlayerSet.sets.get(i).player)) {
					pFound = true;
					pIndex = i;
					break;
				}
			}	
			if (pFound == false) {
				info.getClickedMenu().close(player);
			} else if (selected.currentSold >= selected.maxSell) {
				getRandom(info.getClickedMenu(), info.getClickedSlot().getIndex(), pIndex);
			} else if (player.getInventory().contains(selected.itemName) && info.getClickType().isLeftClick()) {
				economy.depositPlayer((OfflinePlayer) player, (double) selected.sellPrice);
				selected.currentSold += 1;
				if (selected.itemName.isBlock()) {
					player.sendMessage("Sold 1x "+ChatColor.GOLD+plugin.langBlock.get(selected.itemName.toString().toLowerCase())+ChatColor.RESET+" for $" + selected.sellPrice);
				} else if (selected.itemName.isItem()) {
					player.sendMessage("Sold 1x "+ChatColor.GOLD+plugin.langItem.get(selected.itemName.toString().toLowerCase())+ChatColor.RESET+" for $" + selected.sellPrice);
				}
				info.getClickedMenu().update(player);
				ItemStack[] playerInv = player.getInventory().getContents();
				for (int i = 0; i < playerInv.length; i++) {
					if (playerInv[i] != null && playerInv[i].getType() == selected.itemName) {
						playerInv[i].setAmount(playerInv[i].getAmount() - 1);
						break;
					}
				}
				player.updateInventory();
				player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 100, 2);
				if (selected.currentSold >= selected.maxSell) {
					getRandom(info.getClickedMenu(), info.getClickedSlot().getIndex(), pIndex);
					new Data().saveData(plugin.getDataFolder() + "\\players.data");
				}
			} else if (player.getInventory().contains(selected.itemName) && info.getClickType().isRightClick()) {
				ItemStack[] playerInv = player.getInventory().getContents();
				int itemCount = 0;
				for (int i = 0; i < playerInv.length; i++) {
					if (playerInv[i] != null && playerInv[i].getType() == selected.itemName && itemCount + playerInv[i].getAmount() < selected.maxSell-selected.currentSold) {
						itemCount += playerInv[i].getAmount();
						playerInv[i].setAmount(0);
					} else if (playerInv[i] != null && playerInv[i].getType() == selected.itemName && !(itemCount + playerInv[i].getAmount() < selected.maxSell-selected.currentSold)) {
						playerInv[i].setAmount(playerInv[i].getAmount()-(selected.maxSell-selected.currentSold)+itemCount);
						itemCount += (selected.maxSell-selected.currentSold)-itemCount;
						break;
					}
				}
				economy.depositPlayer((OfflinePlayer) player, (double) (selected.sellPrice*itemCount));
				selected.currentSold += itemCount;
				if (selected.itemName.isBlock()) {
					player.sendMessage("Sold "+itemCount+"x "+ChatColor.GOLD+plugin.langBlock.get(selected.itemName.toString().toLowerCase())+ChatColor.RESET+" for $" + selected.sellPrice*itemCount);
				} else if (selected.itemName.isItem()) {
					player.sendMessage("Sold "+itemCount+"x "+ChatColor.GOLD+plugin.langItem.get(selected.itemName.toString().toLowerCase())+ChatColor.RESET+" for $" + selected.sellPrice*itemCount);
				}
				info.getClickedMenu().update(player);
				player.updateInventory();
				player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 100, 2);
				if (selected.currentSold >= selected.maxSell) {
					getRandom(info.getClickedMenu(), info.getClickedSlot().getIndex(), pIndex);
					new Data().saveData(plugin.getDataFolder() + "\\players.data");
				}
			} else {
				if (selected.itemName.isBlock()) {
					player.sendMessage("You don't have any "+ChatColor.GOLD+plugin.langBlock.get(selected.itemName.toString().toLowerCase())+ChatColor.RESET+" to sell.");
				} else if (selected.itemName.isItem()) {
					player.sendMessage("You don't have any "+ChatColor.GOLD+plugin.langItem.get(selected.itemName.toString().toLowerCase())+ChatColor.RESET+" to sell.");
				}
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 100, 0f);
			}
			info.getClickedMenu().update(player);
		});
	}

	public void buildMenu(Menu menu, int index, int i, boolean other) {
		Slot slot = menu.getSlot(i);
		int currentIndex = index;
		int currentLoop = i;
		slot.setItemTemplate(p -> {
			ItemSell selected = PlayerSet.sets.get(currentIndex).set.get(currentLoop);
			ItemStack currentStack = new ItemStack(selected.itemName);
			ItemMeta meta = currentStack.getItemMeta();
			meta.setLore(
					Arrays.asList(new String[] { ChatColor.WHITE + "" + selected.currentSold + "/" + selected.maxSell,
							ChatColor.GREEN + "$" + selected.sellPrice }));
			currentStack.setItemMeta(meta);
			if (!other) {
				addClickHandler(slot, selected);
			}
			return currentStack;
		});
	}

	public void getRandom(Menu menu, int clickedIndex, int playerIndex) {
		List<ItemSell> fromConfigCopy = ItemSell.fromConfig;
		List<ItemSell> items = new ArrayList<ItemSell>();
		for (int i = 0; i < fromConfigCopy.size(); i++) {
			boolean found = false;
			ItemSell current = fromConfigCopy.get(i);
			for (int j = 0; j < PlayerSet.sets.get(playerIndex).set.size(); j++) {
				if (current.itemName == PlayerSet.sets.get(playerIndex).set.get(j).itemName) {
					found = true;
				}
			}
			if (found == false) {
				items.add(new ItemSell(current.itemName, current.sellPrice, current.maxSell, current.rTop, current.rBot,
						current.weight, false));
			}
		}
		int currentWeight = 0;
		for (int i = 0; i < items.size(); i++) {
			ItemSell currenti = items.get(i);
			currenti.rBot = currentWeight;
			currenti.rTop = currentWeight + currenti.weight;
			currentWeight += currenti.weight + 1;
		}
		int randomSelected = rand.nextInt(currentWeight);
		int foundIndex = -1;
		for (int j = 0; j < items.size(); j++) {
			if (randomSelected <= items.get(j).rTop && items.get(j).rBot <= randomSelected) {
				foundIndex = j;
				break;
			}
		}
		PlayerSet.sets.get(playerIndex).set.set(clickedIndex, items.get(foundIndex));
	}

	sellitems(Main instance) {
		economy = instance.getEconomy();
		plugin = instance;
	}
}