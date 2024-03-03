package lusiiplugin;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class CraftingCommand extends Command {
	public CraftingCommand() {
		super("craftingtable", "craft", "crafting");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (!LusiiPlugin.craftCommand) {
			sender.sendMessage("Enable crafting in the config!");
			return true;
		}
		sender.getPlayer().displayGUIWorkbench((int) sender.getPlayer().x, (int) sender.getPlayer().y, (int) sender.getPlayer().z);
		return true;
	}
//
	public boolean opRequired(String[] args) {
		return !LusiiPlugin.craftCommand;
	}
//
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}
