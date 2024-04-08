package lusiiplugin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import lusiiplugin.LusiiPlugin;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityFlag;
import net.minecraft.core.block.entity.TileEntitySign;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumSignPicture;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.ICommandListener;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.net.packet.*;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.util.helper.ChatAllowedCharacters;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.PlayerList;
import net.minecraft.server.net.handler.NetServerHandler;
import net.minecraft.server.world.WorldServer;
import org.apache.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(value = NetServerHandler.class, remap = false)
public class NetServerHandlerMixin extends NetHandler implements ICommandListener {
    @Shadow
	public static Logger logger = Logger.getLogger("Minecraft");
	@Shadow
	private MinecraftServer mcServer;
	@Shadow
	private EntityPlayerMP playerEntity;
	@Shadow
	private double lastPosX;
	@Shadow
	private double lastPosY;
	@Shadow
	private double lastPosZ;
	@Shadow
	private boolean hasMoved = true;
	@Shadow
	public void log(String string) {}
	@Shadow
	public void sendPacket(Packet packet) {}
	@Shadow
	private void handleSlashCommand(String s) {}
	@Shadow
	public String getUsername() {
		return null;
	}
	@Shadow
	public boolean isServerHandler() {
		return true;
	}

	@Inject(
		method = "handleUseEntity",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/server/entity/player/EntityPlayerMP;useCurrentItemOnEntity(Lnet/minecraft/core/entity/Entity;)V",
			shift = At.Shift.BEFORE
		)
	)
	public void ridePlayer(Packet7UseEntity packet, CallbackInfo ci, @Local(ordinal = 0) Entity targetEntity) {
		if (targetEntity instanceof EntityPlayer && this.playerEntity.getHeldItem() == null && targetEntity.vehicle != this.playerEntity && LusiiPlugin.headSit) {
			this.playerEntity.startRiding(targetEntity);
			this.playerEntity.collision = false;
			this.playerEntity.noPhysics = true;
		}
	}

	@Inject(
		method = "handleEntityAction",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/entity/player/EntityPlayerMP;setSneaking(Z)V",
			ordinal = 0,
			shift = At.Shift.BEFORE
		)
	)
	public void handlePlayerRideEject(Packet19EntityAction packet, CallbackInfo ci) {
		if (this.playerEntity.vehicle instanceof EntityPlayer) {
			this.playerEntity.noPhysics = false;
			this.playerEntity.collision = true;
		}
	}

	@Redirect(
		method = "handleFlying",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/net/handler/NetServerHandler; kickPlayer(Ljava/lang/String;) V"
		)
	)
	private void redirectFlyKick(NetServerHandler self, String msg) {
		if ("You moved too quickly :( (Hacking?)".equals(msg)) {
			self.teleportAndRotate(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.yRot, this.playerEntity.xRot);
		} else {
			self.kickPlayer(msg);
		}
	}

	@Redirect(
		method = "handleChat",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/net/handler/NetServerHandler; handleSlashCommand(Ljava/lang/String;) V"
		)
	)
	public void allowColoredCommands(NetServerHandler self, String s) {
		if (LusiiPlugin.colourChat) {
			s = s.replace("$$", "§");
		}
		if (!this.playerEntity.isOperator() && s.contains("§k")) {
			s = s.replace("§k", "$$k");
			this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username, "§e§lHey!§r You may not use obfuscated text!");
		}
		this.handleSlashCommand(s);
	}


	@Redirect(method = "handleChat",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/net/PlayerList; sendEncryptedChatToAllPlayers(Ljava/lang/String;)V"
		)
	)
	public void allowColoredChat(PlayerList instance, String i) {
		String prefix = "<" + this.playerEntity.getDisplayName() + TextFormatting.RESET + "> " + TextFormatting.WHITE;
		String msg = i.substring(prefix.length());

		if (LusiiPlugin.greenText && (msg.startsWith(">") || msg.startsWith(" >"))) {
			msg = TextFormatting.LIME + msg;
		}

		if (LusiiPlugin.colourChat) {
			msg = msg.replace("$$", "§");
		}
		if (this.playerEntity.isOperator()) {
			prefix = TextFormatting.RED + TextFormatting.BOLD.toString() + "[OP] " + TextFormatting.RESET + prefix;
		} else if (prefix.contains("§k") || msg.contains("§k")) {
			msg = msg.replace("§k", "$$k");
			prefix = prefix.replace("§k", "$$k");
			instance.sendChatMessageToPlayer(this.playerEntity.username, "§e§lHey!§r You may not use obfuscated text!");
		}

		System.out.println(prefix + msg); //do not delete!
		instance.sendEncryptedChatToAllPlayers(prefix + msg);
	}

	@Overwrite
	public void handleUpdateSign(Packet130UpdateSign packet) {
		WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
		if (worldserver.isBlockLoaded(packet.xPosition, packet.yPosition, packet.zPosition)) {
			TileEntity tileentity = worldserver.getBlockTileEntity(packet.xPosition, packet.yPosition, packet.zPosition);
			if (tileentity instanceof TileEntitySign) {
				if (!((TileEntitySign) tileentity).getIsEditable() && LusiiPlugin.signEdit) {
					TileEntitySign tileentitysign = (TileEntitySign) tileentity;
					worldserver.markBlockNeedsUpdate(packet.xPosition, packet.yPosition, packet.zPosition);

					if (this.playerEntity.distanceToSqr(tileentitysign.x + 0.5, tileentitysign.y + 0.5, tileentitysign.z + 0.5) > 50.0) {
						this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username, "Too far away!");
						System.out.println(this.playerEntity.username + " tried to edit a sign at " + tileentitysign.x + 0.5 + ", " + tileentitysign.y + 0.5 + ", " + tileentitysign.z + 0.5 + " but was too far away.");
						return;
					}

					for (int i = 0; i < 4; i++) {
						if (!Objects.equals(packet.signLines[i], "")) {
							System.out.println("Original line" + i + ": \"" + tileentitysign.signText[i] + "\" Edited to: \"" + packet.signLines[i] + "\" by " + this.playerEntity.username + " at " + tileentitysign.x + 0.5 + ", " + tileentitysign.y + 0.5 + ", " + tileentitysign.z + 0.5);
							tileentitysign.signText[i] = packet.signLines[i];
						}
					}

					if (packet.picture != tileentitysign.getPicture().getId()) {
						tileentitysign.setPicture(EnumSignPicture.values()[packet.picture]);
					}
					if (packet.color != tileentitysign.getColor().id) {
						tileentitysign.setColor(TextFormatting.FORMATTINGS[packet.color]);
					}


					worldserver.markBlockNeedsUpdate(packet.xPosition, packet.yPosition, packet.zPosition);
					return;
				}
			}

			int l;
			int i;
			for(i = 0; i < 4; ++i) {
				boolean isLineValid = true;
				if (packet.signLines[i].length() > 15) {
					isLineValid = false;
				} else {
					packet.signLines[i] = packet.signLines[i].replaceAll("§", "$");

					for(l = 0; l < packet.signLines[i].length(); ++l) {
						if (ChatAllowedCharacters.ALLOWED_CHARACTERS.indexOf(packet.signLines[i].charAt(l)) < 0) {
							isLineValid = false;
							break;
						}
					}
				}

				if (!isLineValid) {
					packet.signLines[i] = "!?";
				}
			}

			if (tileentity instanceof TileEntitySign) {
				i = packet.xPosition;
				int y = packet.yPosition;
				l = packet.zPosition;
				TileEntitySign tileEntity = (TileEntitySign)tileentity;
				System.out.println("Sign placed by "+ this.playerEntity.username+" at "+ i + ", " + y + ", " + l);
				System.out.println("Contains text: " + Arrays.toString(packet.signLines));
				for(int j1 = 0; j1 < 4; ++j1) {
					tileEntity.signText[j1] = packet.signLines[j1];
				}

				tileEntity.setColor(TextFormatting.FORMATTINGS[packet.color]);
				tileEntity.setPicture(EnumSignPicture.values()[packet.picture]);
				tileEntity.setIsEditable(true);
				tileEntity.onInventoryChanged();
				worldserver.markBlockNeedsUpdate(i, y, l);
			}
		}

	}



    @Overwrite
	public void handleUpdateFlag(Packet141UpdateFlag packet) {
		WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
		if (worldserver.isBlockLoaded(packet.x, packet.y, packet.z)) {
			TileEntity tileentity = worldserver.getBlockTileEntity(packet.x, packet.y, packet.z);
			if (this.playerEntity.distanceToSqr(packet.x + 0.5,packet.y + 0.5,packet.z + 0.5) > 55.0) {
				return;
			}

			if (tileentity instanceof TileEntityFlag) {
				TileEntityFlag tileentityflag = (TileEntityFlag)tileentity;
				if (!Objects.equals(tileentityflag.owner, "")) {
					if (!Objects.equals(packet.owner, tileentityflag.owner)) {
						//System.out.println("."+packet.owner + ". ." + tileentityflag.owner + ".");
						this.mcServer.playerList.sendChatMessageToPlayer(packet.owner,"§e§lHey!§r You may not do that!");
						return;
					}
				}

				tileentityflag.owner = packet.owner;
				if (packet.owner.length() > 24) return;
				for(int i = 0; i < packet.owner.length(); ++i) {
					char c = packet.owner.charAt(i);
					if (ChatAllowedCharacters.ALLOWED_CHARACTERS.indexOf(c) < 0) {
						return;
					}
				}
				for (int i = 0; i < 3; i++) {
					if (tileentityflag.items[i] == null) {continue;}
					if (tileentityflag.items[i].getItem() != Item.dye) {
						worldserver.setBlock(packet.x, packet.y, packet.z,0);
					} else {
						if (tileentityflag.items[i].getMetadata() > 15 || tileentityflag.items[i].getMetadata() < 0) {
							worldserver.setBlock(packet.x, packet.y, packet.z,0);
						}
					}
				}

				System.arraycopy(packet.flagColors, 0, tileentityflag.flagColors, 0, tileentityflag.flagColors.length);
				worldserver.markBlockNeedsUpdate(packet.x, packet.y, packet.z);
			}
		}
	}




	//			if (this.playerEntity.getHeldItem() != null) {
	//				if (this.playerEntity.getHeldItem().getMetadata() < 0) {
	//					if (this.playerEntity.getHeldItem().getMetadata() > 15 && this.playerEntity.getHeldItem() == Item.dye.getDefaultStack()) {
	//						this.mcServer.configManager.sendChatMessageToPlayer(playerEntity.username, "Illegal colour! meta: " + this.playerEntity.getHeldItem().getMetadata());
	//						this.playerEntity.getHeldItem().setMetadata(0);
	//						return;
	//					}
	//					this.mcServer.configManager.sendChatMessageToPlayer(playerEntity.username, "Illegal item! meta: " + this.playerEntity.getHeldItem().getMetadata());
	//					this.playerEntity.getHeldItem().setMetadata(0);
	//					return;
	//				}
	//			}



	@Overwrite
	public void handlePlace(Packet15Place packet) {
		WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
		ItemStack itemstack = this.playerEntity.inventory.getCurrentItem();


		if (this.playerEntity.getHealth() <= 0) {
			System.out.println(this.playerEntity.username + " tried to place a block while dead");
			this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username,"§e§lHey!§r You cannot do that. §5Respawn§r.");
			return;
		}


		boolean flag = worldserver.field_819_z = worldserver.dimension.id != 0 || this.mcServer.playerList.isOp(this.playerEntity.username);
		if (packet.direction == Direction.NONE) {
			if (itemstack == null) {
				return;
			}

			this.playerEntity.playerController.func_6154_a(this.playerEntity, worldserver, itemstack);
		} else {
			int x = packet.xPosition;
			int y = packet.yPosition;
			int z = packet.zPosition;
			Direction direction = packet.direction;
			double xPlaced = packet.xPlaced;
			double yPlaced = packet.yPlaced;
			ChunkCoordinates chunkcoordinates = worldserver.getSpawnPoint();
			int i1 = (int)MathHelper.abs((float)(x - chunkcoordinates.x));
			int j1 = (int)MathHelper.abs((float)(z - chunkcoordinates.z));
			if (i1 > j1) {
				j1 = i1;
			}

			if (this.hasMoved && this.playerEntity.distanceToSqr((double)x + 0.5, (double)y + 0.5, (double)z + 0.5) < 64.0 && (j1 > this.mcServer.spawnProtectionRange || flag)) {


				this.playerEntity.playerController.activateBlockOrUseItem(this.playerEntity, worldserver, itemstack, x, y, z, direction.getSide(), xPlaced, yPlaced);
			}
			if (worldserver.isBlockLoaded(packet.xPosition, packet.yPosition, packet.zPosition) && itemstack == Item.sign.getDefaultStack()) {
				TileEntity tileentity = worldserver.getBlockTileEntity(packet.xPosition, packet.yPosition, packet.zPosition);
				if (tileentity instanceof TileEntitySign) {
					TileEntitySign tileentitysign = (TileEntitySign) tileentity;
					worldserver.markBlockNeedsUpdate(packet.xPosition, packet.yPosition, packet.zPosition);
					return;
				}
			}
			this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(x, y, z, worldserver));
			x += direction.getOffsetX();
			y += direction.getOffsetY();
			z += direction.getOffsetZ();
			this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(x, y, z, worldserver));
		}

		itemstack = this.playerEntity.inventory.getCurrentItem();
		if (itemstack != null && itemstack.stackSize <= 0) {
			this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
		}

		this.playerEntity.isChangingQuantityOnly = true;
		this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.copyItemStack(this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]);
		Slot slot = this.playerEntity.craftingInventory.func_20127_a(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
		this.playerEntity.craftingInventory.updateInventory();
		this.playerEntity.isChangingQuantityOnly = false;
		if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), packet.itemStack)) {
			this.sendPacket(new Packet103SetSlot(this.playerEntity.craftingInventory.windowId, slot.id, this.playerEntity.inventory.getCurrentItem()));
		}

		worldserver.field_819_z = false;
	}
	@Overwrite
	public void handleRespawn(Packet9Respawn packet) {
		if (this.playerEntity.getHealth() <= 0) {
			int score = this.playerEntity.score;
			this.playerEntity = this.mcServer.playerList.recreatePlayerEntity(this.playerEntity, 0);
            this.playerEntity.score = (int) ((double) score * LusiiPlugin.deathCost);
		}
	}

	@Overwrite
	public void handleBlockDig(Packet14BlockDig packet) {
        int x = packet.xPosition;
        int y = packet.yPosition;
        int z = packet.zPosition;
        if (this.playerEntity.getHealth() <= 0) {
            System.out.println(this.playerEntity.username + " tried to break a block while dead");
            this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username, "§e§lHey!§r You cannot do that. §5Respawn§r.");
            return;
        }

		// Copied
        WorldServer world = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
        if (packet.status == 4) {
            this.playerEntity.dropCurrentItem(false);
        } else if (packet.status == 5) {
            this.playerEntity.dropCurrentItem(true);
        } else if (packet.status == 6) {
            this.playerEntity.pickBlock(x, y, z);
        } else {
            double playerDistX = this.playerEntity.x - ((double) x + 0.5);
            double playerDistY = this.playerEntity.y - ((double) y + 0.5);
            double playerDistZ = this.playerEntity.z - ((double) z + 0.5);
            double playerDist = playerDistX * playerDistX + playerDistY * playerDistY + playerDistZ * playerDistZ;
            if (!(playerDist > 44.0)) {
                boolean ignoreSpawnProtection = world.field_819_z = this.mcServer.spawnProtectionRange <= 0 || world.dimension.id != 0 || this.mcServer.playerList.isOp(this.playerEntity.username);
                ChunkCoordinates spawnPos = world.getSpawnPoint();
                int spawnDistX = (int) MathHelper.abs((float) (x - spawnPos.x));
                int spawnDistZ = (int) MathHelper.abs((float) (z - spawnPos.z));
                int distanceFromSpawn = Math.max(spawnDistX, spawnDistZ);
                if (distanceFromSpawn <= this.mcServer.spawnProtectionRange && !ignoreSpawnProtection) {
                    this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(x, y, z, world));
                    world.field_819_z = false;
                } else {
                    if (packet.status == 0) {
                        if (!((world.getBlock(x, y, z) == Block.signPostPlanksOak || world.getBlock(x, y, z) == Block.signWallPlanksOak) && this.playerEntity.isSneaking())) {
                            this.playerEntity.playerController.startMining(x, y, z, packet.side);
                        }
                    } else if (packet.status == 1) {
                        if (!((world.getBlock(x, y, z) == Block.signPostPlanksOak || world.getBlock(x, y, z) == Block.signWallPlanksOak) && this.playerEntity.isSneaking())) {
                            this.playerEntity.playerController.hitBlock(x, y, z, packet.side);
                        }
                    } else if (packet.status == 2 && !this.playerEntity.playerController.destroyBlock(x, y, z))
						if (world.getBlock(x, y, z) == Block.chestLegacy || world.getBlock(x, y, z) == Block.chestPlanksOakPainted || world.getBlock(x, y, z) == Block.chestPlanksOak || world.getBlock(x, y, z) == Block.chestLegacyPainted) {
							System.out.println(this.playerEntity.username + " broke chest at " + x + ", " + y + ", " + z);
							logger.info(this.playerEntity.username + " broke chest at " + x + ", " + y + ", " + z);
						}
                        if (!((world.getBlock(x, y, z) == Block.signPostPlanksOak || world.getBlock(x, y, z) == Block.signWallPlanksOak) && this.playerEntity.isSneaking())) {
                            this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(x, y, z, world));
                        } else {
                            this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username, "Replace the sign to rewrite some lines, or don't.");
                        }
                    }

                    world.field_819_z = false;
                }
            }
        }
}
