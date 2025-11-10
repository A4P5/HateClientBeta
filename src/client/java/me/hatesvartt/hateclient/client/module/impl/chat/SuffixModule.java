package me.hatesvartt.hateclient.client.module.impl.chat;

import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.module.Category;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;

public class SuffixModule extends Module {

    public SuffixModule() {
        super("Suffix", Category.CHAT);

        // Register once globally
        ClientSendMessageEvents.MODIFY_CHAT.register(message -> {
            if (isToggled() && !message.startsWith("/")) {
                return message + " | \uD835\uDD25\uD835\uDD1E\uD835\uDD31\uD835\uDD22\uD835\uDD20\uD835\uDD29\uD835\uDD26\uD835\uDD22\uD835\uDD2B\uD835\uDD31";
            }
            return message;
        });
    }

    @Override
    public void onEnable() { }

    @Override
    public void onDisable() { }

    @Override
    public void onTick() { }
}
