package lusiiplugin.commands;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class MOTDCommand extends Command {
	public MOTDCommand() {
		super("motd");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(handler.asServer().minecraftServer.motd);
			return true;
		} else {
			if (!sender.isAdmin()) {
				sender.sendMessage(handler.asServer().minecraftServer.motd);
				return false;
			}
			StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg).append(" ");
            }
			handler.asServer().minecraftServer.motd = builder.toString().replace("$$","§");
			return true;
		}
	}

	public boolean opRequired(String[] args) {
		return false;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/motd");
		sender.sendMessage("§5Show the server MOTD");
		if (sender.isAdmin() || sender.isConsole()) {
			sender.sendMessage("§3/motd §4<message>");
			sender.sendMessage("§5Set the server MOTD");
		}
	}
}
