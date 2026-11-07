package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

/**
 * A shower cabin on two blocks, open on the side facing the player who placed it. Standing in it
 * under a running shower head gives the advancement.
 */
public class ShowerBlock extends TwoPartBlock
{
    private static final VoxelShape OUTLINE_BOTTOM = Block.box(0, 0, 0, 16, 32, 16);
    private static final VoxelShape OUTLINE_TOP = Block.box(0, -16, 0, 16, 16, 16);

    /** By direction of the block. */
    private static final Map<Direction, VoxelShape> COLLISIONS_TOP = new EnumMap<>(Direction.class);
    private static final Map<Direction, VoxelShape> COLLISIONS_BOTTOM = new EnumMap<>(Direction.class);

    static
    {
        Map<Direction, VoxelShape> walls = new EnumMap<>(Direction.class);
        walls.put(Direction.NORTH, Block.box(0, 0, 0, 16, 16, 1));
        walls.put(Direction.SOUTH, Block.box(0, 0, 15, 16, 16, 16));
        walls.put(Direction.WEST, Block.box(0, 0, 0, 1, 16, 16));
        walls.put(Direction.EAST, Block.box(15, 0, 0, 16, 16, 16));
        VoxelShape floor = Block.box(0, 0, 0, 16, 1, 16);
        for(Direction direction : walls.keySet())
        {
            VoxelShape shape = Shapes.empty();
            for(Map.Entry<Direction, VoxelShape> wall : walls.entrySet())
            {
                if(wall.getKey() != direction.getOpposite())
                {
                    shape = Shapes.or(shape, wall.getValue());
                }
            }
            COLLISIONS_TOP.put(direction, shape);
            COLLISIONS_BOTTOM.put(direction, Shapes.or(shape, floor));
        }
    }

    public ShowerBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(PART, Part.BOTTOM));
    }

    @Override
    protected Direction otherPartDirection(BlockState state)
    {
        return state.getValue(PART) == Part.BOTTOM ? Direction.UP : Direction.DOWN;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        return state != null && this.canPlaceOtherPart(context, state) ? state : null;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if(entity instanceof Player player)
        {
            BlockState head = level.getBlockState(pos.above(state.getValue(PART) == Part.TOP ? 1 : 2));
            if(head.is(BathroomContent.SHOWER_HEAD.get()) && head.getValue(ShowerHeadBlock.ON))
            {
                LegacyTriggers.PLAYER_SHOWER.trigger(player);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(PART) == Part.BOTTOM ? OUTLINE_BOTTOM : OUTLINE_TOP;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return (state.getValue(PART) == Part.BOTTOM ? COLLISIONS_BOTTOM : COLLISIONS_TOP).get(state.getValue(DIRECTION));
    }
}
