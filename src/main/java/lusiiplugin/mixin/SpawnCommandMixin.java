package lusiiplugin.mixin;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.*;
import net.minecraft.core.net.command.commands.SpawnCommand;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.server.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = SpawnCommand.class, remap = false)
public class SpawnCommandMixin extends Command {
	public SpawnCommandMixin() {
		super("spawn", new String[0]);
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (sender instanceof PlayerCommandSender) {
			EntityPlayer player = sender.getPlayer();
			World world = handler.getWorld(player);
			ChunkCoordinates pos = world.getSpawnPoint();
			sender.sendMessage("Teleporting to spawn...");
			if (player instanceof EntityPlayerMP) {
				EntityPlayerMP playerMP = (EntityPlayerMP)player;
				if (playerMP.dimension != 0) {
					playerMP.mcServer.playerList.sendPlayerToOtherDimension(playerMP, 0);
				}

				playerMP.playerNetServerHandler.teleportAndRotate((double)pos.x + 0.5, (double)pos.y + 0.5, (double)pos.z + 0.5, 0.0F, 0.0F);
			}

			player.absMoveTo((double)pos.x + 0.5, (double)pos.y + 0.5, (double)pos.z + 0.5, 0.0F, 0.0F);
			return true;
		} else {
			throw new CommandError("Must be used by a player!");
		}
	}
	@Overwrite
	public boolean opRequired(String[] args) {
		return false;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/spawn");
	}
}
