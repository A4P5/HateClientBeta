package me.hatesvartt.hateclient.client.gui;

import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.module.ModuleManager;
import me.hatesvartt.hateclient.client.settings.KeybindSetting;
import me.hatesvartt.hateclient.client.settings.Setting;
import me.hatesvartt.hateclient.client.settings.TextSetting;
import me.hatesvartt.hateclient.util.ModuleConfig;
import me.hatesvartt.hateclient.util.PanelConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class HudScreen extends Screen {

    private final List<HudPanel> panels = new ArrayList<>();

    public HudScreen() {
        super(Text.of("HateClient"));
        panels.clear();

        float panelX = 50;
        float panelStartY = 50;
        float panelSpacing = 60;
        List<String> addedPanels = new ArrayList<>();

        for (Module module : ModuleManager.getModules()) {
            if (module == null) continue;

            String panelName = module.getCategory().name(); // e.g., "COMBAT"
            panelName = panelName.substring(0, 1).toUpperCase() + panelName.substring(1).toLowerCase();
            float width = 150;
            float height = 18;

            float currentPanelY;
            if (!addedPanels.contains(panelName)) {
                currentPanelY = panelStartY;
                addedPanels.add(panelName);
                panelStartY += panelSpacing;
            } else {
                String finalPanelName = panelName;
                currentPanelY = panels.stream()
                        .filter(p -> p.getTitle().equals(finalPanelName))
                        .findFirst()
                        .map(HudPanel::getY)
                        .orElse(panelStartY);
            }

            addModuleToHud(module, panelName, panelX, currentPanelY, width, height);
        }

        PanelConfig.load(panels);
        ModuleConfig.load();
    }

    private void addModuleToHud(Module module, String panelName, float x, float panelY, float width, float height) {
        HudPanel panel = panels.stream().filter(p -> p.getTitle().equals(panelName)).findFirst().orElse(null);

        if (panel == null) {
            panel = new HudPanel(panelName, x, panelY, width, 100);
            panels.add(panel);
        }

        float y = panel.getButtons().size() * (height + 2);
        panel.addButton(new ModuleButton(module, x + 5, panelY + y, width - 10, height));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        for (HudPanel panel : panels) {
            panel.render(context, mouseX, mouseY);
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (HudPanel panel : panels) {
            for (ModuleButton button : panel.getButtons()) {
                for (Setting<?> s : button.getModule().getSettings()) {
                    if (s instanceof TextSetting t && t.isFocused()) {
                        // Add typed character to the value
                        t.setValue(t.getValue() + chr);
                        return true;
                    }
                }
            }
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean handled = false;

        // Backspace handling
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            for (HudPanel panel : panels) {
                for (ModuleButton button : panel.getButtons()) {
                    for (Setting<?> s : button.getModule().getSettings()) {
                        if (s instanceof TextSetting t && t.isFocused()) {
                            if (!t.getValue().isEmpty()) {
                                t.setValue(t.getValue().substring(0, t.getValue().length() - 1));
                            }
                            handled = true;
                        }
                    }
                }
            }
        }

        // Enter handling → unfocus
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            for (HudPanel panel : panels) {
                for (ModuleButton button : panel.getButtons()) {
                    for (Setting<?> s : button.getModule().getSettings()) {
                        if (s instanceof TextSetting t && t.isFocused()) {
                            t.setFocused(false);
                            handled = true;
                        }
                    }
                }
            }
        }

        if (handled) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (HudPanel panel : panels) panel.mouseClicked((int) mouseX, (int) mouseY, button);
        super.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (HudPanel panel : panels) panel.mouseReleased((int) mouseX, (int) mouseY, button);
        super.mouseReleased(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (HudPanel panel : panels) panel.mouseDragged((int) mouseX, (int) mouseY, button, deltaX, deltaY);
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return false;
    }

    @Override
    public void removed() {
        super.removed();
        PanelConfig.save(panels);
        ModuleConfig.save();
    }
}