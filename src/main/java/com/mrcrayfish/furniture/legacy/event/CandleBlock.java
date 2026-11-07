package com.mrcrayfish.furniture.legacy.event;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CandleBlock extends FurnitureBlock
{
    private static final VoxelShape OUTLINE = Block.box(1, 0, 1, 15, 20.2, 15);
    private static final VoxelShape COLLISION = LegacyUtil.union(
            Block.box(4, 0, 4, 12, 1, 12),
            Block.box(5, 1, 5, 11, 2, 11),
            Block.box(7, 2, 7, 9, 7, 9),
            Block.box(2, 7, 7, 14, 9, 9),
            Block.box(7, 7, 2, 9, 9, 14),
            Block.box(1, 9, 6, 5, 17, 10),
            Block.box(6, 9, 1, 10, 17, 5),
            Block.box(11, 9, 6, 15, 17, 10),
            Block.box(6, 9, 11, 10, 17, 15),
            Block.box(6, 9, 6, 10, 20, 10));
    private static final double PIXEL = 0.0625;
    /* x, y and z offsets of the five flames */
    private static final double[][] FLAMES = {
            {7.95 * PIXEL, 1.45, 7.95 * PIXEL},
            {7.95 * PIXEL, 1.2, 12.95 * PIXEL},
            {7.95 * PIXEL, 1.2, 2.95 * PIXEL},
            {12.95 * PIXEL, 1.2, 7.95 * PIXEL},
            {2.95 * PIXEL, 1.2, 7.95 * PIXEL}
    };

    public CandleBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return OUTLINE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return COLLISION;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        for(double[] flame : FLAMES)
        {
            level.addParticle(ParticleTypes.FLAME, pos.getX() + flame[0], pos.getY() + flame[1], pos.getZ() + flame[2], 0.0, 0.0, 0.0);
        }
        if(random.nextBoolean())
        {
            double xRandom = 0.001 * (random.nextInt(4) - 2);
            double zRandom = 0.001 * (random.nextInt(4) - 2);
            for(double[] flame : FLAMES)
            {
                level.addParticle(ParticleTypes.SMOKE, pos.getX() + flame[0], pos.getY() + flame[1], pos.getZ() + flame[2], xRandom + (random.nextInt(3) - 1) * 0.01, 0.01, zRandom + (random.nextInt(3) - 1) * 0.01);
            }
        }
    }
}
