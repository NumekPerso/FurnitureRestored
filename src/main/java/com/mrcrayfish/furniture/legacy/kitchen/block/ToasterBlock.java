package com.mrcrayfish.furniture.legacy.kitchen.block;

import com.mrcrayfish.furniture.legacy.LegacyUtil;
import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.ToasterBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

public class ToasterBlock extends ApplianceBlock
{
    private static final Map<Direction, VoxelShape> SHAPES = LegacyUtil.horizontalShapes(Direction.EAST, 5, 0, 3, 11, 7.2, 13);
    private static final Map<Direction, VoxelShape> COLLISIONS = LegacyUtil.horizontalShapes(Direction.EAST, 5, 0, 3, 11, 6.4, 13);

    public ToasterBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof ToasterBlockEntity toaster)
        {
            ItemStack stack = player.getItemInHand(hand);
            if(!stack.isEmpty() && !toaster.isToasting())
            {
                if(KitchenRecipes.find(KitchenRecipes.TOASTER, level, stack).isPresent())
                {
                    if(toaster.addSlice(stack))
                    {
                        consume(player, stack);
                    }
                }
                else
                {
                    toaster.removeSlice();
                }
            }
            else if(player.isCrouching())
            {
                if(!toaster.isToasting())
                {
                    toaster.startToasting();
                }
            }
            else if(!toaster.isToasting())
            {
                toaster.removeSlice();
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
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
        return new ToasterBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return level.isClientSide() ? null : createTickerHelper(type, KitchenContent.TOASTER_ENTITY.get(), ToasterBlockEntity::serverTick);
    }
}
