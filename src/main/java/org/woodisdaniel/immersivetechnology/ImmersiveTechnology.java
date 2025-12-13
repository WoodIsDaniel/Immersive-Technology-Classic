package org.woodisdaniel.immersivetechnology;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.minecraft.world.item.CreativeModeTabs;
import org.woodisdaniel.immersivetechnology.api.crafting.ModRecipeTypes;
import org.woodisdaniel.immersivetechnology.client.gui.ItemTrashCanScreen;
import org.woodisdaniel.immersivetechnology.common.gui.ModMenuTypes;
import org.woodisdaniel.immersivetechnology.common.block.ModBlocks;
import org.woodisdaniel.immersivetechnology.common.block.entity.ModBlockEntities;
import org.woodisdaniel.immersivetechnology.common.fluid.ModFluids;
import org.woodisdaniel.immersivetechnology.common.item.ModItems;
import org.woodisdaniel.immersivetechnology.common.ModCreativeModeTabs;
import org.woodisdaniel.immersivetechnology.common.multiblock.ModMultiblocks;
import org.woodisdaniel.immersivetechnology.common.multiblock.logic.ModMultiblockLogics;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ImmersiveTechnology.MOD_ID)
public class ImmersiveTechnology {
    public static final String MOD_ID = "immersivetechnology";
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ImmersiveTechnology(IEventBus modEventBus, ModContainer modContainer) {
        // Core Setup
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::addCreative);

        // Game Events
        NeoForge.EVENT_BUS.register(this);

        // Config
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Creative Tabs
        ModCreativeModeTabs.register(modEventBus);

        // Register Items, Blocks, Fluids, BlockEntities, Multiblocks, Menus, Recipes.
        ModItems.register(modEventBus);

        ModBlocks.register(modEventBus);

        ModFluids.register(modEventBus);

        ModBlockEntities.register(modEventBus);

        ModMenuTypes.register(modEventBus);

        ModMultiblockLogics.init(modEventBus);

        ModRecipeTypes.init(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModMultiblocks::init);

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
//            event.accept(ModItems.RUBY);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {

        }

        // Item Trash Can
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.ITEM_TRASH_CAN_MENU.get(), ItemTrashCanScreen::new);
        }
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ITEM_TRASH_CAN_BE.get(),
                (blockEntity, side) -> blockEntity.inventory
        );
    }
}