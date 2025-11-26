package org.woodisdaniel.immersivetechnology.common.fluid;

import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.woodisdaniel.immersivetechnology.ImmersiveTechnology;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, ImmersiveTechnology.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, ImmersiveTechnology.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(BuiltInRegistries.BLOCK, ImmersiveTechnology.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, ImmersiveTechnology.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> DISTILLED_WATER_TYPE = FLUID_TYPES.register("distilled_water", () ->
            new FluidType(FluidType.Properties.create()
                    .descriptionId("block.immersivetechnology.distilled_water")
                    .fallDistanceModifier(0F)
                    .canExtinguish(true)
                    .supportsBoating(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .density(1000)
                    .viscosity(1000)
                    .temperature(300)
            ));

    public static final DeferredHolder<Fluid, BaseFlowingFluid> SOURCE_DISTILLED_WATER = FLUIDS.register("distilled_water", () ->
            new BaseFlowingFluid.Source(getFluidProperties()));

    public static final DeferredHolder<Fluid, BaseFlowingFluid> FLOWING_DISTILLED_WATER = FLUIDS.register("flowing_distilled_water", () ->
            new BaseFlowingFluid.Flowing(getFluidProperties()));

    public static final DeferredHolder<Block, LiquidBlock> DISTILLED_WATER_BLOCK = BLOCKS.register("distilled_water_block", () ->
            new LiquidBlock(SOURCE_DISTILLED_WATER.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));

    public static final DeferredHolder<Item, Item> DISTILLED_WATER_BUCKET = ITEMS.register("distilled_water_bucket", () ->
            new BucketItem(SOURCE_DISTILLED_WATER.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    private static final BaseFlowingFluid.Properties DISTILLED_WATER_PROPERTIES = new BaseFlowingFluid.Properties(
            DISTILLED_WATER_TYPE,
            SOURCE_DISTILLED_WATER,
            FLOWING_DISTILLED_WATER
    )
            .slopeFindDistance(2)
            .levelDecreasePerBlock(1)
            .block(DISTILLED_WATER_BLOCK)
            .bucket(DISTILLED_WATER_BUCKET);

    private static BaseFlowingFluid.Properties getFluidProperties() {
        return DISTILLED_WATER_PROPERTIES;
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}