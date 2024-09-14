package net.id.paradiselost.blocks.blockentity;

import net.id.paradiselost.blocks.mechanical.TreeTapBlock;
import net.id.paradiselost.recipe.ParadiseLostRecipeTypes;
import net.id.paradiselost.recipe.TreeTapRecipe;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TreeTapBlockEntity extends LootableContainerBlockEntity implements SidedInventory {

    private final DefaultedList<ItemStack> inventory;

    public TreeTapBlockEntity(BlockPos pos, BlockState state) {
        super(ParadiseLostBlockEntityTypes.TREE_TAP, pos, state);
        inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

	public void handleUse(PlayerEntity player, Hand hand, ItemStack handStack) {
        ItemStack stored = inventory.get(0);
        if (!handStack.isEmpty() && inventory.get(0).isEmpty()) {
            inventory.set(0, handStack.split(1));
        } else {
            player.giveItemStack(stored);
            inventory.set(0, ItemStack.EMPTY);
        }
        markDirty();
	}

    public int[] getAvailableSlots(Direction side) {
        return new int[1];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return dir != Direction.DOWN && this.inventory.get(0).isEmpty();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > 1) {
            stack.setCount(1);
        }
        inventoryChanged();
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        inventory.set(0, list.get(0));
    }

    private void inventoryChanged() {
        markDirty();
        if (world != null && !world.isClient) updateInClientWorld();
    }

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
        this.inventory.clear();
		Inventories.readNbt(nbt, inventory);
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		Inventories.writeNbt(nbt, inventory);
	}

    @Override
    protected Text getContainerName() {
        return null;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return null;
    }

    public BlockState getTappedState() {
		return this.world.getBlockState(this.pos.offset(getCachedState().get(Properties.HORIZONTAL_FACING).getOpposite()));
	}

	public void tryCraft() {
		ItemStack stack = getStack(0);
		if (stack.isEmpty()) {
			return;
		}

		Optional<RecipeEntry<TreeTapRecipe>> recipe = this.world.getRecipeManager().getFirstMatch(ParadiseLostRecipeTypes.TREE_TAP_RECIPE_TYPE, this, this.getWorld());
		if (recipe.isPresent() && world.random.nextInt(recipe.get().value().getChance()) == 0) {
			ItemStack output = recipe.get().value().craft(this, world.getRegistryManager());
            Block convertBlock = recipe.get().value().getOutputBlock();
            BlockPos attachedPos = this.pos.offset(world.getBlockState(this.pos).get(TreeTapBlock.FACING).getOpposite());
            BlockState attachedBlock = world.getBlockState(attachedPos);
            if (convertBlock != Blocks.BEE_NEST) {
                stack.decrement(1);

                if (convertBlock != world.getBlockState(this.pos).getBlock()) {
                    world.setBlockState(attachedPos, convertBlock.getDefaultState(), 0);
                }
                if (!world.isClient) world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, world.getRandom().nextFloat() * 0.4f + 0.8f);

                this.inventory.set(0, output);
                inventoryChanged();
            } else if (attachedBlock.get(BeehiveBlock.HONEY_LEVEL) == 5) {
                stack.decrement(1);

                world.setBlockState(attachedPos, attachedBlock.with(BeehiveBlock.HONEY_LEVEL, 0));

                if (!world.isClient) world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, world.getRandom().nextFloat() * 0.4f + 0.8f);

                this.inventory.set(0, output);
                inventoryChanged();
            }
		}
        tryTansferItemsOut();
	}

    public void tryTansferItemsOut() {
        ItemStack stack = getStack(0);
        if (stack.isEmpty()) {
            return;
        }

        ItemStack contents = this.inventory.get(0);
        BlockEntity possibleHopper = world.getBlockEntity(pos.down());
        if (possibleHopper instanceof Inventory) {
            contents = HopperBlockEntity.transfer(this, (Inventory) possibleHopper, contents, Direction.UP);
        }
        this.inventory.set(0, contents);
        inventoryChanged();
    }

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		this.writeNbt(nbtCompound);
		return nbtCompound;
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	public void updateInClientWorld() {
		((ServerWorld) world).getChunkManager().markForUpdate(pos);
	}

}
