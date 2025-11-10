package me.hatesvartt.hateclient.client.settings;

public class TextSetting extends Setting<String> {
    private boolean focused = false;

    public TextSetting(String name, String defaultValue) {
        super(name, defaultValue); // call parent constructor
    }

    @Override
    public void setValue(String value) {
        // convert & → §
        if (value != null) value = value.replace('&', '§');
        super.setValue(value);
    }

    @Override
    public String getValue() {
        String value = super.getValue();
        return value != null ? value.replace('&', '§') : "";
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }
}
