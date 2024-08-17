package net.id.paradiselost.blocks.decorative;

import net.id.paradiselost.blocks.blockentity.ParadiseSignBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ParadiseWallSignBlock extends WallSignBlock {
    public ParadiseWallSignBlock(Settings settings, WoodType woodType) {
        super(settings, woodType);
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        Identifier identifier = Registries.BLOCK.getId(this.asBlock()); // this is stupid
        this.lootTableId = identifier.withPrefixedPath("blocks/");
        return new ParadiseSignBlockEntity(pos, state);
    }
}
