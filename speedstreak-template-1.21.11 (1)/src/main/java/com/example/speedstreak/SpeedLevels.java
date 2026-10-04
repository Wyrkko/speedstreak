package com.example.speedstreak;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public final class SpeedLevels {
    private SpeedLevels() {}

    public static int get(ServerPlayerEntity player) {
        return player.getAttachedOrElse(SpeedStreak.SPEED_LEVEL, 0);
    }

    public static void set(ServerPlayerEntity player, int level) {
        player.setAttached(SpeedStreak.SPEED_LEVEL, MathHelper.clamp(level, 0, SpeedStreak.MAX_LEVEL));
        sync(player);
    }

    public static void add(ServerPlayerEntity player, int delta) {
        set(player, get(player) + delta);
    }

    /** Makes the player's Speed effect match their stored level. */
    public static void sync(ServerPlayerEntity player) {
        if (!player.isAlive()) return;

        int level = get(player);
        StatusEffectInstance current = player.getStatusEffect(StatusEffects.SPEED);

        if (level <= 0) {
            if (current != null && current.isInfinite()) {
                player.removeStatusEffect(StatusEffects.SPEED);
            }
            return;
        }

        int wanted = level - 1;
        boolean needsUpdate = current == null
                || current.getAmplifier() < wanted
                || (current.isInfinite() && current.getAmplifier() != wanted);

        if (needsUpdate) {
            player.removeStatusEffect(StatusEffects.SPEED);
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED, StatusEffectInstance.INFINITE, wanted, false, false, true));
        }
    }
}
