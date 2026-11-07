package com.mrcrayfish.furniture.legacy.decor;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import com.mrcrayfish.furniture.legacy.shared.CoffeeTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;

/**
 * Modern coffee table, connected to the same block on its four sides.
 */
public class ModernCoffeeTableBlock extends FurnitureBlock implements EntityBlock
{
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;

    private static final VoxelShape TOP = Block.box(0, 6.5, 0, 16, 8, 16);
    private static final VoxelShape LEG_SOUTH_WEST = Block.box(0, 0, 14.4, 1.6, 6.5, 16);
    private static final VoxelShape LEG_NORTH_WEST = Block.box(0, 0, 0, 1.6, 6.5, 1.6);
    private static final VoxelShape LEG_SOUTH_EAST = Block.box(14.4, 0, 14.4, 16, 6.5, 16);
    private static final VoxelShape LEG_NORTH_EAST = Block.box(14.4, 0, 0, 16, 6.5, 1.6);

    private final Map<BlockState, VoxelShape> shapes = new HashMap<>();

    public ModernCoffeeTableBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false));
        for(BlockState state : this.getStateDefinition().getPossibleStates())
        {
            this.shapes.put(state, createShape(state));
        }
    }

    private static VoxelShape createShape(BlockState state)
    {
        boolean north = state.getValue(NORTH);
        boolean east = state.getValue(EAST);
        boolean south = state.getValue(SOUTH);
        boolean west = state.getValue(WEST);
        VoxelShape shape = TOP;
        if(!north && !west) shape = Shapes.or(shape, LEG_NORTH_WEST);
        if(!north && !east) shape = Shapes.or(shape, LEG_NORTH_EAST);
        if(!south && !west) shape = Shapes.or(shape, LEG_SOUTH_WEST);
        if(!south && !east) shape = Shapes.or(shape, LEG_SOUTH_EAST);
        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return this.shapes.get(state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!(level.getBlockEntity(pos) instanceof CoffeeTableBlockEntity table))
            return InteractionResult.PASS;

        ItemStack placed = table.getItem();
        if(!placed.isEmpty())
        {
            if(!level.isClientSide())
            {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, placed));
                table.setItem(ItemStack.EMPTY, 0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        ItemStack held = player.getItemInHand(hand);
        if(held.isEmpty())
            return InteractionResult.PASS;

        if(!level.isClientSide())
        {
            table.setItem(held.copyWithCount(1), player.getDirection().get2DDataValue());
            if(!player.getAbilities().instabuild)
            {
                held.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if(state.getBlock() != newState.getBlock() && level.getBlockEntity(pos) instanceof CoffeeTableBlockEntity table)
        {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), table.getItem());
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new CoffeeTableBlockEntity(pos, state);
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

    private BlockState withConnections(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.setValue(NORTH, level.getBlockState(pos.north()).is(this))
                .setValue(EAST, level.getBlockState(pos.east()).is(this))
                .setValue(SOUTH, level.getBlockState(pos.south()).is(this))
                .setValue(WEST, level.getBlockState(pos.west()).is(this));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, EAST, SOUTH, WEST);
    }
}
