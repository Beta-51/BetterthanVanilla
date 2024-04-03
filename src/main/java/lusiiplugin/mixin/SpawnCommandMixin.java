package lusiiplugin.mixin;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.*;
import net.minecraft.core.net.command.commands.SpawnCommand;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.server.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SpawnCommand.class, remap = false)
public class SpawnCommandMixin extends Command {
	public SpawnCommandMixin() {
		super("spawn");
	}

	@Inject(
		method = "execute",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/core/net/command/CommandSender;sendMessage(Ljava/lang/String;)V",
			shift = At.Shift.AFTER, by = 1
		)
	)
	public void trackTP(CommandHandler handler, CommandSender sender, String[] args, CallbackInfoReturnable<Boolean> cir) {

		LusiiPlugin.updateTPInfo(sender.getPlayer());
	}

	@Shadow
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		return true;
	}

	@Overwrite
	public boolean opRequired(String[] args) {
		return !LusiiPlugin.spawnCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/spawn");
	}
}
