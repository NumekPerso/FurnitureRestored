package com.mrcrayfish.furniture.legacy.decor;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.entity.SeatEntity;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Modern chair: a comparator reads 1 when someone is sitting.
 */
public class ModernChairBlock extends FurnitureHorizontalBlock
{
    private static final VoxelShape COMMON = LegacyUtil.union(
            Block.box(1.6, 8, 1.6, 14.4, 9.6, 14.4),
            Block.box(1.6, 0, 12.8, 3.2, 8, 14.4),
            Block.box(12.8, 0, 12.8, 14.4, 8, 14.4),
            Block.box(12.8, 0, 1.6, 14.4, 8, 3.2),
            Block.box(1.6, 0, 1.6, 3.2, 8, 3.2));
    private static final Map<Direction, VoxelShape> SHAPES = createShapes();

    public ModernChairBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH));
    }

    private static Map<Direction, VoxelShape> createShapes()
    {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        LegacyUtil.horizontalShapes(Direction.EAST, 13.2, 9.6, 1.6, 14.4, 19.2, 14.4).forEach((direction, backrest) -> shapes.put(direction, Shapes.or(COMMON, backrest)));
        return shapes;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES.get(state.getValue(DIRECTION));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if(player.isShiftKeyDown())
        {
            return InteractionResult.PASS;
        }
        InteractionResult result = SeatEntity.create(level, pos, 6 * 0.0625, player, state.getValue(DIRECTION));
        if(!level.isClientSide())
        {
            level.updateNeighbourForOutputSignal(pos, this);
        }
        return result;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if(placer instanceof Player player)
        {
            LegacyTriggers.PLACE_CHAIR.trigger(player);
            LegacyTriggers.PLACE_CHAIR_OR_TABLE.trigger(player);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        return level.getEntitiesOfClass(SeatEntity.class, new AABB(pos)).isEmpty() ? 0 : 1;
    }
}
