package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Controls the blocks linked with its item. The direction points at the wall it is attached to.
 * The drop keeps the list of links.
 */
public class LightSwitchBlock extends FurnitureHorizontalBlock implements EntityBlock
{
    public static final BooleanProperty ON = BooleanProperty.create("on");

    private static final Map<Direction, VoxelShape> SELECTION = LegacyUtil.horizontalShapes(Direction.EAST, 14, 3, 4, 16, 13, 12);
    private static final Map<Direction, VoxelShape> COLLISION = LegacyUtil.horizontalShapes(Direction.EAST, 15, 4, 5, 16, 12, 11);

    public LightSwitchBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.NORTH).setValue(ON, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SELECTION.get(state.getValue(DIRECTION));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return COLLISION.get(state.getValue(DIRECTION));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction face = context.getClickedFace();
        return face.getAxis().isHorizontal() ? this.defaultBlockState().setValue(DIRECTION, face.getOpposite()) : null;
    }

    /** Only checked when placing: the switch stays if the wall goes away. */
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        Direction facing = state.getValue(DIRECTION);
        BlockPos wall = pos.relative(facing);
        return level.getBlockState(wall).isFaceSturdy(level, wall, facing.getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!(level.getBlockEntity(pos) instanceof LightSwitchBlockEntity lightSwitch))
        {
            return InteractionResult.PASS;
        }
        if(!level.isClientSide())
        {
            lightSwitch.setState(!state.getValue(ON));
            level.playSound(null, pos, LegacySounds.LIGHT_SWITCH.get(), SoundSource.BLOCKS, 1.0F, 0.9F + level.getRandom().nextFloat() * 0.1F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        List<ItemStack> drops = super.getDrops(state, builder);
        if(builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof LightSwitchBlockEntity lightSwitch && lightSwitch.hasLights())
        {
            for(ItemStack drop : drops)
            {
                if(drop.is(this.asItem()))
                {
                    drop.getOrCreateTag().put(LightSwitchItem.BLOCK_ENTITY_TAG, lightSwitch.saveWithoutMetadata());
                }
            }
        }
        return drops;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new LightSwitchBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(ON);
    }
}
