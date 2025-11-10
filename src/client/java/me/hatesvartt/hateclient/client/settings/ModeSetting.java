package me.hatesvartt.hateclient.client.settings;

import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> options;

    public ModeSetting(String name, List<String> options, String defaultValue) {
        super(name, defaultValue);

        if (!options.contains(defaultValue)) {
            throw new IllegalArgumentException("Default value must be in options list");
        }

        this.options = options;
    }

    public List<String> getOptions() {
        return options;
    }

    // Cycle to next option
    public void next() {
        int index = options.indexOf(getValue());
        index = (index + 1) % options.size();
        setValue(options.get(index));
    }

    // Optional: cycle to previous
    public void previous() {
        int index = options.indexOf(getValue());
        index = (index - 1 + options.size()) % options.size();
        setValue(options.get(index));
    }
}