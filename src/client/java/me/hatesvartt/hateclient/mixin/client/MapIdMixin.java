package me.hatesvartt.hateclient.mixin.client;

import me.hatesvartt.hateclient.client.module.impl.player.MapDownload;
import me.hatesvartt.hateclient.client.module.impl.exploit.MapSploitModule;
import me.hatesvartt.hateclient.client.utils.MapIdStorage;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MapIdMixin {

    @Inject(method = "onMapUpdate", at = @At("HEAD"))
    private void onMapUpdate(MapUpdateS2CPacket packet, CallbackInfo ci) {
        int mapId = packet.mapId().id();

        // Only download if armed
        MapDownload.downloadMapIfArmed(mapId, packet);

        // Store the last map ID in the helper
        MapIdStorage.setLastMapId(mapId);
    }
}
