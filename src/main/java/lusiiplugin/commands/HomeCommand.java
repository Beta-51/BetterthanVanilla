package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.*;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.*;
import net.minecraft.core.world.chunk.ChunkCoordinates;

import java.util.Optional;

public class HomeCommand extends Command {
	public HomeCommand() {
		super("home");
	}
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		EntityPlayer p = sender.getPlayer();
		PlayerData playerData = PlayerData.get(p);
		PlayerData.Homes homes = playerData.homes();
		PlayerData.TPInfo tpInfo = playerData.tpInfo();
		String homeName;
		if (args.length > 0) {
			homeName = String.join(" ", args);
		} else {
			homeName = "home";
		}

		Optional<WorldPosition> homePos;

		if (homeName.equals("bed")) {
			ChunkCoordinates b = p.getPlayerSpawnCoordinate();
			if (b == null) {
				sender.sendMessage("§1You do not have a bed! You should work on that!");
				return true;
			}
			homePos = Optional.of(new WorldPosition(b.x, b.y+1.0, b.z, 0));
		} else {
			homePos = homes.getHomePos(homeName);
		}
		boolean homeNotFound = !homePos.isPresent();

		if (homeNotFound) {
			sender.sendMessage("§1You do not have a home named: §4" + homeName);
			sender.sendMessage("§1View your homes with: §3/homes");
			return true;
		}

		if (tpInfo.canTP() || sender.isAdmin()) {
			tpInfo.update();
			WorldPosition h = homePos.get();
			if (LusiiPlugin.teleport(p, h)) {
				sender.sendMessage("§4Teleported to §1" + homeName);
			}
		} else {
			int waitTime = tpInfo.cooldown();
			sender.sendMessage("§4Teleport available in §1" + waitTime + "§4 seconds.");
		}

		return true;
	}

	public boolean opRequired(String[] args) {
		return !LusiiPlugin.homeCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/home §4[home]");
		sender.sendMessage("§5Teleport to one of your homes");
	}
}
