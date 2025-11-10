package me.hatesvartt.hateclient.client.gui;

import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.settings.*;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ModuleButton {
    private final Module module;
    private float x, y, width, height;
    private boolean expanded = false; // track if settings are open
    private IntSetting draggedSetting = null;

    public ModuleButton(Module module, float x, float y, float width, float height) {
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // ----------------------
    // Getters / Setters
    // ----------------------
    public Module getModule() { return module; }
    public float getHeight() { return height; }
    public float getWidth() { return width; }
    public float getX() { return x; }
    public float getY() { return y; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }

    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }

    // ----------------------
    // Mouse interaction
    // ----------------------
    public void mouseClicked(int mouseX, int mouseY, int button) {

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            if (button == 0) {
                module.toggle();      // left-click: toggle module
            } else if (button == 1) {
                expanded = !expanded; // right-click: expand/collapse settings

                // --- Add this block to unfocus text boxes when collapsing ---
                if (!expanded) {
                    for (Setting<?> s : module.getSettings()) {
                        if (s instanceof TextSetting t) {
                            t.setFocused(false);
                        }
                    }
                }
            }
        }


        if (expanded) {
            float settingY = y + height + 2;
            for (Setting<?> s : module.getSettings()) {

                // --- Boolean / Int / Mode ---
                if (mouseX >= x + 10 && mouseX <= x + 110 && mouseY >= settingY && mouseY <= settingY + 12) {
                    if (s instanceof BooleanSetting b && button == 0) b.setValue(!b.getValue());
                    else if (s instanceof IntSetting i && button == 0) draggedSetting = i;
                    else if (s instanceof ModeSetting m && button == 0) m.next();
                        // --- Keybind ---
                    else if (s instanceof KeybindSetting k && button == 0) {
                        if (k.isListening()) k.stopListening(); // cancel if already listening
                        else k.startListening(); // start listening for key input
                    } if (s instanceof TextSetting t) {
                        String label = t.getName() + ": ";
                        int labelWidth = MinecraftClient.getInstance().textRenderer.getWidth(label);
                        int boxX = (int) x + 10 + labelWidth;
                        int boxWidth = 80;
                        int boxY = (int) settingY - 2;

                        if (mouseX >= boxX - 2 && mouseX <= boxX + boxWidth && mouseY >= boxY && mouseY <= boxY + 12) {
                            t.setFocused(true);
                        } else {
                            t.setFocused(false);
                        }
                    }
                }

                settingY += 18;
            }
        }
    }

    public void mouseDragged(int mouseX, int mouseY, int button, double deltaX, double deltaY) {
        if (draggedSetting != null) {
            int barWidth = 100;
            float sliderX = mouseX - (x + 10);
            int value = (int) ((sliderX / barWidth) * (draggedSetting.getMax() - draggedSetting.getMin()) + draggedSetting.getMin());
            value = Math.max(draggedSetting.getMin(), Math.min(draggedSetting.getMax(), value));
            draggedSetting.setValue(value);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        draggedSetting = null; // stop dragging slider
    }

    // ----------------------
    // Rendering
    // ----------------------
    public void render(DrawContext context, int mouseX, int mouseY) {
        // Draw module name
        int color = module.isToggled() ? 0xFFFF4949 : 0xBFD3D3D3;
        context.drawText(MinecraftClient.getInstance().textRenderer, Text.of(module.getName()), (int)x + 5, (int)y + 5, color, false);

        // Draw expanded settings
        if (expanded) {
            float settingY = y + height + 2;
            for (Setting<?> s : module.getSettings()) {
                if (s instanceof KeybindSetting k) {
                    String text = k.getName() + ": " + (k.isListening() ? "Press a key..." : k.getKeyName());
                    context.drawText(MinecraftClient.getInstance().textRenderer, Text.of(text), (int)x + 10, (int)settingY, 0xFFFFFFFF, false);
                } else if (s instanceof BooleanSetting b) {
                    int boolColor = b.getValue() ? 0xFF00FF00 : 0xFFFF0000;
                    context.drawText(MinecraftClient.getInstance().textRenderer, Text.of(b.getName() + ": " + b.getValue()), (int)x + 10, (int)settingY, boolColor, false);
                } else if (s instanceof IntSetting i) {
                    String sliderText = i.getName() + ": " + i.getValue();
                    context.drawText(MinecraftClient.getInstance().textRenderer, Text.of(sliderText), (int)x + 10, (int)settingY, 0xFFFFFFFF, false);
                    int barWidth = 100;
                    float filled = ((float)(i.getValue() - i.getMin()) / (i.getMax() - i.getMin())) * barWidth;
                    context.fill((int)x + 10, (int)settingY + 12, (int)(x + 10 + barWidth), (int)settingY + 14, 0xFF555555); // background
                    context.fill((int)x + 10, (int)settingY + 12, (int)(x + 10 + filled), (int)settingY + 14, 0xFF00FF00); // filled
                } else if (s instanceof ModeSetting m) {
                    String text = m.getName() + ": " + m.getValue();
                    context.drawText(MinecraftClient.getInstance().textRenderer, Text.of(text), (int)x + 10, (int)settingY, 0xFFFFFFFF, false);
                } else if (s instanceof TextSetting t) {
                    String label = t.getName() + ": ";
                    int labelX = (int) x + 10;
                    int textY = (int) settingY;
                    int boxX = labelX + MinecraftClient.getInstance().textRenderer.getWidth(label);
                    int boxWidth = 80;
                    int boxY = textY - 2;

                    // Draw label
                    context.drawText(MinecraftClient.getInstance().textRenderer, Text.of(label),
                            labelX, textY, 0xFFFFFFFF, false);

                    // Draw input box background
                    int boxColor = t.isFocused() ? 0xFF444444 : 0xFF222222;
                    context.fill(boxX - 2, boxY, boxX + boxWidth, boxY + 12, boxColor);

                    // Draw text inside box
                    context.drawText(MinecraftClient.getInstance().textRenderer, Text.of(t.getValue()),
                            boxX, textY, 0xFFAAAAAA, false);

                    // Draw blinking cursor if focused
                    if (t.isFocused() && (System.currentTimeMillis() / 500) % 2 == 0) {
                        int cursorX = boxX + MinecraftClient.getInstance().textRenderer.getWidth(t.getValue());
                        context.fill(cursorX, textY, cursorX + 1, textY + 10, 0xFFFFFFFF);
                    }
                }
                settingY += 18;
            }
        }
    }
}
