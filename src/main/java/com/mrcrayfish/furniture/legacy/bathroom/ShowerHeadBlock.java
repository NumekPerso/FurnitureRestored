package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Turned on and off by hand. The block entity only ticks while it is on.
 */
public class ShowerHeadBlock extends WallMountedBlock implements EntityBlock
{
    public static final BooleanProperty ON = BooleanProperty.create("on");

    private static final Map<Direction, VoxelShape> SHAPES = LegacyUtil.horizontalShapes(Direction.EAST, 5.6, 2.4, 5.6, 16, 7.2, 10.4);

    public ShowerHeadBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(ON, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!level.isClientSide())
        {
            boolean on = !state.getValue(ON);
            level.setBlock(pos, state.setValue(ON, on), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, on ? 0.6F : 0.5F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES.get(state.getValue(DIRECTION));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state)
    {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new ShowerHeadBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        if(!state.getValue(ON))
            return null;
        if(level.isClientSide())
            return createTickerHelper(type, BathroomContent.SHOWER_HEAD_BLOCK_ENTITY.get(), ShowerHeadBlockEntity::clientTick);
        return createTickerHelper(type, BathroomContent.SHOWER_HEAD_BLOCK_ENTITY.get(), ShowerHeadBlockEntity::serverTick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(ON);
    }
}
