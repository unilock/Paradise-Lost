package net.id.paradiselost.mixin.item;

import net.id.paradiselost.blocks.ParadiseLostBlocks;
import net.id.paradiselost.loot.ParadiseLostLootTables;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"))
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();

        if (world.getBlockState(blockPos).getBlock() == ParadiseLostBlocks.MOTHER_AUREL_WOODSTUFF.log() && !world.isClient) {
            ServerWorld server = (ServerWorld) world;
            LootTable supplier = server.getServer().getLootManager().getTable(ParadiseLostLootTables.MOTHER_AUREL_STRIPPING);
            List<ItemStack> items = supplier.generateLoot(new LootContext.Builder(server)
                    .parameter(LootContextParameters.BLOCK_STATE, world.getBlockState(blockPos))
                    .parameter(LootContextParameters.ORIGIN, Vec3d.of(blockPos))
                    .parameter(LootContextParameters.TOOL, context.getStack())
                    .build(LootContextTypes.BLOCK)
            );
            Vec3d offsetDirection = context.getHitPos();
            for (ItemStack item : items) {
                ItemEntity itemEntity = new ItemEntity(context.getWorld(), offsetDirection.x, offsetDirection.y, offsetDirection.z, item);
                world.spawnEntity(itemEntity);
            }
        }
    }
}
