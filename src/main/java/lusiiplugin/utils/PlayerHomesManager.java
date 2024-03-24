package lusiiplugin.utils;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.entity.player.EntityPlayer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class PlayerHomesManager {
	private HashMap<String, PlayerHomes> allPlayerHomes;
	private Path filePath;

	public PlayerHomesManager() {
		filePath = Paths.get(LusiiPlugin.SAVE_DIR).resolve("homes.ser");
		allPlayerHomes = new HashMap<>();

		if (Files.exists(filePath)) {
			try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
				allPlayerHomes = (HashMap<String, PlayerHomes>) ois.readObject();
				System.out.println("Homes loaded.");
			} catch (IOException | ClassNotFoundException e) {
				LusiiPlugin.LOGGER.error("Could not load homes from file", e);
				System.out.println("Could not load homes from file");
			}
		} else {
			save();
		}
	}

	public void save() {
		try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
			oos.writeObject(allPlayerHomes);
			System.out.println("HashMap saved to disk.");
		} catch (IOException ignored) {
			LusiiPlugin.LOGGER.warn("Chunk claims failed to save to disk! This is a major issue if you do not want griefing!");
		}
	}

	public void importOldHomes() {
		File playersDir = new File("world/players");
		File homesDir = new File("player-homes");

		if (!homesDir.exists()) {
			System.out.println("No old homes found");
			return;
		}
		System.out.println("Migrating homes...");


		// 1. Read Player Usernames
		for (File playerFile : playersDir.listFiles()) {
			String username = playerFile.getName().replace(".dat", "");

			// 2. Read Home Files
			PlayerHomes ph = PlayerHomes.blank();
			for (File homeFile : homesDir.listFiles()) {
				// Trim the filename to remove any leading or trailing whitespace
				String fileName = homeFile.getName().trim();
				if (fileName.startsWith(username)) {
					String homeName = fileName.substring(username.length(), homeFile.getName().length() - 4).trim(); // remove ".txt"
					if (homeName.equals("bed")) {
						homeName = "bedOld";
					}
					if (homeName.equals(username)) {
						homeName = "home";
					}
					List<String> lines = null;
					try {
						lines = Files.readAllLines(Paths.get(homeFile.getPath()));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					if (lines.size() >= 4) {
						double x = Double.parseDouble(lines.get(0));
						double y = Double.parseDouble(lines.get(1));
						double z = Double.parseDouble(lines.get(2));
						int dim = Integer.parseInt(lines.get(3));
						System.out.println(username + " " + homeName + " " + x + " " + y + " " + z);
						ph.addHome(homeName, x, y, z, dim);
					}
				}
			}

			// 3. Write to HashMap
			if (!allPlayerHomes.isEmpty()) {
				allPlayerHomes.put(username, ph);
			}
		}

		if (!homesDir.renameTo(new File("player-homes-old"))) {
			System.err.println("Failed to rename the player-homes directory.");
		}

		// 4. Save to disk
		save();
	}

	public PlayerHomes getPlayerHomes(EntityPlayer p) {
		return allPlayerHomes.computeIfAbsent(p.username, k -> PlayerHomes.blank());
	}
}
