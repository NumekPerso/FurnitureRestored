package com.mrcrayfish.furniture.legacy.event;

import com.mrcrayfish.furniture.tileentity.MailBoxBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * Envelope or package not signed yet. An envelope only opens when no block is targeted, a package
 * also opens on a block.
 */
public class UnsignedMailItem extends Item implements IItemInventory, IAuthored
{
    private final MailLayout layout;

    public UnsignedMailItem(MailLayout layout, Properties properties)
    {
        super(properties);
        this.layout = layout;
    }

    @Override
    public Item getSignedItem()
    {
        return this.layout == MailLayout.ENVELOPE ? EventContent.SIGNED_ENVELOPE.get() : EventContent.SIGNED_PACKAGE.get();
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if(player == null)
            return InteractionResult.PASS;
        boolean mailBox = level.getBlockEntity(context.getClickedPos()) instanceof MailBoxBlockEntity;
        boolean envelope = this.layout == MailLayout.ENVELOPE;
        if(!mailBox && !envelope)
        {
            return InteractionResult.PASS;
        }
        if(!level.isClientSide())
        {
            if(mailBox)
            {
                if(player.isSecondaryUseActive())
                {
                    player.sendSystemMessage(Component.translatable(envelope ? "furniture_restored.message.envelope_sign" : "furniture_restored.message.package_sign"));
                }
            }
            else
            {
                player.sendSystemMessage(Component.translatable("furniture_restored.message.envelope_open"));
            }
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
            MailItemMenu.open(serverPlayer, stack, this.layout, true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
