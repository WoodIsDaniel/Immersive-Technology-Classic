package woodisdaniel.immersivetechnology.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import woodisdaniel.immersivetechnology.ImmersiveTechnology;
import woodisdaniel.immersivetechnology.common.multiblock.logic.ModMultiblockLogics;

public class DistillerMultiblock extends IETemplateMultiblock
{
    public DistillerMultiblock()
    {
        super(
                ResourceLocation.fromNamespaceAndPath(ImmersiveTechnology.MOD_ID, "multiblocks/distiller"),
                new BlockPos(1, 1, 1), new BlockPos(1, 1, 1), new BlockPos(3, 3, 3),
                ModMultiblockLogics.DISTILLER
        );
    }

    @Override
    public float getManualScale()
    {
        return 12;
    }
}