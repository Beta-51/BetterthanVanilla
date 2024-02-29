package lusiiplugin.mixin;

import net.minecraft.core.HitResult;
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
import net.minecraft.core.util.helper.AES;
import net.minecraft.core.util.helper.ChatAllowedCharacters;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.ChatEmotes;
import net.minecraft.server.net.handler.NetServerHandler;
import net.minecraft.server.world.WorldServer;
import org.apache.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.security.Key;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mixin(value = NetServerHandler.class, remap = false)
public class NetServerHandlerMixin extends NetHandler implements ICommandListener {

    @Shadow
	public static Logger logger = Logger.getLogger("Minecraft");
	@Shadow
	private MinecraftServer mcServer;
	@Shadow
	private EntityPlayerMP playerEntity;
	@Shadow
	private boolean hasMoved;
	@Shadow
	private Map field_10_k = new HashMap();

	@Shadow
	public void log(String string) {
	}
	@Shadow
	public void sendPacket(Packet packet) {
	}
	@Shadow
	public void kickPlayer(String s) {
	}
	@Overwrite
	public void handleUseEntity(Packet7UseEntity packet) {
		WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
		Entity targetEntity = worldserver.func_6158_a(packet.targetEntity);
		if (targetEntity != null && this.playerEntity.distanceToSqr(targetEntity) < 36.0) {
			boolean canAttack = this.playerEntity.canEntityBeSeen(targetEntity);
			if (!canAttack) {
				float f1 = MathHelper.cos(-this.playerEntity.yRot * 0.01745329F - 3.141593F);
				float f2 = MathHelper.sin(-this.playerEntity.yRot * 0.01745329F - 3.141593F);
				float f3 = -MathHelper.cos(-this.playerEntity.xRot * 0.01745329F);
				float f4 = MathHelper.sin(-this.playerEntity.xRot * 0.01745329F);
				Vec3d viewVector = Vec3d.createVector((double)(f2 * f3), (double)f4, (double)(f1 * f3));
				viewVector.xCoord *= 8.0;
				viewVector.yCoord *= 8.0;
				viewVector.zCoord *= 8.0;
				viewVector.xCoord += this.playerEntity.x;
				viewVector.yCoord += this.playerEntity.y;
				viewVector.zCoord += this.playerEntity.z;
				Vec3d playerViewPos = Vec3d.createVector(this.playerEntity.x, this.playerEntity.y + (double)this.playerEntity.getHeadHeight(), this.playerEntity.z);
				HitResult movingObjectPosition = targetEntity.bb.func_1169_a(playerViewPos, viewVector);
				canAttack = movingObjectPosition != null && worldserver.checkBlockCollisionBetweenPoints(playerViewPos, movingObjectPosition.location) == null;
			}

			if (canAttack) {
				if (packet.isLeftClick == 0) {
					if (targetEntity instanceof EntityPlayer && this.playerEntity.inventory.getCurrentItem() == null && targetEntity.vehicle != this.playerEntity) {
						this.playerEntity.startRiding(targetEntity);
						this.playerEntity.collision = false;
						this.playerEntity.noPhysics = true;
					} else {
						this.playerEntity.useCurrentItemOnEntity(targetEntity);
					}
				} else if (packet.isLeftClick == 1) {
					this.playerEntity.attackTargetEntityWithCurrentItem(targetEntity);
				}
			}
		}

	}









