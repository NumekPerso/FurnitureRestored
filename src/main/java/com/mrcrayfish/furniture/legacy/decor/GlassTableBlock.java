package com.mrcrayfish.furniture.legacy.decor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Glass table. Its frame stops at the edge of each block, which leaves a hole at an inner corner.
 * A corner property fills it when the two sides are connected and the diagonal is free. Placing or
 * breaking the diagonal changes the state of the two side tables, and that change is what updates
 * this block.
 */
public class GlassTableBlock extends ModernTableBlock
{
    public static final BooleanProperty CORNER_NORTH_WEST = BooleanProperty.create("corner_north_west");
    public static final BooleanProperty CORNER_NORTH_EAST = BooleanProperty.create("corner_north_east");
    public static final BooleanProperty CORNER_SOUTH_WEST = BooleanProperty.create("corner_south_west");
    public static final BooleanProperty CORNER_SOUTH_EAST = BooleanProperty.create("corner_south_east");

    public GlassTableBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(CORNER_NORTH_WEST, false).setValue(CORNER_NORTH_EAST, false).setValue(CORNER_SOUTH_WEST, false).setValue(CORNER_SOUTH_EAST, false));
    }

    @Override
    protected BlockState withConnections(BlockState state, BlockGetter level, BlockPos pos)
    {
        BlockState connected = super.withConnections(state, level, pos);
        boolean north = connected.getValue(FORWARD);
        boolean south = connected.getValue(BACK);
        boolean west = connected.getValue(LEFT);
        boolean east = connected.getValue(RIGHT);
        return connected.setValue(CORNER_NORTH_WEST, north && west && !level.getBlockState(pos.north().west()).is(this))
                .setValue(CORNER_NORTH_EAST, north && east && !level.getBlockState(pos.north().east()).is(this))
                .setValue(CORNER_SOUTH_WEST, south && west && !level.getBlockState(pos.south().west()).is(this))
                .setValue(CORNER_SOUTH_EAST, south && east && !level.getBlockState(pos.south().east()).is(this));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(CORNER_NORTH_WEST, CORNER_NORTH_EAST, CORNER_SOUTH_WEST, CORNER_SOUTH_EAST);
    }
}
