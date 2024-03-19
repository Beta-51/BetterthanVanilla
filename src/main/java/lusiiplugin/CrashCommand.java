package lusiiplugin;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.player.inventory.InventoryBasic;
import net.minecraft.core.player.inventory.InventoryLargeChest;
import net.minecraft.core.player.inventory.InventoryPlayer;

public class CrashCommand extends Command {
	public CrashCommand() {
		super("crash");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		int slotValue = 2147483647;
		EntityPlayer player;
		player = handler.getPlayer(args[0]);
		if (player == null) {
			return false;
		}
		if (args[1] != null) {
			slotValue = Integer.parseInt(args[1]);
		}
		IInventory inventory = new InventoryBasic("Googative",slotValue);
		player.displayGUIChest(inventory);
		return true;
	}
//
	public boolean opRequired(String[] args) {
		return true;
	}
//
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}
