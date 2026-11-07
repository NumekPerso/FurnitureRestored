package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Locale;

/**
 * A storage block with a chest screen. Stands side by side with the same direction connect.
 */
public class TvStandBlock extends FurnitureHorizontalBlock implements EntityBlock
{
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    public TvStandBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.NORTH).setValue(TYPE, Type.NONE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.withType(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        return direction.getAxis().isHorizontal() ? this.withType(state, level, pos) : state;
    }

    private BlockState withType(BlockState state, BlockGetter level, BlockPos pos)
    {
        Direction facing = state.getValue(DIRECTION);
        boolean right = this.isSameStand(level.getBlockState(pos.relative(facing.getClockWise())), facing);
        boolean left = this.isSameStand(level.getBlockState(pos.relative(facing.getCounterClockWise())), facing);
        Type type = left && right ? Type.BOTH : left ? Type.RIGHT : right ? Type.LEFT : Type.NONE;
        return state.setValue(TYPE, type);
    }

    private boolean isSameStand(BlockState state, Direction facing)
    {
        return state.is(this) && state.getValue(DIRECTION) == facing;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof TvStandBlockEntity stand)
        {
            player.openMenu(stand);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if(level.getBlockEntity(pos) instanceof TvStandBlockEntity stand)
        {
            stand.updateOpenerCount();
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new TvStandBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(TYPE);
    }

    public enum Type implements StringRepresentable
    {
        NONE, LEFT, RIGHT, BOTH;

        @Override
        public String getSerializedName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
