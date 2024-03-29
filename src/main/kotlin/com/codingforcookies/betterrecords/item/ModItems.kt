package com.codingforcookies.betterrecords.item

import com.codingforcookies.betterrecords.ID
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod.EventBusSubscriber(modid = ID)
object ModItems {

    val itemNewRecord: ModItem = ItemRecord("record")
    val itemFrequencyCrystal: ModItem = ItemFrequencyCrystal("frequencycrystal")
    val itemWire: ModItem = ItemWire("wire")
    val itemWireCutters: ModItem = ItemWireCutter("wirecutters")

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        event.registry.registerAll(
                itemNewRecord,
                itemFrequencyCrystal,
                itemWire,
                itemWireCutters
        )
    }
}
