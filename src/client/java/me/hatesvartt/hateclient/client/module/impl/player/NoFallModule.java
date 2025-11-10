package me.hatesvartt.hateclient.client.module.impl.player;

import me.hatesvartt.hateclient.client.module.*;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.settings.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class NoFallModule extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public NoFallModule() {
        super("No Fall", Category.PLAYER);

        this.addSetting(modeSetting);
    }

    public enum NoFallMode {
        PACKET,     // Send on-ground packet
        BOUNCE,     // Small upward motion before hitting ground
        VELOCITY,   // Cancel downward velocity
        ELYTRA,     // Use Elytra if available
        GROUND      // Spoof on-ground + fallDistance
    }

    private final ModeSetting modeSetting = new ModeSetting(
            "Mode",
            java.util.List.of("Packet", "Bounce", "Velocity", "Elytra", "Ground"),
            "Packet"
    );

    private NoFallMode getMode() {
        return switch (modeSetting.getValue()) {
            case "Bounce" -> NoFallMode.BOUNCE;
            case "Velocity" -> NoFallMode.VELOCITY;
            case "Elytra" -> NoFallMode.ELYTRA;
            case "Ground" -> NoFallMode.GROUND;
            default -> NoFallMode.PACKET;
        };
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        switch (getMode()) {
            case PACKET -> {
                if (player.fallDistance > 2) {
                    player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, true));
                }
            }

            case BOUNCE -> {
                // Only trigger if fallDistance > 3 (enough to cause damage)
                if (player.fallDistance > 3) {
                    // Check blocks below the player (up to 5 blocks)
                    int maxCheck = 5;
                    boolean groundNear = false;
                    BlockPos playerPos = player.getBlockPos();
                    for (int i = 1; i <= maxCheck; i++) {
                        if (!mc.world.isAir(playerPos.down(i))) {
                            groundNear = true;
                            break;
                        }
                    }

                    if (groundNear) {
                        // Apply a tiny upward velocity to prevent fall damage
                        Vec3d vel = player.getVelocity();
                        player.setVelocity(vel.x, 0.25, vel.z); // small bounce
                        player.fallDistance = 0; // reset fall distance
                    }
                }
            }

            case VELOCITY -> {
                if (player.fallDistance > 3 && player.getVelocity().y < 0) {
                    player.setVelocity(player.getVelocity().x, 0, player.getVelocity().z); // cancel downward motion
                    player.fallDistance = 0;
                }
            }

            case ELYTRA -> {
                if (player.fallDistance > 3 && !player.isGliding()) {
                    player.startGliding(); // activates Elytra to negate fall damage
                    player.fallDistance = 0;
                }
            }

            case GROUND -> {
                if (!player.isOnGround() && player.fallDistance > 2) {
                    player.setOnGround(true);
                    player.fallDistance = 0;
                    player.velocityModified = true;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        //
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.setOnGround(false);
        }
    }
}
