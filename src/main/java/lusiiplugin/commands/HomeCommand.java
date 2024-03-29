package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.HomePosition;
import lusiiplugin.utils.PlayerHomes;
import lusiiplugin.utils.PlayerTPInfo;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.*;
import net.minecraft.core.net.packet.Packet61PlaySoundEffect;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.handler.NetServerHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

public class HomeCommand extends Command {
	public HomeCommand() {
		super("home");
	}
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		EntityPlayer p = sender.getPlayer();
		PlayerHomes homes = LusiiPlugin.getPlayerHomes(p);
		PlayerTPInfo tpInfo = LusiiPlugin.getTPInfo(p);

		String homeName;
		if (args.length > 0) {
			homeName = String.join(" ", args);
		} else {
			homeName = "home";
		}

		Optional<HomePosition> homePos;
		if (homeName.equals("bed")) {
			ChunkCoordinates b = p.getPlayerSpawnCoordinate();
			if (b == null) {
				sender.sendMessage("§1You do not have a bed! You should work on that!");
				return true;
			}
			homePos = Optional.of(new HomePosition(b.x, b.y+0.6, b.z, 0));
		} else {
			homePos = homes.getHomePos(homeName);
		}

		if (!homePos.isPresent()) {
			sender.sendMessage("§1You do not have a home named: §4" + homeName);
			sender.sendMessage("§1View your homes with: §3/homes");
			return true;
		}

		if (tpInfo.canTP() || sender.isAdmin()) {
			tpInfo.update(p);
			HomePosition h = homePos.get();
			LusiiPlugin.teleport(p, h);
			sender.sendMessage("§4Teleported to §1" + homeName);
		} else {
			int waitTime = tpInfo.cooldown();
			sender.sendMessage("§4Teleport available in §1" + waitTime + "§4 seconds.");
		}

		return true;

	}

	public EntityPlayer getPlayer(CommandHandler handler, String name) {
		EntityPlayer player = handler.getPlayer(name);
		if (player == null) {
			throw new CommandError("§4Player not found: §1" + name);
		} else {
			return player;
		}
	}

	public boolean opRequired(String[] args) {
		return !LusiiPlugin.homeCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/home [home]");
	}
}
