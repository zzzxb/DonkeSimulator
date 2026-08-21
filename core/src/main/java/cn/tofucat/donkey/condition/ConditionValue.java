package cn.tofucat.donkey.condition;

/**
 *
 * @author zzzxb
 * 2026/8/19
 */
public class ConditionValue {
    private final String name;
    private final String value;

    ConditionValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getString() {
        return value;
    }

    public String getString(String defaultValue) {
        return value == null || value.isEmpty() ? defaultValue : value;
    }

    public int getInt() {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getInt(int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public long getLong() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public long getLong(long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public float getFloat() {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    public float getFloat(float defaultValue) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean isEmpty() {
        return name == null || name.isEmpty();
    }

    @Override
    public String toString() {
        return name + ":" + value;
    }
}
