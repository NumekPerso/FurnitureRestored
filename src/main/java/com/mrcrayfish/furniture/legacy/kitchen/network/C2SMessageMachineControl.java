package com.mrcrayfish.furniture.legacy.kitchen.network;

import com.mrcrayfish.furniture.legacy.kitchen.menu.MachineControlMenu;
import com.mrcrayfish.furniture.network.message.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Start and Stop buttons of the microwave, the washing machine and the dishwasher. It acts on the
 * menu the player has open, so it needs no position and cannot reach another machine.
 */
public class C2SMessageMachineControl implements IMessage<C2SMessageMachineControl>
{
    private boolean start;

    public C2SMessageMachineControl() {}

    public C2SMessageMachineControl(boolean start)
    {
        this.start = start;
    }

    @Override
    public void encode(C2SMessageMachineControl message, FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(message.start);
    }

    @Override
    public C2SMessageMachineControl decode(FriendlyByteBuf buffer)
    {
        return new C2SMessageMachineControl(buffer.readBoolean());
    }

    @Override
    public void handle(C2SMessageMachineControl message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> IMessage.callServerConsumer(message, supplier, (player, msg) -> {
            if(player.containerMenu instanceof MachineControlMenu menu && player.containerMenu.stillValid(player))
            {
                if(msg.start)
                {
                    menu.startMachine();
                }
                else
                {
                    menu.stopMachine();
                }
            }
        }));
        supplier.get().setPacketHandled(true);
    }
}
