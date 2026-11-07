package com.mrcrayfish.furniture.legacy.kitchen.block;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.RunningMachine;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Horizontal block with a block entity. The models of this module have no rotation when facing east.
 */
public abstract class HorizontalEntityBlock extends FurnitureHorizontalBlock implements EntityBlock
{
    protected HorizontalEntityBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH));
    }

    /** Drops the content of a block entity that is not a Container, see FurnitureBlock#onRemove. */
    public static void dropSyncedContents(BlockState state, Level level, BlockPos pos, BlockState newState)
    {
        if(state.getBlock() != newState.getBlock() && level.getBlockEntity(pos) instanceof SyncedBlockEntity blockEntity)
        {
            Containers.dropContents(level, pos, blockEntity.getDropStacks());
        }
    }

    public static void consume(Player player, ItemStack stack)
    {
        if(!player.getAbilities().instabuild)
        {
            stack.shrink(1);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        dropSyncedContents(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        if(level.getBlockEntity(pos) instanceof RunningMachine machine)
        {
            return machine.isRunning() ? 1 : 0;
        }
        return super.getAnalogOutputSignal(state, level, pos);
    }
}
