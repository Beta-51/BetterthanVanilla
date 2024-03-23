package lusiiplugin.commands;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.entity.projectile.EntityArrow;
import net.minecraft.core.net.command.*;
import net.minecraft.core.world.World;

import static java.lang.Math.floor;

public class SitCommand extends Command {
	public SitCommand() {
		super("sit", "");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		EntityPlayer p = sender.getPlayer();
		if (!p.onGround || p.isPassenger()) {
			sender.sendMessage("You may only use this on the ground!");
			return false;
		}

		LocationTarget location = new LocationTarget(handler, sender);
		Entity entity = createEntity(EntityArrow.class, location.getWorld());
		entity.spawnInit();
		entity.moveTo(floor(location.getX())+.5, location.getY()-0.8, floor(location.getZ())+.5, 90F, 0.0F);
		location.getWorld().entityJoinedWorld(entity);
		p.startRiding(entity);
		return true;
	}

	public boolean opRequired(String[] args) {
		return true;
	}

	public static Entity createEntity(Class<? extends Entity> entityClass, World world) {
		try {
			return entityClass.getConstructor(World.class).newInstance(world);
		} catch (Exception var3) {
			throw new CommandError("Error! Contact an Operator about this! SitCommand.java");
		}
	}


	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		if (sender.isAdmin() || sender.isConsole()) {
			sender.sendMessage("/sit");
		}
	}
}
