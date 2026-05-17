package woodisdaniel.immersivetechnology.datagen.crafting.builder;

import blusunrize.immersiveengineering.api.crafting.FermenterRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import blusunrize.immersiveengineering.data.recipes.builder.IERecipeBuilder;
import blusunrize.immersiveengineering.data.recipes.builder.BaseHelpers.ItemInput;
import blusunrize.immersiveengineering.data.recipes.builder.BaseHelpers.ItemOutput;

public class DistillerRecipeBuilder extends IERecipeBuilder<DistillerRecipeBuilder>
        implements ItemInput<DistillerRecipeBuilder>, ItemOutput<DistillerRecipeBuilder>
{
    private IngredientWithSize input;
    private FluidStack fluidOutput;
    private TagOutput itemOutput = TagOutput.EMPTY;
    private int energy;

    private DistillerRecipeBuilder()
    {
    }

    public static DistillerRecipeBuilder builder()
    {
        return new DistillerRecipeBuilder();
    }

    @Override
    public DistillerRecipeBuilder input(IngredientWithSize input)
    {
        this.input = input;
        return this;
    }

    @Override
    public DistillerRecipeBuilder output(TagOutput output)
    {
        this.itemOutput = output;
        return this;
    }

    public DistillerRecipeBuilder output(Fluid output, int amount)
    {
        return output(new FluidStack(output, amount));
    }

    public DistillerRecipeBuilder setEnergy(int energy)
    {
        this.energy = energy;
        return this;
    }

    public DistillerRecipeBuilder output(FluidStack output)
    {
        this.fluidOutput = output;
        return this;
    }

    public void build(RecipeOutput out, ResourceLocation name)
    {
        FermenterRecipe recipe = new FermenterRecipe(fluidOutput, itemOutput, input, energy);
        out.accept(name, recipe, null, getConditions());
    }
}
