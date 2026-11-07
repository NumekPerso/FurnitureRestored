package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Decoration only.
 */
public class ComputerBlock extends FurnitureHorizontalBlock
{
    private static final VoxelShape SHAPE = box(1.6, 0, 1.6, 14.4, 16, 14.4);

    public ComputerBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        if(placer instanceof Player player)
        {
            LegacyTriggers.PLACE_APPLIANCE.trigger(player);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }
}
