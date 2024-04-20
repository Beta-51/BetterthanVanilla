package lusiiplugin.mixin;

import net.minecraft.core.net.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.net.handler.NetLoginHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(value = NetLoginHandler.class, remap = false)
public abstract class NetLoginHandlerMixin {
	@Shadow
	private MinecraftServer mcServer;

	@Inject(method = "doLogin", at = @At("TAIL"))
	private void welcomeMessage(Packet1Login packet1login, CallbackInfo ci) {
		String username = packet1login.username;
		File playerData = new File("world/players/" + username + ".dat");

		if (!playerData.exists()) {
			this.mcServer.serverCommandHandler.sendMessageToAllPlayers("§4Welcome §3" + username + "§4 to the server!" );
		}
	}
}
