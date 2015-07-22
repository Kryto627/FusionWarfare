package calemi.fusionwarfare.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import calemi.fusionwarfare.FusionWarfare;
import calemi.fusionwarfare.Reference;
import calemi.fusionwarfare.init.InitCreativeTabs;
import calemi.fusionwarfare.tileentity.TileEntityBase;
import calemi.fusionwarfare.tileentity.TileEntitySecurity;
import calemi.fusionwarfare.tileentity.TileEntitySupplyCrate;
import calemi.fusionwarfare.tileentity.network.TileEntityNetworkCable;
import calemi.fusionwarfare.tileentity.network.TileEntityNetworkController;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBasicMachineBase extends BlockContainerBase {

	public Class tileEntity;
	public int guiID;
	public boolean isDirectional;
	public boolean hasCustomModel;

	public String topImage, bottomImage, sideImage1, sideImage2, sideImage3, sideImage4;
	public String particleImage;
	
	float pixel = 1F/16F;

	@SideOnly(Side.CLIENT)
	private IIcon block_top, block_bottom, block_front, block_side_2, block_side_3, block_side_4;
	
	public BlockBasicMachineBase(String imagePath, Class tileEntity, int guiID, boolean isDirectional, boolean isRegistered, String topImage, String bottomImage, String sideImage1, String sideImage2, String sideImage3, String sideImage4) {
		super(imagePath, 2, Material.iron, 6F, 10F, Block.soundTypeMetal, isRegistered);
		this.tileEntity = tileEntity;
		this.guiID = guiID;
		this.isDirectional = isDirectional;
		this.hasCustomModel = false;
		this.topImage = topImage;
		this.bottomImage = bottomImage;
		this.sideImage1 = sideImage1;
		this.sideImage2 = sideImage2;
		this.sideImage3 = sideImage3;
		this.sideImage4 = sideImage4;
	}
	
	public BlockBasicMachineBase(String imagePath, Class tileEntity, int guiID, boolean isDirectional, boolean isRegistered, String topImage, String bottomImage, String sideImage) {
		this(imagePath, tileEntity, guiID, isDirectional, isRegistered, topImage, bottomImage, sideImage, sideImage, sideImage, sideImage);
		this.hasCustomModel = false;
	}
	
	public BlockBasicMachineBase(String imagePath, String particleImage, Class tileEntity, int guiID, int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd) {
		this(imagePath, tileEntity, guiID, false, true, "", "", "");
		this.hasCustomModel = true;
		this.particleImage = particleImage;
		setBlockBounds(xStart * pixel, yStart * pixel, zStart * pixel, xEnd * pixel, yEnd * pixel, zEnd * pixel);
	}
	
	public BlockBasicMachineBase(String imagePath, Class tileEntity, int guiID, boolean isDirectional, String sideImage) {
		this(imagePath, tileEntity, guiID, isDirectional, true, "mech_top_1", "steel_casing", sideImage);
		this.hasCustomModel = false;
	}
	
	public BlockBasicMachineBase(String imagePath, Class tileEntity, int guiID, boolean isDirectional) {
		this(imagePath, tileEntity, guiID, isDirectional, true, "mech_top_1", "steel_casing", "mech_side");
		this.hasCustomModel = false;
	}
	
	public int getRenderType() {
		return hasCustomModel ? -1 : super.getRenderType();
	}

	public boolean renderAsNormalBlock() {
		return !hasCustomModel;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {

		if (hasCustomModel) {
			return super.getIcon(side, meta);
		}
		
		if (isDirectional) {

			if (meta == 0 && side == 3) {

				return block_front;
			}

			if (meta == 2 && side == 2) {
				return block_front;
			}

			if (meta == 3 && side == 5) {
				return block_front;
			}

			if (meta == 1 && side == 4) {
				return block_front;
			}
		}
		
		if (side == 0) {
			return block_bottom;
		}

		if (side == 1) {
			return block_top;
		}
				
		if (side == 3) {
			return block_side_2;
		}
		
		if (side == 4) {
			return block_side_3;
		}
		
		if (side == 5) {
			return block_side_4;
		}

		return blockIcon;
	}

	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		
		if (!w.isRemote && e instanceof EntityPlayer) {
			
			EntityPlayer player = (EntityPlayer)e;
			
			if (w.getTileEntity(x, y, z) instanceof TileEntitySecurity) {
				
				if (player.getTeam() != null) {
					((TileEntitySecurity)w.getTileEntity(x, y, z)).teamName = player.getTeam().getRegisteredName();
				}
				
				else {
					player.addChatMessage(new ChatComponentText("You are not on a team. No security will be added"));	
				}
			}			
		}
		
		int l = MathHelper.floor_double((double) (e.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
		if (isDirectional)
			w.setBlockMetadataWithNotify(x, y, z, l, 2);
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconreg) {

		if (!hasCustomModel) {
			
			this.block_top = iconreg.registerIcon(Reference.MOD_ID + ":" + topImage);
			this.block_bottom = iconreg.registerIcon(Reference.MOD_ID + ":" + bottomImage);
			if (isDirectional) this.block_front = iconreg.registerIcon(Reference.MOD_ID + ":" + imagePath + "_front");
			this.block_side_2 = iconreg.registerIcon(Reference.MOD_ID + ":" + sideImage2);
			this.block_side_3 = iconreg.registerIcon(Reference.MOD_ID + ":" + sideImage3);
			this.block_side_4 = iconreg.registerIcon(Reference.MOD_ID + ":" + sideImage4);
			this.blockIcon = iconreg.registerIcon(Reference.MOD_ID + ":" + sideImage1);
		}
		
		else {
			this.blockIcon = iconreg.registerIcon(Reference.MOD_ID + ":" + particleImage);
		}	
	}

	@Override
	public int getGuiID() {
		return guiID;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		
		try {
			return (TileEntity) tileEntity.newInstance();
		} 
		
		catch (Exception e) {
			e.printStackTrace();
		} 
				
		return null;
	}
}
