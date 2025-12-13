package org.woodisdaniel.immersivetechnology.common.datagen;

import blusunrize.immersiveengineering.common.register.IEFluids;
import blusunrize.immersiveengineering.data.recipes.IERecipeProvider;
import blusunrize.immersiveengineering.data.recipes.builder.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.concurrent.CompletableFuture;

public class ModMultiblockRecipes extends IERecipeProvider
{
    // partial bucket values for bottling & mixing
    private static final int half_bucket = FluidType.BUCKET_VOLUME/2;
    private static final int quarter_bucket = FluidType.BUCKET_VOLUME/4;
    private static final int eighth_bucket = FluidType.BUCKET_VOLUME/8;

    public ModMultiblockRecipes(PackOutput p_248933_, CompletableFuture<HolderLookup.Provider> provider)
    {
        super(p_248933_, provider);
    }

    @Override
    protected void buildRecipes(RecipeOutput out)
    {
        distiller(out);
    }


    private void distiller(RecipeOutput out)
    {
        Fluid ethanol = IEFluids.ETHANOL.getStill();
        FermenterRecipeBuilder.builder()
                .output(ethanol, 80)
                .input(Items.SUGAR_CANE)
                .setEnergy(6400)
                .build(out, toRL("fermenter/sugar_cane"));
        FermenterRecipeBuilder.builder()
                .output(ethanol, 20)
                .input(Items.MELON_SLICE)
                .setEnergy(6400)
                .build(out, toRL("fermenter/melon_slice"));
        FermenterRecipeBuilder.builder()
                .output(ethanol, 80)
                .input(Items.APPLE)
                .setEnergy(6400)
                .build(out, toRL("fermenter/apple"));
        FermenterRecipeBuilder.builder()
                .output(ethanol, 80)
                .input(Tags.Items.CROPS_POTATO)
                .setEnergy(6400)
                .build(out, toRL("fermenter/potato"));
        FermenterRecipeBuilder.builder()
                .output(ethanol, 40)
                .input(Tags.Items.CROPS_BEETROOT)
                .setEnergy(6400)
                .build(out, toRL("fermenter/beetroot"));
        FermenterRecipeBuilder.builder()
                .output(ethanol, 50)
                .input(Items.SWEET_BERRIES)
                .setEnergy(6400)
                .build(out, toRL("fermenter/sweet_berries"));
        FermenterRecipeBuilder.builder()
                .output(ethanol, 100)
                .input(Items.GLOW_BERRIES)
                .setEnergy(6400)
                .build(out, toRL("fermenter/glow_berries"));
        FermenterRecipeBuilder.builder()
                .output(ethanol, 250)
                .output(Items.GLASS_BOTTLE)
                .input(Items.HONEY_BOTTLE)
                .setEnergy(6400)
                .build(out, toRL("fermenter/honey"));
    }
}