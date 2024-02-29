package lusiiplugin;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.entity.projectile.EntityArrow;
import net.minecraft.core.net.command.*;
import net.minecraft.core.net.packet.Packet34EntityTeleport;
import net.minecraft.core.world.World;
import net.minecraft.server.entity.player.EntityPlayerMP;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.floor;
import static net.minecraft.core.net.command.commands.TeleportCommand.teleport;

public class HomeCommand extends Command {
	public HomeCommand() {
		super("home", "");
	}
	static List<String> lines;

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		EntityPlayer player;
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < args.length; ++i) {
			builder.append(args[i]).append(" ");
		}
		String subdirectory = "player-homes";
		if (args.length == 0 || builder.toString().equals("home")) {
			String filePath = subdirectory + File.separator + sender.getPlayer().username + ".txt";
			File file = new File(filePath);
			if (!file.exists()) {
				sender.sendMessage("You do not have a home!");
				return false;
			}
			lines = readTxtLinesUnnamed(sender.getPlayer().username);
		} else {
			String filePath = subdirectory + File.separator + sender.getPlayer().username + builder + ".txt";
			File file = new File(filePath);
			if (!file.exists()) {
				sender.sendMessage("You do not have a named home!");
				return false;
			}
			lines = readTxtLines(String.valueOf(builder),sender.getPlayer().username);
		}
		player = sender.getPlayer();
		//teleport(handler, sender, player, Integer.parseInt(lines.get(4)), Double.parseDouble(lines.get(0)), Double.parseDouble(lines.get(1)), Double.parseDouble(lines.get(2)), (double)player.yRot, (double)player.xRot, (EntityPlayer)null);
		teleport(handler, sender, player, Integer.parseInt(lines.get(3)), Double.parseDouble(lines.get(0)), Double.parseDouble(lines.get(1)), Double.parseDouble(lines.get(2)), (double)player.yRot, (double)player.xRot, (EntityPlayer)null);
		//handler.asServer().minecraftServer.playerList.sendPlayerToOtherDimension((EntityPlayerMP)sender.getPlayer(),Integer.parseInt(lines.get(4)));
		//handler.asServer().minecraftServer.playerList.sendPacketToPlayer(sender.getPlayer().username,new Packet34EntityTeleport(sender.getPlayer().id,Integer.parseInt(lines.get(0)), Integer.parseInt(lines.get(1)), Integer.parseInt(lines.get(2)),(byte)sender.getPlayer().yRot,(byte)sender.getPlayer().xRot));
		//sender.getPlayer().absMoveTo(Double.parseDouble(lines.get(0)), Double.parseDouble(lines.get(1)), Double.parseDouble(lines.get(2)),sender.getPlayer().yRot,sender.getPlayer().xRot);
		return true;
	}



	public EntityPlayer getPlayer(CommandHandler handler, String name) {
		EntityPlayer player = handler.getPlayer(name);
		if (player == null) {
			throw new CommandError("Player not found: " + name);
		} else {
			return player;
		}
	}

	public static void teleport(CommandHandler handler, CommandSender sender, EntityPlayer p1, Integer dimension, double x, double y, double z, double yaw, double pitch, EntityPlayer p2) {
		if (p1 instanceof EntityPlayerMP) {
			EntityPlayerMP p1MP = (EntityPlayerMP)p1;
			if (dimension != null && p1MP.dimension != dimension && handler instanceof ServerCommandHandler) {
				ServerCommandHandler serverCommandHandler = (ServerCommandHandler)handler;
				serverCommandHandler.minecraftServer.playerList.sendPlayerToOtherDimension(p1MP, dimension);
			}

			p1MP.playerNetServerHandler.teleportAndRotate(x, y, z, p1MP.yRot, p1MP.xRot);
		} else {
			p1.absMoveTo(x, y, z, p1.yRot, p1.xRot);
		}

	}




	public boolean opRequired(String[] args) {
		return LusiiPlugin.homeCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/home [home]");
	}
	private List<String> readTxtLinesUnnamed(String username) {
		String subdirectory = "player-homes";
		String filePath = subdirectory + File.separator + username + ".txt";
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		try {
			return Files.readAllLines(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<String> readTxtLines(String name, String username) {
		String subdirectory = "player-homes";
		String filePath = subdirectory + File.separator + username + name + ".txt";
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		try {
			return Files.readAllLines(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
