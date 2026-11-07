package com.mrcrayfish.furniture.legacy.event;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * A placed present: breaking it unwraps it, it drops its content and never itself.
 */
public class PresentBlock extends FurnitureBlock implements EntityBlock
{
    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 5.6, 12);

    public PresentBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof PresentBlockEntity present)
        {
            level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.75F, 1.0F);
            player.sendSystemMessage(Component.translatable("furniture_restored.message.present_christmas", Component.literal(present.getOwnerName()).withStyle(ChatFormatting.RED)));
            LegacyTriggers.UNWRAP_PRESENT.trigger(player);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if(!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof PresentBlockEntity present)
        {
            Containers.dropContents(level, pos, present.getItems());
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    /* Registered with an item, so the data generator gives it a loot table: the content is the only drop */
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return Collections.emptyList();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new PresentBlockEntity(pos, state);
    }
}
