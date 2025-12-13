package org.woodisdaniel.immersivetechnology.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import org.woodisdaniel.immersivetechnology.common.gui.ItemTrashCanMenu;

public class ItemTrashCanBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public ItemTrashCanBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ITEM_TRASH_CAN_BE.get(), pos, blockState);
    }

    private int cooldown = 0;

    public static void tick(Level level, BlockPos pos, BlockState state, ItemTrashCanBlockEntity entity) {
        if(level.isClientSide()) {
            return;
        }

        if(entity.cooldown > 0) {
            entity.cooldown--;
            return;
        }

        if(!entity.inventory.getStackInSlot(0).isEmpty()) {
            // Remove amount or slot.
            entity.inventory.extractItem(0, 1, false);

            // Set cooldown in ticks.
            entity.cooldown = 8;
        }
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Item Trash Can");
    }

    @Nullable
    @Override
    public  AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new ItemTrashCanMenu(i, inventory, this);
    }
}