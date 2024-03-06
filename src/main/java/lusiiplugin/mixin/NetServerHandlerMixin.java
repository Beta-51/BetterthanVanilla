package lusiiplugin.mixin;

import lusiiplugin.LusiiPlugin;
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
import net.minecraft.core.net.NetworkManager;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.net.packet.*;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.util.helper.AES;
import net.minecraft.core.util.helper.ChatAllowedCharacters;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
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

import java.io.IOException;
import java.security.Key;
import java.util.*;

@Mixin(value = NetServerHandler.class, remap = false)
public class NetServerHandlerMixin extends NetHandler implements ICommandListener {

    @Shadow
	public static Logger logger = Logger.getLogger("Minecraft");
	public NetworkManager netManager;
	public boolean connectionClosed = false;
	private MinecraftServer mcServer;
	private EntityPlayerMP playerEntity;
	private int field_15_f;
	private int field_22004_g;
	private int playerInAirTime;
	private boolean field_22003_h;
	private double lastPosX;
	private double lastPosY;
	private double lastPosZ;
	private boolean hasMoved = true;
	private Map field_10_k = new HashMap();

	@Shadow
	public void log(String string) {
	}

	public List<Integer> getVanishedEntityIds() throws IOException {
		List<Integer> playerEntitiesVanishedIDs = new ArrayList<>();
		for (int i = 0; i < LusiiPlugin.readVanishedFileLines().size(); i++) {
			playerEntitiesVanishedIDs.add(mcServer.playerList.getPlayerEntity(LusiiPlugin.readVanishedFileLines().get(i)).id);
		}
		return playerEntitiesVanishedIDs;
	}

