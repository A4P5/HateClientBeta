package me.hatesvartt.hateclient.client.module.impl.player;

import me.hatesvartt.hateclient.client.module.*;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.settings.BooleanSetting;
import me.hatesvartt.hateclient.client.settings.IntSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.LinkedList;

public class BlinkModule extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final LinkedList<Packet<?>> packetQueue = new LinkedList<>();
    private long lastSendTime = 0;
    private OtherClientPlayerEntity fakePlayer;

    public BlinkModule() {
        super("Blink", Category.PLAYER);

        this.addSetting(new IntSetting("Interval", 500, 50, 5000)); // ms
        this.addSetting(new BooleanSetting("Visualize", true));
    }

    @Override
    public void onEnable() {
        packetQueue.clear();
        lastSendTime = System.currentTimeMillis();

        if (((BooleanSetting) getSetting("Visualize")).getValue()) {
            ClientPlayerEntity player = mc.player;
            if (player != null && mc.world != null) {
                // Create a fake player safely
                fakePlayer = new OtherClientPlayerEntity(mc.world, player.getGameProfile());
                fakePlayer.copyPositionAndRotation(player);
                fakePlayer.setHealth(player.getHealth());
                fakePlayer.noClip = true; // avoid collisions
                mc.world.addEntity(fakePlayer); // just add entity
            }
        }
    }

    @Override
    public void onDisable() {
        // Send all queued packets when disabled
        sendQueuedPackets();
        packetQueue.clear();

        // Remove fake player
        if (mc.world != null && fakePlayer != null) {
            mc.world.removeEntity(fakePlayer.getId(), Entity.RemovalReason.DISCARDED);
            fakePlayer = null;
        }
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        int interval = ((IntSetting) getSetting("Interval")).getValue();
        long now = System.currentTimeMillis();

        // Queue a full movement packet (position + rotation)
        PlayerMoveC2SPacket.Full packet = new PlayerMoveC2SPacket.Full(
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getYaw(),
                player.getPitch(),
                player.isOnGround(),
                player.horizontalCollision // include horizontal collision for correctness
        );
        packetQueue.add(packet);

        // Send queued packets if interval has passed
        if (now - lastSendTime >= interval) {
            sendQueuedPackets();
            lastSendTime = now;
        }
    }

    // Example send method
    private void sendQueuedPackets() {
        if (mc.getNetworkHandler() == null) return;

        for (Packet<?> p : packetQueue) {
            mc.getNetworkHandler().sendPacket(p);
        }
        packetQueue.clear();
    }
}