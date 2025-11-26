package org.woodisdaniel.immersivetechnology.common.fluid;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.woodisdaniel.immersivetechnology.ImmersiveTechnology;

@EventBusSubscriber(modid = ImmersiveTechnology.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FluidRenderer {

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {

            private static final ResourceLocation WATER_STILL = ResourceLocation.withDefaultNamespace("block/water_still");
            private static final ResourceLocation WATER_FLOW = ResourceLocation.withDefaultNamespace("block/water_flow");
            private static final ResourceLocation WATER_OVERLAY = ResourceLocation.withDefaultNamespace("block/water_overlay");

            @Override
            public ResourceLocation getStillTexture() { return WATER_STILL; }

            @Override
            public ResourceLocation getFlowingTexture() { return WATER_FLOW; }

            @Override
            public ResourceLocation getOverlayTexture() { return WATER_OVERLAY; }

            @Override
            public int getTintColor() {

                return 0xFF00FFFF;
            }
        }, ModFluids.DISTILLED_WATER_TYPE.get());
    }
}