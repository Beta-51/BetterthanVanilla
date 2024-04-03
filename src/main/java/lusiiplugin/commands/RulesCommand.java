package lusiiplugin.commands;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandError;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class RulesCommand extends Command {
	public RulesCommand() {
		super("rules");
	}
	static List<String> lines;
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (args.length > 1) {
			return false;
		}

		String subdirectory = "config";
		String filePath = subdirectory + File.separator + "BtVRules" + ".txt";
		// Create the subdirectory if it doesn't exist
		File directory = new File(subdirectory);
		File file = new File(filePath);
		if (!file.exists()) {
			if (!sender.isAdmin()) {
				sender.sendMessage("This page does not exist! Contact an §e§lOperator§r if this isn't right.");
				return true;
			}
			String filePath2 = subdirectory + File.separator + "BtVRules2" + ".txt";
			File directory2 = new File(subdirectory);
			File file2 = new File(filePath2);
			if (!file2.exists()) {
				directory2.mkdirs();
			}
			sender.sendMessage("BtVRules.txt does not exist. Creating it for you...");
			directory.mkdirs(); // Create the directory and its parent directories if necessary
			sender.sendMessage("Done! Check your config folder for BtVRules.txt!");
			sender.sendMessage("Once you modify it you do not have to restart the server!");
			sender.sendMessage("For colour coding look at /colours!");
			sender.sendMessage("Check out the pre-generated rules we made for you!");


			// Use FileWriter constructor with "true" to enable append mode
			FileWriter myWriter = null;
			FileWriter myWriter2 = null;
			try {
                myWriter = new FileWriter(filePath, true);
				myWriter2 = new FileWriter(filePath2, true);
				myWriter.write("§3Basic rules:" + "\n");
				myWriter.write("§3No cheating" + "\n");
				myWriter.write("§3No harassing" + "\n");
				myWriter.write("§3No minecraft youtuber shenanigans" + "\n");
				myWriter.write("§3Continue on /rules 2");
				myWriter.close();
				myWriter2.write("§3You can edit these in the config!" + "\n");
				myWriter2.write("§3You may also add more txt files, just" + "\n");
				myWriter2.write("§3follow the \"BtVRules#.txt\" pattern!" + "\n");
				myWriter2.write("§3You can also delete this file if you" + "\n");
				myWriter2.write("§3do not need it.");
				myWriter2.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return true;
		}
		if (args.length == 0) { // do not look at this please
			lines = readTxtLines(1);
		} else if (this.parseInt(args[0]) <= 1) {
			lines = readTxtLines(1);
		} else {
			lines = readTxtLines(this.parseInt(args[0]));
		}
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
		sender.sendMessage("§3/rules §4[page]");
		sender.sendMessage("§5Display the server rules");
	}

	public int parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception var3) {
			throw new CommandError("Not a number: \"" + str + "\"");
		}
	}


	private List<String> readTxtLines(int i) {
		String subdirectory = "config";
		String filePath;
		if (i == 1) {
			filePath = subdirectory + File.separator + "BtVRules" + ".txt";
		} else {
			filePath = subdirectory + File.separator + "BtVRules" +i+ ".txt";
		}
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
