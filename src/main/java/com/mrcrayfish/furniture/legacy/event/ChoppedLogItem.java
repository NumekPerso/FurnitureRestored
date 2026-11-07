package com.mrcrayfish.furniture.legacy.event;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Places a fire pit, or adds a log to the unlit pit it is used on. Also a fuel.
 */
public class ChoppedLogItem extends BlockItem
{
    private static final int BURN_TIME = 500;

    public ChoppedLogItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if(!state.is(this.getBlock()) || state.getValue(FirePitBlock.LIT))
        {
            return super.useOn(context);
        }
        int stage = state.getValue(FirePitBlock.STAGE);
        if(stage >= FirePitBlock.LAST_STAGE)
        {
            return InteractionResult.FAIL;
        }
        if(!level.isClientSide())
        {
            level.playSound(null, pos, SoundType.WOOD.getPlaceSound(), SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            level.setBlock(pos, state.setValue(FirePitBlock.STAGE, stage + 1), Block.UPDATE_ALL);
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType)
    {
        return BURN_TIME;
    }

    /* A BlockItem takes the name of its block, this one has its own */
    @Override
    public String getDescriptionId()
    {
        return "item.furniture_restored.chopped_log";
    }

    /* The fire pit keeps FirePitItem as its item: Block#asItem must not return the log */
    @Override
    public void registerBlocks(Map<Block, Item> map, Item item) {}

    @Override
    public void removeFromBlockToItemMap(Map<Block, Item> map, Item item) {}
}
