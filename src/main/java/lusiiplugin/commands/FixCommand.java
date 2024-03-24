package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.player.gamemode.Gamemode;

public class FixCommand extends Command {
	public FixCommand() {
		super("fix", "");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		sender.getPlayer().gamemode = Gamemode.survival;
		sender.getPlayer().gamemode = Gamemode.creative;
		sender.sendMessage("Fixed item spawning in creative!");
		return true;
	}


	public boolean opRequired(String[] args) {
		return !LusiiPlugin.gamemodeAll;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}
