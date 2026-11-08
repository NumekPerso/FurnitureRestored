package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.legacy.LegacyItems;
import com.mrcrayfish.furniture.tileentity.BasicLootBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Copies the book in the input slot using ink. Ink burns like furnace fuel once started.
 */
public class PrinterBlockEntity extends BasicLootBlockEntity
{
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_INK = 1;
    public static final int SLOT_OUTPUT = 2;

    private static final int[] SLOTS_TOP = {SLOT_INPUT};
    private static final int[] SLOTS_BOTTOM = {SLOT_OUTPUT, SLOT_INK};
    private static final int[] SLOTS_SIDES = {SLOT_INK};

    private int inkTime;
    private int inkTotal;
    private int printTime;
    private int printTotal;

    private final ContainerData data = new ContainerData()
    {
        @Override
        public int get(int index)
        {
            return switch(index)
            {
                case 0 -> PrinterBlockEntity.this.inkTime;
                case 1 -> PrinterBlockEntity.this.inkTotal;
                case 2 -> PrinterBlockEntity.this.printTime;
                case 3 -> PrinterBlockEntity.this.printTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value)
        {
            switch(index)
            {
                case 0 -> PrinterBlockEntity.this.inkTime = value;
                case 1 -> PrinterBlockEntity.this.inkTotal = value;
                case 2 -> PrinterBlockEntity.this.printTime = value;
                case 3 -> PrinterBlockEntity.this.printTotal = value;
            }
        }

        @Override
        public int getCount()
        {
            return 4;
        }
    };

    public PrinterBlockEntity(BlockPos pos, BlockState state)
    {
        super(ElectronicsContent.PRINTER_ENTITY.get(), pos, state);
    }

    public static int getInkTime(ItemStack stack)
    {
        if(stack.is(Items.BLACK_DYE))
        {
            return 1000;
        }
        if(stack.is(LegacyItems.INK_CARTRIDGE.get()))
        {
            return 5000;
        }
        return 0;
    }

    public static boolean isCopyable(ItemStack stack)
    {
        return stack.is(ElectronicsContent.PRINTER_COPYABLE);
    }

    private static int getPrintTime(ItemStack stack)
    {
        return stack.is(Items.ENCHANTED_BOOK) ? 10000 : 1000;
    }

    public boolean isPrinting()
    {
        return this.inkTime > 0;
    }

    private boolean canPrint()
    {
        return isCopyable(this.getItem(SLOT_INPUT)) && this.getItem(SLOT_OUTPUT).isEmpty();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PrinterBlockEntity printer)
    {
        boolean wasPrinting = printer.isPrinting();
        boolean changed = false;

        if(printer.isPrinting())
        {
            printer.inkTime--;
        }

        ItemStack ink = printer.getItem(SLOT_INK);
        if(!printer.isPrinting() && (ink.isEmpty() || printer.getItem(SLOT_INPUT).isEmpty()))
        {
            if(printer.printTime > 0)
            {
                printer.printTime = Mth.clamp(printer.printTime - 2, 0, printer.printTotal);
            }
        }
        else
        {
            if(!printer.isPrinting() && printer.canPrint())
            {
                printer.inkTime = printer.inkTotal = getInkTime(ink);
                if(printer.isPrinting())
                {
                    ItemStack remainder = ink.getCraftingRemainingItem();
                    ink.shrink(1);
                    if(ink.isEmpty())
                    {
                        printer.setItem(SLOT_INK, remainder);
                    }
                    changed = true;
                }
            }

            if(printer.isPrinting() && printer.canPrint())
            {
                // Read every tick: the input can change while ink is still burning
                printer.printTotal = getPrintTime(printer.getItem(SLOT_INPUT));
                printer.printTime++;
                if(printer.printTime >= printer.printTotal)
                {
                    printer.printTime = 0;
                    printer.printItem();
                    changed = true;
                }
            }
            else
            {
                printer.printTime = 0;
            }
        }

        // setChanged also updates comparators, which read isPrinting
        if(changed || wasPrinting != printer.isPrinting())
        {
            printer.setChanged();
        }
    }

    private void printItem()
    {
        this.setItem(SLOT_OUTPUT, this.getItem(SLOT_INPUT).copyWithCount(1));
    }

    public ContainerData getData()
    {
        return this.data;
    }

    @Override
    public int getContainerSize()
    {
        return 3;
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable("container.furniture_restored.printer");
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory)
    {
        return new PrinterMenu(windowId, playerInventory, this, this.data);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    {
        return switch(index)
        {
            case SLOT_INPUT -> isCopyable(stack) && this.getItem(SLOT_INPUT).isEmpty();
            case SLOT_INK -> getInkTime(stack) > 0 || stack.is(Items.BUCKET);
            default -> false;
        };
    }

    @Override
    public int[] getSlotsForFace(Direction direction)
    {
        return direction == Direction.DOWN ? SLOTS_BOTTOM : direction == Direction.UP ? SLOTS_TOP : SLOTS_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction)
    {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction)
    {
        // Like a furnace: the copies come out, the ink slot only gives back an empty bucket, and
        // the book being copied stays (a hopper above used to pull it out through the top face)
        return switch(index)
        {
            case SLOT_OUTPUT -> true;
            case SLOT_INK -> stack.is(Items.BUCKET);
            default -> false;
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putShort("BurnTime", (short) this.inkTime);
        tag.putInt("CurrentTimePrintTime", this.inkTotal);
        tag.putShort("CookTime", (short) this.printTime);
        tag.putShort("CookTimeTotal", (short) this.printTotal);
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.inkTime = tag.getShort("BurnTime");
        this.inkTotal = tag.getInt("CurrentTimePrintTime");
        this.printTime = tag.getShort("CookTime");
        this.printTotal = tag.getShort("CookTimeTotal");
    }
}
