package com.mrcrayfish.furniture.legacy.event;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/**
 * Built with up to four chopped logs (stage 0 to 3), lit with flint and steel once complete and put
 * out with a left click.
 */
public class FirePitBlock extends FurnitureBlock
{
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final int LAST_STAGE = 3;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 3, 16);
    /* A single chopped log lies along the x axis, see the fire_pit_0 model */
    private static final VoxelShape SINGLE_LOG_SHAPE = Block.box(0, 0, 6.5, 16, 3, 9.5);

    public FirePitBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(STAGE, 0).setValue(LIT, false));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        ItemStack held = player.getItemInHand(hand);
        if(state.getValue(LIT) || !(held.getItem() instanceof FlintAndSteelItem))
            return InteractionResult.PASS;
        if(!level.isClientSide() && state.getValue(STAGE) == LAST_STAGE)
        {
            level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            level.setBlock(pos, state.setValue(LIT, true), Block.UPDATE_ALL);
            held.hurtAndBreak(1, player, entity -> entity.broadcastBreakEvent(hand));
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player)
    {
        if(!level.isClientSide() && state.getValue(LIT))
        {
            level.setBlock(pos, state.setValue(LIT, false).setValue(STAGE, LAST_STAGE), Block.UPDATE_ALL);
        }
    }

    /* A pit being built gives its logs back. A lit pit drops itself through its loot table */
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        if(state.getValue(LIT))
        {
            return super.getDrops(state, builder);
        }
        return List.of(new ItemStack(EventContent.CHOPPED_LOG.get(), state.getValue(STAGE) + 1));
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state)
    {
        return new ItemStack(EventContent.CHOPPED_LOG.get());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if(!state.getValue(LIT))
            return;
        if(random.nextInt(24) == 0)
        {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
        }
        for(int i = 0; i < 2; i++)
        {
            double x = pos.getX() + random.nextDouble() * 0.8 + 0.2;
            double y = pos.getY() + random.nextDouble() * 0.5 + 0.5;
            double z = pos.getZ() + random.nextDouble() * 0.8 + 0.2;
            level.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(STAGE) == 0 && !state.getValue(LIT) ? SINGLE_LOG_SHAPE : SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(STAGE, LIT);
    }
}
