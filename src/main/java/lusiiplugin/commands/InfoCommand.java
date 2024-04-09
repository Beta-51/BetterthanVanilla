package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class InfoCommand extends Command {
	public InfoCommand() {
		super("info");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		int page = 0;
		if (args.length > 0) {
			try {
				page = Integer.parseInt(args[0]);
			} catch (NumberFormatException ignored) {
				// We don't need to do anything here. page = 0
			}
        }
		for (String line : LusiiPlugin.info.get(page)) sender.sendMessage(line);
		return true;
	}

	public boolean opRequired(String[] args) {
        return false;
    }

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/info");
		sender.sendMessage("§5Display useful information");
	}
}
