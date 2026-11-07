package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import com.mrcrayfish.furniture.legacy.IPowered;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class CeilingFanBlock extends FurnitureBlock implements EntityBlock, IPowered
{
    private static final VoxelShape SELECTION = box(4, 5, 4, 12, 16, 12);
    private static final VoxelShape COLLISION = box(5, 6, 5, 11, 16, 11);

    public CeilingFanBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SELECTION;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return COLLISION;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return context.getClickedFace() == Direction.DOWN ? this.defaultBlockState() : null;
    }

    /** Only checked when placing: the fan stays if the ceiling goes away. */
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos ceiling = pos.above();
        return level.getBlockState(ceiling).isFaceSturdy(level, ceiling, Direction.DOWN);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof CeilingFanBlockEntity fan)
        {
            fan.setPowered(!fan.isPowered());
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void setPowered(Level level, BlockPos pos, BlockState state, boolean powered)
    {
        if(level.getBlockEntity(pos) instanceof CeilingFanBlockEntity fan)
        {
            fan.setPowered(powered);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new CeilingFanBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return level.isClientSide() ? createTickerHelper(type, ElectronicsContent.CEILING_FAN_ENTITY.get(), CeilingFanBlockEntity::clientTick) : null;
    }
}
