package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import com.mrcrayfish.furniture.legacy.IPowered;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

/**
 * Hangs from a ceiling. Works by clicks or by redstone, a click while sneaking changes the mode.
 */
public class CeilingLightBlock extends FurnitureBlock implements IPowered
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);

    private static final VoxelShape SHAPE = box(5, 6.4, 5, 11, 16, 11);

    public CeilingLightBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(MODE, Mode.RIGHT_CLICK));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return context.getClickedFace() == Direction.DOWN ? this.defaultBlockState() : null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return !level.getBlockState(pos.above()).isAir();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        return direction == Direction.UP && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!level.isClientSide())
        {
            if(player.isShiftKeyDown())
            {
                boolean toRedstone = state.getValue(MODE) == Mode.RIGHT_CLICK;
                level.setBlock(pos, state.setValue(MODE, toRedstone ? Mode.REDSTONE : Mode.RIGHT_CLICK), Block.UPDATE_ALL);
                player.sendSystemMessage(Component.translatable(toRedstone ? "furniture_restored.message.ceiling_light.mode1" : "furniture_restored.message.ceiling_light.mode2"));
            }
            else if(state.getValue(MODE) == Mode.RIGHT_CLICK)
            {
                level.setBlock(pos, state.setValue(LIT, !state.getValue(LIT)), Block.UPDATE_ALL);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {
        if(!level.isClientSide() && state.getValue(MODE) == Mode.REDSTONE)
        {
            boolean powered = level.hasNeighborSignal(pos);
            if(powered != state.getValue(LIT))
            {
                level.setBlock(pos, state.setValue(LIT, powered), Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    public void setPowered(Level level, BlockPos pos, BlockState state, boolean powered)
    {
        level.setBlock(pos, state.setValue(LIT, powered), Block.UPDATE_ALL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(LIT, MODE);
    }

    public enum Mode implements StringRepresentable
    {
        RIGHT_CLICK, REDSTONE;

        @Override
        public String getSerializedName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
