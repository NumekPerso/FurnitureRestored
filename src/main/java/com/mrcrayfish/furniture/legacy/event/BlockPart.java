package com.mrcrayfish.furniture.legacy.event;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * The two halves of a block standing on two spaces.
 */
public enum BlockPart implements StringRepresentable
{
    BOTTOM("bottom"),
    TOP("top");

    public static final EnumProperty<BlockPart> PROPERTY = EnumProperty.create("part", BlockPart.class);

    private final String name;

    BlockPart(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return this.name;
    }
}
