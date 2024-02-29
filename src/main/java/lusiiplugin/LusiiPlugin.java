package lusiiplugin;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.net.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class LusiiPlugin implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "betterthanvanilla";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TomlConfigHandler CONFIG;
	static {
		Toml toml = new Toml();
		toml.addCategory("WorldUtils");
		toml.addEntry("WorldUtils.StaticFire", "Static fire (Fire does not spread, and does not delete blocks).", false);
		toml.addEntry("WorldUtils.DisableTNT", "Removes all PrimedTNT entities the moment they tick, and prevents TNT from being ignited.", false);
		toml.addCategory("Commands");
		toml.addEntry("Commands.Give", "Let non-opped players use /give.", false);
		toml.addEntry("Commands.Home", "Let non-opped players use /home commands.", true);
		toml.addEntry("Commands.HomeLimit","Max amount of homes a player may have. 0 = infinite", 5);
		toml.addEntry("Commands.Spawn", "Let non-opped players use /spawn.", true);
		toml.addEntry("Commands.Gamemode", "Let non-opped players use /gamemode.", false);
		toml.addEntry("Commands.Clear", "Let non-opped players use /clear.", false);
		toml.addCategory("PlayerUtils");
		toml.addEntry("PlayerUtils.signEdit", "Allows players to edit signs by sneaking when breaking a sign and replacing it.", true);
		toml.addEntry("PlayerUtils.headSit", "Allows players to sit on eachothers' heads when holding nothing in their hand.", false);
		toml.addEntry("PlayerUtils.colourChat", "Allows players to use $$ as colour code for colourful chatting, obfuscation is disabled.", true);
		toml.addEntry("PlayerUtils.greenText", "Allows players to turn their text green by putting '>' at the start of their messages.", true);
		CONFIG = new TomlConfigHandler(MOD_ID, toml);
		staticFire = CONFIG.getBoolean("WorldUtils.StaticFire");
		disableTNT = CONFIG.getBoolean("WorldUtils.DisableTNT");
		giveCommand = CONFIG.getBoolean("Commands.Give");
		homeCommand = CONFIG.getBoolean("Commands.Home");
		maxHomes = CONFIG.getInt("Commands.HomeLimit");
		spawnCommand = CONFIG.getBoolean("Commands.Spawn");
		gamemodeAll = CONFIG.getBoolean("Commands.Gamemode");
		clearCommand = CONFIG.getBoolean("Commands.Clear");
		signEdit = CONFIG.getBoolean("PlayerUtils.signEdit");
		headSit = CONFIG.getBoolean("PlayerUtils.headSit");
		colourChat = CONFIG.getBoolean("PlayerUtils.colourChat");
		greenText = CONFIG.getBoolean("PlayerUtils.greenText");
	}
	public static int maxHomes;
	public static boolean signEdit;
	public static boolean headSit;
	public static boolean colourChat;
	public static boolean greenText;
	public static boolean gamemodeAll;
	public static boolean spawnCommand;
	public static boolean giveCommand;
	public static boolean homeCommand;
	public static boolean disableTNT;
	public static boolean staticFire;
	public static boolean clearCommand;
	@Override
    public void onInitialize() {
        LOGGER.info("Better than Vanilla initialized.");
    }

	@Override
	public void beforeGameStart() {

	}
	//Code taken from playerlogger plugin. I needed it. Seriously. I would've thrown something if i didn't have something to go off of.
	public static void logFile(String fileContents, String fileName) {
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
	}
	@Override
	public void onRecipesReady() {
	}
}
