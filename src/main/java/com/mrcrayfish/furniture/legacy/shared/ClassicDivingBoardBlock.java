package com.mrcrayfish.furniture.legacy.shared;

import com.mrcrayfish.furniture.block.DivingBoardBlock;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

/**
 * The diving board of the 1.12.2 version: same behaviour as the diving board (two parts, bounce on
 * the board), with the 1.12.2 models. The shapes follow those models: the board, and on the base
 * the two side rails.
 */
public class ClassicDivingBoardBlock extends DivingBoardBlock
{
    private static final Map<Direction, VoxelShape> BOARD = new EnumMap<>(Direction.class);
    private static final Map<Direction, VoxelShape> BASE = new EnumMap<>(Direction.class);

    static
    {
        for(Direction direction : Direction.Plane.HORIZONTAL)
        {
            /* Boxes of the divingboard_base model, facing north. The rails go past the block, they are cut at its edge */
            VoxelShape board = box(direction, 2, 4, 0, 14, 6, 16);
            VoxelShape rails = LegacyUtil.union(
                    box(direction, 0, 0, 1, 2, 16, 3), box(direction, 14, 0, 1, 16, 16, 3),
                    box(direction, 0, 0, 13, 2, 9, 15), box(direction, 14, 0, 13, 16, 9, 15),
                    box(direction, 0, 14, 3, 2, 16, 16), box(direction, 14, 14, 3, 16, 16, 16));
            BOARD.put(direction, board);
            BASE.put(direction, LegacyUtil.union(board, rails));
        }
    }

    public ClassicDivingBoardBlock(Properties properties)
    {
        super(properties);
    }

    /**
     * A box given for the north facing model, turned like the blockstate turns the model: y 0 for
     * north, 90 for east, 180 for south and 270 for west (clockwise seen from above).
     */
    private static VoxelShape box(Direction direction, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        return switch(direction)
        {
            case EAST -> Block.box(16 - z2, y1, x1, 16 - z1, y2, x2);
            case SOUTH -> Block.box(16 - x2, y1, 16 - z2, 16 - x1, y2, 16 - z1);
            case WEST -> Block.box(z1, y1, 16 - x2, z2, y2, 16 - x1);
            default -> Block.box(x1, y1, z1, x2, y2, z2);
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        Direction direction = state.getValue(DIRECTION);
        return (state.getValue(PART) == DivingBoardPart.BASE ? BASE : BOARD).get(direction);
    }
}
