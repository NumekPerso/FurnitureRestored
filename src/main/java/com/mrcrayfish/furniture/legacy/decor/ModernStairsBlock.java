package com.mrcrayfish.furniture.legacy.decor;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * Stairs connected to the stairs above and below them. These are diagonal neighbours, which do not
 * receive shape updates, so the connections are refreshed when a stair is placed or removed.
 */
public class ModernStairsBlock extends FurnitureHorizontalBlock
{
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private static final Map<Direction, VoxelShape> SHAPES = createShapes();

    public ModernStairsBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(TYPE, Type.NONE));
    }

    private static Map<Direction, VoxelShape> createShapes()
    {
        Map<Direction, VoxelShape> top = LegacyUtil.horizontalShapes(Direction.EAST, 8, 0, 0, 16, 16, 16);
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        LegacyUtil.horizontalShapes(Direction.EAST, 0, 0, 0, 16, 8, 16).forEach((direction, bottom) -> shapes.put(direction, Shapes.or(bottom, top.get(direction))));
        return shapes;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES.get(state.getValue(DIRECTION));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        return state.setValue(TYPE, this.getType(state, context.getLevel(), context.getClickedPos()));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        super.onPlace(state, level, pos, oldState, isMoving);
        if(!oldState.is(this) || oldState.getValue(DIRECTION) != state.getValue(DIRECTION))
        {
            this.updateDiagonals(level, pos, state.getValue(DIRECTION));
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        super.onRemove(state, level, pos, newState, isMoving);
        if(!newState.is(this) || newState.getValue(DIRECTION) != state.getValue(DIRECTION))
        {
            this.updateDiagonals(level, pos, state.getValue(DIRECTION));
        }
    }

    private void updateDiagonals(Level level, BlockPos pos, Direction facing)
    {
        this.updateConnection(level, pos.above().relative(facing), facing);
        this.updateConnection(level, pos.below().relative(facing.getOpposite()), facing);
    }

    private void updateConnection(Level level, BlockPos pos, Direction facing)
    {
        BlockState state = level.getBlockState(pos);
        if(state.is(this) && state.getValue(DIRECTION) == facing)
        {
            BlockState updated = state.setValue(TYPE, this.getType(state, level, pos));
            if(updated != state)
            {
                level.setBlock(pos, updated, Block.UPDATE_ALL);
            }
        }
    }

    private Type getType(BlockState state, BlockGetter level, BlockPos pos)
    {
        Direction facing = state.getValue(DIRECTION);
        boolean up = this.isStair(level.getBlockState(pos.above().relative(facing)), facing);
        boolean down = this.isStair(level.getBlockState(pos.below().relative(facing.getOpposite())), facing);
        if(up && down)
        {
            return Type.BOTH;
        }
        return up ? Type.DOWN : down ? Type.UP : Type.NONE;
    }

    private boolean isStair(BlockState other, Direction facing)
    {
        return other.is(this) && other.getValue(DIRECTION) == facing;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(TYPE);
    }

    public enum Type implements StringRepresentable
    {
        NONE, UP, DOWN, BOTH;

        @Override
        public String getSerializedName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
