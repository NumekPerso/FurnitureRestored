package com.mrcrayfish.furniture.legacy.event;

import net.minecraft.world.item.Item;

/**
 * An item that a player can sign from its screen.
 */
public interface IAuthored
{
    /** The item of the signed stack, which can be this item. */
    Item getSignedItem();
}
