package lusiiplugin.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.player.PlayerManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(value = PlayerManager.class, remap = false)
public abstract class PlayerManagerMixin {

	@Shadow
	@Final
	private MinecraftServer server;

	@Inject(method = "addPlayer", at = @At("HEAD"))
	private void welcomeMessage(EntityPlayerMP player, CallbackInfo ci) {
		File playerData = new File("world/players/" + player.username + ".dat");

		if (!playerData.exists()) {
			server.serverCommandHandler.sendMessageToAllPlayers("§4Welcome §3" + player.username + "§4 to the server!" );
		}
	}
}
