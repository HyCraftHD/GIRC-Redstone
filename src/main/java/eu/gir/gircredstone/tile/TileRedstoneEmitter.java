package eu.gir.gircredstone.tile;

import eu.gir.gircredstone.block.BlockRedstoneAcceptor;
import eu.gir.girsignals.linkableApi.ILinkableTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

public class TileRedstoneEmitter extends TileEntity implements ILinkableTile {

	private static final String ID_X = "xLinkedPos";
	private static final String ID_Y = "yLinkedPos";
	private static final String ID_Z = "zLinkedPos";

	private BlockPos linkedpos = null;
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (pos != null && compound != null) {
			compound.setInteger(ID_X, pos.getX());
			compound.setInteger(ID_Y, pos.getY());
			compound.setInteger(ID_Z, pos.getZ());
		}
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound != null && compound.hasKey(ID_X) && compound.hasKey(ID_Y) && compound.hasKey(ID_Z)) {
			this.linkedpos = new BlockPos(compound.getInteger(ID_X), compound.getInteger(ID_Y), compound.getInteger(ID_Z));
		}
	}

	@Override
	public boolean link(final BlockPos pos) {
		if(pos == null)
			return false;
		this.linkedpos = pos;
		return true;
	}
	
	@Override
	public boolean unlink() {
		if(this.linkedpos == null)
			return false;
		this.linkedpos = null;
		return true;
	}
	
	public BlockPos getLinkedPos() {
		return this.linkedpos;
	}

	public void redstoneUpdate(final boolean enabled) {
		if (linkedpos != null) {
			final boolean flag = !world.isBlockLoaded(linkedpos);
			Chunk lchunk = null;
			if(flag) {
				final Chunk chunk = world.getChunkFromBlockCoords(linkedpos);
				final ChunkProviderServer provider = (ChunkProviderServer) world.getChunkProvider();
				lchunk = provider.loadChunk(chunk.x, chunk.z);
				if(lchunk == null)
					return;
			}
			final IBlockState state = world.getBlockState(linkedpos);
			if (state.getBlock() instanceof BlockRedstoneAcceptor) {
				world.setBlockState(linkedpos, state.withProperty(BlockRedstoneAcceptor.POWER, enabled));
			}
			if(lchunk != null) {
				final ChunkProviderServer provider = (ChunkProviderServer) world.getChunkProvider();
				provider.queueUnload(lchunk);
			}
		}
	}

	@Override
	public boolean hasLink() {
		return this.linkedpos != null;
	}

}
