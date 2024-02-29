package lusiiplugin.mixin;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.*;
import net.minecraft.core.net.command.commands.ClearCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Arrays;

@Mixin(value = ClearCommand.class, remap = false)
public class ClearCommandMixin extends Command {
	public ClearCommandMixin() {
		super("clear", new String[0]);
	}
@Overwrite
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		EntityPlayer player = null;
		if (args.length == 0) {
			player = sender.getPlayer();
		}

		if (args.length == 1 && (sender.isAdmin() || sender.isConsole())) {
			player = handler.getPlayer(args[0]);
		}

		if (player == null) {
			throw new CommandError("Must be used by a player, or define a player name!");
		} else {
			for(int i = 0; i < player.inventory.getSizeInventory(); ++i) {
				player.inventory.setInventorySlotContents(i, (ItemStack)null);
			}

			Arrays.fill(player.inventory.armorInventory, (Object)null);
			return true;
		}
	}
	@Overwrite
	public boolean opRequired(String[] args) {
		return false;
	}

	@Overwrite
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		if (sender instanceof PlayerCommandSender) {
			sender.sendMessage("/clear");
		}
	}
}

