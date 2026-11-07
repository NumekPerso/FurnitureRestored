package com.mrcrayfish.furniture.legacy.decor;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

/**
 * Takes the water source two blocks below and places it behind the tap.
 */
public class TapBlock extends FurnitureHorizontalBlock
{
    private static final Map<Direction, VoxelShape> SHAPES = LegacyUtil.horizontalShapes(Direction.EAST, 2, 0, 6.4, 9, 16, 9.6);

    public TapBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES.get(state.getValue(DIRECTION));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if(!level.isClientSide())
        {
            if(hasWaterSource(level, pos))
            {
                BlockPos target = pos.relative(state.getValue(DIRECTION).getOpposite());
                if(level.isEmptyBlock(target))
                {
                    level.setBlock(target, Blocks.WATER.defaultBlockState(), Block.UPDATE_ALL);
                    level.setBlock(pos.below(2), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
                else
                {
                    player.sendSystemMessage(Component.translatable("furniture_restored.message.tap_blocked"));
                }
            }
            else
            {
                player.sendSystemMessage(Component.translatable("furniture_restored.message.tap_nowater"));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static boolean hasWaterSource(Level level, BlockPos pos)
    {
        BlockState state = level.getBlockState(pos.below(2));
        return state.is(Blocks.WATER) && state.getValue(LiquidBlock.LEVEL) == 0;
    }
}
