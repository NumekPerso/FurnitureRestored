package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Lamps stack, a stack shows the shade on its top lamp only. A click on an unlit lamp toggles the
 * top of its stack, a click on a lit lamp turns it off.
 */
public class LampBlock extends FurnitureBlock
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    private static final VoxelShape SHAPE = box(1, 0, 1, 15, 16, 15);

    public LampBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return Shapes.block();
    }

    private static boolean isLamp(BlockState state)
    {
        return state.getBlock() instanceof LampBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState().setValue(UP, isLamp(level.getBlockState(pos.above()))).setValue(DOWN, isLamp(level.getBlockState(pos.below())));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        return switch(direction)
        {
            case UP -> state.setValue(UP, isLamp(neighbourState));
            case DOWN -> state.setValue(DOWN, isLamp(neighbourState));
            default -> state;
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        // A dye of the same color is not used, but it does not toggle the lamp either
        if(LegacyUtil.tryRecolor(state, level, pos, player, hand, color -> LegacyUtil.get(ElectronicsContent.LAMPS, color)) || player.getItemInHand(hand).getItem() instanceof DyeItem)
        {
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if(!level.isClientSide())
        {
            if(state.getValue(LIT))
            {
                level.setBlock(pos, state.setValue(LIT, false), Block.UPDATE_ALL);
            }
            else
            {
                BlockPos top = pos;
                while(isLamp(level.getBlockState(top.above())))
                {
                    top = top.above();
                }
                BlockState topState = level.getBlockState(top);
                level.setBlock(top, topState.setValue(LIT, !topState.getValue(LIT)), Block.UPDATE_ALL);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(LIT, UP, DOWN);
    }
}
