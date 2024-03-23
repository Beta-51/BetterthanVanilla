package lusiiplugin.commands;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class TPAAllCommand extends Command {
	public TPAAllCommand() {
		super("tpaall");
	}

	public boolean opRequired(String[] args) {
		return true;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		return true;
	}
}
