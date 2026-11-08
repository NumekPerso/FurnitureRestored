package com.mrcrayfish.furniture.legacy.event;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Reads and writes the content and the author in the NBT of a present, envelope or package, with this
 * structure: {Author: "name", Envelope: {Items: [{Slot: 0b, id: ..., Count: 1b}]}}
 */
public final class MailStorage
{
    private static final String AUTHOR = "Author";

    private MailStorage() {}

    public static NonNullList<ItemStack> read(ItemStack stack, MailLayout layout)
    {
        NonNullList<ItemStack> items = NonNullList.withSize(layout.size(), ItemStack.EMPTY);
        CompoundTag tag = stack.getTag();
        if(tag != null)
        {
            ContainerHelper.loadAllItems(tag.getCompound(layout.tagKey()), items);
        }
        return items;
    }

    public static void write(ItemStack stack, MailLayout layout, Container container)
    {
        NonNullList<ItemStack> items = NonNullList.withSize(layout.size(), ItemStack.EMPTY);
        for(int i = 0; i < items.size(); i++)
        {
            items.set(i, container.getItem(i));
        }
        stack.getOrCreateTag().put(layout.tagKey(), ContainerHelper.saveAllItems(new CompoundTag(), items, true));
    }

    /** Removes the content, keeping the author. */
    public static void clear(ItemStack stack, MailLayout layout)
    {
        CompoundTag tag = stack.getTag();
        if(tag != null)
        {
            tag.remove(layout.tagKey());
            if(tag.isEmpty())
            {
                stack.setTag(null);
            }
        }
    }

    public static boolean hasItems(ItemStack stack, MailLayout layout)
    {
        CompoundTag tag = stack.getTag();
        return tag != null && !tag.getCompound(layout.tagKey()).getList("Items", Tag.TAG_COMPOUND).isEmpty();
    }

    @Nullable
    public static String getAuthor(ItemStack stack)
    {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(AUTHOR, Tag.TAG_STRING) ? tag.getString(AUTHOR) : null;
    }

    public static boolean isSigned(ItemStack stack)
    {
        return getAuthor(stack) != null;
    }

    public static void setAuthor(ItemStack stack, String author)
    {
        stack.getOrCreateTag().putString(AUTHOR, author);
    }
}
