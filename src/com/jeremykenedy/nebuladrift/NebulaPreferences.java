package com.jeremykenedy.nebuladrift;

import android.content.Context;
import android.content.SharedPreferences;

final class NebulaPreferences {
    static final String FILE = "nebula_preferences";
    private static final String PALETTE = "palette";
    private static final String FORM = "form";
    private static final String SPEED = "speed";
    private static final String DENSITY = "density";
    private static final String STARS = "stars";
    private static final String BRIGHTNESS = "brightness";
    private static final String TWINKLE = "twinkle";
    private static final String METEORS = "meteors";
    private static final String RANDOM_MASK = "random_mask";
    private final SharedPreferences preferences;

    NebulaPreferences(Context context) {
        preferences = context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    NebulaOptions read() {
        NebulaOptions defaults = NebulaOptions.defaults();
        return new NebulaOptions(
                preferences.getInt(PALETTE, defaults.palette),
                preferences.getInt(FORM, defaults.form),
                preferences.getInt(SPEED, defaults.speed),
                preferences.getInt(DENSITY, defaults.density),
                preferences.getInt(STARS, defaults.stars),
                preferences.getInt(BRIGHTNESS, defaults.brightness),
                preferences.getBoolean(TWINKLE, defaults.twinkle),
                preferences.getBoolean(METEORS, defaults.meteors),
                preferences.getInt(RANDOM_MASK, 0));
    }

    void write(NebulaOptions options) {
        preferences
                .edit()
                .putInt(PALETTE, options.palette)
                .putInt(FORM, options.form)
                .putInt(SPEED, options.speed)
                .putInt(DENSITY, options.density)
                .putInt(STARS, options.stars)
                .putInt(BRIGHTNESS, options.brightness)
                .putBoolean(TWINKLE, options.twinkle)
                .putBoolean(METEORS, options.meteors)
                .putInt(RANDOM_MASK, options.randomMask)
                .apply();
    }

    void set(String key, String value) {
        NebulaOptions old = read();
        int palette = old.palette;
        int form = old.form;
        int speed = old.speed;
        int density = old.density;
        int stars = old.stars;
        int brightness = old.brightness;
        boolean twinkle = old.twinkle;
        boolean meteors = old.meteors;
        int mask = old.randomMask;
        switch (key) {
            case "palette":
                palette = enumValue(value, NebulaOptions.PALETTES, palette);
                mask = setRandom(mask, 1, value);
                break;
            case "form":
                form = enumValue(value, NebulaOptions.FORMS, form);
                mask = setRandom(mask, 2, value);
                break;
            case "speed":
                speed = numericValue(value, 1, 5, speed);
                mask = setRandom(mask, 4, value);
                break;
            case "density":
                density = numericValue(value, 1, 5, density);
                mask = setRandom(mask, 8, value);
                break;
            case "stars":
                stars = numericValue(value, 1, 5, stars);
                mask = setRandom(mask, 16, value);
                break;
            case "brightness":
                brightness = numericValue(value, 1, 5, brightness);
                mask = setRandom(mask, 32, value);
                break;
            case "twinkle":
                twinkle = booleanValue(value, twinkle);
                mask = setRandom(mask, 64, value);
                break;
            case "meteors":
                meteors = booleanValue(value, meteors);
                mask = setRandom(mask, 128, value);
                break;
            default:
                throw new IllegalArgumentException("Unsupported setting: " + key);
        }
        write(
                new NebulaOptions(
                        palette, form, speed, density, stars, brightness, twinkle, meteors, mask));
    }

    private static int enumValue(String value, String[] values, int fallback) {
        if ("random".equalsIgnoreCase(value)) return fallback;
        for (int i = 0; i < values.length; i++) if (values[i].equalsIgnoreCase(value)) return i;
        throw new IllegalArgumentException("Unsupported choice: " + value);
    }

    private static int numericValue(String value, int minimum, int maximum, int fallback) {
        if ("random".equalsIgnoreCase(value)) return fallback;
        try {
            return NebulaOptions.clamp(Integer.parseInt(value), minimum, maximum);
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Expected a numeric setting value", error);
        }
    }

    private static boolean booleanValue(String value, boolean fallback) {
        if ("random".equalsIgnoreCase(value)) return fallback;
        if ("true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value) || "off".equalsIgnoreCase(value)) return false;
        throw new IllegalArgumentException("Expected an on, off, or random setting value");
    }

    private static int setRandom(int mask, int bit, String value) {
        if ("random".equalsIgnoreCase(value)) return mask | bit;
        return mask & ~bit;
    }
}
