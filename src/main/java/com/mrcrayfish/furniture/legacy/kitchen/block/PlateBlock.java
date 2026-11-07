package com.mrcrayfish.furniture.legacy.kitchen.block;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.PlateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PlateBlock extends FurnitureBlock implements EntityBlock
{
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 2, 13);

    public PlateBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof PlateBlockEntity plate)
        {
            ItemStack stack = player.getItemInHand(hand);
            ItemStack food = plate.getFood();
            if(food.isEmpty())
            {
                if(stack.isEdible())
                {
                    plate.setFood(stack.copyWithCount(1), player.getDirection().get2DDataValue());
                    HorizontalEntityBlock.consume(player, stack);
                }
            }
            else
            {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, food));
                plate.setFood(ItemStack.EMPTY, plate.getRotation());
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        HorizontalEntityBlock.dropSyncedContents(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new PlateBlockEntity(pos, state);
    }
}
