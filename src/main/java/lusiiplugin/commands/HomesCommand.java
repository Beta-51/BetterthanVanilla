package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.HomePosition;
import lusiiplugin.utils.PlayerHomes;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HomesCommand extends Command {
	public HomesCommand() {
		super("homes");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		EntityPlayer p = sender.getPlayer();
		PlayerHomes homes = LusiiPlugin.getPlayerHomes(p);
		Optional<ArrayList<String>> homesList = homes.getHomesList();

		if (!homesList.isPresent()) {
			sender.sendMessage("§1You do not have any homes!");
			sender.sendMessage("§1Set a home with: §3/sethome [name]");
			return true;
		}

		ArrayList<String> list = homesList.get();
		String homesString = String.join("§1, §4", list);
		sender.sendMessage("§1Homes: §4" + homesString);
		return true;
	}

	public boolean opRequired(String[] args) {
		return !LusiiPlugin.homeCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/homes");
	}
}
