package woodisdaniel.immersivetechnology.common.multiblock.logic;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.*;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.api.tool.MachineInterfaceHandler;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.IESounds;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.sound.MultiblockSound;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import woodisdaniel.immersivetechnology.common.crafting.DistillerRecipe;
import woodisdaniel.immersivetechnology.common.multiblock.shape.DistillerShape;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DistillerLogic implements IMultiblockLogic<DistillerLogic.State>, IServerTickableComponent<DistillerLogic.State>, IClientTickableComponent<DistillerLogic.State> {

    public static final BlockPos REDSTONE_POS = new BlockPos(2, 1, 2);
    private static final MultiblockFace FLUID_OUTPUT = new MultiblockFace(3, 0, 1, RelativeBlockFace.RIGHT);
    private static final CapabilityPosition FLUID_OUTPUT_CAP = CapabilityPosition.opposing(FLUID_OUTPUT);
    private static final CapabilityPosition ENERGY_POS = new CapabilityPosition(0, 1, 2, RelativeBlockFace.UP);

    public static final int TANK_CAPACITY = 24* FluidType.BUCKET_VOLUME;
    public static final int ENERGY_CAPACITY = 16000;
    public static final int NUM_INPUT_SLOTS = 8;
    public static final int OUTPUT_SLOT = NUM_INPUT_SLOTS;
    public static final int EMPTY_FLUID_SLOT = OUTPUT_SLOT+1;
    public static final int FILLED_FLUID_SLOT = EMPTY_FLUID_SLOT+1;
    public static final int NUM_SLOTS = FILLED_FLUID_SLOT+1;

    @Override
    public void tickServer(IMultiblockContext<DistillerLogic.State> context)
    {
        final DistillerLogic.State state = context.getState();
        final boolean rsEnabled = state.rsState.isEnabled(context);
        final boolean wasActive = state.active;
        state.active = state.processor.tickServer(state, context.getLevel(), rsEnabled);
        if(wasActive!=state.active)
            context.requestMasterBESync();
        if(!rsEnabled)
            return;
        boolean changed = false;
        if(state.energy.getEnergyStored() > 0&&state.processor.getQueueSize() < state.processor.getMaxQueueSize())
            changed = enqueueNewProcesses(context);

        changed |= FluidUtils.multiblockFluidOutput(
                state.fluidOutput.get(), state.tank, EMPTY_FLUID_SLOT, FILLED_FLUID_SLOT, state.inventory
        );
        changed |= outputItem(context);

        if(changed)
            context.markMasterDirty();
    }

    @Override
    public void tickClient(IMultiblockContext<DistillerLogic.State> context)
    {
        final DistillerLogic.State state = context.getState();
        if(!state.isPlayingSound.getAsBoolean())
        {
            final Vec3 soundPos = context.getLevel().toAbsolute(new Vec3(1.5, 1.5, 1.5));
            state.isPlayingSound = MultiblockSound.startSound(
                    () -> state.active, context.isValid(), soundPos, IESounds.fermenter, 0.25f
            );
        }
    }

    private boolean enqueueNewProcesses(IMultiblockContext<DistillerLogic.State> ctx)
    {
        final DistillerLogic.State state = ctx.getState();
        final Level level = ctx.getLevel().getRawLevel();
        boolean addedAny = false;
        final int[] usedInvSlots = new int[NUM_INPUT_SLOTS];
        for(final MultiblockProcess<DistillerRecipe, ProcessContext.ProcessContextInMachine<DistillerRecipe>> process : state.processor.getQueue())
            if(process instanceof MultiblockProcessInMachine)
                for(int i : ((MultiblockProcessInMachine<DistillerRecipe>)process).getInputSlots())
                    usedInvSlots[i]++;

        Integer[] preferredSlots = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7};
        Arrays.sort(preferredSlots, 0, 8, Comparator.comparingInt(arg0 -> usedInvSlots[arg0]));
        for(int slot : preferredSlots)
        {
            ItemStack stack = state.inventory.getStackInSlot(slot);
            if(!stack.isEmpty())
            {
                stack = stack.copy();
                stack.shrink(usedInvSlots[slot]);
            }
            if(!stack.isEmpty()&&stack.getCount() > 0)
            {
                RecipeHolder<DistillerRecipe> recipe = DistillerRecipe.findRecipe(level, stack);
                if(recipe!=null)
                {
                    MultiblockProcessInMachine<DistillerRecipe> process = new MultiblockProcessInMachine<>(recipe, slot);
                    if(state.processor.addProcessToQueue(process, level, false))
                        addedAny = true;
                }
            }
        }
        return addedAny;
    }

    private boolean outputItem(IMultiblockContext<DistillerLogic.State> ctx)
    {
        final DistillerLogic.State state = ctx.getState();
        final ItemStack outputStack = state.inventory.getStackInSlot(OUTPUT_SLOT);
        if(outputStack.isEmpty()||!ctx.getLevel().shouldTickModulo(8))
            return false;
        IItemHandler outputHandler = state.itemOutput.get();
        if(outputHandler==null)
            return false;
        ItemStack stack = outputStack.copyWithCount(1);
        stack = ItemHandlerHelper.insertItem(outputHandler, stack, false);
        if(stack.isEmpty())
        {
            outputStack.shrink(1);
            return true;
        }
        return false;
    }

    @Override
    public DistillerLogic.State createInitialState(IInitialMultiblockContext<DistillerLogic.State> capabilitySource)
    {
        return new DistillerLogic.State(capabilitySource);
    }

    @Override
    public void registerCapabilities(IMultiblockComponent.CapabilityRegistrar<DistillerLogic.State> register)
    {
        register.registerAtOrNull(Capabilities.EnergyStorage.BLOCK, ENERGY_POS, state -> state.energy);
        register.registerAtOrNull(Capabilities.FluidHandler.BLOCK, FLUID_OUTPUT_CAP, state -> state.fluidHandler);
        register.register(Capabilities.ItemHandler.BLOCK, (state, position) -> {
            if(new BlockPos(0, 1, 0).equals(position.posInMultiblock()))
                return state.insertionHandler;
            else if(new BlockPos(1, 1, 1).equals(position.posInMultiblock()))
                return state.extractionHandler;
            else
                return null;
        });
        register.registerAtBlockPos(MachineInterfaceHandler.IMachineInterfaceConnection.CAPABILITY, REDSTONE_POS, state -> state.mifHandler);
    }

    @Override
    public void dropExtraItems(DistillerLogic.State state, Consumer<ItemStack> drop)
    {
        MBInventoryUtils.dropItems(state.inventory, drop);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType)
    {
        return DistillerShape.SHAPE_GETTER;
    }

    public static ComparatorManager<DistillerLogic.State> makeComparator()
    {
        return ComparatorManager.makeSimple(
                ComparatorManager.SimpleComparatorValue.inventory(DistillerLogic.State::getInventory, 0, 8), DistillerLogic.REDSTONE_POS
        );
    }

    public static class State implements IMultiblockState, ProcessContext.ProcessContextInMachine<DistillerRecipe>
    {
        private final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        private final FluidTank tank = new FluidTank(TANK_CAPACITY);
        private final SlotwiseItemHandler inventory;
        private final MultiblockProcessor<DistillerRecipe, ProcessContextInMachine<DistillerRecipe>> processor;
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        private final Supplier<@Nullable IFluidHandler> fluidOutput;
        private final Supplier<@Nullable IItemHandler> itemOutput;
        private final IItemHandler insertionHandler;
        private final IItemHandler extractionHandler;
        private final IFluidHandler fluidHandler;
        private final MachineInterfaceHandler.IMachineInterfaceConnection mifHandler;

        // Client/sync field
        public boolean active;
        private BooleanSupplier isPlayingSound = () -> false;

        public State(IInitialMultiblockContext<DistillerLogic.State> ctx)
        {
            this.processor = new MultiblockProcessor<>(
                    8, 0, 8, ctx.getMarkDirtyRunnable(), DistillerRecipe.RECIPES::getById
            );
            this.inventory = SlotwiseItemHandler.makeWithGroups(List.of(
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.NO_CONSTRAINT, NUM_INPUT_SLOTS),
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.OUTPUT, 1),
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.FLUID_INPUT, 1),
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.OUTPUT, 1)
            ), ctx.getMarkDirtyRunnable());
            this.fluidOutput = ctx.getCapabilityAt(Capabilities.FluidHandler.BLOCK, FLUID_OUTPUT);
            this.itemOutput = ctx.getCapabilityAt(
                    Capabilities.ItemHandler.BLOCK, new BlockPos(2, 1, 1), RelativeBlockFace.LEFT
            );
            this.insertionHandler = new WrappingItemHandler(
                    this.inventory, true, false, new WrappingItemHandler.IntRange(0, NUM_INPUT_SLOTS)
            );
            this.extractionHandler = new WrappingItemHandler(
                    this.inventory, false, true, new WrappingItemHandler.IntRange(OUTPUT_SLOT, OUTPUT_SLOT+1)
            );
            this.fluidHandler = new ArrayFluidHandler(
                    tank, true, false, ctx.getMarkDirtyRunnable()
            );
            this.mifHandler = () -> new MachineInterfaceHandler.MachineCheckImplementation[]{
                    new MachineInterfaceHandler.MachineCheckImplementation<>((BooleanSupplier)() -> this.active, MachineInterfaceHandler.BASIC_ACTIVE),
                    new MachineInterfaceHandler.MachineCheckImplementation<>(insertionHandler, MachineInterfaceHandler.BASIC_ITEM_IN),
                    new MachineInterfaceHandler.MachineCheckImplementation<>(extractionHandler, MachineInterfaceHandler.BASIC_ITEM_OUT),
                    new MachineInterfaceHandler.MachineCheckImplementation<>(fluidHandler, MachineInterfaceHandler.BASIC_FLUID_OUT),
                    new MachineInterfaceHandler.MachineCheckImplementation<>(energy, MachineInterfaceHandler.BASIC_ENERGY),
            };
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider)
        {
            nbt.put("energy", energy.serializeNBT(provider));
            nbt.put("tank", tank.writeToNBT(provider, new CompoundTag()));
            nbt.put("inventory", inventory.serializeNBT(provider));
            nbt.put("processor", processor.toNBT(provider));
        }

        @Override
        public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider)
        {
            energy.deserializeNBT(provider, nbt.get("energy"));
            tank.readFromNBT(provider, nbt.getCompound("tank"));
            inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
            processor.fromNBT(
                    nbt.get("processor"),
                    (getRecipe, data, p) -> new MultiblockProcessInMachine<>(getRecipe, data),
                    provider
            );
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt, HolderLookup.Provider provider)
        {
            nbt.putBoolean("active", active);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt, HolderLookup.Provider provider)
        {
            active = nbt.getBoolean("active");
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
            return energy;
        }

        @Override
        public int[] getOutputSlots()
        {
            return new int[]{OUTPUT_SLOT};
        }

        @Override
        public int[] getOutputTanks()
        {
            return new int[]{0};
        }

        @Override
        public IFluidTank[] getInternalTanks()
        {
            return new IFluidTank[]{tank};
        }

        @Override
        public IItemHandlerModifiable getInventory()
        {
            return inventory;
        }

        public FluidTank getTank()
        {
            return tank;
        }
    }
}
