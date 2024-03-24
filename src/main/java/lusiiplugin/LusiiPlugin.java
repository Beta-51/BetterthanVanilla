package lusiiplugin;

import lusiiplugin.utils.HomePosition;
import lusiiplugin.utils.PlayerHomes;
import lusiiplugin.utils.PlayerHomesManager;
import lusiiplugin.utils.PlayerTPInfo;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.BlockPortal;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.packet.Packet20NamedEntitySpawn;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.handler.NetServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class LusiiPlugin implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "betterthanvanilla";
	public static final String SAVE_DIR = "lusiibtv";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TomlConfigHandler CONFIG;
	public static boolean enableSkyDimensionPortal;
	public static int NickLength;
	public static boolean enableAntiTrampleFence;
	public static boolean RTPCommand;
	public static int RTPCost;
	public static boolean TPACommand;
	public static int TPACost;
	public static int TPTimeout;
	public static boolean BackCommand;
	public static boolean disableTrample;
	public static int DisableTNTOverworld;
	public static int DisableTNTNether;
	public static int DisableTNTSky;
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

	static {
		Toml toml = new Toml();
		toml.addCategory("WorldUtils");
		toml.addEntry("WorldUtils.StaticFire", "Static fire (Fire does not spread, and does not delete blocks).", false);
		toml.addEntry("WorldUtils.DisableBedExplosions", "Remove explosions from beds in non-respawn dimensions.", false);
		toml.addEntry("WorldUtils.EnableSkyDimensionPortals", "Enable making portals to the Sky Dimension", false);
		toml.addEntry("WorldUtils.AddTicksCatchableFishing", "Adds time in ticks to the window of time that you can catch fish, very useful for players with higher ping. 10-15 is recommended. 0 to disable, -40 or lower to make fishing impossible.",10);
		toml.addEntry("WorldUtils.DisableTNTOverworld", "Disable TNT in the overworld after y level. 0 = no TNT", 127);
		toml.addEntry("WorldUtils.DisableTNTNether", "Disable TNT in the nether after y level. 0 = no TNT", 256);
		toml.addEntry("WorldUtils.DisableTNTSky", "Disable TNT in the sky dimension after y level. 0 = no TNT", 256);
		toml.addEntry("WorldUtils.EnableAntiTrampleFences", "Re-enable farmland trample prevention by putting fences under them", false);
		toml.addEntry("WorldUtils.DisableTrample", "Completely disables trampling crops", false);
		toml.addCategory("Commands");
		toml.addEntry("Commands.Give", "Let non-opped players use /give.", false);
		toml.addEntry("Commands.Home", "Let non-opped players use /home commands.", true);
		toml.addEntry("Commands.HomeLimit","Max amount of homes a player may have. 0 = infinite", 5);
		toml.addEntry("Commands.Spawn", "Let non-opped players use /spawn.", true);
		toml.addEntry("Commands.Gamemode", "Let non-opped players use /gamemode.", false);
		toml.addEntry("Commands.Clear", "Let non-opped players use /clear.", false);
		toml.addEntry("Commands.Craft", "Let non-opped players use /craft", true);
		toml.addEntry("Commands.TPA", "Let non-opped players use /tpa", true);
		toml.addEntry("Commands.Back", "Let non-opped players use /back", true);
		toml.addEntry("Commands.TPACost","Amount of points that TPA will take on use. 0 to disable (Not recommended, easily spammable)", 100);
		toml.addEntry("Commands.TPTimeout", "Number of seconds between uses of /tpa, /rtp, and /home", 15);
		toml.addEntry("Commands.RTP", "Let players use /RTP", true);
		toml.addEntry("Commands.RTPCost","Amount of points that RTP will take on use. 0 to disable (Not recommended, easily spammable)", 1000);
		toml.addEntry("Commands.NickLength","Nickname length limit, default = 16", 16);
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
		DisableTNTOverworld = CONFIG.getInt("WorldUtils.DisableTNTOverworld");
		DisableTNTNether = CONFIG.getInt("WorldUtils.DisableTNTNether");
		DisableTNTSky = CONFIG.getInt("WorldUtils.DisableTNTSky");
		enableAntiTrampleFence = CONFIG.getBoolean("WorldUtils.EnableAntiTrampleFences");
		disableTrample = CONFIG.getBoolean("WorldUtils.DisableTrample");
		giveCommand = CONFIG.getBoolean("Commands.Give");
		homeCommand = CONFIG.getBoolean("Commands.Home");
		maxHomes = CONFIG.getInt("Commands.HomeLimit");
		spawnCommand = CONFIG.getBoolean("Commands.Spawn");
		gamemodeAll = CONFIG.getBoolean("Commands.Gamemode");
		clearCommand = CONFIG.getBoolean("Commands.Clear");
		craftCommand = CONFIG.getBoolean("Commands.Craft");
		RTPCommand = CONFIG.getBoolean("Commands.RTP");
		RTPCost = CONFIG.getInt("Commands.RTPCost");
		BackCommand = CONFIG.getBoolean("Commands.Back");
		TPACommand = CONFIG.getBoolean("Commands.TPA");
		TPACost = CONFIG.getInt("Commands.TPACost");
		TPTimeout = CONFIG.getInt("Commands.TPTimeout");
		NickLength = CONFIG.getInt("Commands.NickLength");
		signEdit = CONFIG.getBoolean("PlayerUtils.signEdit");
		headSit = CONFIG.getBoolean("PlayerUtils.headSit");
		colourChat = CONFIG.getBoolean("PlayerUtils.colourChat");
		greenText = CONFIG.getBoolean("PlayerUtils.greenText");
		MOTD = CONFIG.getString("ServerUtils.MOTD");
	}

	public static String MOTD;
	private static HashMap<String, PlayerTPInfo> TPInfo = new HashMap<>();
	private static PlayerHomesManager homeManager;
	public static final Set<String> vanished = new HashSet();
	public static File vanishedFile;

	@Override
	public void onInitialize() {
		if (enableSkyDimensionPortal) {
			((BlockPortal) BlockPortal.portalParadise).portalTriggerId = BlockPortal.fluidWaterFlowing.id;
		}

		Path saveDirPath = Paths.get(SAVE_DIR);
		if (!Files.exists(saveDirPath)) {
			try {
				Files.createDirectories(saveDirPath);
				System.out.println("created" + saveDirPath);
			} catch (IOException e) {
				LOGGER.error("Could not create save directory: " + SAVE_DIR, e);
				System.out.println("Could not create save directory: " + SAVE_DIR);

				return; // Exit if the directory cannot be created
			}
		}

		homeManager = new PlayerHomesManager();

		LOGGER.info("Better than Vanilla initialized.");
	}

	public static void convertOldHomes() {
		homeManager.importOldHomes();
	}

	public static PlayerHomes getPlayerHomes(EntityPlayer p) {
		return homeManager.getPlayerHomes(p);
	}

	public static void savePlayerHomes() {
		homeManager.save();
	}

	public static PlayerTPInfo getTPInfo(EntityPlayer p) {
		return TPInfo.computeIfAbsent(p.username, k -> new PlayerTPInfo(p));
	}

	public static void teleport(EntityPlayer p, double x, double y, double z) {
		NetServerHandler s = ((EntityPlayerMP) p).playerNetServerHandler;
		s.teleport(x, y, z);
		p.moveTo(x, y, z, p.yRot, p.xRot);
	}

	public static void teleport(EntityPlayer startPlayer, EntityPlayer endPlayer) {
		NetServerHandler hs = ((EntityPlayerMP) startPlayer).playerNetServerHandler;
		NetServerHandler he = ((EntityPlayerMP) endPlayer).playerNetServerHandler;
		double x = endPlayer.x;
		double y = endPlayer.y;
		double z = endPlayer.z;
		float xr = endPlayer.xRot;
		float yr = endPlayer.yRot;
		hs.teleport(x, y, z);
		startPlayer.moveTo(x, y, z, yr, xr);
		// Show the teleported player to the accepting player instantly
		// instead of waiting on the server to send it
		he.sendPacket(new Packet20NamedEntitySpawn(startPlayer));
	}

	public static void teleport(EntityPlayer p, Vec3d pos) {
		NetServerHandler s = ((EntityPlayerMP) p).playerNetServerHandler;
		s.teleport(pos.xCoord, pos.yCoord, pos.zCoord);
		p.moveTo( pos.xCoord, pos.yCoord, pos.zCoord, p.yRot, p.xRot);
	}

	public static void teleport(EntityPlayer p, HomePosition h) {
		NetServerHandler s = ((EntityPlayerMP) p).playerNetServerHandler;
		s.teleport(h.x, h.y, h.z);
		p.moveTo(h.x, h.y, h.z, p.yRot, p.xRot);
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
			Iterator<String> iterator = vanished.iterator();

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
