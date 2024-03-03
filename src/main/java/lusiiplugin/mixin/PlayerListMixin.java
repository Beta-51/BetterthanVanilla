package lusiiplugin.mixin;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.Packet20NamedEntitySpawn;
import net.minecraft.core.net.packet.Packet24MobSpawn;
import net.minecraft.core.net.packet.Packet30Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = PlayerList.class, remap = false)
public class PlayerListMixin {
	@Shadow
	public List<EntityPlayerMP> playerEntities = new ArrayList();


	private MinecraftServer mcServer;

	public List<Integer> getVanishedEntityIds() throws IOException {
		List<Integer> playerEntitiesVanishedIDs = new ArrayList<>();
		for (int i = 0; i < LusiiPlugin.readVanishedFileLines().size(); i++) {
			playerEntitiesVanishedIDs.add(mcServer.playerList.getPlayerEntity(LusiiPlugin.readVanishedFileLines().get(i)).id);
		}
		return playerEntitiesVanishedIDs;
	}


	@Overwrite
	public void sendPacketToAllPlayers(Packet packet) throws IOException {
		for(int i = 0; i < this.playerEntities.size(); ++i) {
			if (packet instanceof Packet24MobSpawn || packet instanceof Packet20NamedEntitySpawn || packet instanceof Packet30Entity) {
				if (getVanishedEntityIds().contains(((Packet24MobSpawn) packet).entityId) || getVanishedEntityIds().contains(((Packet20NamedEntitySpawn) packet).entityId) || getVanishedEntityIds().contains(((Packet30Entity) packet).entityId)) {
					return;
				}
			}


			EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playerEntities.get(i);
			entityplayermp.playerNetServerHandler.sendPacket(packet);
		}

	}

	@Overwrite
	public void sendPacketToAllPlayersInDimension(Packet packet, int i) throws IOException {
		for(int j = 0; j < this.playerEntities.size(); ++j) {
			if (packet instanceof Packet24MobSpawn || packet instanceof Packet20NamedEntitySpawn || packet instanceof Packet30Entity) {
				if (getVanishedEntityIds().contains(((Packet24MobSpawn) packet).entityId) || getVanishedEntityIds().contains(((Packet20NamedEntitySpawn) packet).entityId) || getVanishedEntityIds().contains(((Packet30Entity) packet).entityId)) {
					return;
				}
			}

			EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playerEntities.get(j);
			if (entityplayermp.dimension == i) {
				entityplayermp.playerNetServerHandler.sendPacket(packet);
			}
		}

	}
}
