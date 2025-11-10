package me.hatesvartt.hateclient.client.module.impl.render;

import me.hatesvartt.hateclient.client.module.Category;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.utils.Renderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;

public class MobOwnerModule extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public MobOwnerModule() {
        super("MobOwner", Category.RENDER);
    }

    @Override
    public void onEnable() {
        System.out.println("MobOwner enabled");
    }

    @Override
    public void onDisable() {
        System.out.println("MobOwner disabled");
    }

    @Override
    public String onRender(DrawContext context, float tickDelta) {
        if (!isToggled() || mc.world == null || mc.player == null) return null;

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            Renderer.renderEntityNameAboveHead(context, entity, tickDelta);
        }

        return null;
    }
}