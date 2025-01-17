package net.id.paradiselost.client.model;

import com.google.common.collect.Maps;
import com.terraformersmc.terraform.boat.api.client.TerraformBoatClientHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.id.paradiselost.ParadiseLost;
import net.id.paradiselost.client.model.armor.PhoenixArmorModel;
import net.id.paradiselost.client.model.entity.*;
import net.id.paradiselost.items.ParadiseLostItems;
import net.id.paradiselost.mixin.client.render.EntityModelLayersAccessor;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class ParadiseLostModelLayers {

    public static final TexturedModelData INNER_ARMOR_MODEL_DATA = TexturedModelData.of(BipedEntityModel.getModelData(new Dilation(0.5F), 0.0F), 64, 32);
    public static final TexturedModelData OUTER_ARMOR_MODEL_DATA = TexturedModelData.of(BipedEntityModel.getModelData(new Dilation(1.0F), 0.0F), 64, 32);

    public static final Map<EntityModelLayer, TexturedModelData> ENTRIES = Maps.newHashMap();

    public static final EntityModelLayer ENVOY = register("envoy", "main", EnvoyEntityModel.getTexturedModelData());
    public static final EntityModelLayer ENVOY_INNER_ARMOR = register("envoy", "inner_armor", INNER_ARMOR_MODEL_DATA);
    public static final EntityModelLayer ENVOY_OUTER_ARMOR = register("envoy", "outer_armor", OUTER_ARMOR_MODEL_DATA);
    public static final EntityModelLayer MOA = register("moa", "main", MoaModel.getTexturedModelData());
    public static final EntityModelLayer PHOENIX_ARMOR = register("phoenix_armor", "main", PhoenixArmorModel.getTexturedModelData());


    public static EntityModelLayer register(Identifier id, String layer, TexturedModelData data) {
        EntityModelLayer entityModelLayer = new EntityModelLayer(id, layer);
        if (!EntityModelLayersAccessor.getLayers().add(entityModelLayer)) {
            throw new IllegalStateException("Duplicate registration for " + entityModelLayer);
        } else {
            ENTRIES.put(entityModelLayer, data);
            return entityModelLayer;
        }
    }

    public static EntityModelLayer register(String id, String layer, TexturedModelData data) {
        return register(ParadiseLost.locate(id), layer, data);
    }

    public static void initClient() {
		for (ParadiseLostItems.BoatSet boatSet : ParadiseLostItems.BOAT_SETS) {
			TerraformBoatClientHelper.registerModelLayers(boatSet.id(), false);
		}
    }
}
