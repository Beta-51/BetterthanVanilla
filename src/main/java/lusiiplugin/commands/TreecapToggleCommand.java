package lusiiplugin.commands;

import lusiiplugin.utils.PlayerData;
import net.minecraft.core.net.command.*;

public class TreecapToggleCommand extends Command {
	public TreecapToggleCommand() {
		super("tt", "ttreecap", "toggletreecap");
	}

	@Override
	public boolean execute(CommandHandler handler, CommandSender sender, String[] strings) {
		boolean treecapEnabled = PlayerData.get(sender.getPlayer()).treecap().toggle();

		if (treecapEnabled) {
			sender.sendMessage("§1Treecapitator enabled.");
		} else {
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
