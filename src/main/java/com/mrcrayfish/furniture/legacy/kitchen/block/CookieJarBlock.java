package com.mrcrayfish.furniture.legacy.kitchen.block;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.CookieJarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CookieJarBlock extends FurnitureBlock implements EntityBlock
{
    public static final IntegerProperty COOKIE_COUNT = IntegerProperty.create("cookie_count", 0, 6);

    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 10.4, 12);

    public CookieJarBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(COOKIE_COUNT, 0));
    }

    private static void spawnCookie(Level level, BlockPos pos)
    {
        level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, new ItemStack(Items.COOKIE)));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        int count = state.getValue(COOKIE_COUNT);
        ItemStack stack = player.getItemInHand(hand);
        if(stack.is(Items.COOKIE) && count < 6)
        {
            if(!level.isClientSide())
            {
                level.setBlock(pos, state.setValue(COOKIE_COUNT, count + 1), Block.UPDATE_ALL);
                HorizontalEntityBlock.consume(player, stack);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if(count > 0)
        {
            if(!level.isClientSide())
            {
                level.setBlock(pos, state.setValue(COOKIE_COUNT, count - 1), Block.UPDATE_ALL);
                spawnCookie(level, pos);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    /** Broken by a player: the cookies come out. */
    @Override
    public void destroy(LevelAccessor accessor, BlockPos pos, BlockState state)
    {
        if(accessor instanceof Level level && !level.isClientSide())
        {
            for(int i = 0; i < state.getValue(COOKIE_COUNT); i++)
            {
                spawnCookie(level, pos);
            }
        }
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        return state.getValue(COOKIE_COUNT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new CookieJarBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(COOKIE_COUNT);
    }
}
