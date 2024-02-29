package lusiiplugin;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomesCommand extends Command {
	public HomesCommand() {
		super("homes", "");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		String subdirectory = "player-homes";
		String filePath = subdirectory + File.separator + sender.getPlayer().username + ".txt";
		List<String> results = new ArrayList<String>();


		File[] files = new File(subdirectory).listFiles();
		for (File file : files) {
			if (file.isFile()) {
				if (file.getName().startsWith(sender.getPlayer().username)) {
					if (file.getName().equals(sender.getPlayer().username + ".txt")) {
						results.add("home");
					} else {
						results.add(file.getName().replace(".txt", "").replaceFirst(sender.getPlayer().username, ""));
					}
				}
			}
		}

		String theResults = results.toString().replace(" , ", ", ");
		theResults = theResults.replace("[", "");
		theResults = theResults.replace("]", "");
		sender.sendMessage("§4Homes: §r" + theResults);
		return true;
		}





	public boolean opRequired(String[] args) {
		return LusiiPlugin.homeCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/homes");
	}
}
