package turniplabs.examplemod;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.player.gamemode.Gamemode;

import static net.minecraft.core.net.command.commands.GiveCommand.givePlayerItem;

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
		return false;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}
