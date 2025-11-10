package me.hatesvartt.hateclient.client.module.impl.render;

import me.hatesvartt.hateclient.client.module.Category;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.util.ModInfoUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class SplashModule extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public SplashModule() {
        super("Splash", Category.RENDER);
    }

    @Override
    public void onEnable() {
        System.out.println("Splash enabled");
    }

    @Override
    public void onDisable() {
        System.out.println("Splash disabled");
    }

    @Override
    public String onRender(DrawContext context, float tickDelta) {
        if (!isToggled()) return null;
        String display = ModInfoUtil.getModName() + " " + ModInfoUtil.getModVersion();
        context.drawTextWithShadow(mc.textRenderer, Text.of(display), 5, 5, 0xFFFFFFFF);
        return display;
    }
}