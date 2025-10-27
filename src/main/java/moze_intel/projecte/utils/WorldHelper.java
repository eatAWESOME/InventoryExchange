package moze_intel.projecte.utils;


import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import moze_intel.projecte.PECore;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class for anything that touches a World. Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class WorldHelper {

	private static final Map<Block, IntegerProperty> AGE_PROPERTIES = new Reference2ObjectOpenHashMap<>();

	public static void clearCachedAgeProperties() {
		AGE_PROPERTIES.clear();
	}
	
	/**
	 * Checks if the chunk at the given position is loaded but does not validate the position is in bounds of the world.
	 *
	 * @param world world
	 * @param pos   position
	 *
	 * @see #isBlockLoaded(BlockGetter, BlockPos)
	 */
	@Contract("null, _ -> false")
	public static boolean isChunkLoaded(@Nullable LevelReader world, @NotNull BlockPos pos) {
		return isChunkLoaded(world, SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
	}

	/**
	 * Checks if the chunk at the given position is loaded.
	 *
	 * @param world    world
	 * @param chunkPos Chunk position
	 */
	@Contract("null, _ -> false")
	public static boolean isChunkLoaded(@Nullable LevelReader world, ChunkPos chunkPos) {
		return isChunkLoaded(world, chunkPos.x, chunkPos.z);
	}

	/**
	 * Checks if the chunk at the given position is loaded.
	 *
	 * @param world  world
	 * @param chunkX Chunk X coordinate
	 * @param chunkZ Chunk Z coordinate
	 */
	@Contract("null, _, _ -> false")
	public static boolean isChunkLoaded(@Nullable LevelReader world, int chunkX, int chunkZ) {
		if (world == null) {
			return false;
		} else if (world instanceof LevelAccessor accessor) {
			return accessor.hasChunk(chunkX, chunkZ);
		}
		return world.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) != null;
	}
	
	/**
	 * Checks if a position is in bounds of the world, and is loaded
	 *
	 * @param world world
	 * @param pos   position
	 *
	 * @return True if the position is loaded or the given world is of a superclass of IWorldReader that does not have a concept of being loaded.
	 *
	 * @implNote From Mekanism
	 */
	public static boolean isBlockLoaded(@Nullable BlockGetter world, @NotNull BlockPos pos) {
		if (world == null) {
			return false;
		} else if (world instanceof LevelReader reader) {
			if (reader instanceof Level level && !level.isInWorldBounds(pos)) {
				return false;
			}
			//TODO: If any cases come up where things are behaving oddly due to the change from reader.hasChunkAt(pos)
			// re-evaluate this and if the specific case is being handled properly
			return isChunkLoaded(reader, pos);
		}
		return true;
	}

	/**
	 * Gets a block entity if the location is loaded
	 *
	 * @param level world
	 * @param pos   position
	 *
	 * @return block entity if found, null if either not found or not loaded
	 *
	 * @implNote From Mekanism
	 */
	@Nullable
	public static BlockEntity getBlockEntity(@Nullable BlockGetter level, @NotNull BlockPos pos) {
		if (!isBlockLoaded(level, pos)) {
			//If the world is null or its a world reader and the block is not loaded, return null
			return null;
		}
		return level.getBlockEntity(pos);
	}

	/**
	 * Gets a block entity if the location is loaded
	 *
	 * @param clazz Class type of the block entity we expect to be in the position
	 * @param level world
	 * @param pos   position
	 *
	 * @return block entity if found, null if either not found, not loaded, or of the wrong type
	 *
	 * @implNote From Mekanism
	 */
	@Nullable
	public static <BE extends BlockEntity> BE getBlockEntity(@NotNull Class<BE> clazz, @Nullable BlockGetter level, @NotNull BlockPos pos) {
		return getBlockEntity(clazz, level, pos, false);
	}

	/**
	 * Gets a block entity if the location is loaded
	 *
	 * @param clazz        Class type of the block entity we expect to be in the position
	 * @param level        world
	 * @param pos          position
	 * @param logWrongType Whether or not an error should be logged if a block entity of a different type is found at the position
	 *
	 * @return block entity if found, null if either not found or not loaded, or of the wrong type
	 *
	 * @implNote From Mekanism
	 */
	@Nullable
	public static <BE extends BlockEntity> BE getBlockEntity(@NotNull Class<BE> clazz, @Nullable BlockGetter level, @NotNull BlockPos pos, boolean logWrongType) {
		BlockEntity blockEntity = getBlockEntity(level, pos);
		if (blockEntity == null) {
			return null;
		}
		if (clazz.isInstance(blockEntity)) {
			return clazz.cast(blockEntity);
		} else if (logWrongType) {
			PECore.LOGGER.warn("Unexpected block entity class at {}, expected {}, but found: {}", pos, clazz, blockEntity.getClass());
		}
		return null;
	}
}