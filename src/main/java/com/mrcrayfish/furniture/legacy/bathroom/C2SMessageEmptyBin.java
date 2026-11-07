package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.network.message.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * The Empty button of the bin. It acts on the menu the player has open, so it needs no position.
 */
public class C2SMessageEmptyBin implements IMessage<C2SMessageEmptyBin>
{
    @Override
    public void encode(C2SMessageEmptyBin message, FriendlyByteBuf buffer) {}

    @Override
    public C2SMessageEmptyBin decode(FriendlyByteBuf buffer)
    {
        return new C2SMessageEmptyBin();
    }

    @Override
    public void handle(C2SMessageEmptyBin message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> IMessage.callServerConsumer(message, supplier, C2SMessageEmptyBin::handleOnServer));
        supplier.get().setPacketHandled(true);
    }

    private static void handleOnServer(ServerPlayer player, C2SMessageEmptyBin message)
    {
        if(player.containerMenu instanceof BinMenu menu && menu.stillValid(player))
        {
            menu.empty();
        }
    }
}
