package com.mrcrayfish.furniture.legacy.shared;

import com.mrcrayfish.furniture.block.BlindsBlock;
import com.mrcrayfish.furniture.block.ChairBlock;
import com.mrcrayfish.furniture.block.FreezerBlock;
import com.mrcrayfish.furniture.block.FridgeBlock;
import com.mrcrayfish.furniture.block.HedgeBlock;
import com.mrcrayfish.furniture.block.RockPath;
import com.mrcrayfish.furniture.block.TableBlock;
import com.mrcrayfish.furniture.common.ModTags;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import com.mrcrayfish.furniture.tileentity.MailBoxBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Mail box protection and the advancement triggers of placed blocks.
 */
public class SharedEvents
{
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
        Player player = event.getPlayer();
        if(event.getLevel().getBlockEntity(event.getPos()) instanceof MailBoxBlockEntity mailBox && !mailBox.canAccess(player))
        {
            player.displayClientMessage(Component.translatable("furniture_restored.message.mailbox_break").withStyle(ChatFormatting.RED), false);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event)
    {
        if(!(event.getEntity() instanceof Player player))
            return;

        /* Blocks placing a second block, like the fridge above a freezer, post the multi place event */
        if(event instanceof BlockEvent.EntityMultiPlaceEvent multi)
        {
            for(BlockSnapshot snapshot : multi.getReplacedBlockSnapshots())
            {
                this.triggerPlaced(player, snapshot.getCurrentBlock());
            }
            return;
        }
        this.triggerPlaced(player, event.getPlacedBlock());
    }

    private void triggerPlaced(Player player, BlockState state)
    {
        Block block = state.getBlock();
        if(block instanceof ChairBlock)
        {
            LegacyTriggers.PLACE_CHAIR.trigger(player);
            LegacyTriggers.PLACE_CHAIR_OR_TABLE.trigger(player);
        }
        else if(block instanceof TableBlock)
        {
            LegacyTriggers.PLACE_TABLE.trigger(player);
            LegacyTriggers.PLACE_CHAIR_OR_TABLE.trigger(player);
        }
        else if(block instanceof BlindsBlock)
        {
            LegacyTriggers.PLACE_BLINDS_OR_CURTAINS.trigger(player);
        }
        else if(block instanceof HedgeBlock || block instanceof RockPath || state.is(ModTags.Blocks.PICKET_FENCES))
        {
            LegacyTriggers.PLACE_OUTDOOR_FURNITURE.trigger(player);
        }
        else if(block instanceof FreezerBlock || block instanceof FridgeBlock)
        {
            LegacyTriggers.PLACE_APPLIANCE.trigger(player);
        }
    }
}
