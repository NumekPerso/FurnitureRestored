package com.mrcrayfish.furniture.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Helpers shared by the legacy modules.
 */
public class LegacyUtil
{
    /**
     * Creates one shape per horizontal direction, as seen from the blockstate rotation.
     * Coordinates are in pixels, for the direction whose model has no y rotation.
     *
     * @param north the direction without rotation in the blockstate file
     */
    public static Map<Direction, VoxelShape> horizontalShapes(Direction north, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        Direction direction = north;
        double[] box = {x1, z1, x2, z2};
        for(int i = 0; i < 4; i++)
        {
            shapes.put(direction, Block.box(Math.min(box[0], box[2]), y1, Math.min(box[1], box[3]), Math.max(box[0], box[2]), y2, Math.max(box[1], box[3])));
            box = new double[]{16 - box[1], box[0], 16 - box[3], box[2]}; // 90 degrees clockwise
            direction = direction.getClockWise();
        }
        return shapes;
    }

    /**
     * Replaces the block with its other color when the player uses a dye, keeping every shared
     * property and the block entity data. Returns true if the dye was used.
     */
    public static boolean tryRecolor(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, Function<DyeColor, Block> blockForColor)
    {
        ItemStack stack = player.getItemInHand(hand);
        if(!(stack.getItem() instanceof DyeItem dye))
            return false;
        Block target = blockForColor.apply(dye.getDyeColor());
        if(target == null || target == state.getBlock())
            return false;
        if(!level.isClientSide())
        {
            BlockEntity old = level.getBlockEntity(pos);
            CompoundTag data = old != null ? old.saveWithoutMetadata() : null;
            if(old != null)
            {
                level.removeBlockEntity(pos); // Keeps the inventory from being dropped by onRemove
            }
            level.setBlock(pos, copyProperties(state, target.defaultBlockState()), Block.UPDATE_ALL);
            BlockEntity replaced = level.getBlockEntity(pos);
            if(replaced != null && data != null)
            {
                replaced.load(data);
                replaced.setChanged();
            }
            if(!player.getAbilities().instabuild)
            {
                stack.shrink(1);
            }
        }
        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static BlockState copyProperties(BlockState from, BlockState to)
    {
        for(Property property : from.getProperties())
        {
            if(to.hasProperty(property))
            {
                to = to.setValue(property, from.getValue(property));
            }
        }
        return to;
    }

    public static Block get(Map<DyeColor, RegistryObject<Block>> map, DyeColor color)
    {
        return map.get(color).get();
    }

    public static VoxelShape union(VoxelShape... shapes)
    {
        VoxelShape result = Shapes.empty();
        for(VoxelShape shape : shapes)
        {
            result = Shapes.or(result, shape);
        }
        return result;
    }
}
