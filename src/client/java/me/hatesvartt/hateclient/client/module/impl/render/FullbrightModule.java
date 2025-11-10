package me.hatesvartt.hateclient.client.module.impl.render;

import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.module.Category;
import me.hatesvartt.hateclient.client.settings.IntSetting;
import me.hatesvartt.hateclient.client.settings.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.world.LightType;

import java.util.List;

public class FullbrightModule extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public FullbrightModule() {
        super("Fullbright", Category.RENDER);
        this.addSetting(modeSetting);
        this.addSetting(minimumLightLevel);
    }

    public enum Mode {
        GAMMA,
        LUMINANCE,
        POTION
    }

    // Settings
    private final ModeSetting modeSetting = new ModeSetting("Mode", List.of("Gamma", "Luminance", "Potion"), "Gamma");
    private final IntSetting minimumLightLevel = new IntSetting("Minimum Light Level", 8, 0, 15);

    private LightType lightType = LightType.BLOCK;

    private Mode getMode() {
        return switch (modeSetting.getValue()) {
            case "Gamma" -> Mode.GAMMA;
            case "Luminance" -> Mode.LUMINANCE;
            case "Potion" -> Mode.POTION;
            default -> Mode.GAMMA;
        };
    }

    @Override
    public void onEnable() {
        if (getMode() == Mode.LUMINANCE && mc.worldRenderer != null) mc.worldRenderer.reload();
    }

    @Override
    public void onDisable() {
        Mode mode = getMode();
        if (mode == Mode.LUMINANCE && mc.worldRenderer != null) mc.worldRenderer.reload();
        else if (mode == Mode.POTION) disableNightVision();

        if (mode == Mode.GAMMA) disableGamma();
    }

    // ===================== Gamma Mode =====================
    private double originalGamma = 0.0;

    private void enableGamma() {
        if (mc.options != null && mc.options.getGamma() != null) {
            // Store the current gamma so we can restore it later
            originalGamma = mc.options.getGamma().getValue();
            // Set maximum gamma
            mc.options.getGamma().setValue(1.0);
        }
    }

    private void disableGamma() {
        if (mc.options != null && mc.options.getGamma() != null) {
            // Restore the original gamma
            mc.options.getGamma().setValue(originalGamma);
        }
    }

    // ===================== Luminance Mode =====================
    /**
     * Returns modified light level for Luminance mode.
     * Ensures blocks are at least the minimumLightLevel set by the user.
     */
    public int getModifiedLight(LightType type, int originalLight) {
        if (!isToggled() || getMode() != Mode.LUMINANCE || type != lightType) return originalLight;
        return Math.max(originalLight, minimumLightLevel.getValue());
    }

    // ===================== Potion Mode =====================
    @Override
    public void onTick() {
        if (!isToggled() || mc.player == null) return;

        Mode mode = getMode();

        if (mode == Mode.GAMMA) {
            enableGamma();
        }

        if (mode == Mode.POTION) {
            if (mc.player.hasStatusEffect(Registries.STATUS_EFFECT.getEntry(StatusEffects.NIGHT_VISION.value()))) {
                mc.player.removeStatusEffect(Registries.STATUS_EFFECT.getEntry(StatusEffects.NIGHT_VISION.value()));
            }
            mc.player.addStatusEffect(new StatusEffectInstance(
                    Registries.STATUS_EFFECT.getEntry(StatusEffects.NIGHT_VISION.value()),
                    4,
                    0
            ));
        }
    }

    private void disableNightVision() {
        if (mc.player == null) return;
        if (mc.player.hasStatusEffect(Registries.STATUS_EFFECT.getEntry(StatusEffects.NIGHT_VISION.value()))) {
            mc.player.removeStatusEffect(Registries.STATUS_EFFECT.getEntry(StatusEffects.NIGHT_VISION.value()));
        }
    }

    // ===================== Helpers =====================
    public boolean getGamma() {
        return isToggled() && getMode() == Mode.GAMMA;
    }
}
