package com.mrcrayfish.furniture.legacy.kitchen.block;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import com.mrcrayfish.furniture.entity.SeatEntity;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BarStoolBlock extends FurnitureBlock
{
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 13, 14);

    public BarStoolBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(LegacyUtil.tryRecolor(state, level, pos, player, hand, color -> LegacyUtil.get(KitchenContent.BAR_STOOLS, color)))
        {
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if(player.isCrouching())
        {
            return InteractionResult.PASS;
        }
        /* A stool has no direction. The seat looks at the opposite of the given direction: the player keeps looking ahead */
        return SeatEntity.create(level, pos, 9 * 0.0625, player, player.getDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }
}
