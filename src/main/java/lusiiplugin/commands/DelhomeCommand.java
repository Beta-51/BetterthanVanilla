package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.PlayerHomes;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.packet.Packet11PlayerPosition;
import net.minecraft.server.entity.player.EntityPlayerMP;

import java.io.File;

public class DelhomeCommand extends Command {
	public DelhomeCommand() {
		super("delhome", "");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		EntityPlayer p = sender.getPlayer();
		PlayerHomes homes = LusiiPlugin.getPlayerHomes(p);

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


		if (homes.delHome(homeName)) {
			sender.sendMessage("§4Removed home: §1" + homeName);
			LusiiPlugin.savePlayerHomes();
		} else {
			sender.sendMessage("§4You don't have a home named: §1" + homeName);
			sender.sendMessage("§4Use: §3/sethome " + homeName + "§4 to create");
		}
		return true;
	}

	public boolean opRequired(String[] args) {
		return !LusiiPlugin.homeCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/delhome");
	}
}
