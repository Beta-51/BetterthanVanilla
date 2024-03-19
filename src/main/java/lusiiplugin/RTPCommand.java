package lusiiplugin;

import net.minecraft.client.world.chunk.provider.ChunkProviderStatic;
import net.minecraft.core.block.Block;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.command.ServerCommandHandler;
import net.minecraft.core.net.packet.*;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.chunk.IChunkLoader;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.player.PlayerManager;

import java.util.Random;

import static lusiiplugin.HomeCommand.teleport;

public class RTPCommand extends Command {
	public Random random;
	public RTPCommand() {
		super("rtp", "");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (!sender.isConsole()){
			if (sender.getPlayer().score < LusiiPlugin.RTPCost) {
				sender.sendMessage("You do not have enough points to use this command! You need " + (LusiiPlugin.RTPCost - sender.getPlayer().score) + " more points!");
				return true;
			}
			if (sender.getPlayer().dimension != 0) {
				sender.sendMessage("You may only use this in the overworld!");
				return true;
			}
		}
		World world;
		world = sender.getPlayer().world;
		int randX = (int) Math.floor(Math.random() *(25000 - -25000 + 1) + -25000);
		int randZ = (int) Math.floor(Math.random() *(25000 - -25000 + 1) + -25000);
		ServerCommandHandler serverCommandHandler = (ServerCommandHandler)handler;
		//serverCommandHandler.minecraftServer.playerList.sendPlayerToOtherDimension((EntityPlayerMP) sender.getPlayer(), 1);
		((ServerCommandHandler) handler).minecraftServer.playerList.sendPacketToPlayer(sender.getPlayer().username,new Packet9Respawn((byte) sender.getPlayer().dimension, (byte) 0));
		((EntityPlayerMP) sender.getPlayer()).playerNetServerHandler.teleportAndRotate(randX, 256, randZ, sender.getPlayer().yRot, sender.getPlayer().xRot);
		sender.sendMessage("Teleported!");
		sender.getPlayer().score -= LusiiPlugin.RTPCost;
		((EntityLiving) sender.getPlayer()).fireImmuneTicks = 200;
		((EntityLiving) sender.getPlayer()).onGround = false;
		sender.getPlayer().maxHurtTime = 200;
		sender.getPlayer().hurtTime = 200;
		sender.getPlayer().airSupply = 1000;
		((EntityLiving) sender.getPlayer()).fallDistance = -1000;

		return true;
	}


	public boolean opRequired(String[] args) {
		return false;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}
