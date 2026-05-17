package woodisdaniel.immersivetechnology.common.block.entity;

import woodisdaniel.immersivetechnology.ImmersiveTechnology;
import woodisdaniel.immersivetechnology.common.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ImmersiveTechnology.MOD_ID);

    public static final Supplier<BlockEntityType<ItemTrashCanBlockEntity>> ITEM_TRASH_CAN_BE =
            BLOCK_ENTITIES.register("item_trash_can_be", () -> BlockEntityType.Builder.of(
                    ItemTrashCanBlockEntity::new, ModBlocks.ITEM_TRASH_CAN.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}