	@Overwrite
	public void handleUpdateSign(Packet130UpdateSign packet) {
		WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
		if (worldserver.isBlockLoaded(packet.xPosition, packet.yPosition, packet.zPosition)) {
			TileEntity tileentity = worldserver.getBlockTileEntity(packet.xPosition, packet.yPosition, packet.zPosition);
			if (tileentity instanceof TileEntitySign && !((TileEntitySign) tileentity).getIsEditable()) {
				TileEntitySign tileentitysign = (TileEntitySign)tileentity;
				worldserver.markBlockNeedsUpdate(packet.xPosition, packet.yPosition, packet.zPosition);

				if (this.playerEntity.distanceToSqr(tileentitysign.x + 0.5,tileentitysign.y + 0.5,tileentitysign.z + 0.5) > 50.0) {
					this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username,"Too far away!");
					System.out.println(this.playerEntity.username + " tried to edit a sign at " + tileentitysign.x + 0.5 + ", " + tileentitysign.y + 0.5 + ", " + tileentitysign.z + 0.5 + " but was too far away.");
					return;
				}

				for (int i = 0; i < 4; i++) {
					if (!Objects.equals(packet.signLines[i], "")) {
						System.out.println("Original line"+i+": \""+tileentitysign.signText[i]+"\" Edited to: \""+packet.signLines[i]+"\" by " + this.playerEntity.username + " at " + tileentitysign.x + 0.5 + ", " + tileentitysign.y + 0.5 + ", " + tileentitysign.z + 0.5);
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
				System.out.println("Sign placed by "+ this.playerEntity.username+" at "+ tileEntity.x + 0.5 + ", " + tileEntity.y + 0.5 + ", " + tileEntity.z + 0.5);
				System.out.println("Contains text: " + Arrays.toString(packet.signLines));
				for(int j1 = 0; j1 < 4; ++j1) {
					tileEntity.signText[j1] = packet.signLines[j1];
				}

				tileEntity.setColor(TextFormatting.FORMATTINGS[packet.color]);
				tileEntity.setPicture(EnumSignPicture.values()[packet.picture]);
				tileEntity.setIsEditable(false);
				tileEntity.onInventoryChanged();
				worldserver.markBlockNeedsUpdate(i, y, l);
			}
		}

	}

	@Overwrite
	public void handleEntityAction(Packet19EntityAction packet) {
		if (packet.state == 1) {
			if (this.playerEntity.vehicle instanceof EntityPlayer) {
				this.playerEntity.noPhysics = false;
				this.playerEntity.collision = true;
			}
			this.playerEntity.setSneaking(true);
		} else if (packet.state == 2) {
			this.playerEntity.setSneaking(false);
		} else if (packet.state == 3) {
			this.playerEntity.wakeUpPlayer(false, true);
			this.hasMoved = false;
		}

	}



	@Overwrite
	public void handleChat(Packet3Chat packet) {
		String s;
		if (packet.encrypted) {
			try {
				s = AES.decrypt(packet.message, (Key)AES.keyChain.get(this.playerEntity.username));
			} catch (Exception var5) {
				throw new RuntimeException(var5);
			}
		} else {
			s = packet.message;
		}
		if (s.length() > 256) {
			this.kickPlayer("Chat message too long");
		} else {
			s = s.trim();
			s = s.replace('§', '$');
			for(int i = 0; i < s.length(); ++i) {
				char c = s.charAt(i);
				if (ChatAllowedCharacters.ALLOWED_CHARACTERS.indexOf(c) < 0) {
					this.sendPacket(new Packet3Chat(String.valueOf(TextFormatting.GRAY) + TextFormatting.ITALIC + "[SERVER] Illegal characters in chat message."));
					return;
				}
			}
			if (s.startsWith("/")) {
				this.handleSlashCommand(s);
			} else {
				s = ChatEmotes.process(s);
				if (s.startsWith(">")) {
					if (this.playerEntity.isOperator()) {
						s = TextFormatting.RED + TextFormatting.BOLD.toString() + "[OP] " + TextFormatting.RESET + "<" + this.playerEntity.getDisplayName() + TextFormatting.RESET + "> " + TextFormatting.LIME + s;
					} else {
						s = "<" + this.playerEntity.getDisplayName() + TextFormatting.RESET + "> " + TextFormatting.LIME + s;
					}
				} else if (this.playerEntity.isOperator()) {
					s = TextFormatting.RED + TextFormatting.BOLD.toString() + "[OP] " + TextFormatting.RESET + "<" + this.playerEntity.getDisplayName() + TextFormatting.RESET + "> " + s;
				} else {
					s = "<" + this.playerEntity.getDisplayName() + TextFormatting.RESET + "> " + TextFormatting.WHITE + s;
				}
				s = s.replace("$$","§");
				if (!this.playerEntity.isOperator()) {
					s = s.replace("§k", "$$k");
					this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username,"§4§lHey!§r You may not use obfuscated text!");
				}

				System.out.println(s);
				logger.info(s);
				this.mcServer.playerList.sendEncryptedChatToAllPlayers(s);
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



	public Integer[] debugStick(Block block) {
		if (block.equals(Block.algae)) {
			return new Integer[]{0};
		} else if (block.equals(Block.bed)) {
			return new Integer[]{0, 1, 2, 3};
		}
		return new Integer[0];
	}

	@Overwrite
	public void handlePlace(Packet15Place packet) {
		WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
		ItemStack itemstack = this.playerEntity.inventory.getCurrentItem();

		if (this.playerEntity.health <= 0) {
			System.out.println(this.playerEntity.username + " tried to place a block while dead");
			this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username,"§e§lHey!§r You cannot do that. §5Respawn§r.");
			return;
		}

		if (itemstack == Item.stick.getDefaultStack()) {
			if (itemstack.getMetadata() == 1) {
				Block block = worldserver.getBlock(packet.xPosition, packet.yPosition, packet.zPosition);
				Integer blockMeta = worldserver.getBlockMetadata(packet.xPosition, packet.yPosition, packet.zPosition);
				Integer length = debugStick(worldserver.getBlock(packet.xPosition, packet.yPosition, packet.zPosition)).length;
				for (int i = 0; i < debugStick(worldserver.getBlock(packet.xPosition, packet.yPosition, packet.zPosition)).length; i++) { // scroll through numbers
					if (Objects.equals(debugStick(worldserver.getBlock(packet.xPosition, packet.yPosition, packet.zPosition))[i], blockMeta)) { // if they match do next step
						if (i < debugStick(worldserver.getBlock(packet.xPosition, packet.yPosition, packet.zPosition)).length) {
							debugStick(worldserver.getBlock(packet.xPosition, packet.yPosition, packet.zPosition));
						}


					}
				}
			}
		}

		if (itemstack != null) {
			if (itemstack == Item.dye.getDefaultStack()) {
				if (itemstack.getMetadata() > 15 || itemstack.getMetadata() < 0) {
					this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
					this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username,"§eIllegal item!");
					return;
				}
			}
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
	public void handleBlockDig(Packet14BlockDig packet) {
        int x = packet.xPosition;
        int y = packet.yPosition;
        int z = packet.zPosition;
        if (this.playerEntity.health <= 0) {
            System.out.println(this.playerEntity.username + " tried to break a block while dead");
            this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username, "§e§lHey!§r You cannot do that. §5Respawn§r.");
            return;
        }


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
                        } else {
                            //this.mcServer.configManager.sendChatMessageToPlayer(this.playerEntity.username,"Replace a sign to rewrite some lines, or don't.");
                        }
                    } else if (packet.status == 1) {
                        if (!((world.getBlock(x, y, z) == Block.signPostPlanksOak || world.getBlock(x, y, z) == Block.signWallPlanksOak) && this.playerEntity.isSneaking())) {
                            this.playerEntity.playerController.hitBlock(x, y, z, packet.side);
                        } else {
                            //this.mcServer.configManager.sendChatMessageToPlayer(this.playerEntity.username,"Replace a sign to rewrite the sign, or don't.");
                        }
                    } else if (packet.status == 2 && !this.playerEntity.playerController.destroyBlock(x, y, z)) {
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




	@Shadow
	private void handleSlashCommand(String s) {}

	@Shadow
	public String getUsername() {
		return null;
	}
	@Shadow
	public boolean isServerHandler() {
		return false;
	}
}
