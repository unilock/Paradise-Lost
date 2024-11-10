package net.id.paradiselost.mixin.client.render;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(EntityModelLayers.class)
public interface EntityModelLayersAccessor {
	@Accessor("LAYERS")
	static Set<EntityModelLayer> getLayers() {
		throw new AssertionError("This should not occur!");
	}
}
