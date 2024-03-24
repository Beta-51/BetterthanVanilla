package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class ConvertHomesCommand extends Command {
	public ConvertHomesCommand() {
		super("converthomes");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		LusiiPlugin.convertOldHomes();
		return true;
	}

	public boolean opRequired(String[] args) {
		return true;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}

