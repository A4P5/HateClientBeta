package me.hatesvartt.hateclient.client.module.impl.chat;

import me.hatesvartt.hateclient.client.module.Category;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.settings.BooleanSetting;
import me.hatesvartt.hateclient.client.settings.ModeSetting;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class PrefixModule extends Module {

    private boolean registered = false;

    public PrefixModule() {
        super("Prefix", Category.CHAT);

        this.addSetting(new ModeSetting("Symbol", List.of(">", ":", "'"), ">"));
    }

    @Override
    public void onEnable() {
        if (!registered) {
            ClientSendMessageEvents.MODIFY_CHAT.register(message -> {
                ModeSetting prefix = (ModeSetting) getSetting("Symbol");

                if (isToggled() && !message.startsWith("/")) {
                    return prefix.getValue() + " " + message;
                }
                return message;
            });
            registered = true;
        }
    }

    @Override
    public void onDisable() { }

    @Override
    public void onTick() { }
}