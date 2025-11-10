package me.hatesvartt.hateclient.client.chat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;

public class SendMessage {
    public static void sendChatMessage(String message) {
        System.out.println("Sending message: " + message);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            // Colors (hex RGB)
            int purple = 0xAA00FF;
            int blue   = 0x00BFFF;
            int pink   = 0xFF66CC;
            int white  = 0xFFFFFF;

            // Build the prefix: [ purple + "Hate" (blue) + "Client" (pink) + ] purple
            Text prefix = Text.literal("[")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(purple)))
                    .append(Text.literal("HateClient"))
                    .append(Text.literal("] ")
                            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(purple))));

            // Message body (white)
            Text body = Text.literal(message)
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(white)));

            // Send the final combined message
            client.player.sendMessage(prefix.copy().append(body), false);
        }
    }
}