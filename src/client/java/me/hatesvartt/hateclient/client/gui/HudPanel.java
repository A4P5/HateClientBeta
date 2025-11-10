package me.hatesvartt.hateclient.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class HudPanel {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private float x, y;
    private final float width, height;
    private boolean dragging = false;
    private float dragOffsetX, dragOffsetY;
    private final String title;
    private final int headerHeight = 20; // draggable header
    private final List<ModuleButton> buttons = new ArrayList<>();

    public HudPanel(String title, float x, float y, float width, float height) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getTitle() {
        return title;
    }


    public void addButton(ModuleButton button) {
        buttons.add(button);
    }

    public List<ModuleButton> getButtons() {
        return buttons;
    }

    private boolean expanded = false;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void render(DrawContext context, int mouseX, int mouseY) {
        // Calculate total panel height dynamically
        float totalHeight = headerHeight + 2; // start with header height + small offset
        for (ModuleButton button : buttons) {
            totalHeight += button.getHeight() + 2; // button height + spacing
            if (button.isExpanded()) {
                totalHeight += button.getModule().getSettings().size() * 18; // add settings height
            }
        }

        // Draw panel background with dynamic height
        context.fill((int)x, (int)y, (int)(x + width), (int)(y + totalHeight), 0x88000000);

        // Draw header
        context.fill((int)x, (int)y, (int)(x + width), (int)(y + headerHeight), 0xAA222222);
        context.drawText(mc.textRenderer, Text.of(title), (int)x + 5, (int)y + 5, 0xFFFFFFFF, false);

        // Draw buttons below header
        float offsetY = y + headerHeight + 2;
        for (ModuleButton button : buttons) {
            button.setPosition(x + 5, offsetY);
            button.render(context, mouseX, mouseY);
            offsetY += button.getHeight() + 2;

            if (button.isExpanded()) {
                offsetY += button.getModule().getSettings().size() * 18;
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        // Drag only if clicked inside header
        if(button == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + headerHeight) {
            dragging = true;
            dragOffsetX = mouseX - x;
            dragOffsetY = mouseY - y;
        }

        // Pass clicks to buttons
        for(ModuleButton b : buttons) b.mouseClicked(mouseX, mouseY, button);
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        dragging = false;
        for(ModuleButton b : buttons) b.mouseReleased(mouseX, mouseY, button); // implement if needed
    }

    public void mouseDragged(int mouseX, int mouseY, int button, double deltaX, double deltaY) {
        if(dragging) {
            x = mouseX - dragOffsetX;
            y = mouseY - dragOffsetY;

            // Clamp to screen
            x = Math.max(0, Math.min(mc.getWindow().getScaledWidth() - width, x));
            y = Math.max(0, Math.min(mc.getWindow().getScaledHeight() - height, y));
        }

        for(ModuleButton b : buttons) b.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() { return x; }
    public float getY() { return y; }
}