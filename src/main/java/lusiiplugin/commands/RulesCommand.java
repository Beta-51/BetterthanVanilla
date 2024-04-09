package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandError;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class RulesCommand extends Command {
	public RulesCommand() {
		super("rules");
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
		for (String line : LusiiPlugin.rules.get(page)) sender.sendMessage(line);
		return true;
	}

	public boolean opRequired(String[] args) {
		return false;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/rules §4[page]");
		sender.sendMessage("§5Display the server rules");
	}
}
