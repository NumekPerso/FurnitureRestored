package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import com.mrcrayfish.furniture.network.message.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * The fart key. The server checks the condition again, it does not trust what the client says.
 */
public class C2SMessageFart implements IMessage<C2SMessageFart>
{
    @Override
    public void encode(C2SMessageFart message, FriendlyByteBuf buffer) {}

    @Override
    public C2SMessageFart decode(FriendlyByteBuf buffer)
    {
        return new C2SMessageFart();
    }

    @Override
    public void handle(C2SMessageFart message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> IMessage.callServerConsumer(message, supplier, C2SMessageFart::handleOnServer));
        supplier.get().setPacketHandled(true);
    }

    private static void handleOnServer(ServerPlayer player, C2SMessageFart message)
    {
        if(ToiletBlock.isSeatedOnToilet(player))
        {
            player.level().playSound(null, player.blockPosition(), LegacySounds.randomFart(player.getRandom()), SoundSource.BLOCKS, 0.75F, player.getRandom().nextFloat());
            LegacyTriggers.PLAYER_FART.trigger(player);
        }
    }
}
