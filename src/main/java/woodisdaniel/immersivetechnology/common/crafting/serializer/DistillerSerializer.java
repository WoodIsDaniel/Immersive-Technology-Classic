package woodisdaniel.immersivetechnology.common.crafting.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import woodisdaniel.immersivetechnology.common.crafting.DistillerRecipe;
import woodisdaniel.immersivetechnology.common.multiblock.logic.ModMultiblockLogics;

public class DistillerSerializer extends IERecipeSerializer<DistillerRecipe>
{
    public static final DualMapCodec<RegistryFriendlyByteBuf, DistillerRecipe> CODECS = DualCompositeMapCodecs.composite(
            optionalFluidOutput("fluid"), r -> r.fluidOutput,
            optionalItemOutput("result"), r -> r.itemOutput,
            IngredientWithSize.CODECS.fieldOf("input"), r -> r.input,
            DualCodecs.INT.fieldOf("energy"), MultiblockRecipe::getBaseEnergy,
            DistillerRecipe::new
    );

    @Override
    protected DualMapCodec<RegistryFriendlyByteBuf, DistillerRecipe> codecs()
    {
        return CODECS;
    }

    @Override
    public ItemStack getIcon()
    {
        return ModMultiblockLogics.DISTILLER.iconStack();
    }
}