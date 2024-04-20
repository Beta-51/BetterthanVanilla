package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.PlayerData;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class TPRequestsCommand extends Command {
	public TPRequestsCommand() {
		super("tprequests", "tpreq", "tpr");
	}

	public boolean opRequired(String[] args) {
		return !LusiiPlugin.TPACommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/tpreq");
		sender.sendMessage("§5View your teleport requests");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (sender.isConsole()) return true;

		EntityPlayer p = sender.getPlayer();
		PlayerData.TPInfo tpInfo  = PlayerData.get(p).tpInfo();
		if (tpInfo.hasNoRequests()) {
			sender.sendMessage("§4You don't have any requests.");
			return true;
		}
		String requests = String.join(", ", tpInfo.getAllRequests());

		sender.sendMessage("§1Current TP requests: " + requests);

		return true;
	}
}
