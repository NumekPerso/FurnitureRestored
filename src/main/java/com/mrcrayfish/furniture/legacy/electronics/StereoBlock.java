package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Map;

/**
 * A click plays the next music disc, a click while sneaking stops the music.
 */
public class StereoBlock extends FurnitureHorizontalBlock implements EntityBlock
{
    private static final Map<Direction, VoxelShape> SELECTION = LegacyUtil.horizontalShapes(Direction.EAST, 4.8, 0, 0, 11.2, 8, 16);
    /* Follows the model */
    private static final Map<Direction, VoxelShape> COLLISION = LegacyUtil.horizontalShapes(Direction.EAST, 4.8, 0, 0, 11.2, 7.2, 16);

    public StereoBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.NORTH));
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

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof StereoBlockEntity stereo)
        {
            if(!player.isShiftKeyDown())
            {
                List<Item> discs = getDiscs();
                if(!discs.isEmpty())
                {
                    int track = stereo.nextTrack(discs.size());
                    level.levelEvent(null, LevelEvent.SOUND_PLAY_JUKEBOX_SONG, pos, Item.getId(discs.get(track)));
                    LegacyTriggers.STEREO_ACTIVATED.trigger(player);
                }
            }
            else if(stereo.isPlaying())
            {
                stereo.stop();
                level.levelEvent(LevelEvent.SOUND_STOP_JUKEBOX_SONG, pos, 0);
                player.sendSystemMessage(Component.translatable("furniture_restored.message.stereo1"));
            }
            else
            {
                player.sendSystemMessage(Component.translatable("furniture_restored.message.stereo2"));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static List<Item> getDiscs()
    {
        return BuiltInRegistries.ITEM.getTag(ItemTags.MUSIC_DISCS).map(discs -> discs.stream().map(Holder::value).toList()).orElse(List.of());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if(!state.is(newState.getBlock()))
        {
            level.levelEvent(LevelEvent.SOUND_STOP_JUKEBOX_SONG, pos, 0);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    /** 0 when stopped, otherwise grows with the track number. */
    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        return level.getBlockEntity(pos) instanceof StereoBlockEntity stereo ? Math.min(stereo.getTrack() + 1, 15) : 0;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new StereoBlockEntity(pos, state);
    }
}
