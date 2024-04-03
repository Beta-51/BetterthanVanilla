package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.PlayerTPInfo;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.packet.Packet20NamedEntitySpawn;
import net.minecraft.server.entity.player.EntityPlayerMP;

import java.util.Objects;

public class TPConfirmCommand extends Command {
	public TPConfirmCommand() {
		super("tpconfirm", "tpyes", "ty");
	}

	public boolean opRequired(String[] args) {
		return !LusiiPlugin.TPACommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/tpyes §4[username]");
		sender.sendMessage("§5Confirm a teleport request, optionally specify a player");
		sender.sendMessage("§5for multiple requests");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (sender.isConsole()) return true;

		EntityPlayer endPlayer = sender.getPlayer();
		PlayerTPInfo endInfo = LusiiPlugin.getTPInfo(endPlayer);

		if (endPlayer.dimension != 0) {
			sender.sendMessage("§4You may only use this in the overworld!");
			return true;
		}
		if (endInfo.hasNoRequest()) {
			sender.sendMessage("§4You don't have any requests.");
			return true;
		}

		String startPlayerName = endInfo.getNewestRequest();

		if (args.length > 0) {
			String target = args[0];
			if (Objects.equals(target, "wyspr")) target = "wyspr_"; // ;p

			if (endInfo.hasRequestFrom(target)) {
				startPlayerName = target;
			} else {
				sender.sendMessage("§1You don't have a request from §4" + target);
				return true;
			}
		}

		endInfo.removeRequest(startPlayerName);

		EntityPlayer startPlayer = handler.getPlayer(startPlayerName);
		if (startPlayer == null) {
			sender.sendMessage("§4" + startPlayerName + "§1 is not online.");
			return true;
		}

		PlayerTPInfo startInfo = LusiiPlugin.getTPInfo(startPlayer);
		startInfo.update(startPlayer);

		LusiiPlugin.teleport(startPlayer, endPlayer);

		if (startPlayer.isPassenger() || endPlayer.isPassenger()) {
			sender.sendMessage("§4You may not use this command as a passenger!");
			return true;
		}

		startPlayer.score -= LusiiPlugin.TPACost;

		handler.sendMessageToPlayer(startPlayer, "§1Teleported to " + endPlayer.getDisplayName());

		return true;
	}
}
