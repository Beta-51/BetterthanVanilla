package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.net.command.*;

import java.util.List;

public class TreecapToggleCommand extends Command {
	public TreecapToggleCommand() {
		super("tt", "ttreecap", "toggletreecap");
	}

	@Override
	public boolean execute(CommandHandler handler, CommandSender sender, String[] strings) {
		List<String> noCap = LusiiPlugin.noCapPlayers;
		String username = sender.getPlayer().username;

		if (noCap.contains(username)) {
			noCap.remove(username);
			sender.sendMessage("§1Treecapitator enabled.");
		} else {
			noCap.add(username);
			sender.sendMessage("§1Treecapitator disabled.");
		}

		return true;
	}

	public boolean opRequired(String[] args) {
		return false;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/tt");
		sender.sendMessage("§5Toggles treecapitator.");
	}
}
