package com.codingforcookies.betterrecords.crafting.recipe

import com.codingforcookies.betterrecords.api.sound.IShufflableSoundHolder
import com.codingforcookies.betterrecords.item.ModItems
import net.minecraft.init.Blocks
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistryEntry

class RecipeShufflable : IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {

    init {
        setRegistryName("recipeshufflable")
    }

    override fun canFit(width: Int, height: Int) = width * height >= 2

    override fun matches(inventoryCrafting: InventoryCrafting, worldIn: World?): Boolean {
        var foundShufflable = false
        var foundTorch = false

        (0 until inventoryCrafting.sizeInventory)
                .map { inventoryCrafting.getStackInSlot(it) }
                .filter { !it.isEmpty }
                .forEach {
                    if (it.item is IShufflableSoundHolder && it.hasTagCompound() && !foundShufflable) {
                        foundShufflable = true
                    } else if (it.item == Item.getItemFromBlock(Blocks.REDSTONE_TORCH) && !foundTorch) {
                        foundTorch = true
                    } else {
                        return false
                    }
                }

        return foundShufflable && foundTorch
    }

    override fun getCraftingResult(inventoryCrafting: InventoryCrafting): ItemStack? {
        val shufflable =
                (0 until inventoryCrafting.sizeInventory)
                        .map { inventoryCrafting.getStackInSlot(it) }
                        .filter { !it.isEmpty }
                        .find { it.item is IShufflableSoundHolder }

        return shufflable?.copy()?.apply {
            (this.item as IShufflableSoundHolder).setShufflable(this, true)
        }
    }

    override fun getRecipeOutput() = ItemStack(ModItems.itemNewRecord)

    override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> {
        inv.clear()
        return NonNullList.create()
    }
}
