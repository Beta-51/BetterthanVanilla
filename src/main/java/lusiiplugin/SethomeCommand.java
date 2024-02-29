package lusiiplugin;

import com.mojang.nbt.CompoundTag;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import org.apache.commons.lang3.mutable.Mutable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.core.net.command.commands.GiveCommand.givePlayerItem;

public class SethomeCommand extends Command {
	public SethomeCommand() {
		super("sethome", "");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < args.length; ++i) {
			builder.append(args[i]).append(" ");
		}
		String subdirectory = "player-homes";
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

		if (args.length == 0 || builder.toString().equals("home")) {
			String filePath = subdirectory + File.separator + sender.getPlayer().username + ".txt";
			File file = new File(filePath);

			if (!file.exists() && results.size() < LusiiPlugin.maxHomes) {
				LusiiPlugin.logFile(sender.getPlayer().x + "\n" + sender.getPlayer().y + "\n" + sender.getPlayer().z + "\n" + sender.getPlayer().dimension, sender.getPlayer().username);
				sender.sendMessage("§4Created home!");
				return true;
			} else if (file.exists()) {
				sender.sendMessage("§4You already have a home! Use /delhome!");
				return true;
			} else {
				sender.sendMessage("§4You've reached the max amount of homes!");
				return true;
			}
		}
		String filePath = subdirectory + File.separator + sender.getPlayer().username + builder + ".txt";
		File file = new File(filePath);
		if (!file.exists() && results.size() < LusiiPlugin.maxHomes) {
			LusiiPlugin.logFile(sender.getPlayer().x + "\n" + sender.getPlayer().y + "\n" + sender.getPlayer().z + "\n" + sender.getPlayer().dimension, sender.getPlayer().username + builder);
			sender.sendMessage("§4Created home " + builder + "!");
			return true;
		} else if (file.exists()) {
			sender.sendMessage("§4You already have a home! Use /delhome!");
			return true;
		} else {
			sender.sendMessage("§4You've reached the max amount of homes!");
			return true;
		}
	}





	public boolean opRequired(String[] args) {
		return LusiiPlugin.homeCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/sethome");
	}
}
