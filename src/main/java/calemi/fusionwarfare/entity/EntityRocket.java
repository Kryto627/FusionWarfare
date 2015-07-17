package calemi.fusionwarfare.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class EntityRocket extends Entity {

	public EntityPlayer shooter;
	
	public EntityRocket(World world) {
		super(world);
	}

	public EntityRocket(World world, EntityPlayer entity) {
		super(world);

		setSize(0.5F, 0.5F);

		shooter = entity;
		
		posX = entity.posX;
		posY = entity.posY + 1.5;
		posZ = entity.posZ;

		rotationPitch = entity.rotationPitch;
		rotationYaw = entity.rotationYaw;

		motionX = (double) (-MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * 2);
		motionZ = (double) (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * 2);
		motionY = (double) (-MathHelper.sin(rotationPitch / 180.0F * (float) Math.PI) * 2);
	
		motionX *= 0.5F;
		motionY *= 0.5F;
		motionZ *= 0.5F;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		
		if (ticksExisted > 20 * 60) {			
			setDead();			
		}
			
		for (int i = 0; i < 10; i++) {

			worldObj.spawnParticle("smoke", posX - motionX, posY - motionY, posZ - motionZ, 0, 0, 0);				
			if (i % 10 == 0) worldObj.spawnParticle("flame", posX - motionX, posY - motionY, posZ - motionZ, 0, 0, 0);
		}
		
		if (!worldObj.isRemote) {
			
			if (worldObj.getBlock((int)posX, (int)posY, (int)posZ) != Blocks.air) {
			
				worldObj.createExplosion(shooter, posX, posY, posZ, 5, false);
				setDead();
			}
			
			for (Object o : worldObj.loadedEntityList) {
		
				if (o instanceof Entity) {
				
					Entity entity = (Entity)o;
								
					if (ticksExisted > 5 && entity != this && entity.getDistance(posX, posY, posZ) < 2) {
						
						worldObj.createExplosion(shooter, posX, posY, posZ, 6, false);
						setDead();
						break;
					}				
				}			
			}
		}	
	}

	@Override
	protected void entityInit() {}


	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
	
		shooter = worldObj.getPlayerEntityByName(nbt.getString("shooter"));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
	
		nbt.setString("shooter", ((EntityPlayer)shooter).getDisplayName());
	}
}
