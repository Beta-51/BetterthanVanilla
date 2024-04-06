package lusiiplugin;

import lusiiplugin.utils.*;
import lusiiplugin.utils.TPA.PlayerTPInfo;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockPortal;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.packet.Packet20NamedEntitySpawn;
import net.minecraft.core.net.packet.Packet9Respawn;
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
	public static final String CFG_DIR = "config";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TomlConfigHandler CONFIG;
	public static boolean enableSkyDimensionPortal;
	public static int NickLength;
	public static boolean enableAntiTrampleFence;
	public static double deathCost;
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
		toml.addEntry("WorldUtils.DeathCost", "Points taken upon death, will be multiplied with this number.", 0.95);
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
		deathCost = CONFIG.getDouble("WorldUtils.DeathCost");
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
	public static ConfigBuilder info;
	public static ConfigBuilder rules;
	public static final Set<String> vanished = new HashSet();
	public static File vanishedFile;

	@Override
	public void onInitialize() {
		if (enableSkyDimensionPortal) {
			((BlockPortal) Block.portalParadise).portalTriggerId = Block.fluidWaterFlowing.id;

		}

		System.out.println();
		System.out.println("Better than Vanilla loading.");
		System.out.println();

		Path oldInfoFile = Paths.get(LusiiPlugin.CFG_DIR)
			.resolve("BetterThanVanillaInfo.txt");
		Path newInfoFile = Paths.get(LusiiPlugin.CFG_DIR)
			.resolve("BTVInfo.txt");

		if (Files.exists(oldInfoFile) && !Files.exists(newInfoFile)) {
			try {
				Files.move(oldInfoFile, newInfoFile);
			} catch (IOException e) {
				System.out.println("Could not create migrate info file!");
				System.out.println("Generating new from default");
			}
		}

		initInfo();
//		initRules();

		Path saveDirPath = Paths.get(SAVE_DIR);
		if (!Files.exists(saveDirPath)) {
			try {
				Files.createDirectories(saveDirPath);
				System.out.println("Created /" + saveDirPath +"/ for BTV data files");
			} catch (IOException e) {
				System.out.println("Could not create save directory: " + SAVE_DIR);
				return; // Exit if the directory cannot be created
			}
		}

		homeManager = new PlayerHomesManager();
		System.out.println();
		System.out.println("Better than Vanilla initialized.");
		System.out.println();
	}

	public static void initInfo() {
		info = new ConfigBuilder("BTVInfo.txt",
			Arrays.asList(
				"<aqua>Thanks for installing Better than Vanilla!<r>",
				"<aqua>this is an automatically generated message<r>",
				"<aqua>and you may customize it in the config folder!<r>",
				"<aqua>Once you have modified this file run <lime>/info reload<r><aqua>!<r>",
				"///",
				"/// ---------------================= INFO SYNTAX =================--------------- ",
				"///",
				"/// - Lines staring with '///' are a comment and are not displayed to the user. ",
				"///",
				"/// - Use html like tags for formatting",
				"///    Example: <red><b>BOLD RED<r> normal text",
				"/// - You can escape the '<' and '>' symbols with a '\\'",
				"///    Example: \\<blue>",
				"///",
				"///  Formatting tags: ",
				"///             +-----------------------------------------------+",
				"///             | white   | gray    | grey   | silver | black   |",
				"///             |---------+---------+--------+--------+---------|",
				"///             | red     | orange  | yellow | green  | blue    |",
				"///             |---------+---------+--------+--------+---------|",
				"///             | magenta | brown   | cyan   | lime   | aqua    |",
				"///             |---------+---------+--------+--------+---------|",
				"///             | i = italics       | purple | pink   |",
				"///             |-------------------+--------+--------|",
				"///             | u = underline     | b = bold        |",
				"///             |-------------------+-----------------|",
				"///             | r / reset = reset | s = strike      |",
				"///             |-------------------+-----------------|",
				"///             | o = obfuscate     |",
				"///             +-------------------+"
			),
			true
		);
	}

	public static void initRules() {
		rules = new ConfigBuilder("BTVRules.txt",
			Arrays.asList(
				"<aqua>Basic rules:<r>",
				"<aqua>No cheating<r>",
				"<aqua>No harassing<r>",
				"<aqua>No minecraft youtuber shenanigans<r>",
				"<aqua>You can edit these in the config!<r>",
				"<aqua><r>",
				"<aqua><r>",
				"<aqua><r>",
				"<aqua><r>",
				"<aqua><r>",
				"<aqua><r>",
				"<aqua><r>",
				"<aqua><r>",
				"/// See BTVInfo.txt for formatting rules!"
			),
			true
		);
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

	public static void updateTPInfo(EntityPlayer p) {
		getTPInfo(p).update(p);
	}

	public static boolean teleport(EntityPlayer p, double x, double y, double z, int dimension) {
		return teleport(p, new HomePosition(x, y, z, dimension));
	}

	public static boolean teleport(EntityPlayer startPlayer, EntityPlayer endPlayer) {
		if (startPlayer.isPassenger() || endPlayer.isPassenger()) {
			startPlayer.addChatMessage("§4You cannot teleport as, or to, a passenger!");
			endPlayer.addChatMessage("§4You cannot teleport as, or to, a passenger!");
			return false;
		}
		NetServerHandler startHandle = ((EntityPlayerMP) startPlayer).playerNetServerHandler;
		NetServerHandler endHandle = ((EntityPlayerMP) endPlayer).playerNetServerHandler;
		double x = endPlayer.x;
		double y = endPlayer.y;
		double z = endPlayer.z;
		float xr = endPlayer.xRot;
		float yr = endPlayer.yRot;
		if (startPlayer.dimension != endPlayer.dimension) {
			EntityPlayerMP mp = (EntityPlayerMP) startPlayer;
			MinecraftServer.getInstance().playerList.sendPlayerToOtherDimension(mp, endPlayer.dimension);
			startHandle.sendPacket(new Packet9Respawn((byte) endPlayer.dimension, (byte) 0));
		}
		startHandle.teleportAndRotate(x, y, z, yr, xr);
		startPlayer.moveTo(x, y, z, yr, xr);
		// Show the teleported player to the accepting player instantly
		// instead of waiting on the server to send it
		endHandle.sendPacket(new Packet20NamedEntitySpawn(startPlayer));
		return true;
	}

	public static boolean teleport(EntityPlayer p, HomePosition h) {
		EntityPlayerMP mp = (EntityPlayerMP) p;
		NetServerHandler s = mp.playerNetServerHandler;
		if (p.isPassenger()) {
			p.addChatMessage("§4You can't teleport while you're a passenger.");
			return false;
		}
		if (p.dimension != h.dim) {
            MinecraftServer.getInstance().playerList.sendPlayerToOtherDimension(mp, h.dim);
			s.sendPacket(new Packet9Respawn((byte) h.dim, (byte) 0));
		}
		s.teleportAndRotate(h.x, h.y, h.z, p.yRot, p.xRot);
		p.moveTo(h.x, h.y, h.z, p.yRot, p.xRot);
		return true;
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

	@Override
	public void afterGameStart() {
		MinecraftServer mcs = MinecraftServer.getInstance();
		mcs.motd = MOTD;
	}
	@Override
	public void onRecipesReady() {
	}
}
