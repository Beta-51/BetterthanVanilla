package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.Objects;

public class InfoCommand extends Command {
	public InfoCommand() {
		super("info");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (Objects.equals(args[0], "reload")) {
			LusiiPlugin.initInfo();
		} else {
			for (String line : LusiiPlugin.infoText) sender.sendMessage(line);
		}
		return true;
	}

	public boolean opRequired(String[] args) {
		return false;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/info");
	}
}
