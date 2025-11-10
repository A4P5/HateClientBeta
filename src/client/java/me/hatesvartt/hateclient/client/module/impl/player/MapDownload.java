package me.hatesvartt.hateclient.client.module.impl.player;

import me.hatesvartt.hateclient.client.chat.SendMessage;
import me.hatesvartt.hateclient.client.module.Category;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.settings.ModeSetting;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.item.map.MapState;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MapDownload extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final ModeSetting formatSetting = new ModeSetting("Format", List.of(".png", ".jpg", ".webp", ".jpeg"), ".png");
    private static final ModeSetting sizeSetting = new ModeSetting("Size", List.of("128", "256", "512", "1028"), "1028");

    private static boolean armed = false; // true when module is enabled and waiting for a map packet
    private static boolean downloaded = false;  // set to true when a map has been downloaded

    public MapDownload() {
        super("Download Map", Category.PLAYER);
        this.addSetting(formatSetting);
        this.addSetting(sizeSetting);
    }

    @Override
    public void onEnable() {
        armed = true;
        SendMessage.sendChatMessage("§7Map download armed. Waiting for next map packet...");
    }

    @Override
    public void onDisable() {
        armed = false;
    }

    /**
     * Called by the Mixin when a MapUpdateS2CPacket is received
     */
    public static void downloadMapIfArmed(int mapId, MapUpdateS2CPacket packet) {
        if (!armed) return;
        armed = false; // reset
        MapIdComponent mapIdComponent = new MapIdComponent(mapId);
        assert mc.world != null;
        MapState state = mc.world.getMapState(mapIdComponent);
        if (state == null || state.colors == null) {
            SendMessage.sendChatMessage("§cMap data not loaded yet!");
            return;
        }

        downloadMap(mapId, state);
    }

    /**
     * Actual map download logic
     */
    public static void downloadMap(int mapId, MapState state) {
        int baseSize = 128;
        BufferedImage img = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < baseSize; y++) {
            for (int x = 0; x < baseSize; x++) {
                int colorIndex = state.colors[x + y * baseSize] & 255;
                img.setRGB(x, y, getMapColorRGB(colorIndex));
            }
        }

        int upscale = 1028; // or use sizeSetting
        BufferedImage scaled = new BufferedImage(upscale, upscale, BufferedImage.TYPE_INT_ARGB);
        scaled.getGraphics().drawImage(img, 0, 0, upscale, upscale, null);

        String serverName = mc.getCurrentServerEntry() != null
                ? mc.getCurrentServerEntry().address.replace(":", "_")
                : "singleplayer";

        File dir = new File(mc.runDirectory, "hateclient/" + serverName + "/mapdownloads/");
        if (!dir.exists()) dir.mkdirs();

        File output = new File(dir, mapId + ".png"); // or use formatSetting

        try {
            ImageIO.write(scaled, "png", output);
            SendMessage.sendChatMessage("§aMap saved to: §f" + output.getAbsolutePath());
            downloaded = true;
        } catch (IOException e) {
            SendMessage.sendChatMessage("§cError saving map: " + e.getMessage());
            downloaded = true;
        }
    }

    private static final int[] MAP_PALETTE = new int[]{
            MapColor.CLEAR.color, MapColor.PALE_GREEN.color, MapColor.PALE_YELLOW.color,
            MapColor.WHITE_GRAY.color, MapColor.BRIGHT_RED.color, MapColor.PALE_PURPLE.color,
            MapColor.IRON_GRAY.color, MapColor.DARK_GREEN.color, MapColor.WHITE.color,
            MapColor.LIGHT_BLUE_GRAY.color, MapColor.DIRT_BROWN.color, MapColor.STONE_GRAY.color,
            MapColor.WATER_BLUE.color, MapColor.OAK_TAN.color, MapColor.OFF_WHITE.color,
            MapColor.ORANGE.color,
            MapColor.MAGENTA.color,
            MapColor.LIGHT_BLUE.color,
            MapColor.YELLOW.color,
            MapColor.LIME.color,
            MapColor.PINK.color,
            MapColor.GRAY.color,
            MapColor.LIGHT_GRAY.color,
            MapColor.CYAN.color,
            MapColor.PURPLE.color,
            MapColor.BLUE.color,
            MapColor.BROWN.color,
            MapColor.GREEN.color,
            MapColor.RED.color,
            MapColor.BLACK.color,
            MapColor.GOLD.color,
            MapColor.DIAMOND_BLUE.color,
            MapColor.LAPIS_BLUE.color,
            MapColor.EMERALD_GREEN.color,
            MapColor.SPRUCE_BROWN.color,
            MapColor.DARK_RED.color,
            MapColor.TERRACOTTA_WHITE.color,
            MapColor.TERRACOTTA_ORANGE.color,
            MapColor.TERRACOTTA_MAGENTA.color,
            MapColor.TERRACOTTA_LIGHT_BLUE.color,
            MapColor.TERRACOTTA_YELLOW.color,
            MapColor.TERRACOTTA_LIME.color,
            MapColor.TERRACOTTA_PINK.color,
            MapColor.TERRACOTTA_GRAY.color,
            MapColor.TERRACOTTA_LIGHT_GRAY.color,
            MapColor.TERRACOTTA_CYAN.color,
            MapColor.TERRACOTTA_PURPLE.color,
            MapColor.TERRACOTTA_BLUE.color,
            MapColor.TERRACOTTA_BROWN.color,
            MapColor.TERRACOTTA_GREEN.color,
            MapColor.TERRACOTTA_RED.color,
            MapColor.TERRACOTTA_BLACK.color,
            MapColor.DULL_RED.color,
            MapColor.DULL_PINK.color,
            MapColor.DARK_CRIMSON.color,
            MapColor.TEAL.color,
            MapColor.DARK_AQUA.color,
            MapColor.DARK_DULL_PINK.color,
            MapColor.BRIGHT_TEAL.color,
            MapColor.DEEPSLATE_GRAY.color,
            MapColor.RAW_IRON_PINK.color,
            MapColor.LICHEN_GREEN.color,
    };

    private static int getMapColorRGB(int colorIndex) {
        if (colorIndex / 4 == 0) return 0;

        int baseColor = MAP_PALETTE[colorIndex / 4];
        int shade = colorIndex & 3;
        float brightness = switch (shade) {
            case 0 -> 0.71f;
            case 1 -> 0.86f;
            case 2 -> 1.0f;
            case 3 -> 0.53f;
            default -> 1.0f;
        };

        int r = Math.round(((baseColor >> 16) & 0xFF) * brightness);
        int g = Math.round(((baseColor >> 8) & 0xFF) * brightness);
        int b = Math.round((baseColor & 0xFF) * brightness);

        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public void onTick() {
        if (downloaded) {       // check if a map was downloaded
            downloaded = false; // reset the flag
            toggle();           // auto-disable module safely
        }
    }
}
