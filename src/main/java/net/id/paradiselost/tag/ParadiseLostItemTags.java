package net.id.paradiselost.tag;

import net.id.paradiselost.ParadiseLost;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ParadiseLostItemTags {
    public static final TagKey<Item> MOA_TEMPTABLES = register("moa_temptables");
    public static final TagKey<Item> RIGHTEOUS_WEAPONS = register("righteous_weapons");
    public static final TagKey<Item> SACRED_WEAPONS = register("sacred_weapons");
    public static final TagKey<Item> IGNITING_TOOLS = register("igniting_tools");

    private static TagKey<Item> register(String id) {
        return TagKey.of(RegistryKeys.ITEM, ParadiseLost.locate(id));
    }
}
