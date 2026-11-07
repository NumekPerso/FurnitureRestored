package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * Hangs from a ceiling and scans for fire in a 9x9x9 cube around it. While on, the siren loops and
 * the ceiling block is powered. A click turns it off, it starts again if the fire is still there.
 */
public class FireAlarmBlock extends FurnitureHorizontalBlock
{
    public static final BooleanProperty ON = BooleanProperty.create("on");

    private static final VoxelShape SHAPE = box(5, 14, 5, 11, 16, 11);
    private static final int SCAN_TICKS = 10;
    private static final int SIREN_TICKS = 34;

    public FireAlarmBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.NORTH).setValue(ON, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return context.getClickedFace() == Direction.DOWN ? super.getStateForPlacement(context) : null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return !level.getBlockState(pos.above()).isAir();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        return direction == Direction.UP && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        if(placer instanceof Player player)
        {
            LegacyTriggers.PLACE_APPLIANCE.trigger(player);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!state.getValue(ON))
        {
            return InteractionResult.PASS;
        }
        if(!level.isClientSide())
        {
            level.setBlock(pos, state.setValue(ON, false), Block.UPDATE_ALL);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        // A tick that is already scheduled is kept, and reads the new state when it runs
        level.scheduleTick(pos, this, state.getValue(ON) ? SIREN_TICKS : 1);
        if(oldState.is(this) && oldState.getValue(ON) != state.getValue(ON))
        {
            level.updateNeighborsAt(pos.above(), this);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        super.onRemove(state, level, pos, newState, isMoving);
        if(!state.is(newState.getBlock()) && state.getValue(ON))
        {
            level.updateNeighborsAt(pos.above(), this);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if(state.getValue(ON))
        {
            playSiren(level, pos);
            level.scheduleTick(pos, this, SIREN_TICKS);
        }
        else if(isFireNearby(level, pos))
        {
            playSiren(level, pos);
            level.setBlock(pos, state.setValue(ON, true), Block.UPDATE_ALL);
        }
        else
        {
            level.scheduleTick(pos, this, SCAN_TICKS);
        }
    }

    private static boolean isFireNearby(Level level, BlockPos pos)
    {
        for(BlockPos scanPos : BlockPos.betweenClosed(pos.offset(-4, -4, -4), pos.offset(4, 4, 4)))
        {
            if(level.getBlockState(scanPos).is(BlockTags.FIRE))
            {
                return true;
            }
        }
        return false;
    }

    private static void playSiren(Level level, BlockPos pos)
    {
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, LegacySounds.FIRE_ALARM.get(), SoundSource.BLOCKS, 5.0F, 1.0F);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
    {
        return direction == Direction.DOWN && state.getValue(ON) ? 15 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(ON);
    }
}
