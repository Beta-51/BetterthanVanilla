package lusiiplugin.commands;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class PayCommand extends Command {
	public PayCommand() {
		super("pay");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (args.length < 2) {
			return false;
		}

		int payAmount = Integer.parseInt(args[1]);
		EntityPlayer receivePlayer = handler.getPlayer(args[0]);
		EntityPlayer sendPlayer = sender.getPlayer();

		if (sendPlayer.score < payAmount) {
			sender.sendMessage("§e§lInsufficient funds!");
			return true;
		}
		if (payAmount <= 0) {
			sender.sendMessage("§e§lAmount must be above 0.");
			return true;
		}
		if (receivePlayer == null) {
			sender.sendMessage("§4You must specify a player!");
			return false;
		}
		sendPlayer.score -= payAmount;
		receivePlayer.score += payAmount;
		sender.sendMessage("§1Paid §4" + receivePlayer.username + " §3" + payAmount + "§1 points.");
		receivePlayer.addChatMessage("§4" + sendPlayer.username + "§1 has paid you §3" + payAmount + "§1 points.");
		return true;
	}


	public boolean opRequired(String[] args) {
		return false;
	}



	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/pay §4<username> <amount>");
		sender.sendMessage("§5Pay another user with points");
	}
}
