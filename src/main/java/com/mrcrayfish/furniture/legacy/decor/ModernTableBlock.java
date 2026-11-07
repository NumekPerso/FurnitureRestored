package com.mrcrayfish.furniture.legacy.decor;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Table with a leg in each free corner. Only tables of the same block connect. Forward is north,
 * back south, left west and right east.
 */
public class ModernTableBlock extends FurnitureBlock
{
    public static final BooleanProperty BACK = BooleanProperty.create("back");
    public static final BooleanProperty FORWARD = BooleanProperty.create("forward");
    public static final BooleanProperty LEFT = BooleanProperty.create("left");
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");

    private static final VoxelShape TOP = Block.box(0, 14, 0, 16, 16, 16);
    private static final VoxelShape LEG_LOW_X_HIGH_Z = Block.box(0.25, 0, 13.75, 2.25, 14, 15.75);
    private static final VoxelShape LEG_HIGH_X_HIGH_Z = Block.box(13.75, 0, 13.75, 15.75, 14, 15.75);
    private static final VoxelShape LEG_LOW_X_LOW_Z = Block.box(0.25, 0, 0.25, 2.25, 14, 2.25);
    private static final VoxelShape LEG_HIGH_X_LOW_Z = Block.box(13.75, 0, 0.25, 15.75, 14, 2.25);

    private final Map<BlockState, VoxelShape> shapes = new HashMap<>();

    public ModernTableBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(BACK, false).setValue(FORWARD, false).setValue(LEFT, false).setValue(RIGHT, false));
        for(BlockState state : this.getStateDefinition().getPossibleStates())
        {
            this.shapes.put(state, createShape(state));
        }
    }

    private static VoxelShape createShape(BlockState state)
    {
        boolean north = state.getValue(FORWARD);
        boolean south = state.getValue(BACK);
        boolean west = state.getValue(LEFT);
        boolean east = state.getValue(RIGHT);
        VoxelShape shape = TOP;
        if(!north && !west) shape = Shapes.or(shape, LEG_LOW_X_LOW_Z);
        if(!north && !east) shape = Shapes.or(shape, LEG_HIGH_X_LOW_Z);
        if(!south && !west) shape = Shapes.or(shape, LEG_LOW_X_HIGH_Z);
        if(!south && !east) shape = Shapes.or(shape, LEG_HIGH_X_HIGH_Z);
        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return this.shapes.get(state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if(placer instanceof Player player)
        {
            LegacyTriggers.PLACE_TABLE.trigger(player);
            LegacyTriggers.PLACE_CHAIR_OR_TABLE.trigger(player);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.withConnections(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        return this.withConnections(state, level, pos);
    }

    protected BlockState withConnections(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.setValue(BACK, level.getBlockState(pos.south()).is(this))
                .setValue(FORWARD, level.getBlockState(pos.north()).is(this))
                .setValue(LEFT, level.getBlockState(pos.west()).is(this))
                .setValue(RIGHT, level.getBlockState(pos.east()).is(this));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BACK, FORWARD, LEFT, RIGHT);
    }
}
