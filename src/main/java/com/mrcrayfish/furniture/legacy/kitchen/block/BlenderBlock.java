package com.mrcrayfish.furniture.legacy.kitchen.block;

import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.BlenderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BlenderBlock extends ApplianceBlock
{
    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape COLLISION = Block.box(4.5, 0, 4.5, 11.5, 15, 11.5);

    public BlenderBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof BlenderBlockEntity blender)
        {
            ItemStack stack = player.getItemInHand(hand);
            if(!blender.hasDrink())
            {
                if(!stack.isEmpty() && !blender.isFull() && !blender.isBlending())
                {
                    blender.addIngredient(stack);
                    if(!player.getAbilities().instabuild)
                    {
                        player.setItemInHand(hand, ItemStack.EMPTY);
                    }
                }
                else if(!blender.isBlending())
                {
                    if(player.isCrouching())
                    {
                        blender.tryStartBlending();
                    }
                    else
                    {
                        blender.removeIngredient();
                    }
                }
            }
            else if(stack.is(KitchenContent.CUP_ITEM.get()))
            {
                ItemStack drink = blender.takeDrink();
                if(stack.getCount() == 1 && !player.getAbilities().instabuild)
                {
                    player.setItemInHand(hand, drink);
                }
                else
                {
                    consume(player, stack);
                    if(!player.getInventory().add(drink))
                    {
                        player.drop(drink, false);
                    }
                }
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.5F);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return COLLISION;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new BlenderBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return level.isClientSide() ? createTickerHelper(type, KitchenContent.BLENDER_ENTITY.get(), BlenderBlockEntity::clientTick) : createTickerHelper(type, KitchenContent.BLENDER_ENTITY.get(), BlenderBlockEntity::serverTick);
    }
}
