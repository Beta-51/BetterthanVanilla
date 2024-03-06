package lusiiplugin;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.BlockPortal;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class LusiiPlugin implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "betterthanvanilla";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TomlConfigHandler CONFIG;
	static {
		Toml toml = new Toml();
		toml.addCategory("WorldUtils");
		toml.addEntry("WorldUtils.StaticFire", "Static fire (Fire does not spread, and does not delete blocks).", false);
		toml.addEntry("WorldUtils.DisableBedExplosions", "Remove explosions from beds in non-respawn dimensions.", false);
		toml.addEntry("WorldUtils.EnableSkyDimensionPortals", "Enable making portals to the Sky Dimension", false);
		toml.addEntry("WorldUtils.AddTicksCatchableFishing", "Adds time in ticks to the window of time that you can catch fish, very useful for players with higher ping. 10-15 is recommended. 0 to disable, -40 or lower to make fishing impossible.",10);
		toml.addEntry("WorldUtils.DisableTNTOverworld", "Disable TNT in the overworld", false);
		toml.addEntry("WorldUtils.DisableTNTNether", "Disable TNT in the nether", false);
		toml.addEntry("WorldUtils.DisableTNTSky", "Disable TNT in the sky dimension", false);
		toml.addCategory("Commands");
		toml.addEntry("Commands.Give", "Let non-opped players use /give.", false);
		toml.addEntry("Commands.Home", "Let non-opped players use /home commands.", true);
		toml.addEntry("Commands.HomeLimit","Max amount of homes a player may have. 0 = infinite", 5);
		toml.addEntry("Commands.Spawn", "Let non-opped players use /spawn.", true);
		toml.addEntry("Commands.Gamemode", "Let non-opped players use /gamemode.", false);
		toml.addEntry("Commands.Clear", "Let non-opped players use /clear.", false);
		toml.addEntry("Commands.Craft", "Let non-opped players use /craft", true);
		toml.addCategory("PlayerUtils");
		toml.addEntry("PlayerUtils.signEdit", "Allows players to edit signs by sneaking when breaking a sign and replacing it.", true);
		toml.addEntry("PlayerUtils.headSit", "Allows players to sit on eachothers' heads when holding nothing in their hand.", false);
		toml.addEntry("PlayerUtils.colourChat", "Allows players to use $$ as colour code for colourful chatting, obfuscation is disabled.", true);
		toml.addEntry("PlayerUtils.greenText", "Allows players to turn their text green by putting '>' at the start of their messages.", true);
		toml.addCategory("ServerUtils");
		toml.addEntry("ServerUtils.MOTD", "Message of the day, shows up in server list. Don't be inappropriate!", "§5§lJoin us!");

		CONFIG = new TomlConfigHandler(MOD_ID, toml);
		staticFire = CONFIG.getBoolean("WorldUtils.StaticFire");
		disableBedExplosion = CONFIG.getBoolean("WorldUtils.DisableBedExplosions");
		enableSkyDimensionPortal = CONFIG.getBoolean("WorldUtils.EnableSkyDimensionPortals");
		addedTicksCatchable = CONFIG.getInt("WorldUtils.AddTicksCatchableFishing");
		DisableTNTOverworld = CONFIG.getBoolean("WorldUtils.DisableTNTOverworld");
		DisableTNTNether = CONFIG.getBoolean("WorldUtils.DisableTNTNether");
		DisableTNTSky = CONFIG.getBoolean("WorldUtils.DisableTNTSky");
		giveCommand = CONFIG.getBoolean("Commands.Give");
		homeCommand = CONFIG.getBoolean("Commands.Home");
		maxHomes = CONFIG.getInt("Commands.HomeLimit");
		spawnCommand = CONFIG.getBoolean("Commands.Spawn");
		gamemodeAll = CONFIG.getBoolean("Commands.Gamemode");
		clearCommand = CONFIG.getBoolean("Commands.Clear");
		craftCommand = CONFIG.getBoolean("Commands.Craft");
		signEdit = CONFIG.getBoolean("PlayerUtils.signEdit");
		headSit = CONFIG.getBoolean("PlayerUtils.headSit");
		colourChat = CONFIG.getBoolean("PlayerUtils.colourChat");
		greenText = CONFIG.getBoolean("PlayerUtils.greenText");
		MOTD = CONFIG.getString("ServerUtils.MOTD");
	}
	public static boolean enableSkyDimensionPortal;
	public static boolean DisableTNTOverworld;
	public static boolean DisableTNTNether;
	public static boolean DisableTNTSky;
	public static int addedTicksCatchable;
	public static int maxHomes;
	public static boolean disableBedExplosion;
	public static boolean signEdit;
	public static boolean headSit;
	public static boolean colourChat;
	public static boolean greenText;
	public static boolean gamemodeAll;
	public static boolean spawnCommand;
	public static boolean giveCommand;
	public static boolean homeCommand;
	public static boolean staticFire;
	public static boolean clearCommand;

	public static boolean craftCommand;
	public static String MOTD;

	public static final Set<String> vanished = new HashSet();
	public static File vanishedFile;

	@Override
    public void onInitialize() {

		MinecraftServer server = MinecraftServer.getInstance();
		String subdirectory = "player-homes";


        // Create the subdirectory if it doesn't exist
		File directory = new File(subdirectory);
		if (!directory.exists()) {
			directory.mkdirs(); // Create the directory and its parent directories if necessary
		}

		String vanishFile = "vanished.txt";

		// Create the subdirectory if it doesn't exist
        vanishedFile = new File(vanishFile);

        LOGGER.info("Better than Vanilla initialized.");

		if (enableSkyDimensionPortal) {
				((BlockPortal) BlockPortal.portalParadise).portalTriggerId = BlockPortal.fluidWaterFlowing.id;
		}


    }

	public static void vanishPlayer(String s) {
		vanished.add(s.toLowerCase());
		writeVanishedPlayers();
	}

	public static void unvanishPlayer(String s) {
		vanished.remove(s.toLowerCase());
		writeVanishedPlayers();
	}
	public static List<String> readVanishedFileLines() throws IOException {
		return Files.readAllLines(vanishedFile.toPath());
	}


	private static void writeVanishedPlayers() {
		try {
			PrintWriter printwriter = new PrintWriter(new FileWriter(vanishedFile, false));
			Iterator iterator = vanished.iterator();

			while(iterator.hasNext()) {
				String s = (String)iterator.next();
				printwriter.println(s);
			}

			printwriter.close();
		} catch (Exception var4) {
			LOGGER.warn("Failed to save ban list: " + var4);
		}

	}

	@Override
	public void beforeGameStart() {

	}
	//Code taken from playerlogger plugin. I needed it. Seriously. I would've thrown something if i didn't have something to go off of.
	public static void homesUtil(String fileContents, String fileName) {
		try {
			// Specify the subdirectory and file name
			String subdirectory = "player-homes";
			String filePath = subdirectory + File.separator + fileName + ".txt";

			// Create the subdirectory if it doesn't exist
			File directory = new File(subdirectory);
			if (!directory.exists()) {
				directory.mkdirs(); // Create the directory and its parent directories if necessary
			}

			// Use FileWriter constructor with "true" to enable append mode
			FileWriter myWriter = new FileWriter(filePath, true);

			// changed sysout formatting -MilkFrog
			myWriter.write(fileContents + "\n");
			myWriter.close();
		} catch (IOException e) {
			System.out.println("A big bad error occurred.");
			e.printStackTrace();
		}
	}
	@Override
	public void afterGameStart() {
		MinecraftServer mcs = MinecraftServer.getInstance();
		mcs.motd = MOTD;

	}
	@Override
	public void onRecipesReady() {
	}
}
