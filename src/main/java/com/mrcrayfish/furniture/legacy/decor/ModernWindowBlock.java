package com.mrcrayfish.furniture.legacy.decor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.EnumMap;
import java.util.Map;

/**
 * Pane connected to its six neighbours: the pane_* properties select the frame, the properties
 * inherited from IronBarsBlock select the sides.
 */
public class ModernWindowBlock extends IronBarsBlock
{
    public static final BooleanProperty PANE_NORTH = BooleanProperty.create("pane_north");
    public static final BooleanProperty PANE_EAST = BooleanProperty.create("pane_east");
    public static final BooleanProperty PANE_SOUTH = BooleanProperty.create("pane_south");
    public static final BooleanProperty PANE_WEST = BooleanProperty.create("pane_west");
    public static final BooleanProperty PANE_UP = BooleanProperty.create("pane_up");
    public static final BooleanProperty PANE_DOWN = BooleanProperty.create("pane_down");

    private static final Map<Direction, BooleanProperty> PANES = new EnumMap<>(Direction.class);

    static
    {
        PANES.put(Direction.NORTH, PANE_NORTH);
        PANES.put(Direction.EAST, PANE_EAST);
        PANES.put(Direction.SOUTH, PANE_SOUTH);
        PANES.put(Direction.WEST, PANE_WEST);
        PANES.put(Direction.UP, PANE_UP);
        PANES.put(Direction.DOWN, PANE_DOWN);
    }

    public ModernWindowBlock(Properties properties)
    {
        super(properties);
        BlockState state = this.defaultBlockState();
        for(BooleanProperty pane : PANES.values())
        {
            state = state.setValue(pane, false);
        }
        this.registerDefaultState(state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = super.getStateForPlacement(context);
        for(Direction direction : Direction.values())
        {
            state = state.setValue(PANES.get(direction), attachesToBlock(level.getBlockState(pos.relative(direction))));
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos).setValue(PANES.get(direction), attachesToBlock(neighbour));
    }

    private static boolean attachesToBlock(BlockState state)
    {
        return state.getBlock() instanceof IronBarsBlock || state.getBlock() instanceof ModernSlidingDoorBlock;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PANE_NORTH, PANE_EAST, PANE_SOUTH, PANE_WEST, PANE_UP, PANE_DOWN);
    }
}
