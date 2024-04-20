package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.PlayerData;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class SethomeCommand extends Command {
	public SethomeCommand() {
		super("sethome", "");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		EntityPlayer p = sender.getPlayer();
		PlayerData.Homes homes = PlayerData.get(p).homes();

		String homeName;
		if (args.length > 0) {
			homeName = String.join(" ", args);
		} else {
			homeName = "home";
		}

		if (homeName.equals("bed")) {
			sender.sendMessage("§4This home is reserved for your bed.");
			return true;
		}

		if (homes.getAmount() == LusiiPlugin.maxHomes) {
			sender.sendMessage("§4You've reached the max amount of homes!");
			return true;
		}

		if (homes.setHome(p, homeName)) {
			sender.sendMessage("§4Created home: §1" + homeName);
//			homes.save();
		} else {
			sender.sendMessage("§4You already have a home named: §1" + homeName);
			sender.sendMessage("§4Use: §3/delhome " + homeName + "§4 to remove");
		}
		return true;
	}

	public boolean opRequired(String[] args) {
		return !LusiiPlugin.homeCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/sethome §4[home]");
		sender.sendMessage("§5Set a new home at your position.");
		EntityPlayer p = sender.getPlayer();
		PlayerData playerData = PlayerData.get(p);
		int homeCount = playerData.homes().getAmount();
		sender.sendMessage("§5Current homes: §4" + homeCount + "§1/" + LusiiPlugin.maxHomes);
	}
}
