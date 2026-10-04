package com.example.speedstreak;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public final class ModItems {
    private ModItems() {}

    public static final RegistryKey<Item> SPEED_BOTTLE_KEY =
            RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SpeedStreak.MOD_ID, "speed_bottle"));

    public static final Item SPEED_BOTTLE = Registry.register(Registries.ITEM, SPEED_BOTTLE_KEY,
            new SpeedBottleItem(new Item.Settings()
                    .registryKey(SPEED_BOTTLE_KEY)
                    .maxCount(16)
                    .rarity(Rarity.UNCOMMON)
                    .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .component(DataComponentTypes.CONSUMABLE, ConsumableComponents.drink().build())
                    .useRemainder(Items.GLASS_BOTTLE)));

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register(entries -> entries.add(SPEED_BOTTLE));
    }
}
