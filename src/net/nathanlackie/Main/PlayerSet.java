package net.nathanlackie.Main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayerSet implements Serializable {
	private static final long serialVersionUID = 2567109031223261320L;
	public static List<PlayerSet> sets = new ArrayList<PlayerSet>();
	public List<ItemSell> set = new ArrayList<ItemSell>();
	public String player;

	PlayerSet(List<ItemSell> one, String two) {
		sets.add(this);
		this.set = one;
		this.player = two;
	}
}
