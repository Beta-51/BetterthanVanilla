package lusiiplugin;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class OPChatCommand extends Command {
	public OPChatCommand() {
		super("opchat", "opc","chatop");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < args.length; ++i) {
				builder.append(args[i]).append(" ");
			}
			handler.asServer().sendMessageToAdmins("[§e§lOP CHAT§r] <" + sender.getPlayer().username + "§r> " +builder.toString().replace("$$","§"));
			return true;
	}

	public boolean opRequired(String[] args) {
		return true;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		if (sender.isAdmin() || sender.isConsole()) {
			sender.sendMessage("/motd <msg>");
		}
	}
}
