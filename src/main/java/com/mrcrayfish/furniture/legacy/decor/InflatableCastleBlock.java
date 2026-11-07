package com.mrcrayfish.furniture.legacy.decor;

import com.mrcrayfish.furniture.legacy.LegacySounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

/**
 * Bouncy block without fall damage. Castles of every color connect together.
 */
public class InflatableCastleBlock extends Block
{
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    public InflatableCastleBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(TYPE, Type.NONE));
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance)
    {
        if(entity instanceof LivingEntity)
        {
            float height = entity.fallDistance;
            if(height > 0 && !entity.isSuppressingBounce())
            {
                height = Math.min(height, 4);
                Vec3 motion = entity.getDeltaMovement();
                entity.setDeltaMovement(motion.x, Math.sqrt(0.22 * (height + 1)), motion.z);
                level.playSound(null, pos, LegacySounds.BOUNCE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                level.addParticle(ParticleTypes.ENTITY_EFFECT, entity.getX(), entity.getY(), entity.getZ(), 1.0, 1.0, 1.0);
            }
            entity.fallDistance = 0;
        }
    }

    /**
     * Keeps the speed given by fallOn. The vanilla method would cancel the bounce.
     */
    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity)
    {
        Vec3 motion = entity.getDeltaMovement();
        if(motion.y < 0.0)
        {
            entity.setDeltaMovement(motion.x, 0.0, motion.z);
        }
    }

    @Override
    public boolean addLandingEffects(BlockState state, ServerLevel level, BlockPos pos, BlockState otherState, LivingEntity entity, int particles)
    {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(TYPE, getType(context.getLevel(), context.getClickedPos()));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        return state.setValue(TYPE, getType(level, pos));
    }

    private static Type getType(BlockGetter level, BlockPos pos)
    {
        boolean north = level.getBlockState(pos.north()).getBlock() instanceof InflatableCastleBlock;
        boolean east = level.getBlockState(pos.east()).getBlock() instanceof InflatableCastleBlock;
        boolean south = level.getBlockState(pos.south()).getBlock() instanceof InflatableCastleBlock;
        boolean west = level.getBlockState(pos.west()).getBlock() instanceof InflatableCastleBlock;
        if(!north && !east && !south && !west) return Type.NONE;
        if(!north && !east && south && !west) return Type.NORTH;
        if(north && !east && !south && !west) return Type.SOUTH;
        if(!north && !east && !south && west) return Type.EAST;
        if(!north && east && !south && !west) return Type.WEST;
        if(north && !east && south && !west) return Type.HORIZONTAL_NORTH_SOUTH;
        if(!north && east && !south && west) return Type.HORIZONTAL_EAST_WEST;
        if(!north && east && south && !west) return Type.CORNER_EAST_SOUTH;
        if(!north && !east && south && west) return Type.CORNER_WEST_SOUTH;
        if(north && east && !south && !west) return Type.CORNER_NORTH_EAST;
        if(north && !east && !south && west) return Type.CORNER_NORTH_WEST;
        if(!north && east && south && west) return Type.THREE_NORTH;
        if(north && !east && south && west) return Type.THREE_EAST;
        if(north && east && !south && west) return Type.THREE_SOUTH;
        if(north && east && south && !west) return Type.THREE_WEST;
        return Type.CENTER;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(TYPE);
    }

    public enum Type implements StringRepresentable
    {
        NONE, NORTH, EAST, SOUTH, WEST,
        HORIZONTAL_EAST_WEST, HORIZONTAL_NORTH_SOUTH,
        CORNER_EAST_SOUTH, CORNER_WEST_SOUTH, CORNER_NORTH_EAST, CORNER_NORTH_WEST,
        THREE_NORTH, THREE_EAST, THREE_SOUTH, THREE_WEST,
        CENTER;

        @Override
        public String getSerializedName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
