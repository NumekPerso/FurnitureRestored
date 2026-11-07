package com.mrcrayfish.furniture.legacy.event;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Connects to the chimney above it. Only the top of a stack smokes.
 */
public class ChimneyBlock extends FurnitureBlock
{
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    public ChimneyBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(TYPE, Type.TOP));
    }

    private Type typeUnder(BlockState above)
    {
        return above.is(this) ? Type.BOTTOM : Type.TOP;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(TYPE, this.typeUnder(context.getLevel().getBlockState(context.getClickedPos().above())));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        return direction == Direction.UP ? state.setValue(TYPE, this.typeUnder(neighbor)) : state;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if(state.getValue(TYPE) != Type.TOP)
            return;
        for(int i = 0; i < 2; i++)
        {
            double x = pos.getX() + 0.25 + 0.5 * random.nextDouble();
            double z = pos.getZ() + 0.25 + 0.5 * random.nextDouble();
            level.addParticle(ParticleTypes.SMOKE, x, pos.getY() + 0.9, z, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.LARGE_SMOKE, x, pos.getY() + 0.9, z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(TYPE);
    }

    public enum Type implements StringRepresentable
    {
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        Type(String name)
        {
            this.name = name;
        }

        @Override
        public String getSerializedName()
        {
            return this.name;
        }
    }
}
