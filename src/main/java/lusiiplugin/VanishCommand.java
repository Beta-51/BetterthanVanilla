package lusiiplugin;

import lusiiplugin.mixin.EntityPlayerMPMixin;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.packet.Packet72UpdatePlayerProfile;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;

import java.io.File;

public class VanishCommand extends Command {
	public VanishCommand() {
		super("vanish", "v");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		EntityPlayer player = sender.getPlayer();
		sender.sendMessage("unfinished and doesn't work");
		return true;

		//if (!LusiiPlugin.vanished.contains(player.username)) {
		//	LusiiPlugin.vanishPlayer(player.username);
		//	sender.sendMessage("Vanished!");
		//} else {
		//	LusiiPlugin.unvanishPlayer(player.username);
		//	sender.sendMessage("No longer vanished!");
		//}
		//return true;
	}


	public boolean opRequired(String[] args) {
		return true;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}
