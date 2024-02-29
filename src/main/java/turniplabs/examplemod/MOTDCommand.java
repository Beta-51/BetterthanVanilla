package turniplabs.examplemod;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class MOTDCommand extends Command {
	public MOTDCommand() {
		super("motd", "");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(handler.asServer().minecraftServer.motd);
			return false;
		} else {
			if (!sender.isAdmin()) {
				sender.sendMessage(handler.asServer().minecraftServer.motd);
			}
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < args.length; ++i) {
				builder.append(args[i]).append(" ");
			}
			handler.asServer().minecraftServer.motd = builder.toString().replace("$$","§");
			return true;
		}
	}

	public boolean opRequired(String[] args) {
		return false;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		if (sender.isAdmin() || sender.isConsole()) {
			sender.sendMessage("/motd <msg>");
		}
	}
}
