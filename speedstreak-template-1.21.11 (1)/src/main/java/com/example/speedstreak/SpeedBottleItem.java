package com.example.speedstreak;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class SpeedBottleItem extends Item {
    public SpeedBottleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            int level = SpeedLevels.get(player);
            if (level >= SpeedStreak.MAX_LEVEL) {
                player.sendMessage(Text.literal("You're already at max speed!"), true);
                return stack; // not consumed
            }
            SpeedLevels.add(player, 1);
        }
        return super.finishUsing(stack, world, user); // consumes it, returns glass bottle
    }
}
