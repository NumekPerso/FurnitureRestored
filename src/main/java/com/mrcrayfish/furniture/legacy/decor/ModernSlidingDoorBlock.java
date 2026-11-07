package com.mrcrayfish.furniture.legacy.decor;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.IPowered;
import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Sliding door on two blocks. OPEN holds the door position and POWERED the last redstone signal
 * seen, so that only a change of signal moves the door and a door opened by hand stays open. The
 * block entity only animates the panel.
 */
public class ModernSlidingDoorBlock extends FurnitureHorizontalBlock implements EntityBlock, IPowered
{
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private static final Map<Direction, VoxelShape> BOTTOM = LegacyUtil.horizontalShapes(Direction.EAST, 6, 0, 0, 10, 32, 16);
    private static final Map<Direction, VoxelShape> BOTTOM_OPENED = LegacyUtil.horizontalShapes(Direction.EAST, 6, 0, 0, 10, 32, 2.96);
    private static final Map<Direction, VoxelShape> TOP = LegacyUtil.horizontalShapes(Direction.EAST, 6, -16, 0, 10, 16, 16);
    private static final Map<Direction, VoxelShape> TOP_OPENED = LegacyUtil.horizontalShapes(Direction.EAST, 6, -16, 0, 10, 16, 2.96);

    public ModernSlidingDoorBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false).setValue(POWERED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        boolean open = state.getValue(OPEN);
        Map<Direction, VoxelShape> shapes;
        if(state.getValue(HALF) == DoubleBlockHalf.UPPER)
        {
            shapes = open ? TOP_OPENED : TOP;
        }
        else
        {
            shapes = open ? BOTTOM_OPENED : BOTTOM;
        }
        return shapes.get(state.getValue(DIRECTION));
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    /**
     * The door can only be placed on the top of a block. The side of the block that was clicked
     * chooses between the two opposite directions, so two doors placed side by side face each other.
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if(context.getClickedFace() != Direction.UP || pos.getY() >= level.getMaxBuildHeight() - 1 || !level.getBlockState(pos.above()).canBeReplaced(context))
        {
            return null;
        }
        Direction facing = context.getHorizontalDirection();
        double hitX = context.getClickLocation().x - pos.getX();
        double hitZ = context.getClickLocation().z - pos.getZ();
        int stepX = facing.getStepX();
        int stepZ = facing.getStepZ();
        boolean side = stepX < 0 && hitZ < 0.5 || stepX > 0 && hitZ > 0.5 || stepZ < 0 && hitX > 0.5 || stepZ > 0 && hitX < 0.5;
        boolean powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
        return this.defaultBlockState().setValue(DIRECTION, side ? facing.getOpposite() : facing).setValue(OPEN, powered).setValue(POWERED, powered);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        DoubleBlockHalf half = state.getValue(HALF);
        if(direction.getAxis() == Direction.Axis.Y && (half == DoubleBlockHalf.LOWER) == (direction == Direction.UP))
        {
            return neighbour.is(this) && neighbour.getValue(HALF) != half ? state : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
    }

    /**
     * Only the lower half has drops: when one half is broken the game also breaks the other one.
     */
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? super.getDrops(state, builder) : Collections.emptyList();
    }

    /**
     * Breaking the upper half in creative mode would drop the item through the lower half.
     */
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        if(!level.isClientSide() && player.isCreative() && state.getValue(HALF) == DoubleBlockHalf.UPPER)
        {
            BlockPos below = pos.below();
            BlockState lower = level.getBlockState(below);
            if(lower.is(this) && lower.getValue(HALF) == DoubleBlockHalf.LOWER)
            {
                level.setBlock(below, Blocks.AIR.defaultBlockState(), Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
                level.levelEvent(player, 2001, below, Block.getId(lower));
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if(!level.isClientSide())
        {
            this.setPowered(level, pos, state, !state.getValue(OPEN));
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {
        if(level.isClientSide())
        {
            return;
        }
        BlockPos otherPos = getOtherHalf(state, pos);
        boolean signal = level.hasNeighborSignal(pos) || level.hasNeighborSignal(otherPos);
        if(signal != state.getValue(POWERED))
        {
            BlockState other = level.getBlockState(otherPos);
            if(other.is(this))
            {
                level.setBlock(otherPos, other.setValue(POWERED, signal), Block.UPDATE_CLIENTS);
            }
            BlockState updated = state.setValue(POWERED, signal);
            level.setBlock(pos, updated, Block.UPDATE_CLIENTS);
            this.setPowered(level, pos, updated, signal);
        }
    }

    /**
     * Moves this door, then the door next to it: it is what opens a pair of doors together.
     */
    @Override
    public void setPowered(Level level, BlockPos pos, BlockState state, boolean powered)
    {
        if(!state.is(this) || state.getValue(OPEN) == powered)
        {
            return;
        }
        level.setBlock(pos, state.setValue(OPEN, powered), Block.UPDATE_ALL);
        level.playSound(null, pos, powered ? LegacySounds.SLIDING_DOOR_OPEN.get() : LegacySounds.SLIDING_DOOR_CLOSE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        BlockPos otherPos = getOtherHalf(state, pos);
        BlockState other = level.getBlockState(otherPos);
        if(other.is(this))
        {
            level.setBlock(otherPos, other.setValue(OPEN, powered), Block.UPDATE_ALL);
        }

        BlockPos nextPos = otherPos.relative(state.getValue(DIRECTION).getClockWise());
        BlockState next = level.getBlockState(nextPos);
        if(next.getBlock() instanceof ModernSlidingDoorBlock door)
        {
            door.setPowered(level, nextPos, next, powered);
        }
    }

    private static BlockPos getOtherHalf(BlockState state, BlockPos pos)
    {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new ModernSlidingDoorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return level.isClientSide() ? createTickerHelper(type, DecorContent.MODERN_SLIDING_DOOR_ENTITY.get(), ModernSlidingDoorBlockEntity::clientTick) : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(HALF, OPEN, POWERED);
    }
}
