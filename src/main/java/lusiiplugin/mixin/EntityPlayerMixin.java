package lusiiplugin.mixin;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import net.minecraft.core.achievement.stat.StatList;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityBobber;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.inventory.Container;
import net.minecraft.core.player.inventory.ContainerPlayer;
import net.minecraft.core.player.inventory.InventoryPlayer;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.server.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = EntityPlayer.class, remap = false)
public class EntityPlayerMixin extends EntityLiving {
	public InventoryPlayer inventory;
	@Shadow
	public Container inventorySlots;
	@Shadow
	public Container craftingInventory;
	@Shadow
	public Gamemode gamemode;
	@Shadow
	public byte field_9371_f;
	@Shadow
	public int score;
	@Shadow
	public List<String> messages;
	@Shadow
	public float field_775_e;
	@Shadow
	public float field_774_f;
	@Shadow
	public boolean isSwinging;
	@Shadow
	public int swingProgressInt;
	@Shadow
	public String username;
	@Shadow
	public int dimension;
	@Shadow
	public double field_20066_r;
	@Shadow
	public double field_20065_s;
	@Shadow
	public double field_20064_t;
	@Shadow
	public double field_20063_u;
	@Shadow
	public double field_20062_v;
	@Shadow
	public double field_20061_w;
	@Shadow
	protected boolean sleeping;
	@Shadow
	public ChunkCoordinates bedChunkCoordinates;
	@Shadow
	private int sleepTimer;
	@Shadow
	public float field_22063_x;
	@Shadow
	public float field_22062_y;
	@Shadow
	public float field_22061_z;
	@Shadow
	private ChunkCoordinates playerSpawnCoordinate;
	@Shadow
	private ChunkCoordinates lastDeathCoordinate;
	@Shadow
	private ChunkCoordinates startMinecartRidingCoordinate;
	@Shadow
	public int timeUntilPortal;
	@Shadow
	protected boolean inPortal;
	@Shadow
	public int portalID;
	@Shadow
	public float timeInPortal;
	@Shadow
	public float prevTimeInPortal;
	@Shadow
	private int damageRemainder;
	@Shadow
	public EntityBobber fishEntity;
	@Shadow
	public volatile String skinURL;
	@Shadow
	public volatile String capeURL;
	@Shadow
	public boolean slimModel;
	@Shadow
	protected float baseSpeed;
	@Shadow
	protected float baseFlySpeed;
	@Shadow
	protected boolean isDwarf;


	public EntityPlayerMixin(World world) {
		super(world);
		this.gamemode = Gamemode.survival;
		this.messages = new ArrayList();
		this.slimModel = false;
		this.baseSpeed = 0.1F;
		this.baseFlySpeed = 0.02F;
		this.isDwarf = false;
		this.field_9371_f = 0;
		this.score = 0;
		this.isSwinging = false;
		this.swingProgressInt = 0;
		this.timeUntilPortal = 20;
		this.inPortal = false;
		this.damageRemainder = 0;
		this.fishEntity = null;
		this.inventorySlots = new ContainerPlayer(this.inventory, !world.isClientSide);
		this.craftingInventory = this.inventorySlots;
		this.heightOffset = 1.62F;
		ChunkCoordinates chunkcoordinates = world.getSpawnPoint();
		this.moveTo((double)chunkcoordinates.x + 0.5, (double)(chunkcoordinates.y + 1), (double)chunkcoordinates.z + 0.5, 0.0F, 0.0F);
		this.health = 20;
		this.entityType = "humanoid";
		this.unusedRotation2 = 180.0F;
		this.fireImmuneTicks = 20;
		this.skinName = "char";
	}



	@Shadow
	protected void init() {

	}

	@Overwrite
	public double getRidingHeight() {
		return (double)(this.heightOffset) + .42;
	}

}
