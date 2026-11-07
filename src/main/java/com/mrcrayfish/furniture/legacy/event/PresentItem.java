package com.mrcrayfish.furniture.legacy.event;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Filled in its screen, then wrapped: only a wrapped present can be placed, and it stays wrapped.
 * The signed stack is the same item with an author.
 */
public class PresentItem extends BlockItem implements IItemInventory, IAuthored
{
    public PresentItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public Item getSignedItem()
    {
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        String author = MailStorage.getAuthor(stack);
        if(author != null)
        {
            tooltip.add(Component.translatable("furniture_restored.mail_signed.info", author).withStyle(ChatFormatting.GRAY));
        }
        else if(stack.hasTag())
        {
            tooltip.add(Component.translatable("furniture_restored.present_unsigned.info").withStyle(ChatFormatting.GRAY));
        }
    }

    /* PASS lets Item#use run: an unsigned present then opens its screen */
    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        if(player == null || !level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP))
            return InteractionResult.PASS;
        if(!MailStorage.isSigned(stack))
        {
            this.message(level, player, "furniture_restored.message.present_sign");
            return InteractionResult.PASS;
        }
        if(!MailStorage.hasItems(stack, MailLayout.PRESENT))
        {
            this.message(level, player, "furniture_restored.message.present_place");
            return InteractionResult.PASS;
        }
        BlockPos above = pos.above();
        BlockState state = this.getBlock().defaultBlockState();
        boolean free = level.isInWorldBounds(above) && level.getBlockState(above).canBeReplaced() && level.isUnobstructed(state, above, CollisionContext.of(player));
        if(!free || !player.mayUseItemAt(above, Direction.UP, stack))
            return InteractionResult.FAIL;
        if(!level.isClientSide())
        {
            NonNullList<ItemStack> items = MailStorage.read(stack, MailLayout.PRESENT);
            level.setBlock(above, state, Block.UPDATE_ALL);
            if(level.getBlockEntity(above) instanceof PresentBlockEntity present)
            {
                present.setContent(player.getName().getString(), items);
            }
            SoundType sound = SoundType.WOOL;
            level.playSound(null, above, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
            stack.shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if(hand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.pass(stack);
        if(player instanceof ServerPlayer serverPlayer)
        {
            if(MailStorage.isSigned(stack))
            {
                player.sendSystemMessage(Component.translatable("furniture_restored.message.present_wrap"));
            }
            else
            {
                MailItemMenu.open(serverPlayer, stack, MailLayout.PRESENT, true);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private void message(Level level, Player player, String key)
    {
        if(!level.isClientSide())
        {
            player.sendSystemMessage(Component.translatable(key));
        }
    }
}