	@Overwrite
	public void sendPacket(Packet packet) {


		this.netManager.addToSendQueue(packet);
		this.field_22004_g = this.field_15_f;
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
					if (targetEntity instanceof EntityPlayer && this.playerEntity.inventory.getCurrentItem() == null && targetEntity.vehicle != this.playerEntity && LusiiPlugin.headSit) {
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



	public void handleFlying(Packet10Flying packet) {
		WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
		this.field_22003_h = true;
		double d1;
		if (!this.hasMoved) {
			d1 = packet.yPosition - this.lastPosY;
			if (packet.xPosition == this.lastPosX && d1 * d1 < 0.01 && packet.zPosition == this.lastPosZ) {
				this.hasMoved = true;
			}
		}

		if (this.hasMoved) {
			double newPosX;
			double newPosY;
			double newPosZ;
			double dx;
			if (this.playerEntity.vehicle != null) {
				float f = this.playerEntity.yRot;
				float f1 = this.playerEntity.xRot;
				this.playerEntity.vehicle.positionRider();
				newPosX = this.playerEntity.x;
				newPosY = this.playerEntity.y;
				newPosZ = this.playerEntity.z;
				double d8 = 0.0;
				dx = 0.0;
				if (packet.rotating) {
					f = packet.yaw;
					f1 = packet.pitch;
				}

				if (packet.moving && packet.yPosition == -999.0 && packet.stance == -999.0) {
					d8 = packet.xPosition;
					dx = packet.zPosition;
				}

				this.playerEntity.onGround = packet.onGround;
				this.playerEntity.onUpdateEntity();
				this.playerEntity.move(d8, 0.0, dx);
				this.playerEntity.absMoveTo(newPosX, newPosY, newPosZ, f, f1);
				this.playerEntity.xd = d8;
				this.playerEntity.zd = dx;
				if (this.playerEntity.vehicle != null && this.playerEntity.vehicle instanceof Entity) {
					worldserver.func_12017_b((Entity)this.playerEntity.vehicle, true);
				}

				if (this.playerEntity.vehicle != null) {
					this.playerEntity.vehicle.positionRider();
				}

				this.mcServer.playerList.func_613_b(this.playerEntity);
				this.lastPosX = this.playerEntity.x;
				this.lastPosY = this.playerEntity.y;
				this.lastPosZ = this.playerEntity.z;
				worldserver.updateEntity(this.playerEntity);
				return;
			}

			if (this.playerEntity.isPlayerSleeping()) {
				this.playerEntity.onUpdateEntity();
				this.playerEntity.absMoveTo(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.yRot, this.playerEntity.xRot);
				worldserver.updateEntity(this.playerEntity);
				return;
			}

			d1 = this.playerEntity.y;
			this.lastPosX = this.playerEntity.x;
			this.lastPosY = this.playerEntity.y;
			this.lastPosZ = this.playerEntity.z;
			newPosX = this.playerEntity.x;
			newPosY = this.playerEntity.y;
			newPosZ = this.playerEntity.z;
			float f2 = this.playerEntity.yRot;
			float f3 = this.playerEntity.xRot;
			if (packet.moving && packet.yPosition == -999.0 && packet.stance == -999.0) {
				packet.moving = false;
			}

			if (packet.moving && packet.yPosition == -999.0 && packet.stance == -999.0) {
				packet.moving = false;
			}

			if (packet.moving) {
				newPosX = packet.xPosition;
				newPosY = packet.yPosition;
				newPosZ = packet.zPosition;
				dx = packet.stance - packet.yPosition;
				if (!this.playerEntity.isPlayerSleeping() && (dx > 1.65 || dx < 0.1)) {
					this.kickPlayer("Illegal stance");
					logger.warn(this.playerEntity.username + " had an illegal stance: " + dx);
					return;
				}

				if (Math.abs(packet.xPosition) > 3.2E7 || Math.abs(packet.zPosition) > 3.2E7) {
					this.playerEntity.resetPos();
					this.kickPlayer("Illegal position");
					return;
				}
			}

			if (packet.rotating) {
				f2 = packet.yaw;
				f3 = packet.pitch;
			}

			this.playerEntity.onUpdateEntity();
			this.playerEntity.ySlideOffset = 0.0F;
			this.playerEntity.absMoveTo(this.lastPosX, this.lastPosY, this.lastPosZ, f2, f3);
			if (!this.hasMoved) {
				return;
			}

			dx = newPosX - this.playerEntity.x;
			double dy = newPosY - this.playerEntity.y;
			double dz = newPosZ - this.playerEntity.z;
			double d14 = dx * dx + dy * dy + dz * dz;
			if (d14 > 100.0) {
				logger.warn(this.playerEntity.username + " moved too quickly!");
				this.teleportAndRotate(this.lastPosX, this.lastPosY, this.lastPosZ, f2, f3);
				//this.kickPlayer("You moved too quickly :( (Hacking?)");
				return;
			}

			float f4 = 0.0625F;
			boolean flag = worldserver.getCubes(this.playerEntity, this.playerEntity.bb.copy().getInsetBoundingBox((double)f4, (double)f4, (double)f4)).size() == 0;
			this.playerEntity.move(dx, dy, dz);
			dx = newPosX - this.playerEntity.x;
			dy = newPosY - this.playerEntity.y;
			if (dy > -0.5 || dy < 0.5) {
				dy = 0.0;
			}

			dz = newPosZ - this.playerEntity.z;
			d14 = dx * dx + dy * dy + dz * dz;
			boolean flag1 = false;
			if (!this.playerEntity.getGamemode().canPlayerFly() && d14 > 0.0625 && !this.playerEntity.isPlayerSleeping()) {
				flag1 = true;
				logger.warn(this.playerEntity.username + " moved wrongly!");
				System.out.println("Got position " + newPosX + ", " + newPosY + ", " + newPosZ);
				System.out.println("Expected " + this.playerEntity.x + ", " + this.playerEntity.y + ", " + this.playerEntity.z);
			}

			this.playerEntity.absMoveTo(newPosX, newPosY, newPosZ, f2, f3);
			boolean flag2 = worldserver.getCubes(this.playerEntity, this.playerEntity.bb.copy().getInsetBoundingBox((double)f4, (double)f4, (double)f4)).size() == 0;
			if (!this.playerEntity.getGamemode().canPlayerFly() && flag && (flag1 || !flag2) && !this.playerEntity.isPlayerSleeping()) {
				this.teleportAndRotate(this.lastPosX, this.lastPosY, this.lastPosZ, f2, f3);
				return;
			}

			AABB axisalignedbb = this.playerEntity.bb.copy().expand((double)f4, (double)f4, (double)f4).addCoord(0.0, -0.55, 0.0);
			if (!this.playerEntity.getGamemode().canPlayerFly() && !this.mcServer.allowFlight && !worldserver.getIsAnySolidGround(axisalignedbb)) {
				if (dy > -0.03125) {
					++this.playerInAirTime;
					if (this.playerInAirTime > 80) {
						logger.warn(this.playerEntity.username + " was kicked for floating too long!");
						this.kickPlayer("Flying is not enabled on this server");
						return;
					}
				}
			} else {
				this.playerInAirTime = 0;
			}

			this.playerEntity.onGround = packet.onGround;
			this.mcServer.playerList.func_613_b(this.playerEntity);
			this.playerEntity.handleFalling(this.playerEntity.y - d1, packet.onGround);
		}

	}
	@Shadow
	private void teleportAndRotate(double lastPosX, double lastPosY, double lastPosZ, float f2, float f3) {
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
				if (s.startsWith(">") && LusiiPlugin.greenText) {
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
				if (LusiiPlugin.colourChat) {
					s = s.replace("$$", "§");
				}
				if (!this.playerEntity.isOperator() && s.contains("§k")) {s = s.replace("§k", "$$k");
					this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username, "§e§lHey!§r You may not use obfuscated text!");
				}

				System.out.println(s);
				logger.info(s);
				this.mcServer.playerList.sendEncryptedChatToAllPlayers(s);
			}
		}
	}


	@Overwrite
	public void handleSendInitialPlayerList() {
		Iterator var1 = this.mcServer.playerList.playerEntities.iterator();
		while(var1.hasNext()) {
			EntityPlayerMP entityPlayerMP = (EntityPlayerMP)var1.next();
			if (LusiiPlugin.vanished.contains(entityPlayerMP.username)) {

			} else {
				this.sendPacket(new Packet72UpdatePlayerProfile(entityPlayerMP.username, entityPlayerMP.nickname, entityPlayerMP.score, entityPlayerMP.chatColor, true, entityPlayerMP.isOperator()));
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

		if (this.playerEntity.health <= 0) {
			System.out.println(this.playerEntity.username + " tried to place a block while dead");
			this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username,"§e§lHey!§r You cannot do that. §5Respawn§r.");
			return;
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
