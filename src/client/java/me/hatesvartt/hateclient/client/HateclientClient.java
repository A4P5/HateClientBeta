package me.hatesvartt.hateclient.client;

import me.hatesvartt.hateclient.client.gui.HudScreen;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.module.ModuleManager;
import me.hatesvartt.hateclient.client.module.impl.chat.PrefixModule;
import me.hatesvartt.hateclient.client.module.impl.chat.SuffixModule;
import me.hatesvartt.hateclient.client.module.impl.combat.AutoTotemModule;
import me.hatesvartt.hateclient.client.module.impl.combat.KillAuraModule;
import me.hatesvartt.hateclient.client.module.impl.exploit.*;
import me.hatesvartt.hateclient.client.module.impl.movement.BoatFlyModule;
import me.hatesvartt.hateclient.client.module.impl.movement.ElytraFlyModule;
import me.hatesvartt.hateclient.client.module.impl.movement.NoGravityModule;
import me.hatesvartt.hateclient.client.module.impl.movement.StrafeModule;
import me.hatesvartt.hateclient.client.module.impl.player.*;
import me.hatesvartt.hateclient.client.module.impl.render.FullbrightModule;
import me.hatesvartt.hateclient.client.module.impl.render.MobOwnerModule;
import me.hatesvartt.hateclient.client.module.impl.render.SplashModule;
import me.hatesvartt.hateclient.util.ModuleConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import org.lwjgl.glfw.GLFW;

import java.awt.print.Book;
import java.lang.reflect.Field;

public class HateclientClient implements ClientModInitializer {

    public static HudScreen currentHudScreen = null;
    public static KeyBinding openHudScreen;
    private boolean modulesLoadedInWorld = false;

    @Override
    public void onInitializeClient() {

        // COMBAT
        KillAuraModule killAura = new KillAuraModule();
        AutoTotemModule totemModule = new AutoTotemModule();

        // RENDER
        SplashModule splashModule = new SplashModule();
        FullbrightModule fullbrightModule = new FullbrightModule();
        MobOwnerModule mobownerModule = new MobOwnerModule();

        // CHAT
        SuffixModule suffixModule = new SuffixModule();
        PrefixModule prefixModule = new PrefixModule();

        // PLAYER
        EntityDesync entityDesync = new EntityDesync();
        VelocityModule velocityModule = new VelocityModule();
        SelfDamageModule selfDamageModule = new SelfDamageModule();
        AutoFrameDupe autoFrameDupe = new AutoFrameDupe();
        BlinkModule blinkModule = new BlinkModule();
        NoFallModule nofallModule = new NoFallModule();
        FramePeekModule framepeekModule = new FramePeekModule();
        MapDownload mapDownload = new MapDownload();

        // MOVEMENT
        StrafeModule strafeModule = new StrafeModule();
        ElytraFlyModule elytraflyModule = new ElytraFlyModule();
        NoGravityModule nogravityModule = new NoGravityModule();
        BoatFlyModule boatflyModule = new BoatFlyModule();

        // EXPLOIT
        AutoPortalDupeModule autoportaldupeModule = new AutoPortalDupeModule();
        SpoofFrameModule spoofframeModule = new SpoofFrameModule();
        ActivatedSpawnerModule activatedSpawnerModule = new ActivatedSpawnerModule();
        MapSploitModule mapSploitModule = new MapSploitModule();
        BookExploit bookExploit = new BookExploit();


        ModuleManager.register(killAura);
        ModuleManager.register(activatedSpawnerModule);
        ModuleManager.register(mapSploitModule);
        ModuleManager.register(framepeekModule);
        ModuleManager.register(spoofframeModule);
        ModuleManager.register(bookExploit);
        ModuleManager.register(totemModule);
        ModuleManager.register(splashModule);
        ModuleManager.register(fullbrightModule);
        ModuleManager.register(autoFrameDupe);
        ModuleManager.register(suffixModule);
        ModuleManager.register(prefixModule);
        ModuleManager.register(entityDesync);
        ModuleManager.register(boatflyModule);
        ModuleManager.register(selfDamageModule);
        ModuleManager.register(mobownerModule);
        ModuleManager.register(strafeModule);
        ModuleManager.register(velocityModule);
        ModuleManager.register(elytraflyModule);
        ModuleManager.register(nofallModule);
        ModuleManager.register(nogravityModule);
        ModuleManager.register(blinkModule);
        ModuleManager.register(autoportaldupeModule);
        ModuleManager.register(mapDownload);

        // Load module settings (without toggling modules)
        ModuleConfig.loadSettingsOnly();

        StringBuilder sb = new StringBuilder("DamageSources:\n");
        for (Field field : DamageSource.class.getDeclaredFields()) {
            if (DamageSource.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    DamageSource ds = (DamageSource) field.get(null);
                    sb.append(field.getName()).append(" -> ").append(ds.getName()).append("\n");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(sb.toString());

        openHudScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hateclient.openhud",
                GLFW.GLFW_KEY_O,
                "category.hateclient.gui"
        ));

        HudRenderCallback.EVENT.register((context, renderTickCounter) -> {
            float tickDelta = renderTickCounter.getDynamicDeltaTicks();
            for (Module module : ModuleManager.getModules()) {
                if (module.isToggled()) module.onRender(context, tickDelta);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            // Load toggled states once a world is loaded
            if (!modulesLoadedInWorld && client.world != null) {
                ModuleConfig.load(); // restore toggled modules + settings
                modulesLoadedInWorld = true;
            }

            // Toggle HUD screen
            while (openHudScreen.wasPressed()) {
                if (currentHudScreen == null) currentHudScreen = new HudScreen();
                MinecraftClient.getInstance().setScreen(
                        MinecraftClient.getInstance().currentScreen instanceof HudScreen ? null : currentHudScreen
                );
            }

            // Tick all modules
            for (Module module : ModuleManager.getModules()) {
                module.checkKeybind();
                if (module.isToggled()) module.onTick();
            }
        });
    }
}