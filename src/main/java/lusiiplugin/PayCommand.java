package lusiiplugin;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.player.inventory.InventoryPlayer;

public class PayCommand extends Command {
	public PayCommand() {
		super("pay");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (args.length < 2) {
			return false;
		}
		if (sender.getPlayer().score < Integer.parseInt(args[1])) {
			sender.sendMessage("§e§lInsufficient funds!");
			return true;
		}
		if (Integer.parseInt(args[1]) <= 0) {
			sender.sendMessage("§e§lInteger must be above 0.");
			return true;
		}
		int payment = Integer.parseInt(args[1]);
		EntityPlayer player;
		player = handler.getPlayer(args[0]);
		if (player == null) {
			return false;
		}
		sender.getPlayer().score -= payment;
		player.score += payment;
		sender.sendMessage("Paid " + player.username + " " + payment + " points.");
		return true;
	}


	public boolean opRequired(String[] args) {
		return false;
	}



	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/pay <username> <amount>");
	}
}
