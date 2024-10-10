package net.id.paradiselost.client.rendering.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.ColorProviderRegistryImpl;
import net.id.paradiselost.blocks.ParadiseLostBlocks;
import net.id.paradiselost.blocks.util.DynamicColorBlock;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.registry.Registries;
import net.minecraft.world.biome.FoliageColors;
import net.minecraft.world.biome.GrassColors;

@Environment(EnvType.CLIENT)
public class ParadiseLostColorProviders {
    public static void initClient() {
        initBlocks();
        initItems();
        initDynamicColorBlocks();
    }

    private static void initBlocks() {
        ColorProviderRegistryImpl.BLOCK.register(((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getFoliageColor(world, pos) : FoliageColors.getDefaultColor()), ParadiseLostBlocks.AUREL_WOODSTUFF.leaves(), ParadiseLostBlocks.AUREL_LEAF_PILE, ParadiseLostBlocks.SHAMROCK);
        ColorProviderRegistryImpl.BLOCK.register(((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getColor(0.5D, 1.0D)), ParadiseLostBlocks.HIGHLANDS_GRASS, ParadiseLostBlocks.GRASS, ParadiseLostBlocks.GRASS_FLOWERING, ParadiseLostBlocks.SHORT_GRASS, ParadiseLostBlocks.TALL_GRASS, ParadiseLostBlocks.FERN, ParadiseLostBlocks.BUSH);
    }

    private static void initItems() {
        ColorProviderRegistryImpl.ITEM.register(((stack, tintIndex) -> 0xf1ff99), ParadiseLostBlocks.AUREL_WOODSTUFF.leaves().asItem(), ParadiseLostBlocks.AUREL_LEAF_PILE.asItem(), ParadiseLostBlocks.SHAMROCK.asItem());
        ColorProviderRegistryImpl.ITEM.register(((stack, tintIndex) -> tintIndex == 0 ? 0xa2dbc2 : -1), ParadiseLostBlocks.HIGHLANDS_GRASS.asItem(), ParadiseLostBlocks.GRASS.asItem(), ParadiseLostBlocks.GRASS_FLOWERING.asItem(), ParadiseLostBlocks.SHORT_GRASS.asItem(), ParadiseLostBlocks.TALL_GRASS.asItem(), ParadiseLostBlocks.FERN.asItem(), ParadiseLostBlocks.BUSH.asItem());
    }

    private static void initDynamicColorBlocks() {
        // Ideally we shouldn't go through the entire block registry, but it's almost instantaneous anyway
        Registries.BLOCK.stream()
                .filter(block -> block instanceof DynamicColorBlock)
                .forEach(block -> {
                    DynamicColorBlock dynamic = (DynamicColorBlock) block;
                    ColorProviderRegistryImpl.BLOCK.register(dynamic.getBlockColorProvider(), block);
                    ColorProviderRegistryImpl.ITEM.register(dynamic.getBlockItemColorProvider(), block.asItem());
                });
    }
}
