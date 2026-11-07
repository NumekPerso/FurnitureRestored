package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * A wooden button with a bell. The direction points at the wall it is attached to.
 */
public class DoorBellBlock extends FurnitureHorizontalBlock
{
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private static final Map<Direction, VoxelShape> SHAPES = LegacyUtil.horizontalShapes(Direction.EAST, 13.6, 4.8, 6.4, 16, 11.2, 9.6);
    private static final int PRESS_TICKS = 30;

    public DoorBellBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.NORTH).setValue(POWERED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES.get(state.getValue(DIRECTION));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction face = context.getClickedFace();
        return face.getAxis().isHorizontal() ? this.defaultBlockState().setValue(DIRECTION, face.getOpposite()) : null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        Direction facing = state.getValue(DIRECTION);
        BlockPos wall = pos.relative(facing);
        return level.getBlockState(wall).isFaceSturdy(level, wall, facing.getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        return direction == state.getValue(DIRECTION) && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!level.isClientSide() && !state.getValue(POWERED))
        {
            setPowered(state, level, pos, true);
            level.playSound(null, pos, LegacySounds.DOOR_BELL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            level.scheduleTick(pos, this, PRESS_TICKS);
            LegacyTriggers.CLICKED_DOORBELL.trigger(player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if(state.getValue(POWERED))
        {
            this.checkArrows(state, level, pos);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if(!level.isClientSide() && !state.getValue(POWERED))
        {
            this.checkArrows(state, level, pos);
        }
    }

    /** An arrow stuck in the button keeps it pressed. */
    private void checkArrows(BlockState state, Level level, BlockPos pos)
    {
        AABB box = state.getShape(level, pos).bounds().move(pos);
        boolean arrow = !level.getEntitiesOfClass(AbstractArrow.class, box).isEmpty();
        boolean powered = state.getValue(POWERED);
        if(arrow != powered)
        {
            setPowered(state, level, pos, arrow);
            level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, arrow ? 0.6F : 0.5F);
        }
        if(arrow)
        {
            level.scheduleTick(pos, this, PRESS_TICKS);
        }
    }

    private static void setPowered(BlockState state, Level level, BlockPos pos, boolean powered)
    {
        level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_ALL);
        level.updateNeighborsAt(getStronglyPoweredPos(state, pos), state.getBlock());
    }

    /** The block opposite the wall. */
    private static BlockPos getStronglyPoweredPos(BlockState state, BlockPos pos)
    {
        return pos.relative(state.getValue(DIRECTION).getOpposite());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        boolean removed = !state.is(newState.getBlock());
        super.onRemove(state, level, pos, newState, isMoving);
        if(removed && !isMoving && state.getValue(POWERED))
        {
            level.updateNeighborsAt(getStronglyPoweredPos(state, pos), this);
        }
    }

    @Override
    public boolean isSignalSource(BlockState state)
    {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
    {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
    {
        return state.getValue(POWERED) && state.getValue(DIRECTION) == direction ? 15 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }
}
