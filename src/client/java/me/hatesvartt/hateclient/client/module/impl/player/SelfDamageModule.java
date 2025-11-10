package me.hatesvartt.hateclient.client.module.impl.player;

import me.hatesvartt.hateclient.client.module.*;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.settings.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.Objects;

public class SelfDamageModule extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private DamageSources damageSources;

    public SelfDamageModule() {
        super("Self Damage", Category.PLAYER);

        this.addSetting(modeSetting);
    }

    private final ModeSetting modeSetting = new ModeSetting(
            "DamageSource",
            java.util.List.of(
                    "inFire",
                    "campfire",
                    "lightningBolt",
                    "onFire",
                    "lava",
                    "hotFloor",
                    "inWall",
                    "cramming",
                    "drown",
                    "starve",
                    "cactus",
                    "fall",
                    "flyIntoWall",
                    "outOfWorld",
                    "generic",
                    "magic",
                    "wither",
                    "dragonBreath",
                    "dryOut",
                    "sweetBerryBush",
                    "freeze",
                    "stalagmite",
                    "outsideBorder",
                    "genericKill"
            ),
            "inFire" //def value
    );

    private void initDamageSources() {
        if (damageSources != null) return;
        if (mc.getServer() == null) return;

        MinecraftServer server = mc.getServer();
        DynamicRegistryManager drm = server.getRegistryManager();
        damageSources = new DamageSources(drm);
    }

    private DamageSource getSelectedDamageSource() {

        return switch (modeSetting.getValue()) {
            case "inFire" -> damageSources.inFire();
            case "campfire" -> damageSources.campfire();
            case "lightningBolt" -> damageSources.lightningBolt();
            case "onFire" -> damageSources.onFire();
            case "lava" -> damageSources.lava();
            case "hotFloor" -> damageSources.hotFloor();
            case "inWall" -> damageSources.inWall();
            case "cramming" -> damageSources.cramming();
            case "drown" -> damageSources.drown();
            case "starve" -> damageSources.starve();
            case "cactus" -> damageSources.cactus();
            case "fall" -> damageSources.fall();
            case "flyIntoWall" -> damageSources.flyIntoWall();
            case "outOfWorld" -> damageSources.outOfWorld();
            case "generic" -> damageSources.generic();
            case "magic" -> damageSources.magic();
            case "wither" -> damageSources.wither();
            case "dragonBreath" -> damageSources.dragonBreath();
            case "dryOut" -> damageSources.dryOut();
            case "sweetBerryBush" -> damageSources.sweetBerryBush();
            case "freeze" -> damageSources.freeze();
            case "stalagmite" -> damageSources.stalagmite();
            case "outsideBorder" -> damageSources.outsideBorder();
            case "genericKill" -> damageSources.genericKill();
            default -> damageSources.generic();
        };
    }

    @Override
    public void onTick() {
        initDamageSources();

        ClientPlayerEntity player = mc.player;
        if (player == null || damageSources == null) return;

        ServerWorld world = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD); // from a ServerPlayerEntity

        DamageSource ds = getSelectedDamageSource();
        player.damage(world, ds, 1.0f); // apply 1 damage
        toggle(); // auto-disable after one tick
    }

    @Override
    public void onEnable() {
        ClientPlayerEntity player = mc.player;
        if (player != null) {
            player.sendMessage(
                    net.minecraft.text.Text.literal("SelfDamageModule enabled, using " + modeSetting.getValue()),
                    false
            );
        }
    }

    @Override
    public void onDisable() {
        ClientPlayerEntity player = mc.player;
        if (player != null) {
            player.sendMessage(
                    net.minecraft.text.Text.literal("SelfDamageModule disabled"),
                    false
            );
        }
    }
}
