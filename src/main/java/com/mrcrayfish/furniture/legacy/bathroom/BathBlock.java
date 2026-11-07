package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.entity.SeatEntity;
import com.mrcrayfish.furniture.legacy.LegacySounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

/**
 * A bath on two blocks. The bottom part is where it was placed, the top part (the one with the
 * taps) is in the direction of the property. Both parts always have the same water level, which is
 * only stored in the blockstate since the models need it there.
 */
public class BathBlock extends TwoPartBlock
{
    public static final int MAX_LEVEL = 16;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, MAX_LEVEL);

    /** A seat exactly on the block edge is not found by the check of SeatEntity#create, which would add a second one. */
    private static final double SEAT_OFFSET = 0.01;

    /** Outlines cover the whole bath, by direction of the other part. */
    private static final Map<Direction, VoxelShape> OUTLINES = new EnumMap<>(Direction.class);
    /** Collisions are a floor and the walls, except on the side of the other part. */
    private static final Map<Direction, VoxelShape> COLLISIONS = new EnumMap<>(Direction.class);

    static
    {
        OUTLINES.put(Direction.NORTH, Block.box(0, 0, -16, 16, 15, 16));
        OUTLINES.put(Direction.SOUTH, Block.box(0, 0, 0, 16, 15, 32));
        OUTLINES.put(Direction.WEST, Block.box(-16, 0, 0, 16, 15, 16));
        OUTLINES.put(Direction.EAST, Block.box(0, 0, 0, 32, 15, 16));

        Map<Direction, VoxelShape> walls = new EnumMap<>(Direction.class);
        walls.put(Direction.NORTH, Block.box(0, 2, 0, 16, 14, 2));
        walls.put(Direction.SOUTH, Block.box(0, 2, 14, 16, 14, 16));
        walls.put(Direction.WEST, Block.box(0, 2, 0, 2, 14, 16));
        walls.put(Direction.EAST, Block.box(14, 2, 0, 16, 14, 16));
        for(Direction open : walls.keySet())
        {
            VoxelShape shape = Block.box(0, 0, 0, 16, 2, 16);
            for(Map.Entry<Direction, VoxelShape> wall : walls.entrySet())
            {
                if(wall.getKey() != open)
                {
                    shape = Shapes.or(shape, wall.getValue());
                }
            }
            COLLISIONS.put(open, shape);
        }
    }

    public BathBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(PART, Part.BOTTOM).setValue(LEVEL, 0));
    }

    @Override
    protected Direction otherPartDirection(BlockState state)
    {
        Direction direction = state.getValue(DIRECTION);
        return state.getValue(PART) == Part.BOTTOM ? direction : direction.getOpposite();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        BlockPos otherPos = pos.relative(this.otherPartDirection(state));
        if(!level.getBlockState(otherPos).is(this))
            return InteractionResult.PASS;

        ItemStack held = player.getItemInHand(hand);
        int water = state.getValue(LEVEL);
        if(held.isEmpty() && !player.isShiftKeyDown())
        {
            return SeatEntity.create(level, pos, SEAT_OFFSET, player, state.getValue(DIRECTION));
        }
        if(held.is(Items.BUCKET))
        {
            if(water > 0)
            {
                this.exchangeWater(state, level, pos, otherPos, -1, SoundEvents.BUCKET_FILL, player, hand, held, new ItemStack(Items.WATER_BUCKET));
            }
        }
        else if(held.is(Items.WATER_BUCKET))
        {
            if(water < MAX_LEVEL)
            {
                this.exchangeWater(state, level, pos, otherPos, 1, SoundEvents.BUCKET_EMPTY, player, hand, held, new ItemStack(Items.BUCKET));
            }
        }
        else if(held.is(Items.GLASS_BOTTLE))
        {
            if(water > 0)
            {
                this.exchangeWater(state, level, pos, otherPos, -1, SoundEvents.BOTTLE_FILL, player, hand, held, BathroomUtil.waterBottle());
            }
        }
        else if(BathroomUtil.isWaterPotion(held))
        {
            if(water < MAX_LEVEL)
            {
                this.exchangeWater(state, level, pos, otherPos, 1, SoundEvents.BOTTLE_EMPTY, player, hand, held, BathroomUtil.emptiedPotion(held));
            }
        }
        else if(water < MAX_LEVEL)
        {
            this.fillFromTap(state, level, pos, otherPos, player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private void exchangeWater(BlockState state, Level level, BlockPos pos, BlockPos otherPos, int change, SoundEvent sound, Player player, InteractionHand hand, ItemStack held, ItemStack result)
    {
        if(level.isClientSide())
        {
            if(change > 0)
            {
                level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5, pos.getY() + 0.75 + state.getValue(LEVEL) * 0.0265, pos.getZ() + 0.5, 0.0, 0.1, 0.0);
            }
            return;
        }
        BathroomUtil.exchange(player, hand, held, result);
        this.setWaterLevel(state, level, pos, otherPos, state.getValue(LEVEL) + change);
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    /** Only the part with the taps can be filled from the source below it. */
    private void fillFromTap(BlockState state, Level level, BlockPos pos, BlockPos otherPos, Player player)
    {
        if(level.isClientSide())
            return;
        if(!BathroomUtil.hasWaterSource(level, pos))
        {
            player.sendSystemMessage(Component.translatable("furniture_restored.message.bath"));
        }
        else if(state.getValue(PART) == Part.TOP)
        {
            this.setWaterLevel(state, level, pos, otherPos, state.getValue(LEVEL) + 1);
            BathroomUtil.consumeWaterSource(level, pos);
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, LegacySounds.TAP.get(), SoundSource.BLOCKS, 0.75F, 0.8F);
        }
    }

    private void setWaterLevel(BlockState state, Level level, BlockPos pos, BlockPos otherPos, int water)
    {
        level.setBlock(pos, state.setValue(LEVEL, water), Block.UPDATE_ALL);
        level.setBlock(otherPos, level.getBlockState(otherPos).setValue(LEVEL, water), Block.UPDATE_ALL);
        level.updateNeighbourForOutputSignal(pos, this);
        level.updateNeighbourForOutputSignal(otherPos, this);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        boolean removed = !state.is(newState.getBlock());
        super.onRemove(state, level, pos, newState, isMoving);
        if(removed)
        {
            level.updateNeighbourForOutputSignal(pos, this);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return OUTLINES.get(this.otherPartDirection(state));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return COLLISIONS.get(this.otherPartDirection(state));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    /** The level goes up to 16 but a redstone signal stops at 15. */
    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        return Math.min(state.getValue(LEVEL), 15);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(LEVEL);
    }
}
