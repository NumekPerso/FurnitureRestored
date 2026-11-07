package com.mrcrayfish.furniture.legacy.event;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Christmas tree on two spaces, each part holding one ornament per side. Shears remove the top part
 * only, the bottom part can stay alone. Only the bottom part drops the item.
 */
public class TreeBlock extends FurnitureHorizontalBlock implements EntityBlock
{
    public static final EnumProperty<BlockPart> PART = BlockPart.PROPERTY;
    private static final VoxelShape OUTLINE_BOTTOM = Block.box(1, 0, 1, 15, 32, 15);
    private static final VoxelShape OUTLINE_BOTTOM_ALONE = Block.box(1, 0, 1, 15, 16, 15);
    private static final VoxelShape OUTLINE_TOP = Block.box(1, -16, 1, 15, 16, 15);
    private static final VoxelShape COLLISION = Block.box(1, 0, 1, 15, 16, 15);

    public TreeBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(PART, BlockPart.BOTTOM));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if(pos.getY() >= level.getMaxBuildHeight() - 1 || !level.getBlockState(pos.above()).canBeReplaced(context))
            return null;
        return super.getStateForPlacement(context);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        level.setBlock(pos.above(), state.setValue(PART, BlockPart.TOP), Block.UPDATE_ALL);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        if(state.getValue(PART) == BlockPart.TOP && direction == Direction.DOWN && !neighbor.is(this))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof TreeBlockEntity tree)
        {
            ItemStack held = player.getItemInHand(hand);
            tree.setOrnament(player.getDirection(), held);
            if(!held.isEmpty() && !player.getAbilities().instabuild)
            {
                held.shrink(1);
            }
            BlockEntityUtil.sendUpdatePacket(tree);
        }
        return InteractionResult.SUCCESS;
    }

    /* The shears lose their durability in ItemStack#mineBlock, there is nothing more to do for them here */
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        if(!level.isClientSide())
        {
            if(state.getValue(PART) == BlockPart.BOTTOM)
            {
                BlockPos above = pos.above();
                if(level.getBlockState(above).is(this))
                {
                    level.destroyBlock(above, false, player);
                }
            }
            else if(!player.getMainHandItem().is(Items.SHEARS))
            {
                BlockPos below = pos.below();
                if(level.getBlockState(below).is(this))
                {
                    level.destroyBlock(below, !player.isCreative(), player);
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return state.getValue(PART) == BlockPart.BOTTOM ? super.getDrops(state, builder) : Collections.emptyList();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if(!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof TreeBlockEntity tree)
        {
            Containers.dropContents(level, pos, tree.getOrnaments());
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        if(state.getValue(PART) == BlockPart.TOP)
        {
            return OUTLINE_TOP;
        }
        return level.getBlockState(pos.above()).is(this) ? OUTLINE_BOTTOM : OUTLINE_BOTTOM_ALONE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return COLLISION;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new TreeBlockEntity(pos, state);
    }
}
