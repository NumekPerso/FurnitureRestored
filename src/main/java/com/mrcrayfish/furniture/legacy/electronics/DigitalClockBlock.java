package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Shows the time in 24 hour format. The case has one block per color, a dye only changes the color
 * of the text, which the block entity stores.
 */
public class DigitalClockBlock extends FurnitureHorizontalBlock implements EntityBlock
{
    private static final Map<Direction, VoxelShape> SELECTION = LegacyUtil.horizontalShapes(Direction.EAST, 5, 0, 3, 10, 5, 13);
    private static final Map<Direction, VoxelShape> COLLISION = LegacyUtil.horizontalShapes(Direction.EAST, 6, 0, 4, 9, 4, 12);

    public DigitalClockBlock(Properties properties)
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
        ItemStack stack = player.getItemInHand(hand);
        if(!(stack.getItem() instanceof DyeItem dye))
        {
            return InteractionResult.PASS;
        }
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof DigitalClockBlockEntity clock && clock.getTextColor() != dye.getDyeColor())
        {
            clock.setTextColor(dye.getDyeColor());
            if(!player.getAbilities().instabuild)
            {
                stack.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag)
    {
        ElectronicsClient.addInfo(tooltip, "furniture_restored.digital_clock.info", null);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new DigitalClockBlockEntity(pos, state);
    }
}
