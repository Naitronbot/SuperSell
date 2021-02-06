package net.nathanlackie.Main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class Data implements Serializable {
	private static transient final long serialVersionUID = -5420997612155656534L;

	public List<PlayerSet> currentSaved = new ArrayList<PlayerSet>();

	public Data() {
		for (int i = 0; i < PlayerSet.sets.size(); i++) {
			this.currentSaved.add(PlayerSet.sets.get(i));
		}
	}

	public boolean saveData(String path) {
		try {
			BukkitObjectOutputStream out = new BukkitObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(path)));
			out.writeObject(this);
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static Data loadData(String path) {
		try {
			BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(path)));
			Data data = (Data) in.readObject();
			in.close();
			return data;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
