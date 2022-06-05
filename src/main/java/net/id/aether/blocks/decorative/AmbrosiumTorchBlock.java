package net.id.aether.blocks.decorative;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class AmbrosiumTorchBlock extends TorchBlock {
    public AmbrosiumTorchBlock(Settings settings) {
        super(settings, new DustParticleEffect(new Vec3f(0.886f, 0.871f, 0.125f), 0.7f));
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int max = random.nextInt(3) + 2;
        for (int i = 0; i <= max; i++) {
            double d = (double) pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.25D;
            double e = (double) pos.getY() + 0.6D + (random.nextDouble() - 0.5D) * 0.25D;
            double f = (double) pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.25D;
            world.addParticle(this.particle, d, e, f, 0.0D, -4D, 0.0D);
        }
    }
}