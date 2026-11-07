package com.mrcrayfish.furniture.legacy.kitchen.item;

import com.mrcrayfish.furniture.item.CreativeItem;
import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.function.Consumer;

/**
 * The empty cup and the drink. Both place the cup block, which keeps the drink.
 */
public class CupItem extends Item implements CreativeItem
{
    public static final String TAG_COLOUR = "Colour";
    public static final String TAG_HEAL = "HealAmount";
    public static final String TAG_NAME = "Name";

    private final boolean hasLiquid;

    public CupItem(Properties properties, boolean hasLiquid)
    {
        super(properties);
        this.hasLiquid = hasLiquid;
    }

    public static ItemStack createDrink(String name, int heal, int[] color)
    {
        ItemStack stack = new ItemStack(KitchenContent.DRINK.get());
        CompoundTag tag = stack.getOrCreateTag();
        tag.putIntArray(TAG_COLOUR, color);
        tag.putInt(TAG_HEAL, heal);
        tag.putString(TAG_NAME, name);
        return stack;
    }

    /** Color of the drink as 0xRRGGBB, white without color. */
    public static int getColor(ItemStack stack)
    {
        CompoundTag tag = stack.getTag();
        if(tag != null && tag.contains(TAG_COLOUR, Tag.TAG_INT_ARRAY))
        {
            int[] color = tag.getIntArray(TAG_COLOUR);
            if(color.length == 3)
            {
                return (color[0] & 255) << 16 | (color[1] & 255) << 8 | (color[2] & 255);
            }
        }
        return 0xFFFFFF;
    }

    @Override
    public void fill(Consumer<ItemStack> output)
    {
        /* A drink comes from a blender and is not in the creative tab */
        if(!this.hasLiquid)
        {
            output.accept(new ItemStack(this));
        }
    }

    @Override
    public Component getName(ItemStack stack)
    {
        CompoundTag tag = stack.getTag();
        if(tag != null && tag.contains(TAG_NAME, Tag.TAG_STRING))
        {
            return Component.literal(tag.getString(TAG_NAME));
        }
        return super.getName(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
    {
        if(this.hasLiquid && entity instanceof Player player)
        {
            CompoundTag tag = stack.getTag();
            if(tag != null)
            {
                player.getFoodData().eat(tag.getInt(TAG_HEAL), 0.5F);
            }
            if(!player.getAbilities().instabuild)
            {
                return new ItemStack(KitchenContent.CUP_ITEM.get());
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if(this.hasLiquid && player.getFoodData().needsFood())
        {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext useContext)
    {
        BlockPlaceContext context = new BlockPlaceContext(useContext);
        if(!context.canPlace())
            return InteractionResult.FAIL;

        Block block = KitchenContent.CUP.get();
        BlockState state = block.getStateForPlacement(context);
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if(state == null || !state.canSurvive(level, pos) || !level.isUnobstructed(state, pos, player == null ? CollisionContext.empty() : CollisionContext.of(player)))
            return InteractionResult.FAIL;

        ItemStack stack = context.getItemInHand();
        if(!level.isClientSide())
        {
            if(!level.setBlock(pos, state, Block.UPDATE_ALL_IMMEDIATE))
                return InteractionResult.FAIL;
            block.setPlacedBy(level, pos, state, player, stack);
            SoundType sound = state.getSoundType(level, pos, player);
            level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
            if(player == null || !player.getAbilities().instabuild)
            {
                stack.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
