package org.woodisdaniel.immersivetechnology.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.woodisdaniel.immersivetechnology.common.block.ModBlocks;
import org.woodisdaniel.immersivetechnology.common.fluid.ModFluids;
import org.woodisdaniel.immersivetechnology.common.item.ModItems;
import org.woodisdaniel.immersivetechnology.ImmersiveTechnology;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ImmersiveTechnology.MOD_ID);

    public static final Supplier<CreativeModeTab> IMMERSIVETECHNOLOGY_TAB = CREATIVE_MODE_TAB.register("immersivetechnology_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SALT.get()))
                    .title(Component.translatable("creativetab.immersivetechnology.immersivetechnology_items"))
                    .displayItems((itemDisplayParameters, output) -> {

                        //output.accept(Items.WOODEN_SWORD);
                        output.accept(ModBlocks.ITEM_TRASH_CAN);
                        output.accept(ModBlocks.REINFORCED_COKE_BRICK);
                        output.accept(ModBlocks.REINFORCED_COKE_BRICK_SLAB);
                        output.accept(ModItems.SALT);
                        output.accept(ModFluids.DISTILLED_WATER_BUCKET.get());

                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}