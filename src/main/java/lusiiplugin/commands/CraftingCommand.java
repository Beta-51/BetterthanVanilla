package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class CraftingCommand extends Command {
	public CraftingCommand() {
		super("craftingtable", "craft", "crafting", "cb");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		EntityPlayer p = sender.getPlayer();
		p.displayGUIWorkbench(p.serverPosX, p.serverPosY, p.serverPosZ);

		return true;
	}
//
	public boolean opRequired(String[] args) {
		return !LusiiPlugin.craftCommand;
	}
//
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/cb");
		sender.sendMessage("§5Opens a crafting table.");
	}
}
