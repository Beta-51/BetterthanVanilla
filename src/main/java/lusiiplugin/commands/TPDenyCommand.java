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

public class TPDenyCommand extends Command {
	public TPDenyCommand() {
		super("tpdeny", "tpno", "tn");
	}

	public boolean opRequired(String[] args) {
		return !LusiiPlugin.TPACommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/tpno §4[username]");
		sender.sendMessage("§5Deny a teleport request, optionally specify a player");
		sender.sendMessage("§5for multiple requests");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (sender.isConsole()) return true;

		EntityPlayer player = sender.getPlayer();
		PlayerData.TPInfo tpInfo = PlayerData.get(player).tpInfo();

		if (tpInfo.hasNoRequests()) {
			sender.sendMessage("§4You don't have any requests.");
			return true;
		}

		Pair<String, RequestType> request = tpInfo.getNewestRequest();
		String denyUser = request.getKey();

		if (args.length > 0) {
			String target = args[0];
			if (Objects.equals(target, "wyspr")) target = "wyspr_"; // ;3

			if (tpInfo.hasRequestFrom(target)) {
				denyUser = target;
			} else {
				sender.sendMessage("§1You don't have a request from §4" + target);
				return true;
			}
		}

		tpInfo.removeRequest(denyUser);

		sender.sendMessage("§1Denied TP request from §4" + denyUser);
		return true;
	}
}
