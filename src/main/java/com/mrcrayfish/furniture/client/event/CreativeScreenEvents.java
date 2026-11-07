package com.mrcrayfish.furniture.client.event;

import com.google.common.collect.Lists;
import com.mrcrayfish.furniture.FurnitureMod;
import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.client.ClientHandler;
import com.mrcrayfish.furniture.client.gui.widget.button.IconButton;
import com.mrcrayfish.furniture.client.gui.widget.button.TagButton;
import com.mrcrayfish.furniture.common.ModTags;
import com.mrcrayfish.furniture.core.ModBlocks;
import com.mrcrayfish.furniture.core.ModCreativeTabs;
import com.mrcrayfish.furniture.core.ModItems;
import com.mrcrayfish.furniture.item.CreativeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class CreativeScreenEvents
{
    private static final ResourceLocation ICONS = new ResourceLocation(Reference.MOD_ID, "textures/gui/icons.png");
    private static final Field SCROLL_OFFS = ObfuscationReflectionHelper.findField(CreativeModeInventoryScreen.class, "f_98508_");
    private static final int ROW_SIZE = 9;
    private static final int VISIBLE_ROWS = 5;
    private static final int GRID_LEFT = 8;
    private static final int GRID_TOP = 17;
    private static final int CELL_SIZE = 18;
    private static final int SEARCH_FRAME_LEFT = 80;
    private static final int SEARCH_FRAME_NEW_LEFT = 109;
    private static final int SEARCH_FRAME_TOP = 4;
    private static final int SEARCH_FRAME_BOTTOM = 15;
    private static int startIndex;
    private static CreativeModeTab lastTab = CreativeModeTabs.getDefaultTab();

    private List<TagFilter> filters;
    private List<TagButton> buttons;
    private Button btnScrollUp;
    private Button btnScrollDown;
    private Button btnEnableAll;
    private Button btnDisableAll;
    private EditBox searchBox;
    private String lastSearch = "";
    private int layoutSize = -1;
    private final Map<Integer, TagFilter> bannerRows = new HashMap<>();
    private int guiCenterX = 0;
    private int guiCenterY = 0;

    @SubscribeEvent
    public void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event)
    {
        this.filters = null;
    }

    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init.Post event)
    {
        if(event.getScreen() instanceof CreativeModeInventoryScreen creativeScreen)
        {
            if(this.filters == null)
            {
                this.compileItems();
            }

            this.guiCenterX = creativeScreen.getGuiLeft();
            this.guiCenterY = creativeScreen.getGuiTop();
            this.searchBox = event.getListenersList().stream().filter(EditBox.class::isInstance).map(EditBox.class::cast).findFirst().orElse(null);
            this.buttons = this.createTagButtons();
            this.buttons.forEach(event::addListener);

            event.addListener(this.btnScrollUp = new IconButton(this.guiCenterX - 22, this.guiCenterY - 12, button -> {
                if(startIndex > 0) startIndex--;
                this.updateTagButtons();
            }, ICONS, 64, 0));

            event.addListener(this.btnScrollDown = new IconButton(this.guiCenterX - 22, this.guiCenterY + 127, button -> {
                if(startIndex <= filters.size() - 4 - 1) startIndex++;
                this.updateTagButtons();
            }, ICONS, 80, 0));

            event.addListener(this.btnEnableAll = new IconButton(this.guiCenterX - 50, this.guiCenterY + 10, button -> {
                this.filters.forEach(filters -> filters.setEnabled(true));
                this.buttons.forEach(TagButton::updateState);
                Screen screen = Minecraft.getInstance().screen;
                if(screen instanceof CreativeModeInventoryScreen)
                {
                    this.updateItems((CreativeModeInventoryScreen) screen);
                }
            }, ICONS, 96, 0));
            this.btnEnableAll.setTooltip(Tooltip.create(Component.translatable("gui.button.furniture_restored.enable_filters")));

            event.addListener(this.btnDisableAll = new IconButton(this.guiCenterX - 50, this.guiCenterY + 32, button -> {
                this.filters.forEach(filters -> filters.setEnabled(false));
                this.buttons.forEach(TagButton::updateState);
                Screen screen = Minecraft.getInstance().screen;
                if(screen instanceof CreativeModeInventoryScreen)
                {
                    this.updateItems((CreativeModeInventoryScreen) screen);
                }
            }, ICONS, 112, 0));
            this.btnDisableAll.setTooltip(Tooltip.create(Component.translatable("gui.button.furniture_restored.disable_filters")));

            this.onSwitchCreativeTab(CreativeModeInventoryScreen.selectedTab, creativeScreen);
        }
    }

    /**
     * The screen fills the tab with its own search results, without the filters. Applies them again
     * as soon as the text changes, before the frame is drawn.
     */
    @SubscribeEvent
    public void onScreenDrawPre(ScreenEvent.Render.Pre event)
    {
        if(event.getScreen() instanceof CreativeModeInventoryScreen creativeScreen && CreativeModeInventoryScreen.selectedTab == ModCreativeTabs.MAIN.get())
        {
            boolean searchChanged = this.searchBox != null && !this.searchBox.getValue().equals(this.lastSearch);
            if(searchChanged || creativeScreen.getMenu().items.size() != this.layoutSize)
            {
                this.updateItems(creativeScreen);
            }
        }
    }

    @SubscribeEvent
    public void onScreenDrawPost(ScreenEvent.Render.Post event)
    {
        if(event.getScreen() instanceof CreativeModeInventoryScreen creativeScreen)
        {
            this.guiCenterX = creativeScreen.getGuiLeft();
            this.guiCenterY = creativeScreen.getGuiTop();

            CreativeModeTab tab = CreativeModeInventoryScreen.selectedTab;
            if(lastTab != tab)
            {
                this.onSwitchCreativeTab(tab, creativeScreen);
                lastTab = tab;
            }
        }
    }

    private void onSwitchCreativeTab(CreativeModeTab tab, CreativeModeInventoryScreen screen)
    {
        if(tab == ModCreativeTabs.MAIN.get())
        {
            this.btnScrollUp.visible = true;
            this.btnScrollDown.visible = true;
            this.btnEnableAll.visible = true;
            this.btnDisableAll.visible = true;
            this.updateTagButtons();
            this.updateItems(screen);
        }
        else
        {
            this.btnScrollUp.visible = false;
            this.btnScrollDown.visible = false;
            this.btnEnableAll.visible = false;
            this.btnDisableAll.visible = false;
            this.buttons.forEach(button -> button.visible = false);
        }
    }

    private List<TagButton> createTagButtons()
    {
        List<TagButton> tagButtons = new ArrayList<>();
        for(TagFilter filter : this.filters)
        {
            TagButton tagButton = new TagButton(this.guiCenterX - 28, this.guiCenterY, filter, button -> {
                Screen screen = Minecraft.getInstance().screen;
                if(screen instanceof CreativeModeInventoryScreen creativeScreen)
                {
                    this.updateItems(creativeScreen);
                }
            });
            tagButton.visible = false;
            tagButtons.add(tagButton);
        }
        return tagButtons;
    }

    private void updateTagButtons()
    {
        this.buttons.forEach(button -> button.visible = false);
        for(int i = startIndex; i < startIndex + 4 && i < this.buttons.size(); i++)
        {
            TagButton button = this.buttons.get(i);
            button.setY(this.guiCenterY + 29 * (i - startIndex) + 11);
            button.visible = true;
        }
        this.btnScrollUp.active = startIndex > 0;
        this.btnScrollDown.active = startIndex <= this.filters.size() - 4 - 1;
    }

    /**
     * Fills the tab one filter after the other: a row left empty for the banner, then the items of the
     * filter. An item in several filters only shows under the first enabled one.
     */
    private void updateItems(CreativeModeInventoryScreen screen)
    {
        CreativeModeInventoryScreen.ItemPickerMenu menu = screen.getMenu();
        this.lastSearch = this.searchBox != null ? this.searchBox.getValue() : "";
        String search = this.lastSearch.trim().toLowerCase(Locale.ROOT);

        this.bannerRows.clear();
        NonNullList<ItemStack> newItems = NonNullList.create();
        Set<Item> placedItems = new HashSet<>();
        for(TagFilter filter : this.filters)
        {
            if(!filter.isEnabled())
                continue;

            NonNullList<ItemStack> stacks = NonNullList.create();
            for(Item item : filter.getItems())
            {
                if(placedItems.contains(item))
                    continue;

                NonNullList<ItemStack> variants = NonNullList.create();
                if(item instanceof CreativeItem creativeItem)
                {
                    creativeItem.fill(variants::add);
                }
                else
                {
                    variants.add(new ItemStack(item));
                }
                if(!search.isEmpty())
                {
                    variants.removeIf(stack -> !matchesSearch(stack, search));
                }
                if(!variants.isEmpty())
                {
                    placedItems.add(item);
                    stacks.addAll(variants);
                }
            }
            if(stacks.isEmpty())
                continue;

            stacks.sort(Comparator.comparingInt(o -> Item.getId(o.getItem())));
            this.bannerRows.put(newItems.size() / ROW_SIZE, filter);
            for(int i = 0; i < ROW_SIZE; i++)
            {
                newItems.add(ItemStack.EMPTY);
            }
            newItems.addAll(stacks);
            while(newItems.size() % ROW_SIZE != 0)
            {
                newItems.add(ItemStack.EMPTY);
            }
        }

        menu.items.clear();
        menu.items.addAll(newItems);
        this.layoutSize = menu.items.size();
        setScroll(screen, 0F);
        menu.scrollTo(0);
    }

    private static void setScroll(CreativeModeInventoryScreen screen, float scroll)
    {
        try
        {
            SCROLL_OFFS.setFloat(screen, scroll);
        }
        catch(IllegalAccessException e)
        {
            throw new IllegalStateException(e);
        }
    }

    /**
     * First visible row, as ItemPickerMenu#scrollTo works it out from the position of the scroll bar.
     */
    private static int getFirstVisibleRow(CreativeModeInventoryScreen screen)
    {
        try
        {
            int hiddenRows = Mth.positiveCeilDiv(screen.getMenu().items.size(), ROW_SIZE) - VISIBLE_ROWS;
            return Math.max((int) ((double) (SCROLL_OFFS.getFloat(screen) * (float) hiddenRows) + 0.5D), 0);
        }
        catch(IllegalAccessException e)
        {
            throw new IllegalStateException(e);
        }
    }

    /**
     * The frame of the search bar is part of the texture of the tab and starts under the name of the
     * mod. Paints its left part over with the colour of the screen and draws its left edge again.
     */
    @SubscribeEvent
    public void onScreenDrawBackground(ContainerScreenEvent.Render.Background event)
    {
        if(!(event.getContainerScreen() instanceof CreativeModeInventoryScreen creativeScreen) || CreativeModeInventoryScreen.selectedTab != ModCreativeTabs.MAIN.get())
            return;

        GuiGraphics graphics = event.getGuiGraphics();
        int top = creativeScreen.getGuiTop();
        int edge = creativeScreen.getGuiLeft() + SEARCH_FRAME_NEW_LEFT;
        graphics.fill(creativeScreen.getGuiLeft() + SEARCH_FRAME_LEFT, top + SEARCH_FRAME_TOP, edge, top + SEARCH_FRAME_BOTTOM + 1, 0xFFC6C6C6);
        graphics.fill(edge, top + SEARCH_FRAME_TOP, edge + 1, top + SEARCH_FRAME_BOTTOM, 0xFF555555);
        graphics.fill(edge, top + SEARCH_FRAME_BOTTOM, edge + 1, top + SEARCH_FRAME_BOTTOM + 1, 0xFF8B8B8B);
    }

    /**
     * Drawn after the slots, which also hides their highlight, and before the tooltips. The coordinates
     * are relative to the top left corner of the screen.
     */
    @SubscribeEvent
    public void onScreenDrawForeground(ContainerScreenEvent.Render.Foreground event)
    {
        if(!(event.getContainerScreen() instanceof CreativeModeInventoryScreen creativeScreen))
            return;
        if(CreativeModeInventoryScreen.selectedTab != ModCreativeTabs.MAIN.get() || this.bannerRows.isEmpty())
            return;

        int firstRow = getFirstVisibleRow(creativeScreen);
        for(int visibleRow = 0; visibleRow < VISIBLE_ROWS; visibleRow++)
        {
            TagFilter filter = this.bannerRows.get(firstRow + visibleRow);
            if(filter != null)
            {
                drawBanner(event.getGuiGraphics(), filter, GRID_LEFT, GRID_TOP + visibleRow * CELL_SIZE);
            }
        }
    }

    /**
     * A tiled block texture of the game, so that it follows the resource pack, with the icon and the
     * name of the filter. The black outline keeps the name readable on the bright textures.
     */
    private static void drawBanner(GuiGraphics graphics, TagFilter filter, int x, int y)
    {
        int width = ROW_SIZE * CELL_SIZE;
        graphics.fill(x, y, x + width, y + CELL_SIZE, 0xFF373737);
        for(int offset = 0; offset < width - 2; offset += 16)
        {
            graphics.blit(filter.getBanner(), x + 1 + offset, y + 1, 0, 0, Math.min(16, width - 2 - offset), 16, 16, 16);
        }

        Font font = Minecraft.getInstance().font;
        Component name = filter.getName();
        graphics.renderItem(filter.getIcon(), x + 2, y + 1);
        int textX = x + 20;
        int textY = y + 5;
        graphics.drawString(font, name, textX - 1, textY, 0xFF000000, false);
        graphics.drawString(font, name, textX + 1, textY, 0xFF000000, false);
        graphics.drawString(font, name, textX, textY - 1, 0xFF000000, false);
        graphics.drawString(font, name, textX, textY + 1, 0xFF000000, false);
        graphics.drawString(font, name, textX, textY, 0xFFFFFFFF, false);
    }

    /**
     * Each word has to be in the translated name or the id of the item, so that an English word also
     * works in another language. "#" searches the tags, like the vanilla search tab.
     */
    private static boolean matchesSearch(ItemStack stack, String search)
    {
        if(search.startsWith("#"))
        {
            String tag = search.substring(1);
            return stack.getTags().anyMatch(key -> key.location().toString().contains(tag));
        }
        String name = stack.getHoverName().getString().toLowerCase(Locale.ROOT);
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        String path = id != null ? id.getPath() : "";
        return Arrays.stream(search.split("\\s+")).allMatch(word -> name.contains(word) || path.contains(word));
    }

    private void compileItems()
    {
        TagFilter[] filters = new TagFilter[] {
            new TagFilter(ModTags.Items.GENERAL, new ItemStack(ModBlocks.CHAIR_OAK.get()), "oak_planks"),
            new TagFilter(ModTags.Items.STORAGE, new ItemStack(ModBlocks.CABINET_OAK.get()), "bookshelf"),
            new TagFilter(ModTags.Items.BEDROOM, new ItemStack(ModBlocks.DESK_OAK.get()), "red_wool"),
            new TagFilter(ModTags.Items.OUTDOORS, new ItemStack(ModBlocks.MAIL_BOX_OAK.get()), "moss_block"),
            new TagFilter(ModTags.Items.KITCHEN, new ItemStack(ModBlocks.KITCHEN_COUNTER_CYAN.get()), "cyan_terracotta"),
            new TagFilter(ModTags.Items.BATHROOM, new ItemStack(com.mrcrayfish.furniture.legacy.bathroom.BathroomContent.TOILET.get()), "quartz_block_side"),
            new TagFilter(ModTags.Items.ELECTRONICS, new ItemStack(com.mrcrayfish.furniture.legacy.electronics.ElectronicsContent.COMPUTER.get()), "iron_block"),
            new TagFilter(ModTags.Items.EVENT, new ItemStack(com.mrcrayfish.furniture.legacy.event.EventContent.WREATH.get()), "snow"),
            new TagFilter(ModTags.Items.LIGHTING, new ItemStack(com.mrcrayfish.furniture.legacy.electronics.ElectronicsContent.LAMPS.get(net.minecraft.world.item.DyeColor.WHITE).get()), "glowstone"),
            new TagFilter(ModTags.Items.ITEMS, new ItemStack(ModItems.SPATULA.get()), "dark_oak_planks")
        };

        ForgeRegistries.ITEMS.getValues().stream()
            .filter(item -> Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).getNamespace().equals(Reference.MOD_ID))
            .forEach(item -> {
                ForgeRegistries.ITEMS.getHolder(item).ifPresent(holder -> {
                    holder.tags().forEach(tagKey -> {
                        for(TagFilter filter : filters) {
                            if(tagKey == filter.getTag()) {
                                filter.add(item);
                            }
                        }
                    });
                });
        });

        this.filters = new ArrayList<>();
        this.filters.addAll(Arrays.asList(filters));
    }

    /**
     * Author: MrCrayfish
     */
    public static class TagFilter
    {
        private final List<Item> items = Lists.newArrayList();
        private final TagKey<Item> tag;
        private final Component name;
        private final ItemStack icon;
        private final ResourceLocation banner;
        private boolean enabled = true;

        public TagFilter(TagKey<Item> tag, ItemStack icon, String bannerBlock)
        {
            this.tag = tag;
            this.name = Component.translatable(String.format("gui.tag_filter.%s.%s", tag.location().getNamespace(), tag.location().getPath().replace("/", ".")));
            this.icon = icon;
            this.banner = new ResourceLocation("textures/block/" + bannerBlock + ".png");
        }

        public TagKey<Item> getTag()
        {
            return tag;
        }

        public ResourceLocation getBanner()
        {
            return this.banner;
        }

        public ItemStack getIcon()
        {
            return this.icon;
        }

        public Component getName()
        {
            return this.name;
        }

        public void setEnabled(boolean enabled)
        {
            this.enabled = enabled;
        }

        public boolean isEnabled()
        {
            return this.enabled;
        }

        public void add(Item item)
        {
            this.items.add(item);
        }

        public void add(Block block)
        {
            this.items.add(Item.byBlock(block));
        }

        public List<Item> getItems()
        {
            return this.items;
        }
    }
}
