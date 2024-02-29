package lusiiplugin;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class InfoCommand extends Command {
	public InfoCommand() {
		super("info", "");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		sender.sendMessage("Welcome to §6§lLusii's Creative Anarchy server§r!");
		sender.sendMessage("Here you may §egrief§r, §5build§r, or §dfight§r!");
		sender.sendMessage("You have access to new commands such as:");
		sender.sendMessage("/gamemode");
		sender.sendMessage("/clear");
		sender.sendMessage("/spawn");
		sender.sendMessage("/give");
		sender.sendMessage("You may also §4§nedit§r signs by sneaking while you break them");
		sender.sendMessage("and replacing a sign in its spot! You may also highlight");
		sender.sendMessage("your message by putting \"§5>§r\" at the start! For example:");
		sender.sendMessage("§5> Hello! This text is green!");
		sender.sendMessage("You can also §bsit§r on players' heads! To get off of their head");
		sender.sendMessage("just sneak!");
		sender.sendMessage("You can also make chat messages §4pretty§r with colour codes!");
		sender.sendMessage("Just use \"$$\" and then a colour code to activate it! For a list");
		sender.sendMessage("of colour codes, type §3§l/colours§r (§3§l/colors§r works too!)");
		sender.sendMessage("§4§l(Open chat and scroll to read!)");

		return true;
	}
//
	public boolean opRequired(String[] args) {
		return false;
	}
//
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}
