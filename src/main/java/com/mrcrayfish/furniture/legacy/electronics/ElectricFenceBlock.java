package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Zaps what touches it, except creative players. Creepers are charged instead. The post is hidden
 * in a straight line, which FenceBlock has no property for.
 */
public class ElectricFenceBlock extends FenceBlock
{
    public static final BooleanProperty POST = BooleanProperty.create("post");
    public static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("furniture_restored", "electric_fence"));

    private static final VoxelShape[] SELECTION = createShapes(16);
    private static final VoxelShape[] COLLISION = createShapes(24);

    public ElectricFenceBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(POST, true));
    }

    private static VoxelShape[] createShapes(double height)
    {
        VoxelShape center = box(7, 0, 7, 9, height, 9);
        VoxelShape north = box(7, 0, 0, 9, height, 7);
        VoxelShape east = box(9, 0, 7, 16, height, 9);
        VoxelShape south = box(7, 0, 9, 9, height, 16);
        VoxelShape west = box(0, 0, 7, 7, height, 9);
        VoxelShape[] shapes = new VoxelShape[16];
        for(int i = 0; i < shapes.length; i++)
        {
            shapes[i] = LegacyUtil.union(center, (i & 1) != 0 ? north : center, (i & 2) != 0 ? east : center, (i & 4) != 0 ? south : center, (i & 8) != 0 ? west : center);
        }
        return shapes;
    }

    private static int getShapeIndex(BlockState state)
    {
        return (state.getValue(NORTH) ? 1 : 0) | (state.getValue(EAST) ? 2 : 0) | (state.getValue(SOUTH) ? 4 : 0) | (state.getValue(WEST) ? 8 : 0);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SELECTION[getShapeIndex(state)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return COLLISION[getShapeIndex(state)];
    }

    @Override
    public boolean connectsTo(BlockState state, boolean sideSolid, Direction direction)
    {
        return state.getBlock() instanceof ElectricFenceBlock || super.connectsTo(state, sideSolid, direction);
    }

    private static BlockState withPost(BlockState state)
    {
        boolean north = state.getValue(NORTH);
        boolean east = state.getValue(EAST);
        boolean south = state.getValue(SOUTH);
        boolean west = state.getValue(WEST);
        boolean straight = north && !east && south && !west || !north && east && !south && west;
        return state.setValue(POST, !straight);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return withPost(super.getStateForPlacement(context));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        return withPost(super.updateShape(state, direction, neighbourState, level, pos, neighbourPos));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        if(placer instanceof Player player)
        {
            LegacyTriggers.PLACE_OUTDOOR_FURNITURE.trigger(player);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if(!(level instanceof ServerLevel serverLevel) || !(entity instanceof LivingEntity) || !entity.isAlive())
            return;

        if(entity instanceof Creeper creeper)
        {
            if(!creeper.isPowered())
            {
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
                if(bolt != null)
                {
                    bolt.moveTo(Vec3.atBottomCenterOf(pos));
                    creeper.setSecondsOnFire(1);
                    creeper.thunderHit(serverLevel, bolt);
                }
            }
        }
        else if(entity instanceof Player player)
        {
            if(!player.isCreative())
            {
                zap(serverLevel, pos, entity);
                LegacyTriggers.PLAYER_ZAPPED.trigger(player);
            }
        }
        else
        {
            zap(serverLevel, pos, entity);
        }
    }

    private static void zap(ServerLevel level, BlockPos pos, Entity entity)
    {
        entity.hurt(createDamageSource(level), 2.0F);
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, LegacySounds.ZAP.get(), SoundSource.BLOCKS, 0.2F, 1.0F);
        RandomSource random = level.getRandom();
        level.sendParticles(DustParticleOptions.REDSTONE, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble(), 1, 0, 0, 0, 0);
    }

    /** Vanilla always prefixes the message id with death.attack, so this key needs this override. */
    private static DamageSource createDamageSource(Level level)
    {
        Holder<DamageType> type = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE_TYPE);
        return new DamageSource(type)
        {
            @Override
            public Component getLocalizedDeathMessage(LivingEntity killed)
            {
                return Component.translatable("death.block.electric_fence", killed.getDisplayName());
            }
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(POST);
    }
}
