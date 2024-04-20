package lusiiplugin.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lusiiplugin.LusiiPlugin;
import net.minecraft.core.entity.player.EntityPlayer;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class PlayerData {
	private final TPInfo tpInfo;
	private final Homes homes;
	private final Treecap treecap;
	private int saveTick = 0;

	public PlayerData(EntityPlayer player) {
		File dataDir = Paths.get(LusiiPlugin.SAVE_DIR, player.username).toFile();
		tpInfo = new TPInfo(new File(dataDir, "tpInfo.json"), player);
		homes = new Homes(new File(dataDir, "homes.json"));
		treecap = new Treecap(new File(dataDir, "treecap.txt"));

		if (!dataDir.exists()) {
			dataDir.mkdir();
			this.save();
			return;
		}

		tpInfo.load();
		homes.load();
		treecap.load();
	}

	public static PlayerData get(EntityPlayer player) {
		return ((Interface) player).betterthanVanilla$getPlayerData();
	}

	public static void update(EntityPlayer player) {
		((Interface) player).betterthanVanilla$setPlayerData(player);
	}

	public Homes homes() {
		return homes;
	}

	public TPInfo tpInfo() {
		return tpInfo;
	}

	public Treecap treecap() {
		return treecap;
	}

	public void save() {
		tpInfo.save();
		homes.save();
		treecap.save();
	}

	public void tick() {
		saveTick++;
		if (saveTick == 6000) { // 60 * 20 * 5 = 6000 (5 mins)
			saveTick = 0;
			save();
		}
	}

	public static class TPInfo implements Serializable {
		private static final long serialVersionUID = 1L; // Ensures version compatibility during deserialization
		private final transient File saveFile;
		private final transient EntityPlayer player;
		private transient Instant lastTPtime;
		private WorldPosition lastPos;
		private ArrayList<String> requestsOrder;
		private HashMap<String, RequestType> requests;

		TPInfo(File file, EntityPlayer p) {
			saveFile = file;
			lastTPtime = Instant.now().minus(Duration.ofSeconds(LusiiPlugin.TPTimeout));
			requestsOrder = new ArrayList<>();
			requests = new HashMap<>();
			lastPos = new WorldPosition(p.x, p.y, p.z, p.dimension);
			player = p;
			if (!saveFile.exists()) {
				save();
			}
		}

		public void save() {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(this);
			if (!saveFile.exists()) {
				try {
					saveFile.createNewFile();
				} catch (IOException e) {
					System.err.println("Error writing file: " + e.getMessage());
				}
			}
			try {
				Files.write(saveFile.toPath(), json.getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				System.err.println("Error writing file: " + e.getMessage());
			}
		}

		public void load() {
			Gson gson = new Gson();
			try {
				String json = new String(Files.readAllBytes(saveFile.toPath()), StandardCharsets.UTF_8);
				TPInfo loadedInfo = gson.fromJson(json, TPInfo.class);
				lastPos = loadedInfo.lastPos;
				requestsOrder = loadedInfo.requestsOrder;
				requests = loadedInfo.requests;
			} catch (IOException e) {
				System.err.println("Error reading file: " + e.getMessage());
			}
		}

		// Returns 0 if tp is available
		public int cooldown() {
			Instant now = Instant.now();
			Instant TPAvailable = lastTPtime.plus(Duration.ofSeconds(LusiiPlugin.TPTimeout));

			if (now.isAfter(TPAvailable)) {
				return 0;
			}

			return Math.toIntExact(Duration.between(now, TPAvailable).getSeconds());
		}

		public boolean canTP() {
			return cooldown() == 0;
		}

		public WorldPosition getLastPos() {
			return new WorldPosition(lastPos.x, lastPos.y, lastPos.z, lastPos.dim);
		}

		public void update() {
			lastTPtime = Instant.now();
			lastPos = new WorldPosition(player.x, player.y, player.z, player.dimension);
		}

		public boolean atNewPos() {
			WorldPosition newPos = new WorldPosition(player.x, player.y, player.z, player.dimension);
			return !(newPos.equals(lastPos));
		}

		/**
		 * Send a teleport request from a user.
		 * <br>
		 * If a user with a pending request sends a new request of a diffent type (TPA vs TPAHERE)
		 * the old request is removed and the new one is moved to the front of the list.
		 * @param username
		 * @return <code>true</code> if request was sent, <code>false</code> if that user has a pending request</code>
		 */
		public boolean sendRequest(String username, RequestType type) {
			if (requests.get(username) == type) {
				return false;
			}
			requestsOrder.remove(username);
			requestsOrder.add(username);
			requests.put(username, type);
			return true;
		}

		public boolean hasNoRequests() {
			return requests.isEmpty();
		}

		public Pair<String, RequestType> getNewestRequest() {
			// Get the index of the newest request
			int lastIndex = requests.size() - 1;

			// Check if there are any requests
			if (lastIndex >= 0) {
				String lastRequestName = requestsOrder.get(lastIndex);
				RequestType req = requests.get(lastRequestName);

				return Pair.of(lastRequestName, req);
			} else {
				return null;
			}
		}

		public List<String> getAllRequests() {
			if (hasNoRequests()) {
				return Collections.singletonList("§4You don't have any requests.");
			}
			ArrayList<String> out = new ArrayList<>();

			for (String username : requestsOrder) {
				RequestType type = requests.get(username);

				out.add("§1[ §3" + username + " §1| §4" + type.toString() + " §1]§r");
			}

			return out;
		}

		public boolean hasRequestFrom(String username) {
			return requests.containsKey(username);
		}

		public void removeRequest(String username) {
			requestsOrder.remove(username);
			requests.remove(username);
		}

		public enum RequestType {
			TPA { @Override public String toString() { return "To you"; } },
			TPAHERE { @Override public String toString() { return "To them"; } }
		}
	}

	public static class Homes implements Serializable {
		private static final long serialVersionUID = 1L; // Ensures version compatibility during deserialization
		private final transient File saveFile;
		private HashMap<String, WorldPosition> homesMap;

		Homes(File file) {
			saveFile = file;
			homesMap = new HashMap<>();
			if (!saveFile.exists()) {
				save();
			}
		}

		public void save() {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(this.homesMap);
			if (!saveFile.exists()) {
				try {
					saveFile.createNewFile();
				} catch (IOException e) {
					System.err.println("Error writing file: " + e.getMessage());
				}
			}
			try {
				Files.write(saveFile.toPath(), json.getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				System.err.println("Error writing file: " + e.getMessage());
			}
		}

		public void load() {
			Gson gson = new Gson();
			try {
				Type type = new TypeToken<HashMap<String, WorldPosition>>() {}.getType();
				this.homesMap = gson.fromJson(new FileReader(saveFile), type);
			} catch (IOException e) {
				System.err.println("Error reading file: " + e.getMessage());
			}
		}

		public int getAmount() {
			return homesMap.size();
		}

		/**
		 * @return <code>true</code> if the home was added, <code>false</code> if the home exists.
		 */
		public boolean setHome(EntityPlayer p, String homeName) {
			if (homesMap.containsKey(homeName)) {
				return false;
			}

			homesMap.put(homeName, new WorldPosition(p.x, p.y, p.z, p.dimension));
			return true;
		}

		public void addHome(String name, double x, double y, double z, int dim) {
			homesMap.put(name, new WorldPosition(x, y, z, dim));
		}

		/**
		 * @return <code>true</code> if the home was removed, <code>false</code> if the home does not exist.
		 */
		public boolean delHome(String homeName) {
			if (!homesMap.containsKey(homeName)) {
				return false;
			}

			homesMap.remove(homeName);
			return true;
		}

		/**
		 * @return An Optional containing the <code>HomePosition</code> for the player, or an empty Optional if it does not exist.
		 */
		public Optional<WorldPosition> getHomePos(String homeName) {
			return Optional.ofNullable(homesMap.get(homeName));
		}

		/**
		 * @return An <code>ArrayList</code> of home names for the player,
		 * or an empty <code>ArrayList</code> if the player has no homes.
		 */
		public List<String> getHomesList() {
			return new ArrayList<>(homesMap.keySet());
		}
	}

	public static class Treecap implements Serializable {
		private static final long serialVersionUID = 1L; // Ensures version compatibility during deserialization
		private final transient File saveFile;
		private boolean inactive;

		Treecap(File file) {
			saveFile = file;
			inactive = false;
			if (!saveFile.exists()) {
				save();
			}
		}

		public void save() {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(this);
			if (!saveFile.exists()) {
				try {
					saveFile.createNewFile();
				} catch (IOException e) {
					System.err.println("Error writing file: " + e.getMessage());
				}
			}
			try {
				Files.write(saveFile.toPath(), json.getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				System.err.println("Error writing file: " + e.getMessage());
			}
		}

		public void load() {
			Gson gson = new Gson();
			try {
				String json = new String(Files.readAllBytes(saveFile.toPath()), StandardCharsets.UTF_8);
				inactive = gson.fromJson(json, Treecap.class).inactive;
			} catch (IOException e) {
				System.err.println("Error reading file: " + e.getMessage());
			}
		}

		public boolean isActive() {
			return !inactive;
		}

		public boolean toggle() {
			inactive = !inactive;
			return inactive;
		}
	}

	public interface Interface {
		PlayerData betterthanVanilla$getPlayerData();
		void betterthanVanilla$setPlayerData(EntityPlayer player);
	}
}
