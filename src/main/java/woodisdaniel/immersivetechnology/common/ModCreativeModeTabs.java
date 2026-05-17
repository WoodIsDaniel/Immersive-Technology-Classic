package woodisdaniel.immersivetechnology.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import woodisdaniel.immersivetechnology.common.block.ModBlocks;
import woodisdaniel.immersivetechnology.common.fluid.ModFluids;
import woodisdaniel.immersivetechnology.common.item.ModItems;
import woodisdaniel.immersivetechnology.ImmersiveTechnology;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ImmersiveTechnology.MOD_ID);

    public static final Supplier<CreativeModeTab> IMMERSIVETECHNOLOGY_TAB = CREATIVE_MODE_TAB.register("immersivetechnology_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.ITEM_TRASH_CAN.get()))
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