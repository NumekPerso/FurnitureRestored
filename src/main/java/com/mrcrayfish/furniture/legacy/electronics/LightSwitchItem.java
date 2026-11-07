package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.legacy.IPowered;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Right click a powered block to link it to the switch, the links go on the placed block entity
 * through BlockEntityTag. Using it in the air while sneaking clears the links. A switch cannot be
 * placed more than 16 blocks away from one of its links.
 */
public class LightSwitchItem extends BlockItem
{
    public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    public static final String LIGHTS_TAG = "lights";
    private static final double MAX_DISTANCE = 16.0;

    public LightSwitchItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if(player != null && !player.isShiftKeyDown() && level.getBlockState(context.getClickedPos()).getBlock() instanceof IPowered)
        {
            addLight(stack, context.getClickedPos());
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        if(placeContext.canPlace())
        {
            BlockPos placePos = placeContext.getClickedPos();
            BlockState state = this.getBlock().getStateForPlacement(placeContext);
            if(state != null && state.canSurvive(level, placePos) && isAnyLightTooFar(stack, placePos))
            {
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if(player.isShiftKeyDown())
        {
            stack.setTag(null);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return InteractionResultHolder.pass(stack);
    }

    private static void addLight(ItemStack stack, BlockPos pos)
    {
        CompoundTag blockEntityTag = stack.getOrCreateTagElement(BLOCK_ENTITY_TAG);
        ListTag lights = blockEntityTag.getList(LIGHTS_TAG, Tag.TAG_LONG);
        LongTag light = LongTag.valueOf(pos.asLong());
        if(!lights.contains(light))
        {
            lights.add(light);
        }
        blockEntityTag.put(LIGHTS_TAG, lights); // getList returns a detached list when the tag has none
    }

    private static boolean isAnyLightTooFar(ItemStack stack, BlockPos placePos)
    {
        CompoundTag blockEntityTag = stack.getTagElement(BLOCK_ENTITY_TAG);
        if(blockEntityTag == null)
        {
            return false;
        }
        Vec3 center = Vec3.atCenterOf(placePos);
        for(Tag light : blockEntityTag.getList(LIGHTS_TAG, Tag.TAG_LONG))
        {
            if(Vec3.atCenterOf(BlockPos.of(((LongTag) light).getAsLong())).distanceTo(center) > MAX_DISTANCE)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        ElectronicsClient.addInfo(tooltip, "furniture_restored.light_switch.info", "furniture_restored.light_switch.desc");
    }
}
