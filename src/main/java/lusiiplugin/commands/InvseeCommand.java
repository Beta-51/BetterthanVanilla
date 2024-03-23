package lusiiplugin.commands;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandError;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.player.inventory.InventoryPlayer;

public class InvseeCommand extends Command {
	public InvseeCommand() {
		super("invsee", "openinv");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		EntityPlayer player;
		player = handler.getPlayer(args[0]);
		if (player == null) {
			return false;
		}


		IInventory inventory = new InventoryPlayer(player).player.inventory;
		sender.getPlayer().displayGUIChest(inventory);
		return true;
	}


	public boolean opRequired(String[] args) {
		return true;
	}



	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/invsee <username>");
	}
}
