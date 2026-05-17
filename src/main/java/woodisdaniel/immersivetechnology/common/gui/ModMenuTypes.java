package woodisdaniel.immersivetechnology.common.gui;

import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import woodisdaniel.immersivetechnology.ImmersiveTechnology;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import woodisdaniel.immersivetechnology.common.multiblock.logic.DistillerLogic;

import static blusunrize.immersiveengineering.common.register.IEMenuTypes.registerMultiblock;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, ImmersiveTechnology.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ItemTrashCanMenu>> ITEM_TRASH_CAN_MENU =
            registerMenuType("item_trash_can_menu", ItemTrashCanMenu::new);

    public static final IEMenuTypes.MultiblockContainer<DistillerLogic.State, DistillerMenu> DISTILLER_MENU =
            registerMultiblock("distiller_menu", DistillerMenu::makeServer, DistillerMenu::makeClient);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name,
                                                                                                              IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}