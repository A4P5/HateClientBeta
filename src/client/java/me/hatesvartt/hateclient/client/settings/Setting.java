package me.hatesvartt.hateclient.client.settings;

public class Setting<T> {
    private final String name;
    private T value;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Set the value from an Object (e.g., deserialized from JSON)
     * Handles Number → Integer/Double conversion safely.
     */
    @SuppressWarnings("unchecked")
    public void setValueObject(Object obj) {
        if (obj == null) return;

        try {
            if (value instanceof Integer && obj instanceof Number) {
                value = (T) (Integer) ((Number) obj).intValue(); // Double → Integer
            } else if (value instanceof Double && obj instanceof Number) {
                value = (T) (Double) ((Number) obj).doubleValue();
            } else if (value instanceof Boolean && obj instanceof Boolean) {
                value = (T) obj;
            } else if (value instanceof String && obj instanceof String) {
                value = (T) obj;
            } else {
                value = (T) obj;
            }
        } catch (ClassCastException e) {
            System.out.println("Failed to set setting " + name + " from value: " + obj);
        }
    }
}
