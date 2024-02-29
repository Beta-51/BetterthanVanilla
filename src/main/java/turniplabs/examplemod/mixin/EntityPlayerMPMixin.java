package turniplabs.examplemod.mixin;


import net.minecraft.core.crafting.ICrafting;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet5PlayerInventory;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.inventory.Container;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.handler.NetServerHandler;
import net.minecraft.server.world.PlayerController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Mixin(value = EntityPlayerMP.class, remap = false)
public class EntityPlayerMPMixin extends EntityPlayer implements ICrafting {

	public NetServerHandler playerNetServerHandler;
	public MinecraftServer mcServer;
	public PlayerController playerController;
	public double field_9155_d;
	public double field_9154_e;
	public List loadedChunks = new LinkedList();
	public Set field_420_ah = new HashSet();
	private int lastHealth = -99999999;
	private int lastScore = -99999999;
	private int ticksOfInvuln = 60;
	private ItemStack[] playerInventory = new ItemStack[]{null, null, null, null, null};
	private int currentWindowId = 0;
	public boolean isChangingQuantityOnly;


	public EntityPlayerMPMixin(MinecraftServer minecraftserver, World world, String s, PlayerController iteminworldmanager) {
		super(world);
		iteminworldmanager.player = this;
		this.playerController = iteminworldmanager;
		ChunkCoordinates chunkcoordinates = world.getSpawnPoint();
		int i = chunkcoordinates.x;
		int j = chunkcoordinates.z;
		int k = chunkcoordinates.y;
		if (!world.worldType.hasCeiling()) {
			i += this.random.nextInt(20) - 10;
			k = world.findTopSolidBlock(i, j);
			j += this.random.nextInt(20) - 10;
		}

		this.moveTo((double)i + 0.5, (double)k, (double)j + 0.5, 0.0F, 0.0F);
		this.mcServer = minecraftserver;
		this.footSize = 0.0F;
		this.username = s;
		this.heightOffset = 0.0F;
		this.gamemode = Gamemode.creative;
	}
	@Overwrite
	public void tick() {
		this.playerController.tick();
		--this.ticksOfInvuln;
		this.craftingInventory.updateInventory();

		for (int i = 0; i < 35; i++) {
			if (this.craftingInventory.getSlot(i) == null) {
				continue;
			}
			if (this.craftingInventory.getSlot(i).getStack() == null) {
				continue;
			}
				if (this.craftingInventory.getSlot(i).getStack() == Item.dye.getDefaultStack() && this.craftingInventory.getSlot(i).getStack().getMetadata() > 15) {
					System.out.println("Illegal items found. Owner: " + this.username + " Item meta: "+this.craftingInventory.getSlot(i).getStack().getMetadata());
					this.craftingInventory.getSlot(i).putStack(null);
				}
				if (this.craftingInventory.getSlot(i).getStack().getMetadata() < 0) {
					System.out.println("Illegal items found. Owner: " + this.username + " Item meta: "+this.craftingInventory.getSlot(i).getStack().getMetadata());
					this.craftingInventory.getSlot(i).putStack(null);
				}
			}


		for(int i = 0; i < 5; ++i) {
			ItemStack itemstack = this.getEquipmentInSlot(i);
			if (itemstack != this.playerInventory[i]) {
				this.mcServer.getEntityTracker(this.dimension).sendPacketToTrackedPlayers(this, new Packet5PlayerInventory(this.id, i, itemstack));
				this.playerInventory[i] = itemstack;
			}
		}

	}


	@Shadow
	public void func_6420_o() {

	}

	@Shadow
	public ItemStack getEquipmentInSlot(int i) {
		return i == 0 ? this.inventory.getCurrentItem() : this.inventory.armorInventory[i - 1];
	}



	@Shadow
	public void updateCraftingInventory(Container container, List list) {

	}


	@Shadow
	public void updateInventorySlot(Container container, int i, ItemStack itemStack) {

	}


	@Shadow
	public void updateCraftingInventoryInfo(Container container, int i, int j) {

	}
}
