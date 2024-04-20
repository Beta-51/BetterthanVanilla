package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.io.IOException;
import java.nio.file.*;

public class TransferHomesCommand extends Command {
	public TransferHomesCommand() {
		super("transferhomes", "th");
	}

	@Override
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (args.length < 2) {
			return false;
		}
		String oldPlayer = args[0];
		String newPlayer = args[1];
		Path dataDir = Paths.get(LusiiPlugin.SAVE_DIR, oldPlayer);

		if (Files.notExists(dataDir)) {
			sender.sendMessage("§4Player data not found for :§3" + oldPlayer);
			return false;
		}

		try {
			Files.move(dataDir, dataDir.resolveSibling(newPlayer), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.err.println("Could not move directory: " + dataDir);
		}
		return true;
	}

	@Override
	public boolean opRequired(String[] strings) {
		return true;
	}

	@Override
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/th OldPlayer NewPlayer");
	}
}
