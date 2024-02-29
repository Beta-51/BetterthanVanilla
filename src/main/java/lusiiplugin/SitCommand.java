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
		EntityFireball entityfireball = new EntityFireball(sender.getPlayer().world, sender.getPlayer(), floor(location.getX())+.5, location.getY()+4, floor(location.getZ())+.5);
		entityfireball.xd = 0;
		entityfireball.yd = 0;
		entityfireball.zd = 0;
		entityfireball.xo = 0;
		entityfireball.yo = 0;
		entityfireball.zo = 0;
		location.getWorld().entityJoinedWorld(entityfireball);
		sender.getPlayer().startRiding(entityfireball);

		//floor(sender.getPlayer().x+.5),sender.getPlayer().y+0.1625,floor(sender.getPlayer().z+.5)
		//Entity entity = createEntity(EntityFireball.class, location.getWorld());
		//entity.spawnInit();
		//entity.moveTo(floor(location.getX())+.5, location.getY()+10, floor(location.getZ())+.5, 90F, 0.0F);
		//entity.yd = 2;
		//((EntitySnowball)entity).collision = false;
		//((EntitySnowball)entity).bbHeight = 0;
		//((EntitySnowball)entity).bbWidth = 0;
		//((EntitySnowball)entity).y = sender.getPlayer().y;
		//entity.noPhysics = true;
		//location.getWorld().entityJoinedWorld(entity);
		//sender.getPlayer().startRiding(entity);
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
