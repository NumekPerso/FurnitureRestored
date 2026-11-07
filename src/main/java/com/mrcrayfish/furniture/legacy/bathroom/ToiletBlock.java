package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.entity.SeatEntity;
import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Sit on it, or sneak to flush the items around it. The direction property points to the tank.
 */
public class ToiletBlock extends FurnitureHorizontalBlock
{
    private static final Map<Direction, VoxelShape> SHAPES = LegacyUtil.horizontalShapes(Direction.EAST, 1.6, 0, 2.4, 16, 16, 13.6);
    private static final Map<Direction, VoxelShape> COLLISIONS = new EnumMap<>(Direction.class);

    static
    {
        VoxelShape seat = Block.box(0, 0, 0, 16, 8, 16);
        LegacyUtil.horizontalShapes(Direction.EAST, 10.4, 8, 0, 16, 17.6, 16).forEach((direction, tank) -> COLLISIONS.put(direction, Shapes.or(tank, seat)));
    }

    public ToiletBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH));
    }

    /**
     * The condition of the fart key, checked on both sides: the player is seated on a toilet.
     */
    public static boolean isSeatedOnToilet(Player player)
    {
        return player.getVehicle() instanceof SeatEntity seat && player.level().getBlockState(seat.blockPosition()).is(BathroomContent.TOILET.get());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!level.isClientSide())
        {
            if(!player.isShiftKeyDown())
            {
                SeatEntity.create(level, pos, 0.4, player, state.getValue(DIRECTION));
                // A keybind component is resolved by the client, so it shows the key this player really uses
                player.sendSystemMessage(Component.translatable("furniture_restored.message.toilet", Component.keybind("key.furniture_restored.fart")).withStyle(ChatFormatting.YELLOW));
                level.updateNeighbourForOutputSignal(pos, this);
            }
            else
            {
                level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(1.0)).forEach(ItemEntity::discard);
                level.playSound(null, pos, LegacySounds.FLUSH.get(), SoundSource.BLOCKS, 0.75F, 1.0F);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
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
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES.get(state.getValue(DIRECTION));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return COLLISIONS.get(state.getValue(DIRECTION));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        for(SeatEntity seat : level.getEntitiesOfClass(SeatEntity.class, new AABB(pos)))
        {
            if(!seat.getPassengers().isEmpty())
            {
                return 1;
            }
        }
        return 0;
    }
}
