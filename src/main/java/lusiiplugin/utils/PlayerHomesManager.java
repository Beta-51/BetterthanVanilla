package lusiiplugin.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lusiiplugin.LusiiPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PlayerHomesManager {
	public HashMap<String, PlayerHomes> allPlayerHomes = new HashMap<>();
	private final Path filePath = Paths.get(LusiiPlugin.SAVE_DIR).resolve("homes.ser");

	public PlayerHomesManager() {
		if (Files.exists(filePath)) {
			try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
				allPlayerHomes = (HashMap<String, PlayerHomes>) ois.readObject();
				allPlayerHomes.forEach((username, value) -> {
					System.out.println(username + ": " + value.userHomes.toString());
					File dataDir = Paths.get(LusiiPlugin.SAVE_DIR, username).toFile();

					if (!dataDir.exists()) {
						dataDir.mkdir();
					}

					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					String json = gson.toJson(value.userHomes);
					try {
						Path homeFile = new File(dataDir, "homes.json").toPath();
						Files.write(homeFile, json.getBytes(StandardCharsets.UTF_8));
					} catch (IOException e) {
						System.err.println("Error writing file: " + e.getMessage());
					}
				});
			} catch (IOException | ClassNotFoundException e) {
			}
			try {
				Files.move(filePath, filePath.resolveSibling("old_homes.ser"));
			} catch (IOException e) {}
		}
	}

}
