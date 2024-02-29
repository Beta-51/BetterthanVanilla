package lusiiplugin;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.projectile.EntityArrow;
import net.minecraft.core.entity.projectile.EntityFireball;
import net.minecraft.core.entity.projectile.EntitySnowball;
import net.minecraft.core.net.command.*;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.IVehicle;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinates;

import java.util.Objects;

import static java.lang.Math.floor;

public class SitCommand extends Command {
	public SitCommand() {
		super("sit", "");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (!sender.getPlayer().onGround || sender.getPlayer().isPassenger()) {
			sender.sendMessage("You may only use this on the ground!");
			return false;
		}

		LocationTarget location = new LocationTarget(handler, sender);
		Entity entity = createEntity(EntityArrow.class, location.getWorld());
		entity.spawnInit();
		entity.moveTo(floor(location.getX())+.5, location.getY()-0.8, floor(location.getZ())+.5, 90F, 0.0F);
		location.getWorld().entityJoinedWorld(entity);
		sender.getPlayer().startRiding(entity);
		//entity.remove();
		return true;
	}

	public boolean opRequired(String[] args) {
		return true;
	}

	public static Entity createEntity(Class<? extends Entity> entityClass, World world) {
		try {
			return (Entity)entityClass.getConstructor(World.class).newInstance(world);
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
