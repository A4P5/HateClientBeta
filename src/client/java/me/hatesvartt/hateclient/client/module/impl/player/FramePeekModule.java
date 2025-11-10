package me.hatesvartt.hateclient.client.module.impl.player;

import me.hatesvartt.hateclient.client.module.Category;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.chat.SendMessage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class FramePeekModule extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public FramePeekModule() {
        super("Frame Peek", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            SendMessage.sendChatMessage("No player or world found.");
            toggle();
            return;
        }

        EntityHitResult hit = getLookedAtEntity(5);
        if (hit == null || !(hit.getEntity() instanceof ItemFrameEntity frame)) {
            SendMessage.sendChatMessage("You’re not looking at an item frame.");
            toggle();
            return;
        }

        ItemStack stack = frame.getHeldItemStack().copy();
        if (stack.isEmpty()) {
            SendMessage.sendChatMessage("The item frame is empty.");
            toggle();
            return;
        }

        // Try to insert the item into player’s inventory
        boolean inserted = mc.player.getInventory().insertStack(stack);
        if (inserted) {
            SendMessage.sendChatMessage("Item from frame added to your inventory!");
        } else {
            SendMessage.sendChatMessage("Inventory is full, couldn’t take item.");
        }

        toggle(); // Auto-disable after execution
    }

    /**
     * Manual entity raycast that works on the client side.
     */
    private EntityHitResult getLookedAtEntity(double range) {
        if (mc.player == null || mc.world == null) return null;

        Vec3d start = mc.player.getCameraPosVec(1.0F);
        Vec3d direction = mc.player.getRotationVec(1.0F);
        Vec3d end = start.add(direction.multiply(range));

        Entity closestEntity = null;
        double closestDistance = range;

        // Create an expanded box along the look vector
        Box box = mc.player.getBoundingBox()
                .stretch(direction.multiply(range))
                .expand(1.0D, 1.0D, 1.0D);

        // Loop through nearby entities and find the closest one intersecting our look ray
        List<Entity> entities = mc.world.getOtherEntities(mc.player, box, e -> e instanceof ItemFrameEntity);

        for (Entity entity : entities) {
            Box targetBox = entity.getBoundingBox().expand(0.3D);
            Vec3d intersection = targetBox.raycast(start, end).orElse(null);
            if (intersection != null) {
                double distance = start.distanceTo(intersection);
                if (distance < closestDistance) {
                    closestEntity = entity;
                    closestDistance = distance;
                }
            }
        }

        if (closestEntity != null) {
            return new EntityHitResult(closestEntity);
        }

        return null;
    }

    public void extractData(MapState data) {
        System.out.println(data);
    }

    @Override
    public void onDisable() {
        // Nothing persistent to clear
    }

    @Override
    public void onTick() {
        // No repeated logic — executes once per toggle
    }
}
