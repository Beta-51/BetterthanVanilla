package lusiiplugin.commands;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class OPChatCommand extends Command {
	public OPChatCommand() {
		super("opchat", "opc","chatop");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg).append(" ");
        }
		handler.asServer().sendMessageToAdmins("[§e§lOP CHAT§r] <" + sender.getPlayer().username + "§r> " +builder.toString().replace("$$","§"));
		return true;
	}

	public boolean opRequired(String[] args) {
		return true;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		if (sender.isAdmin() || sender.isConsole()) {
			sender.sendMessage("§3/opchat §4<message>");
			sender.sendMessage("§5Broadcast a message to all players");
		}
	}
}
