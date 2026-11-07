package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A block on two positions, kept as one class with a part property. Like a vanilla bed, only one
 * part has drops (the bottom) and removing a part removes the other one through updateShape, so a
 * single loot table gives exactly one item however the block is broken.
 */
public abstract class TwoPartBlock extends FurnitureHorizontalBlock
{
    public enum Part implements StringRepresentable
    {
        BOTTOM("bottom"), TOP("top");

        private final String name;

        Part(String name)
        {
            this.name = name;
        }

        @Override
        public String getSerializedName()
        {
            return this.name;
        }
    }

    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    public TwoPartBlock(Properties properties)
    {
        super(properties);
    }

    /** The direction from this part to the other one. */
    protected abstract Direction otherPartDirection(BlockState state);

    /** Where the other part of this one is, or will be. */
    public BlockPos getOtherPartPos(BlockState state, BlockPos pos)
    {
        return pos.relative(this.otherPartDirection(state));
    }

    /** Checks the position of the other part, for a bottom state about to be placed. */
    public boolean canPlaceOtherPart(BlockPlaceContext context, BlockState state)
    {
        Level level = context.getLevel();
        BlockPos other = context.getClickedPos().relative(this.otherPartDirection(state));
        return !level.isOutsideBuildHeight(other) && level.getWorldBorder().isWithinBounds(other) && level.getBlockState(other).canBeReplaced(context);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if(!level.isClientSide())
        {
            level.setBlock(pos.relative(this.otherPartDirection(state)), state.setValue(PART, Part.TOP), Block.UPDATE_ALL);
        }
        if(placer != null)
        {
            BathroomUtil.triggerPlaced(placer);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        if(direction == this.otherPartDirection(state) && !(neighbor.is(this) && neighbor.getValue(PART) != state.getValue(PART)))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        // Breaking the top makes the bottom drop through updateShape, unless it is removed first here
        if(!level.isClientSide() && state.getValue(PART) == Part.TOP && (player.isCreative() || !player.hasCorrectToolForDrops(state)))
        {
            BlockPos bottom = pos.relative(this.otherPartDirection(state));
            BlockState bottomState = level.getBlockState(bottom);
            if(bottomState.is(this))
            {
                level.setBlock(bottom, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                level.levelEvent(player, 2001, bottom, Block.getId(bottomState));
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return state.getValue(PART) == Part.BOTTOM ? super.getDrops(state, builder) : List.of();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }
}
