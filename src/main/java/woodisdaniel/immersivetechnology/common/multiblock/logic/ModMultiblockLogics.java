package woodisdaniel.immersivetechnology.common.multiblock.logic;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.*;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import woodisdaniel.immersivetechnology.common.gui.ModMenuTypes;
import woodisdaniel.immersivetechnology.common.multiblock.ModMultiblocks;

public class ModMultiblockLogics {
    public static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(
            BuiltInRegistries.BLOCK, Lib.MODID
    );
    private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(
            BuiltInRegistries.ITEM, Lib.MODID
    );
    private static final DeferredRegister<BlockEntityType<?>> BE_REGISTER = DeferredRegister.create(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, Lib.MODID
    );

    public static final MultiblockRegistration<DistillerLogic.State> DISTILLER = metal(new DistillerLogic(), "distiller")
            .structure(() -> ModMultiblocks.DISTILLER)
            .redstone(s -> s.rsState, DistillerLogic.REDSTONE_POS)
            .gui(ModMenuTypes.DISTILLER_MENU)
            .comparator(DistillerLogic.makeComparator())
            .build();

    private static <S extends IMultiblockState>
    IEMultiblockBuilder<S> stone(IMultiblockLogic<S> logic, String name, boolean solid)
    {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                .sound(SoundType.NETHER_BRICKS)
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .forceSolidOn()
                .strength(2, 20);
        if(!solid)
            properties.noOcclusion();
        else
            properties.forceSolidOn();
        return new IEMultiblockBuilder<>(logic, name)
                .notMirrored()
                .customBlock(
                        BLOCK_REGISTER, ITEM_REGISTER,
                        r -> new NonMirrorableWithActiveBlock<>(properties, r),
                        MultiblockItem::new
                )
                .defaultBEs(BE_REGISTER);
    }

    private static <S extends IMultiblockState>
    IEMultiblockBuilder<S> metal(IMultiblockLogic<S> logic, String name)
    {
        return new IEMultiblockBuilder<>(logic, name)
                .defaultBEs(BE_REGISTER)
                .defaultBlock(BLOCK_REGISTER, ITEM_REGISTER, IEBlocks.METAL_PROPERTIES_NO_OCCLUSION.get());
    }

    public static void init(IEventBus bus)
    {
        BLOCK_REGISTER.register(bus);
        ITEM_REGISTER.register(bus);
        BE_REGISTER.register(bus);
        IEMultiblockBuilder.handleModBusRegistrations(bus);
    }
}