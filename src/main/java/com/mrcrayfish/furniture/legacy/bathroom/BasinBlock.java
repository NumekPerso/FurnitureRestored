package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Holds one unit of water: filled and emptied with buckets and bottles, or filled from the water
 * source two blocks below, which it uses up.
 */
public class BasinBlock extends FurnitureHorizontalBlock
{
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");

    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static
    {
        VoxelShape top = Block.box(0, 11, 0, 16, 16, 16);
        LegacyUtil.horizontalShapes(Direction.EAST, 8, 0, 3, 16, 11, 13).forEach((direction, pillar) -> SHAPES.put(direction, Shapes.or(pillar, top)));
    }

    public BasinBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(FILLED, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        ItemStack held = player.getItemInHand(hand);
        boolean filled = state.getValue(FILLED);
        if(held.is(Items.BUCKET))
        {
            if(filled)
            {
                this.exchangeWater(state, level, pos, false, SoundEvents.BUCKET_FILL, player, hand, held, new ItemStack(Items.WATER_BUCKET), false);
            }
        }
        else if(held.is(Items.WATER_BUCKET))
        {
            if(!filled)
            {
                this.exchangeWater(state, level, pos, true, SoundEvents.BUCKET_EMPTY, player, hand, held, new ItemStack(Items.BUCKET), true);
            }
        }
        else if(held.is(Items.GLASS_BOTTLE))
        {
            if(filled)
            {
                this.exchangeWater(state, level, pos, false, SoundEvents.BOTTLE_FILL, player, hand, held, BathroomUtil.waterBottle(), true);
            }
        }
        else if(BathroomUtil.isWaterPotion(held))
        {
            if(!filled)
            {
                this.exchangeWater(state, level, pos, true, SoundEvents.BOTTLE_EMPTY, player, hand, held, BathroomUtil.emptiedPotion(held), true);
            }
        }
        else if(!filled)
        {
            this.fillFromTap(state, level, pos, player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private void exchangeWater(BlockState state, Level level, BlockPos pos, boolean filled, SoundEvent sound, Player player, InteractionHand hand, ItemStack held, ItemStack result, boolean splash)
    {
        if(level.isClientSide())
        {
            if(splash)
            {
                this.createSplashParticles(level, pos);
            }
            return;
        }
        BathroomUtil.exchange(player, hand, held, result);
        level.setBlock(pos, state.setValue(FILLED, filled), Block.UPDATE_ALL);
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.updateNeighbourForOutputSignal(pos, this);
    }

    private void fillFromTap(BlockState state, Level level, BlockPos pos, Player player)
    {
        if(BathroomUtil.hasWaterSource(level, pos))
        {
            if(level.isClientSide())
            {
                this.createSplashParticles(level, pos);
                return;
            }
            level.setBlock(pos, state.setValue(FILLED, true), Block.UPDATE_ALL);
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, LegacySounds.TAP.get(), SoundSource.BLOCKS, 0.75F, 1.0F);
            BathroomUtil.consumeWaterSource(level, pos);
            level.updateNeighbourForOutputSignal(pos, this);
        }
        else if(!level.isClientSide())
        {
            player.sendSystemMessage(Component.translatable("furniture_restored.message.basin"));
        }
    }

    private void createSplashParticles(Level level, BlockPos pos)
    {
        for(int i = 0; i < 5; i++)
        {
            level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.3 + 0.4 * level.random.nextDouble(), pos.getY() + 1.05, pos.getZ() + 0.3 + 0.4 * level.random.nextDouble(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if(placer != null)
        {
            BathroomUtil.triggerPlaced(placer);
        }
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
        return SHAPES.get(state.getValue(DIRECTION));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        return state.getValue(FILLED) ? 1 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FILLED);
    }
}
