package com.mrcrayfish.furniture.legacy.kitchen.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

/**
 * An appliance opened with a click. The position is sent to the client to find its block entity.
 */
public abstract class MenuApplianceBlock extends ApplianceBlock
{
    protected MenuApplianceBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof MenuProvider provider)
        {
            NetworkHooks.openScreen(serverPlayer, provider, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
