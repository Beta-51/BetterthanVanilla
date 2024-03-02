package lusiiplugin;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.io.File;

public class DelhomeCommand extends Command {
	public DelhomeCommand() {
		super("delhome", "");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < args.length; ++i) {
			builder.append(args[i]).append(" ");
		}
		String subdirectory = "player-homes";
		if (args.length == 0 || builder.toString().equals("home")) {
			String filePath = subdirectory + File.separator + sender.getPlayer().username + ".txt";
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
				sender.sendMessage("§4Successfully deleted your home!");
				return true;
			}
			sender.sendMessage("§4You do not have a home set!");
			return true;
		}
		String filePath = subdirectory + File.separator + sender.getPlayer().username + builder + ".txt";
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
			sender.sendMessage("§4Deleted home " + builder + "!");
			return true;
		}
		sender.sendMessage("§4You do not have a home named " + builder + "!");
		return true;
	}





	public boolean opRequired(String[] args) {
		return !LusiiPlugin.homeCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/delhome");
	}
}
