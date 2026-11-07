package com.mrcrayfish.furniture.legacy.event;

import com.mrcrayfish.furniture.network.message.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Signs the item whose menu the player has open.
 */
public class C2SMessageSignItem implements IMessage<C2SMessageSignItem>
{
    @Override
    public void encode(C2SMessageSignItem message, FriendlyByteBuf buffer) {}

    @Override
    public C2SMessageSignItem decode(FriendlyByteBuf buffer)
    {
        return new C2SMessageSignItem();
    }

    @Override
    public void handle(C2SMessageSignItem message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> IMessage.callServerConsumer(message, supplier, (player, m) -> sign(player)));
        supplier.get().setPacketHandled(true);
    }

    private static void sign(ServerPlayer player)
    {
        ItemStack stack = player.getMainHandItem();
        if(!(player.containerMenu instanceof MailItemMenu menu) || !menu.isFor(stack))
            return;
        if(!(stack.getItem() instanceof IAuthored authored) || MailStorage.isSigned(stack))
            return;
        if(menu.isContentEmpty())
        {
            player.sendSystemMessage(Component.translatable(menu.getLayout().emptyMessage()));
            return;
        }
        ItemStack signed = new ItemStack(authored.getSignedItem());
        if(stack.getTag() != null)
        {
            signed.setTag(stack.getTag().copy());
        }
        MailStorage.setAuthor(signed, player.getName().getString());
        player.getInventory().setItem(player.getInventory().selected, signed);
        menu.replaceStack(signed);
        player.closeContainer(); // Writes the content of the menu to the signed stack
    }
}
