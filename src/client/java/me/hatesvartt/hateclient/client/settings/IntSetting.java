package me.hatesvartt.hateclient.client.settings;

import me.hatesvartt.hateclient.client.module.Module;

public class IntSetting extends Setting<Integer> {

    private final int min;
    private final int max;

    public IntSetting(String name, int defaultValue, int min, int max) {
        super(name, defaultValue); // <-- pass defaultValue to parent constructor
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public void increase(int amount) {
        setValue(Math.min(getValue() + amount, max));
    }

    public void decrease(int amount) {
        setValue(Math.max(getValue() - amount, min));
    }

    public String getValueAsString() {
        return String.valueOf(getValue());
    }

    public void attachToModule(Module module) {
        module.addSetting(this);
    }
}