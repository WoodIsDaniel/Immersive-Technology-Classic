package org.woodisdaniel.immersivetechnology.common.gui;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.woodisdaniel.immersivetechnology.common.multiblock.logic.DistillerLogic;

public class DistillerMenu extends IEContainerMenu {

    public final EnergyStorage energyStorage;
    public final FluidTank tank;

    public static DistillerMenu makeServer(
            MenuType<?> type, int id, Inventory invPlayer, MultiblockMenuContext<DistillerLogic.State> ctx
    )
    {
        final DistillerLogic.State state = ctx.mbContext().getState();
        return new DistillerMenu(
                multiblockCtx(type, id, ctx), invPlayer, state.getInventory(), state.getEnergy(), state.getTank()
        );
    }

    public static DistillerMenu makeClient(MenuType<?> type, int id, Inventory invPlayer)
    {
        return new DistillerMenu(
                clientCtx(type, id), invPlayer,
                new ItemStackHandler(DistillerLogic.NUM_SLOTS),
                new MutableEnergyStorage(DistillerLogic.ENERGY_CAPACITY),
                new FluidTank(DistillerLogic.TANK_CAPACITY)
        );
    }

    private DistillerMenu(
            MenuContext ctx, Inventory inventoryPlayer, IItemHandler inv,
            MutableEnergyStorage energyStorage, FluidTank tank
    )
    {
        super(ctx);
        this.energyStorage = energyStorage;
        this.tank = tank;

        for(int i = 0; i < 8; i++)
            this.addSlot(new SlotItemHandler(inv, i, 8+(i%4)*18, 19+(i/4)*18));
        this.addSlot(new IESlot.NewOutput(inv, 8, 91, 53));
        this.addSlot(new IESlot.NewFluidContainer(inv, 9, 134, 17, IESlot.NewFluidContainer.Filter.ANY));
        this.addSlot(new IESlot.NewOutput(inv, 10, 134, 53));
        ownSlotCount = 11;

        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 9; j++)
                addSlot(new Slot(inventoryPlayer, j+i*9+9, 8+j*18, 85+i*18));
        for(int i = 0; i < 9; i++)
            addSlot(new Slot(inventoryPlayer, i, 8+i*18, 143));
        addGenericData(GenericContainerData.energy(energyStorage));
        addGenericData(GenericContainerData.fluid(tank));
    }
}