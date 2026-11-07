package com.mrcrayfish.furniture.legacy.decor;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

/**
 * Open and closed curtains as one block. The item places them open. Closed curtains block the light.
 */
public class CurtainsBlock extends FurnitureHorizontalBlock
{
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private static final Map<Direction, VoxelShape> SHAPES = LegacyUtil.horizontalShapes(Direction.EAST, 14, 0, 0, 16, 16, 16);

    public CurtainsBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(OPEN, true).setValue(TYPE, Type.DEFAULT));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES.get(state.getValue(DIRECTION));
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.getValue(OPEN) ? 0 : 15;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.getValue(OPEN);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if(!LegacyUtil.tryRecolor(state, level, pos, player, hand, color -> LegacyUtil.get(DecorContent.CURTAINS, color)) && !level.isClientSide())
        {
            level.setBlock(pos, state.setValue(OPEN, !state.getValue(OPEN)), Block.UPDATE_ALL);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if(placer instanceof Player player)
        {
            LegacyTriggers.PLACE_BLINDS_OR_CURTAINS.trigger(player);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        return state.setValue(TYPE, getType(state, context.getLevel(), context.getClickedPos()));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        return state.setValue(TYPE, getType(state, level, pos));
    }

    private static Type getType(BlockState state, BlockGetter level, BlockPos pos)
    {
        Direction facing = state.getValue(DIRECTION);
        BlockState left = level.getBlockState(pos.relative(facing.getCounterClockWise()));
        BlockState right = level.getBlockState(pos.relative(facing.getClockWise()));
        boolean leftOpen = isCurtain(left, facing, true);
        boolean rightOpen = isCurtain(right, facing, true);
        boolean leftClosed = isCurtain(left, facing, false);
        boolean rightClosed = isCurtain(right, facing, false);
        if(rightOpen)
        {
            return leftOpen ? Type.NONE : leftClosed ? Type.LEFT_CLOSED : Type.RIGHT_OPEN;
        }
        if(leftOpen)
        {
            return rightClosed ? Type.RIGHT_CLOSED : Type.LEFT_OPEN;
        }
        if(rightClosed)
        {
            return leftClosed ? Type.BOTH : Type.RIGHT;
        }
        return leftClosed ? Type.LEFT : Type.DEFAULT;
    }

    private static boolean isCurtain(BlockState other, Direction facing, boolean open)
    {
        return other.getBlock() instanceof CurtainsBlock && other.getValue(DIRECTION) == facing && other.getValue(OPEN) == open;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN, TYPE);
    }

    public enum Type implements StringRepresentable
    {
        DEFAULT, LEFT, RIGHT, LEFT_OPEN, RIGHT_OPEN, LEFT_CLOSED, RIGHT_CLOSED, BOTH, NONE;

        @Override
        public String getSerializedName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
