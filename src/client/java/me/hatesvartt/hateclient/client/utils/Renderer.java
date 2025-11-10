package me.hatesvartt.hateclient.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

public class Renderer {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final float DEFAULT_SCALE = 0.025f;

    public static void renderEntityNameAboveHead(DrawContext context, Entity entity, float tickDelta) {
        if (entity == null || mc.player == null || entity == mc.player || mc.world == null) return;

        // Calculate distance from player for optional scaling
        double distance = mc.player.squaredDistanceTo(entity);
        float scale = (float) (DEFAULT_SCALE * (distance > 16 ? distance / 16f : 1f));

        MatrixStack matrices = new MatrixStack();
        matrices.push();

        // Translate to entity head position + small offset
        matrices.translate(entity.getX(), entity.getY() + entity.getHeight() + 0.5f, entity.getZ());

        // Rotate to face camera
        matrices.multiply(mc.gameRenderer.getCamera().getRotation());

        // Scale text
        matrices.scale(-scale, -scale, scale); // flip X/Y for correct facing

        // Prepare text
        String name = entity.getType().getName().getString();
        Text text = Text.literal(name);

        // Draw text in world space
        context.drawTextWithShadow(mc.textRenderer, text,
                -mc.textRenderer.getWidth(name)/2, // center X
                0, // Y
                0xFFFFFFFF); // White color

        matrices.pop();
    }
}
