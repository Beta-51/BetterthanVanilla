package lusiiplugin;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import static net.minecraft.core.net.command.commands.GiveCommand.givePlayerItem;

public class DebugCommand extends Command {
	public DebugCommand() {
		super("debug", "");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		ItemStack itemStack = new ItemStack(Item.stick, 1, 1);
		itemStack.setMetadata(1);
		itemStack.setCustomName("Debug stick");
		return givePlayerItem(handler, sender, (EntityPlayer) sender, itemStack);
		}


	public boolean opRequired(String[] args) {
		return true;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}
