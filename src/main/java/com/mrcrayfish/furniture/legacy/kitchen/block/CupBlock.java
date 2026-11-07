package com.mrcrayfish.furniture.legacy.kitchen.block;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.CupBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.item.CupItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * The cup placed in the world. It has no item of its own: it drops the cup, or the drink it holds.
 */
public class CupBlock extends FurnitureBlock implements EntityBlock
{
    private static final VoxelShape SHAPE = Block.box(5, 0, 5, 11, 7.5, 11);

    public CupBlock(Properties properties)
    {
        super(properties);
    }

    private static ItemStack getStack(@Nullable BlockEntity blockEntity)
    {
        if(blockEntity instanceof CupBlockEntity cup && !cup.getDrink().isEmpty())
        {
            return cup.getDrink().copy();
        }
        return new ItemStack(KitchenContent.CUP_ITEM.get());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(player.isCrouching() && !player.getAbilities().instabuild && player.getFoodData().needsFood() && level.getBlockEntity(pos) instanceof CupBlockEntity cup)
        {
            ItemStack drink = cup.getDrink();
            if(!drink.isEmpty() && drink.hasTag())
            {
                if(!level.isClientSide())
                {
                    player.getFoodData().eat(drink.getTag().getInt(CupItem.TAG_HEAL), 0.5F);
                    cup.clearDrink();
                    level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
                    level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if(stack.hasTag() && stack.getTag().contains(CupItem.TAG_COLOUR, Tag.TAG_INT_ARRAY) && level.getBlockEntity(pos) instanceof CupBlockEntity cup)
        {
            cup.setDrink(stack);
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return Collections.singletonList(getStack(builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY)));
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state)
    {
        return getStack(level.getBlockEntity(pos));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new CupBlockEntity(pos, state);
    }
}
