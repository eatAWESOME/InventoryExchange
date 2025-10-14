package eatawesome.inventoryexchange.gameObjs.container;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;

public class PortableCraftingMenu extends CraftingMenu {

    private static ContainerLevelAccess portableAccess(Player player) {
        BlockPos pos = player.blockPosition(); // arbitrary, not required to have a crafting table
        return ContainerLevelAccess.create(player.level(), pos);
    }

    public PortableCraftingMenu(int windowId, Inventory playerInv) {
        super(windowId, playerInv, portableAccess(playerInv.player));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            for (int i = 1; i <= 9; i++) {
                if (i < this.slots.size()) {
                    var slot = this.slots.get(i);
                    ItemStack stack = slot.getItem();
                    if (!stack.isEmpty()) {
                        player.drop(stack, false);
                        slot.set(ItemStack.EMPTY);
                    }
                }
            }
        }
    }
}