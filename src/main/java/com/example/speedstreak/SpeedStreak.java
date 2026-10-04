package com.example.speedstreak;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemScatterer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SpeedStreak implements ModInitializer {
    public static final String MOD_ID = "speedstreak";

    // ---- Tweak these ----
    public static final int MAX_LEVEL = 5;
    /** true = you only lose a level when another player kills you; false = any death costs a level. */
    public static final boolean LOSE_LEVEL_ONLY_WHEN_KILLED_BY_PLAYER = false;
    /** true = every PvP kill drops a bottle; false = only when the killer is already at MAX_LEVEL. */
    public static final boolean ALWAYS_DROP_BOTTLE = false;
    // ---------------------

    public static final AttachmentType<Integer> SPEED_LEVEL = AttachmentRegistry.<Integer>create(
            Identifier.of(MOD_ID, "speed_level"),
            builder -> builder.persistent(Codec.INT).copyOnDeath());

    @Override
    public void onInitialize() {
        ModItems.init();

        ServerLivingEntityEvents.AFTER_DEATH.register(SpeedStreak::onDeath);

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> SpeedLevels.sync(newPlayer));

        // Re-applies the effect every second (covers joins, milk buckets, etc.)
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % 20 != 0) return;
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                SpeedLevels.sync(p);
            }
        });
    }

    private static void onDeath(LivingEntity entity, DamageSource source) {
        if (!(entity instanceof ServerPlayerEntity victim)) return;

        ServerPlayerEntity killer = null;
        if (source.getAttacker() instanceof ServerPlayerEntity k && k != victim) {
            killer = k;
        }

        // Victim loses a level
        if (killer != null || !LOSE_LEVEL_ONLY_WHEN_KILLED_BY_PLAYER) {
            SpeedLevels.add(victim, -1);
        }

        if (killer == null) return;

        int level = SpeedLevels.get(killer);
        if (level < MAX_LEVEL) {
            SpeedLevels.add(killer, 1);
            killer.sendMessage(Text.literal("Speed level " + (level + 1) + "/" + MAX_LEVEL), true);
        }

        // Maxed out (or always-drop on): victim drops a bottle instead
        if (level >= MAX_LEVEL || ALWAYS_DROP_BOTTLE) {
            if (victim.getEntityWorld() instanceof ServerWorld world) {
                ItemScatterer.spawn(world, victim.getX(), victim.getY(), victim.getZ(),
                        new ItemStack(ModItems.SPEED_BOTTLE));
            }
        }
    }
}
