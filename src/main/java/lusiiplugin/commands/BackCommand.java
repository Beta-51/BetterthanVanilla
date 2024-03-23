package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.PlayerTPInfo;
import net.fabricmc.loader.impl.game.minecraft.launchwrapper.FabricServerTweaker;
import net.minecraft.client.world.chunk.provider.ChunkProviderStatic;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.*;
import net.minecraft.core.net.command.commands.TeleportCommand;
import net.minecraft.core.net.packet.Packet10Flying;
import net.minecraft.core.net.packet.Packet11PlayerPosition;
import net.minecraft.core.net.packet.Packet34EntityTeleport;
import net.minecraft.core.net.packet.Packet9Respawn;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkLoaderLegacy;
import net.minecraft.core.world.chunk.ChunkLoaderRegion;
import net.minecraft.core.world.chunk.IChunkLoader;
import net.minecraft.core.world.chunk.provider.IChunkProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.PlayerList;
import net.minecraft.server.world.WorldServer;
import net.minecraft.server.world.chunk.provider.ChunkProviderServer;
import org.spongepowered.asm.mixin.FabricUtil;
import turniplabs.halplibe.HalpLibe;

public class BackCommand extends Command {
	public BackCommand() {
		super("back");
	}

	public boolean opRequired(String[] args) {
		return !LusiiPlugin.BackCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (sender.isConsole()) return true;

		EntityPlayer p = sender.getPlayer();

		if (p.dimension != 0) {
			sender.sendMessage("§4You may only use this in the overworld!");
			return true;
		}
		PlayerTPInfo tpInfo = LusiiPlugin.getTPInfo(p);

		if (tpInfo.canTP()) {
			if (tpInfo.atNewPos(p)) {
				Vec3d lastPos = tpInfo.getLastPos();

                tpInfo.update(p);

				p.moveTo(
					lastPos.xCoord,
					lastPos.yCoord,
					lastPos.zCoord,
					p.yRot,
					p.xRot
				);

				sender.sendMessage("§4Went back.");
			} else {
				sender.sendMessage("§4You have not moved!");
			}

        } else {
			int waitTime = tpInfo.cooldown();
			sender.sendMessage("§4Teleport available in §1" + waitTime + "§4 seconds.");
        }

        return true;
	}
}
