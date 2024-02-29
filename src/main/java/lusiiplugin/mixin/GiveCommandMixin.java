package lusiiplugin.mixin;

import lusiiplugin.LusiiPlugin;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.*;
import net.minecraft.core.net.command.commands.GiveCommand;
import net.minecraft.server.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = GiveCommand.class, remap = false)
public class GiveCommandMixin extends Command {
	public GiveCommandMixin() {
		super("give", new String[]{"item", "i"});
	}

	@Overwrite
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		int count = 1;
		int meta = 0;
		int arg = 0;
		if (args.length == 0) {
			return false;
		} else {
			EntityPlayer player;
			if (handler.playerExists(args[0]) && (sender.isAdmin() || sender.isConsole())) {
				player = handler.getPlayer(args[0]);
				++arg;
			} else {
				player = sender.getPlayer();
			}

			if (player == null) {
				throw new CommandError("no player");
			} else {
				String[] ids = args[arg].split(":");
				Item item = getItem(ids[0]);
				if (item == null) {
					throw new CommandError("Item not found: \"" + ids[0] + "\"");
				} else {
					if (ids.length > 1) {
						meta = Integer.parseInt(ids[1]);
					}

					++arg;
					if (args.length > arg) {
						count = Integer.parseInt(args[arg]);
					}

					ItemStack itemStack = new ItemStack(item, count, meta);
					return givePlayerItem(handler, sender, player, itemStack);
				}
			}
		}
	}
	@Overwrite
	public boolean opRequired(String[] args) {
		return LusiiPlugin.giveCommand;
	}

	@Overwrite
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		if (sender instanceof PlayerCommandSender) {
			sender.sendMessage("/give <ID:Meta> <Count>");
		}

	}
	@Shadow
	private static Item getItem(String id) {
		try {
			int i = Integer.parseInt(id);
			return getItem(i);
		} catch (Exception var8) {
			String id2 = "tile." + id;
			String id3 = "item." + id;
			Item[] var4 = Item.itemsList;
			int var5 = var4.length;

			for(int var6 = 0; var6 < var5; ++var6) {
				Item item = var4[var6];
				if (item != null) {
					if (item.getKey().equalsIgnoreCase(id)) {
						return item;
					}

					if (item.getKey().equalsIgnoreCase(id2)) {
						return item;
					}

					if (item.getKey().equalsIgnoreCase(id3)) {
						return item;
					}
				}
			}

			return null;
		}
	}
	@Shadow
	private static Item getItem(int id) {
		return Item.itemsList[id];
	}
	@Shadow
	private static boolean givePlayerItem(CommandHandler handler, CommandSender sender, EntityPlayer player, ItemStack item) {
		if (player instanceof EntityPlayerSP) {
			player.inventory.insertItem(item, true);
			if (item.stackSize > 0) {
				player.dropPlayerItem(item);
			}

			return true;
		} else if (player instanceof EntityPlayerMP) {
			handler.sendCommandFeedback(sender, "Gave " + item.stackSize + " of " + item.getItemName() + " to " + player.getDisplayName());
			EntityPlayerMP playerMP = (EntityPlayerMP)player;
			playerMP.dropPlayerItem(item);
			return true;
		} else {
			return false;
		}
	}
}
