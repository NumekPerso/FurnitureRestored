package com.mrcrayfish.furniture.legacy.kitchen.block;

import com.mrcrayfish.furniture.legacy.LegacyItems;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.ChoppingBoardBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class ChoppingBoardBlock extends HorizontalEntityBlock
{
    private static final Map<Direction, VoxelShape> SHAPES = LegacyUtil.horizontalShapes(Direction.EAST, 3, 0, 0, 13, 1.5, 16);
    private static final Map<Direction, VoxelShape> COLLISIONS = LegacyUtil.horizontalShapes(Direction.EAST, 3, 0, 0, 13, 1, 16);

    public ChoppingBoardBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof ChoppingBoardBlockEntity board)
        {
            ItemStack stack = player.getItemInHand(hand);
            if(!stack.isEmpty() && board.getFood().isEmpty() && KitchenRecipes.find(KitchenRecipes.CHOPPING_BOARD, level, stack).isPresent())
            {
                board.setFood(stack.copyWithCount(1));
                consume(player, stack);
            }
            else if(stack.is(LegacyItems.KNIFE.get()) && !board.getFood().isEmpty())
            {
                if(board.chopFood())
                {
                    stack.hurtAndBreak(1, player, entity -> entity.broadcastBreakEvent(hand));
                }
            }
            else if(!board.getFood().isEmpty())
            {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, board.getFood()));
                board.setFood(ItemStack.EMPTY);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        return level.getBlockEntity(pos) instanceof ChoppingBoardBlockEntity board && !board.getFood().isEmpty() ? 1 : 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES.get(state.getValue(DIRECTION));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return COLLISIONS.get(state.getValue(DIRECTION));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new ChoppingBoardBlockEntity(pos, state);
    }
}
