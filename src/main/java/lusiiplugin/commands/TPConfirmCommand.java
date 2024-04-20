package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.PlayerData;
import lusiiplugin.utils.PlayerData.TPInfo.RequestType;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import org.apache.commons.lang3.tuple.Pair;

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

		EntityPlayer acceptingPlayer = sender.getPlayer();
		PlayerData.TPInfo acceptInfo  = PlayerData.get(acceptingPlayer).tpInfo();

		if (acceptingPlayer.dimension != 0) {
			sender.sendMessage("§4You may only use this in the overworld!");
			return true;
		}
		if (acceptInfo.hasNoRequests()) {
			sender.sendMessage("§4You don't have any requests.");
			return true;
		}

		Pair<String, RequestType> request = acceptInfo.getNewestRequest();
		String confirmUser = request.getKey();

		if (args.length > 0) {
			String target = args[0];
			if (Objects.equals(target, "wyspr")) target = "wyspr_"; // ;p

			if (acceptInfo.hasRequestFrom(target)) {
				confirmUser = target;
			} else {
				sender.sendMessage("§1You don't have a request from §4" + target);
				return true;
			}
		}

		acceptInfo.removeRequest(confirmUser);

		EntityPlayer requestPlayer = handler.getPlayer(confirmUser);
		if (requestPlayer == null) {
			sender.sendMessage("§4" + confirmUser + "§1 is not online.");
			return true;
		}

		PlayerData.TPInfo requestInfo  = PlayerData.get(requestPlayer).tpInfo();

		boolean didTeleport = false;
		RequestType requestType = request.getValue();

		if (requestType == RequestType.TPA) {
			requestInfo.update();
			didTeleport = LusiiPlugin.teleport(requestPlayer, acceptingPlayer);
			if (didTeleport) {
				requestPlayer.addChatMessage("§1Teleported to " + acceptingPlayer.getDisplayName());
			}
		} else if (requestType == RequestType.TPAHERE) {
			acceptInfo.update();
			didTeleport = LusiiPlugin.teleport(acceptingPlayer, requestPlayer);
			if (didTeleport) {
				requestPlayer.addChatMessage("§1Teleported " + acceptingPlayer.getDisplayName() + " to you");
				acceptingPlayer.addChatMessage("§1Teleported to " + requestPlayer.getDisplayName());
			}
		}

		if (didTeleport) {
			requestPlayer.score -= LusiiPlugin.TPACost;
		}

		return true;
	}
}
