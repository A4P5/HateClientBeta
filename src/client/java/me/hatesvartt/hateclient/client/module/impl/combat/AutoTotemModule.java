package me.hatesvartt.hateclient.client.module.impl.combat;

import me.hatesvartt.hateclient.client.module.Category;
import me.hatesvartt.hateclient.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class AutoTotemModule extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final int tickDelay = 2; // ticks between checks
    private int tickCounter = 0;

    private List<net.minecraft.network.packet.Packet<?>> packetsToSend = new ArrayList<>();

    public AutoTotemModule() {
        super("Auto Totem", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        tickCounter = 0;
        packetsToSend.clear();
    }

    @Override
    public void onDisable() {
        packetsToSend.clear();
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;

        tickCounter++;
        if (tickCounter < tickDelay) return;
        tickCounter = 0;

        PlayerEntity player = mc.player;

        // Offhand already has a totem
        if (player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;

        int totemSlot = findTotemSlot(player);
        if (totemSlot == -1) return;

        buildPackets(player, totemSlot);
        sendPackets();
    }

    private int findTotemSlot(PlayerEntity player) {
        for (int i = 0; i < 9; i++) { // Only hotbar
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) return i;
        }
        return -1; // no totem in hotbar
    }

    private void buildPackets(PlayerEntity player, int totemSlot) {
        packetsToSend.clear();

        int selectedSlot = player.getInventory().getSelectedSlot();
        if (totemSlot != selectedSlot) {
            // Temporarily switch to the totem slot
            packetsToSend.add(new UpdateSelectedSlotC2SPacket(totemSlot));
        }

        // Swap to offhand
        packetsToSend.add(new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                BlockPos.ORIGIN, Direction.DOWN
        ));

        if (totemSlot != selectedSlot) {
            // Revert back to original slot
            packetsToSend.add(new UpdateSelectedSlotC2SPacket(selectedSlot));
        }
    }

    private void sendPackets() {
        if (mc.getNetworkHandler() == null) return;
        for (net.minecraft.network.packet.Packet<?> packet : packetsToSend) {
            mc.getNetworkHandler().sendPacket(packet);
        }
        packetsToSend.clear();
    }
}
