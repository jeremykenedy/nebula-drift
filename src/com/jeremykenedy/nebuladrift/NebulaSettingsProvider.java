package com.jeremykenedy.nebuladrift;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

public final class NebulaSettingsProvider extends ContentProvider {
    static final String AUTHORITY = "com.jeremykenedy.nebuladrift.settings";

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(
            Uri uri, String[] projection, String selection, String[] args, String order) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return "application/json";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Read settings with call methods.");
    }

    @Override
    public int delete(Uri uri, String selection, String[] args) {
        throw new UnsupportedOperationException("Settings cannot be deleted.");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] args) {
        throw new UnsupportedOperationException("Use set_setting.");
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if ("get_schema".equals(method)) return result(schema());
        NebulaPreferences preferences = new NebulaPreferences(getContext());
        if ("get_settings".equals(method)) return result(settings(preferences.read()));
        if ("set_random_all".equals(method)) {
            NebulaOptions old = preferences.read();
            preferences.write(
                    new NebulaOptions(
                            old.palette,
                            old.form,
                            old.speed,
                            old.density,
                            old.stars,
                            old.brightness,
                            old.twinkle,
                            old.meteors,
                            255));
            return result(settings(preferences.read()));
        }
        if ("set_random_none".equals(method)) {
            NebulaOptions old = preferences.read();
            preferences.write(
                    new NebulaOptions(
                            old.palette,
                            old.form,
                            old.speed,
                            old.density,
                            old.stars,
                            old.brightness,
                            old.twinkle,
                            old.meteors,
                            0));
            return result(settings(preferences.read()));
        }
        if ("set_setting".equals(method)) {
            if (extras == null || !extras.containsKey("value"))
                throw new IllegalArgumentException("Missing setting value.");
            try {
                preferences.set(arg, extras.getString("value"));
                return result(settings(preferences.read()));
            } catch (IllegalArgumentException error) {
                throw error;
            }
        }
        throw new IllegalArgumentException("Unsupported settings method: " + method);
    }

    private Bundle result(String json) {
        Bundle bundle = new Bundle();
        bundle.putString("json", json);
        return bundle;
    }

    static String schema() {
        StringBuilder fields = new StringBuilder();
        appendChoice(fields, "palette", "Violet", NebulaOptions.PALETTES);
        appendChoice(fields, "form", "Veils", NebulaOptions.FORMS);
        appendInteger(fields, "speed", "2");
        appendInteger(fields, "density", "3");
        appendInteger(fields, "stars", "3");
        appendInteger(fields, "brightness", "3");
        appendBoolean(fields, "twinkle", "true");
        appendBoolean(fields, "meteors", "true");
        return "{\"schemaVersion\":1,\"provider\":\""
                + AUTHORITY
                + "\",\"fields\":["
                + fields
                + "]}";
    }

    private static void appendChoice(
            StringBuilder fields, String key, String defaultValue, String[] choices) {
        comma(fields);
        fields.append("{\"key\":")
                .append(quoted(key))
                .append(",\"type\":\"choice\",\"default\":")
                .append(quoted(defaultValue))
                .append(",\"random\":true,\"choices\":[");
        for (int i = 0; i < choices.length; i++) {
            if (i > 0) fields.append(',');
            fields.append(quoted(choices[i]));
        }
        fields.append("]}");
    }

    private static void appendInteger(StringBuilder fields, String key, String defaultValue) {
        comma(fields);
        fields.append("{\"key\":")
                .append(quoted(key))
                .append(",\"type\":\"integer\",\"default\":")
                .append(defaultValue)
                .append(",\"random\":true,\"minimum\":1,\"maximum\":5}");
    }

    private static void appendBoolean(StringBuilder fields, String key, String defaultValue) {
        comma(fields);
        fields.append("{\"key\":")
                .append(quoted(key))
                .append(",\"type\":\"boolean\",\"default\":")
                .append(defaultValue)
                .append(",\"random\":true}");
    }

    private static void comma(StringBuilder builder) {
        if (builder.length() > 0) builder.append(',');
    }

    private static String quoted(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    static String settings(NebulaOptions options) {
        return "{\"schemaVersion\":1,\"palette\":"
                + quoted(value(options, 1, NebulaOptions.PALETTES[options.palette]))
                + ",\"form\":"
                + quoted(value(options, 2, NebulaOptions.FORMS[options.form]))
                + ",\"speed\":"
                + quoted(value(options, 4, Integer.toString(options.speed)))
                + ",\"density\":"
                + quoted(value(options, 8, Integer.toString(options.density)))
                + ",\"stars\":"
                + quoted(value(options, 16, Integer.toString(options.stars)))
                + ",\"brightness\":"
                + quoted(value(options, 32, Integer.toString(options.brightness)))
                + ",\"twinkle\":"
                + quoted(value(options, 64, Boolean.toString(options.twinkle)))
                + ",\"meteors\":"
                + quoted(value(options, 128, Boolean.toString(options.meteors)))
                + "}";
    }

    private static String value(NebulaOptions options, int bit, String value) {
        return (options.randomMask & bit) == 0 ? value : "Random";
    }
}
