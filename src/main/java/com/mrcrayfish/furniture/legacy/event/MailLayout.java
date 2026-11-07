package com.mrcrayfish.furniture.legacy.event;

import com.mrcrayfish.furniture.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

/**
 * What differs between the three items holding an inventory: where the items are stored in the NBT
 * of the stack, the slots of the menu and the texts of the screen.
 */
public enum MailLayout
{
    PRESENT("Present", "present", null, "furniture_restored.button.wrap", -60, "furniture_restored.message.present_empty", () -> EventContent.PRESENT_MENU.get(), new int[][]{{71, 16}, {89, 16}, {71, 34}, {89, 34}}),
    ENVELOPE("Envelope", "envelope", "container.furniture_restored.envelope", "furniture_restored.button.sign", -50, "furniture_restored.message.envelope_empty", () -> EventContent.ENVELOPE_MENU.get(), new int[][]{{71, 18}, {89, 18}}),
    PACKAGE("Package", "package", "container.furniture_restored.package", "furniture_restored.button.sign", -50, "furniture_restored.message.package_empty", () -> EventContent.PACKAGE_MENU.get(), new int[][]{{62, 15}, {62, 33}, {80, 15}, {80, 33}, {98, 15}, {98, 33}});

    private final String tagKey;
    private final ResourceLocation texture;
    private final String titleKey;
    private final String buttonKey;
    private final int buttonY;
    private final String emptyMessage;
    private final Supplier<MenuType<MailItemMenu>> menuType;
    private final int[][] slots;

    MailLayout(String tagKey, String textureName, String titleKey, String buttonKey, int buttonY, String emptyMessage, Supplier<MenuType<MailItemMenu>> menuType, int[][] slots)
    {
        this.tagKey = tagKey;
        this.texture = new ResourceLocation(Reference.MOD_ID, "textures/gui/legacy/" + textureName + ".png");
        this.titleKey = titleKey;
        this.buttonKey = buttonKey;
        this.buttonY = buttonY;
        this.emptyMessage = emptyMessage;
        this.menuType = menuType;
        this.slots = slots;
    }

    public String tagKey()
    {
        return this.tagKey;
    }

    public ResourceLocation texture()
    {
        return this.texture;
    }

    /** The screen of a present has no title. */
    public Component title()
    {
        return this.titleKey == null ? Component.empty() : Component.translatable(this.titleKey);
    }

    public String buttonKey()
    {
        return this.buttonKey;
    }

    /** Vertical position of the sign button, from the middle of the screen. */
    public int buttonY()
    {
        return this.buttonY;
    }

    public String emptyMessage()
    {
        return this.emptyMessage;
    }

    public MenuType<MailItemMenu> menuType()
    {
        return this.menuType.get();
    }

    public int size()
    {
        return this.slots.length;
    }

    public int slotX(int index)
    {
        return this.slots[index][0];
    }

    public int slotY(int index)
    {
        return this.slots[index][1];
    }
}
