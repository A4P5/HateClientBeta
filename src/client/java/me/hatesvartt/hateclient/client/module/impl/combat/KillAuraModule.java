package me.hatesvartt.hateclient.client.module.impl.combat;

import me.hatesvartt.hateclient.client.module.*;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.settings.BooleanSetting;
import me.hatesvartt.hateclient.client.settings.IntSetting;
import me.hatesvartt.hateclient.client.settings.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;

public class KillAuraModule extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private LivingEntity currentTarget;

    public KillAuraModule() {
        super("Kill Aura", Category.COMBAT);

        this.addSetting(new IntSetting("Delay (m/s)", 5, 1, 30));
        this.addSetting(new IntSetting("Range", 5, 1, 10));
        this.addSetting(new ModeSetting("Priority", List.of("Closest", "LowestHealth", "HighestHealth"), "Closest"));
        this.addSetting(new ModeSetting("Criticals", List.of("None", "Packet", "Vanilla"), "None"));
        this.addSetting(new BooleanSetting("Player Only", true));
        this.addSetting(new BooleanSetting("Sword Only", true));
        this.addSetting(new BooleanSetting("Swing", true)); // toggle swinging
    }

    @Override
    public void onEnable() {
        //
    }

    @Override
    public void onDisable() {
        currentTarget = null;
    }


    private int vanillaCritDelay = 0; // tick delay for Vanilla crits

    private long lastAttackTime = 0;

    private boolean attackCooldownPassed(int delay) {
        long now = System.currentTimeMillis();
        long adjustedDelay = delay * 50L; // convert delay to ms (your m/s setting)
        if (now - lastAttackTime >= adjustedDelay) {
            lastAttackTime = now;
            return true; // attack is allowed
        }
        return false; // still on cooldown
    }

    @Override
    public void onTick() {
        if (!isToggled() || mc.player == null || mc.world == null) return;

        int range = ((IntSetting) getSetting("Range")).getValue();
        boolean playerOnly = ((BooleanSetting) getSetting("Player Only")).getValue();
        boolean swordOnly = ((BooleanSetting) getSetting("Sword Only")).getValue();
        String criticals = ((ModeSetting) getSetting("Criticals")).getValue();
        boolean swingEnabled = ((BooleanSetting) getSetting("Swing")).getValue();
        String priority = ((ModeSetting) getSetting("Priority")).getValue();
        int delay = ((IntSetting) getSetting("Delay (m/s)")).getValue();

        // Clear target if out of range or dead
        if (currentTarget != null && (mc.player.squaredDistanceTo(currentTarget) > range * range || !currentTarget.isAlive())) {
            currentTarget = null;
        }

        currentTarget = null; // reset target each tick
        double bestScore = Double.MAX_VALUE;

        for (var entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (entity == mc.player) continue;
            if (playerOnly && !(entity instanceof PlayerEntity)) continue;
            if (mc.player.squaredDistanceTo(entity) > range * range) continue;

            double score = switch (priority) {
                case "LowestHealth" -> living.getHealth();
                case "HighestHealth" -> -living.getHealth();
                default -> mc.player.squaredDistanceTo(living);
            };

            if (score < bestScore) {
                bestScore = score;
                currentTarget = living;
            }
        }





        // Attack logic
        if (currentTarget != null) {
            double attackRange = range + 0.3; // hitbox buffer
            if (!currentTarget.isAlive() || mc.player.squaredDistanceTo(currentTarget) > attackRange * attackRange) {
                currentTarget = null;
                return;
            }

            if (!attackCooldownPassed(delay)) return;

            // --- Sword Only check ---
            if (swordOnly) {
                //
            }

            // --- Criticals ---
            if ("Packet".equals(criticals) && mc.player.isOnGround()) {
                double y = mc.player.getY();
                mc.player.networkHandler.sendPacket(
                        new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), y + 0.0625, mc.player.getZ(), false, false)
                );
                mc.player.networkHandler.sendPacket(
                        new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), y + 0.001, mc.player.getZ(), false, false)
                );
            } else if ("Vanilla".equals(criticals)) {
                if (mc.player.isOnGround() && vanillaCritDelay == 0) {
                    mc.player.jump();
                    vanillaCritDelay = 1; // attack next tick while falling
                } else if (vanillaCritDelay > 0) {
                    vanillaCritDelay--;
                    if (vanillaCritDelay == 0) {
                        mc.interactionManager.attackEntity(mc.player, currentTarget);
                        if (swingEnabled) mc.player.swingHand(mc.player.getActiveHand());
                        return; // skip normal attack this tick
                    }
                }
            }

            // Normal attack
            mc.interactionManager.attackEntity(mc.player, currentTarget);

            // Swing hand if enabled
            if (swingEnabled) {
                mc.player.swingHand(mc.player.getActiveHand());
            }
        }
    }
}