package lusiiplugin;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class InfoCommand extends Command {
	public InfoCommand() {
		super("info", "");
	}
	static List<String> lines;
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {

		String subdirectory = "config";
		String filePath = subdirectory + File.separator + "BetterThanVanillaInfo" + ".txt";

		// Create the subdirectory if it doesn't exist
		File directory = new File(subdirectory);
		File file = new File(filePath);
		if (!file.exists()) {
			sender.sendMessage("BetterThanVanillaInfo.txt does not exist. Creating it for you...");
			directory.mkdirs(); // Create the directory and its parent directories if necessary
			sender.sendMessage("Done! Check your config folder for BetterThanVanillaInfo.txt!");
			sender.sendMessage("Once you modify it you do not have to restart the server!");
			sender.sendMessage("For colour coding look at /colours!");
			sender.sendMessage("You can also edit what commands and features people have");
			sender.sendMessage("access to! Edit the betterthanvanilla.cfg file in the config folder!");
			sender.sendMessage("You will have to restart the server if you change this config file!");


			// Use FileWriter constructor with "true" to enable append mode
            FileWriter myWriter = null;
            try {
                myWriter = new FileWriter(filePath, true);
				myWriter.write("§3Thanks for installing lusii's plugin!" + "\n");
				myWriter.write("§3this is an automatically generated message" + "\n");
				myWriter.write("§3and you may customize it in the config folder!" + "\n");
				myWriter.write("§3Once you have modified this file re-run §l/info§3!");
			myWriter.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return true;
		}
		lines = readTxtLines();


		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).isEmpty()) {
				sender.sendMessage(" ");
			}
			sender.sendMessage(lines.get(i));
		}




		return true;
	}
//
	public boolean opRequired(String[] args) {
		return false;
	}
//
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}

	private List<String> readTxtLines() {
		String subdirectory = "config";
		String filePath = subdirectory + File.separator + "BetterThanVanillaInfo" + ".txt";
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		try {
			return Files.readAllLines(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
