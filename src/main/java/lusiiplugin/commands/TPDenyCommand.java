package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.PlayerTPInfo;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.Objects;

public class TPDenyCommand extends Command {
	public TPDenyCommand() {
		super("tpdeny", "tpno", "tn");
	}

	public boolean opRequired(String[] args) {
		return !LusiiPlugin.TPACommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (sender.isConsole()) return true;

		EntityPlayer player = sender.getPlayer();
		PlayerTPInfo info = LusiiPlugin.getTPInfo(player);

		if (info.hasNoRequest()) {
			sender.sendMessage("§4You don't have any requests.");
			return true;
		}

		String denyPlayerName = info.getNewestRequest();

		if (args.length > 0) {
			String target = args[0];
			if (Objects.equals(target, "wyspr")) target = "wyspr_"; // ;3

			if (info.hasRequestFrom(target)) {
				denyPlayerName = target;
			} else {
				sender.sendMessage("§1You don't have a request from §4" + target);
				return true;
			}
		}

		info.removeRequest(denyPlayerName);

		sender.sendMessage("§1Denied TP request from §4" + denyPlayerName);
		return true;
	}
}
