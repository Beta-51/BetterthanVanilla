package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.PlayerTPInfo;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.packet.*;
import net.minecraft.server.entity.player.EntityPlayerMP;

import java.util.Random;

public class RTPCommand extends Command {
	public Random random;
	public RTPCommand() {
		super("rtp", "");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (sender.isConsole()) return true;

		EntityPlayer p = sender.getPlayer();
		PlayerTPInfo tpInfo = LusiiPlugin.getTPInfo(p);

		if (p.score < LusiiPlugin.RTPCost) {
			sender.sendMessage("§4You do not have enough points to use this command! You need §1" + (LusiiPlugin.RTPCost - p.score) + "§4 more points!");
			return true;
		}
		if (p.dimension != 0) {
			sender.sendMessage("§4You may only use this in the overworld!");
			return true;
		}
		if (!tpInfo.canTP() && !sender.isAdmin()) {
			int waitTime = tpInfo.cooldown();
			sender.sendMessage("§4Teleport available in §1" + waitTime + "§4 seconds.");
			return true;
		}
		int randX = (int) Math.floor(Math.random() *(25000 - -25000 + 1) + -25000);
		int randZ = (int) Math.floor(Math.random() *(25000 - -25000 + 1) + -25000);

		tpInfo.update(p);

		((EntityPlayerMP) p).playerNetServerHandler.sendPacket(
			new Packet9Respawn((byte) p.dimension, (byte) 0)
		);

		LusiiPlugin.teleport(p, randX, 256, randZ);

		sender.sendMessage("Teleported!");

		p.score -= LusiiPlugin.RTPCost;
		p.fireImmuneTicks = 200;
		p.onGround = false;
		p.maxHurtTime = 200;
		p.hurtTime = 200;
		p.airSupply = 1000;
		p.fallDistance = -1000;

		return true;
	}


	public boolean opRequired(String[] args) {
		return !LusiiPlugin.RTPCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}
