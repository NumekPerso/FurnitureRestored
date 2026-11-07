package com.mrcrayfish.furniture.legacy.event;

import com.mrcrayfish.furniture.item.CreativeItem;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import com.mrcrayfish.furniture.tileentity.MailBoxBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Signed envelope or package: deposited in a mail box with a sneaking click, and opened by a click
 * in the air to take its content.
 */
public class SignedMailItem extends Item implements IItemInventory, CreativeItem
{
    private final MailLayout layout;

    public SignedMailItem(MailLayout layout, Properties properties)
    {
        super(properties);
        this.layout = layout;
    }

    /* Only exists by signing: not in the creative tab */
    @Override
    public void fill(Consumer<ItemStack> output) {}

    @Override
    public Component getName(ItemStack stack)
    {
        return Component.translatable(this.getDescriptionId(stack)).withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD);
    }

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        String author = MailStorage.getAuthor(stack);
        if(author != null)
        {
            tooltip.add(Component.translatable("furniture_restored.mail_signed.info", author).withStyle(ChatFormatting.GRAY));
        }
    }

    /* Runs before MailBoxBlock#use, which would open the mail box */
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if(player == null || !(level.getBlockEntity(context.getClickedPos()) instanceof MailBoxBlockEntity mailBox))
            return InteractionResult.PASS;
        if(!level.isClientSide())
        {
            this.useOnMailBox(stack, player, mailBox);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private void useOnMailBox(ItemStack stack, Player player, MailBoxBlockEntity mailBox)
    {
        if(!MailStorage.hasItems(stack, this.layout))
        {
            player.sendSystemMessage(Component.translatable(this.layout == MailLayout.ENVELOPE ? "furniture_restored.message.envelope_used" : "furniture_restored.message.package_used"));
            return;
        }
        if(!player.isSecondaryUseActive())
            return;
        if(player.isCreative())
        {
            player.sendSystemMessage(Component.translatable("furniture_restored.message.mail_creative"));
            return;
        }
        String owner = mailBox.getOwnerName();
        Component ownerName = Component.literal(owner != null ? owner : "?").withStyle(ChatFormatting.YELLOW);
        for(int i = 0; i < mailBox.getContainerSize(); i++)
        {
            if(mailBox.getItem(i).isEmpty())
            {
                mailBox.setItem(i, stack.copy());
                stack.shrink(1);
                player.sendSystemMessage(Component.translatable("furniture_restored.message.mail_receive", ownerName));
                LegacyTriggers.PLAYER_SENT_MAIL.trigger(player);
                return;
            }
        }
        player.sendSystemMessage(Component.translatable("furniture_restored.message.mail_full", ownerName));
    }

    /* A targeted block keeps its use, but the mail only opens in the air like an unsigned envelope */
    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if(hand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.pass(stack);
        if(player instanceof ServerPlayer serverPlayer)
        {
            String author = MailStorage.getAuthor(stack);
            if(author != null && !author.equals(player.getName().getString()))
            {
                LegacyTriggers.PLAYER_GOT_MAIL.trigger(player);
            }
            MailItemMenu.open(serverPlayer, stack, this.layout, false);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
