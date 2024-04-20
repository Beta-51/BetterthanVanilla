package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.PlayerData;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.List;

public class HomesCommand extends Command {
	public HomesCommand() {
		super("homes");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		EntityPlayer p = sender.getPlayer();
		PlayerData.Homes homes = PlayerData.get(p).homes();
		List<String> homesList = homes.getHomesList();

		if (homesList.isEmpty()) {
			sender.sendMessage("§1You do not have any homes!");
			sender.sendMessage("§1Set a home with: §3/sethome [name]");
			return true;
		}

		String homesString = String.join(", ", homesList);
		sender.sendMessage("§1Homes: §4" + homesString);
		return true;
	}

	public boolean opRequired(String[] args) {
		return !LusiiPlugin.homeCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/homes");
		sender.sendMessage("§5List your homes");
	}
}
