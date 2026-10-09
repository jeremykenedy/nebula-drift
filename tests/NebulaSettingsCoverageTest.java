package com.jeremykenedy.nebuladrift;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public final class NebulaSettingsCoverageTest {
    private NebulaPreferences preferences;
    private NebulaSettingsProvider provider;

    @Before
    public void setUp() {
        RuntimeEnvironment.getApplication()
                .getSharedPreferences(NebulaPreferences.FILE, 0)
                .edit()
                .clear()
                .commit();
        preferences = new NebulaPreferences(RuntimeEnvironment.getApplication());
        provider = Robolectric.buildContentProvider(NebulaSettingsProvider.class).create().get();
    }

    @Test
    public void defaultsPersistAndValidateEverySetting() throws Exception {
        NebulaOptions defaults = preferences.read();
        assertEquals(0, defaults.palette);
        assertEquals(0, defaults.form);
        assertEquals(2, defaults.speed);
        assertEquals(3, defaults.density);
        assertEquals(3, defaults.stars);
        assertEquals(3, defaults.brightness);
        assertTrue(defaults.twinkle);
        assertTrue(defaults.meteors);
        preferences.set("palette", "Emerald");
        preferences.set("form", "Pillars");
        preferences.set("speed", "5");
        preferences.set("density", "0");
        preferences.set("stars", "3");
        preferences.set("brightness", "1");
        preferences.set("twinkle", "off");
        preferences.set("meteors", "false");
        NebulaOptions saved = preferences.read();
        assertEquals(2, saved.palette);
        assertEquals(1, saved.form);
        assertEquals(5, saved.speed);
        assertEquals(1, saved.density);
        assertEquals(3, saved.stars);
        assertEquals(1, saved.brightness);
        assertEquals(false, saved.twinkle);
        assertEquals(false, saved.meteors);
        try {
            preferences.set("palette", "not-a-palette");
            org.junit.Assert.fail("Unknown palette must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Unsupported choice"));
        }
        try {
            preferences.set("stars", "not-a-number");
            org.junit.Assert.fail("Invalid integer must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("numeric"));
        }
        try {
            preferences.set("twinkle", "unknown");
            org.junit.Assert.fail("Invalid boolean must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("on, off, or random"));
        }
        assertEquals(2, preferences.read().palette);
        assertEquals(3, preferences.read().stars);
        assertEquals(false, preferences.read().twinkle);
        preferences.set("twinkle", "true");
        preferences.set("meteors", "on");
        assertTrue(preferences.read().twinkle);
        assertTrue(preferences.read().meteors);
        try {
            preferences.set("unknown", "value");
            fail("Unknown setting must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Unsupported setting"));
        }
    }

    @Test
    public void randomValuesAreIndependentAndCanBeReset() {
        preferences.set("palette", "random");
        preferences.set("speed", "random");
        assertEquals(5, preferences.read().randomMask);
        preferences.set("palette", "Blue");
        assertEquals(4, preferences.read().randomMask);
        preferences.set("speed", "4");
        assertEquals(0, preferences.read().randomMask);
        preferences.set("twinkle", "random");
        assertEquals(64, preferences.read().randomMask);
        preferences.write(new NebulaOptions(0, 1, 2, 3, 4, 5, true, false, 255));
        assertEquals(255, preferences.read().randomMask);
    }

    @Test
    public void providerExposesSchemaReadsAndWritesSettings() throws Exception {
        assertTrue(provider.onCreate());
        assertEquals(
                "application/json",
                provider.getType(Uri.parse("content://" + NebulaSettingsProvider.AUTHORITY)));
        JSONObject schema =
                new JSONObject(provider.call("get_schema", null, null).getString("json"));
        assertEquals(1, schema.getInt("schemaVersion"));
        assertEquals(8, schema.getJSONArray("fields").length());
        JSONObject settings =
                new JSONObject(provider.call("get_settings", null, null).getString("json"));
        assertEquals("Violet", settings.getString("palette"));
        Bundle extras = new Bundle();
        extras.putString("value", "Crimson");
        settings =
                new JSONObject(provider.call("set_setting", "palette", extras).getString("json"));
        assertEquals("Crimson", settings.getString("palette"));
        assertEquals(3, preferences.read().palette);
        extras.putString("value", "Random");
        settings =
                new JSONObject(provider.call("set_setting", "palette", extras).getString("json"));
        assertEquals("Random", settings.getString("palette"));
        assertEquals(1, preferences.read().randomMask);
        assertEquals(
                null,
                provider.query(
                        Uri.parse("content://" + NebulaSettingsProvider.AUTHORITY),
                        null,
                        null,
                        null,
                        null));
    }

    @Test
    public void providerRejectsUnknownCallsMissingValuesAndWritesThroughCrud() {
        try {
            provider.call("unknown", null, null);
            fail("Unknown provider methods must fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Unsupported settings method"));
        }
        try {
            provider.call("set_setting", "palette", new Bundle());
            fail("Missing value must fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Missing setting value"));
        }
        try {
            provider.call("set_setting", "palette", null);
            fail("Null extras must fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Missing setting value"));
        }
        Bundle extras = new Bundle();
        extras.putString("value", "Purple");
        try {
            provider.call("set_setting", "unsupported", extras);
            fail("Unknown key must fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Unsupported setting"));
        }
        try {
            provider.insert(Uri.EMPTY, new ContentValues());
            fail("Insert is unsupported");
        } catch (UnsupportedOperationException expected) {
            assertTrue(true);
        }
        try {
            provider.delete(Uri.EMPTY, null, null);
            fail("Delete is unsupported");
        } catch (UnsupportedOperationException expected) {
            assertTrue(true);
        }
        try {
            provider.update(Uri.EMPTY, new ContentValues(), null, null);
            fail("Update is unsupported");
        } catch (UnsupportedOperationException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void bulkRandomizationSwitchesAllFields() {
        provider.call("set_random_all", null, null);
        assertEquals(255, preferences.read().randomMask);
        provider.call("set_random_none", null, null);
        assertEquals(0, preferences.read().randomMask);
    }
}
