package com.mrcrayfish.furniture.legacy.kitchen.blockentity;

import com.mrcrayfish.furniture.legacy.LegacyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Common part of the washing machine and the dishwasher: the last slot holds the soapy water, the
 * others are repaired one point every 50 ticks (20 with super soapy water).
 */
public abstract class WashingBlockEntity extends KitchenContainerBlockEntity implements RunningMachine
{
    public static final int SOAP_TIME = 5000;
    public static final int DATA_COUNT = 4;
    public static final int DATA_WASHING = 0;
    public static final int DATA_SUPER_MODE = 1;
    public static final int DATA_PROGRESS = 2;
    public static final int DATA_TIME_REMAINING = 3;

    private final int[] topSlots;
    private final int[] bottomSlots;
    private final int[] sideSlots;
    private final int soundInterval;

    protected boolean washing;
    protected boolean superMode;
    protected int progress;
    protected int timeRemaining;
    private int timer;

    protected final ContainerData data = new ContainerData()
    {
        @Override
        public int get(int index)
        {
            return switch(index)
            {
                case DATA_WASHING -> WashingBlockEntity.this.washing ? 1 : 0;
                case DATA_SUPER_MODE -> WashingBlockEntity.this.superMode ? 1 : 0;
                case DATA_PROGRESS -> WashingBlockEntity.this.progress;
                default -> WashingBlockEntity.this.timeRemaining;
            };
        }

        @Override
        public void set(int index, int value)
        {
            switch(index)
            {
                case DATA_WASHING -> WashingBlockEntity.this.washing = value != 0;
                case DATA_SUPER_MODE -> WashingBlockEntity.this.superMode = value != 0;
                case DATA_PROGRESS -> WashingBlockEntity.this.progress = value;
                default -> WashingBlockEntity.this.timeRemaining = value;
            }
        }

        @Override
        public int getCount()
        {
            return DATA_COUNT;
        }
    };

    protected WashingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int soundInterval)
    {
        super(type, pos, state);
        this.soundInterval = soundInterval;
        int soapSlot = this.getContainerSize() - 1;
        this.topSlots = new int[soapSlot];
        this.bottomSlots = new int[soapSlot + 1];
        for(int i = 0; i <= soapSlot; i++)
        {
            this.bottomSlots[i] = i;
            if(i < soapSlot)
            {
                this.topSlots[i] = i;
            }
        }
        this.sideSlots = new int[]{soapSlot};
    }

    protected abstract boolean isRepairable(ItemStack stack);

    protected abstract SoundEvent getRunningSound();

    /** Can the machine run with its current content? */
    protected abstract boolean hasValidContent();

    protected int getSoapSlot()
    {
        return this.getContainerSize() - 1;
    }

    public static boolean isSoap(ItemStack stack)
    {
        return stack.is(LegacyItems.SOAPY_WATER.get()) || stack.is(LegacyItems.SUPER_SOAPY_WATER.get());
    }

    public boolean isWashing()
    {
        return this.washing;
    }

    public int getProgress()
    {
        return this.progress;
    }

    @Override
    public boolean isRunning()
    {
        return this.washing;
    }

    @Override
    public int getMaxStackSize()
    {
        return 1;
    }

    public void startWashing()
    {
        if(this.washing || !this.canWash())
            return;
        if(this.timeRemaining == 0)
        {
            this.useSoap();
        }
        this.washing = true;
        this.timer = 0;
        this.setChanged();
        this.updateComparators();
    }

    public void stopWashing()
    {
        this.progress = 0;
        this.washing = false;
        this.setChanged();
        this.updateComparators();
    }

    private void useSoap()
    {
        ItemStack soap = this.getItem(this.getSoapSlot());
        this.superMode = soap.is(LegacyItems.SUPER_SOAPY_WATER.get());
        this.setItem(this.getSoapSlot(), soap.getCraftingRemainingItem());
        this.timeRemaining = SOAP_TIME;
    }

    private boolean canWash()
    {
        ItemStack soap = this.getItem(this.getSoapSlot());
        if(this.timeRemaining == 0 && !isSoap(soap))
            return false;
        return this.hasValidContent();
    }

    private boolean canRepair()
    {
        return this.progress % (this.superMode ? 20 : 50) == 0;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WashingBlockEntity machine)
    {
        if(!machine.washing)
            return;

        if(!machine.canWash())
        {
            machine.stopWashing();
            return;
        }

        if(machine.canRepair())
        {
            if(machine.repairItems())
            {
                machine.setChanged();
            }
            if(machine.progress >= 360)
            {
                machine.progress = 0;
            }
        }

        machine.timeRemaining--;
        if(machine.timeRemaining <= 0)
        {
            machine.timeRemaining = 0;
            if(isSoap(machine.getItem(machine.getSoapSlot())))
            {
                machine.useSoap();
            }
            else
            {
                machine.stopWashing();
                return;
            }
        }

        machine.progress++;
        level.blockEntityChanged(pos);

        if(machine.timer == machine.soundInterval)
        {
            machine.timer = 0;
        }
        if(machine.timer == 0)
        {
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, machine.getRunningSound(), SoundSource.BLOCKS, 0.75F, 1.0F);
        }
        machine.timer++;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, WashingBlockEntity machine)
    {
        if(machine.washing)
        {
            machine.progress++;
        }
    }

    private boolean repairItems()
    {
        boolean repaired = false;
        for(int i = 0; i < this.getSoapSlot(); i++)
        {
            ItemStack stack = this.getItem(i);
            if(!stack.isEmpty() && stack.isDamaged())
            {
                stack.setDamageValue(stack.getDamageValue() - 1);
                repaired = true;
            }
        }
        return repaired;
    }

    @Override
    public int[] getSlotsForFace(Direction direction)
    {
        return switch(direction)
        {
            case UP -> this.topSlots;
            case DOWN -> this.bottomSlots;
            default -> this.sideSlots;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction)
    {
        if(direction == Direction.UP)
        {
            return this.isRepairable(stack);
        }
        return direction != Direction.DOWN && isSoap(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction)
    {
        return direction == Direction.DOWN && !isSoap(stack) && stack.getDamageValue() == 0;
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.washing = tag.getBoolean("Washing");
        this.superMode = tag.getBoolean("SuperMode");
        this.progress = tag.getInt("Progress");
        this.timeRemaining = tag.getInt("Remaining");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putBoolean("Washing", this.washing);
        tag.putBoolean("SuperMode", this.superMode);
        tag.putInt("Progress", this.progress);
        tag.putInt("Remaining", this.timeRemaining);
    }
}
