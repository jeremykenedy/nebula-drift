package com.jeremykenedy.nebuladrift;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class SettingsActivity extends Activity {
    private static final String[] KEYS = {
        "palette", "form", "speed", "density", "stars", "brightness", "twinkle", "meteors"
    };
    private static final String[] LABELS = {
        "Color palette",
        "Nebula structure",
        "Drift speed",
        "Cloud density",
        "Star density",
        "Brightness",
        "Star twinkle",
        "Meteor trails"
    };
    private static final int[] RANDOM_BITS = {1, 2, 4, 8, 16, 32, 64, 128};
    private static final String[][] VALUES = {
        {"Violet", "Blue", "Emerald", "Crimson"},
        {"Veils", "Pillars", "Supernova", "Dark matter"},
        {"1", "2", "3", "4", "5"},
        {"1", "2", "3", "4", "5"},
        {"1", "2", "3", "4", "5"},
        {"1", "2", "3", "4", "5"},
        {"false", "true"},
        {"false", "true"}
    };

    private NebulaPreferences preferences;
    private LinearLayout rows;
    private int focusedRow = -1;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        preferences = new NebulaPreferences(this);
        ScrollView scroll = new ScrollView(this);
        rows = new LinearLayout(this);
        rows.setOrientation(LinearLayout.VERTICAL);
        rows.setPadding(36, 24, 36, 24);
        TextView title = new TextView(this);
        title.setText("Nebula Drift settings");
        title.setTextSize(28);
        title.setPadding(0, 0, 0, 14);
        rows.addView(title);
        Button randomAll = button("Randomize all settings");
        randomAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        focusedRow = -1;
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
                        renderRows();
                        rows.getChildAt(1).requestFocus();
                    }
                });
        rows.addView(randomAll, new LinearLayout.LayoutParams(-1, -2));
        Button fixedAll = button("Use fixed settings");
        fixedAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        focusedRow = -1;
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
                        renderRows();
                        rows.getChildAt(2).requestFocus();
                    }
                });
        rows.addView(fixedAll, new LinearLayout.LayoutParams(-1, -2));
        scroll.addView(rows);
        setContentView(scroll);
        renderRows();
    }

    private void renderRows() {
        while (rows.getChildCount() > 3) rows.removeViewAt(3);
        NebulaOptions current = preferences.read();
        String[] active = {
            NebulaOptions.PALETTES[current.palette],
            NebulaOptions.FORMS[current.form],
            Integer.toString(current.speed),
            Integer.toString(current.density),
            Integer.toString(current.stars),
            Integer.toString(current.brightness),
            Boolean.toString(current.twinkle),
            Boolean.toString(current.meteors)
        };
        Button[] settingButtons = new Button[KEYS.length];
        for (int i = 0; i < KEYS.length; i++)
            settingButtons[i] = addRow(i, active[i], (current.randomMask & RANDOM_BITS[i]) != 0);
        if (focusedRow >= 0 && focusedRow < settingButtons.length)
            settingButtons[focusedRow].requestFocus();
    }

    private Button addRow(int index, String active, boolean random) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOrientation(LinearLayout.HORIZONTAL);
        TextView label = new TextView(this);
        label.setText(LABELS[index]);
        label.setTextSize(20);
        row.addView(label, new LinearLayout.LayoutParams(0, -2, 1f));
        Button value = button(random ? "Random" : displayValue(index, active));
        value.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        focusedRow = index;
                        String next = nextValue(index, active, random);
                        preferences.set(KEYS[index], next);
                        renderRows();
                    }
                });
        row.addView(value, new LinearLayout.LayoutParams(320, -2));
        rows.addView(row, new LinearLayout.LayoutParams(-1, -2));
        return value;
    }

    private String nextValue(int index, String active, boolean random) {
        if (random) return VALUES[index][0];
        for (int i = 0; i < VALUES[index].length; i++) {
            if (VALUES[index][i].equalsIgnoreCase(active))
                return i + 1 == VALUES[index].length ? "random" : VALUES[index][i + 1];
        }
        return VALUES[index][0];
    }

    private String displayValue(int index, String value) {
        if (index == 6 || index == 7) return Boolean.parseBoolean(value) ? "On" : "Off";
        if (index >= 2)
            return new String[] {"Very low", "Low", "Balanced", "High", "Very high"}
                    [Integer.parseInt(value) - 1];
        return value;
    }

    private Button button(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(18);
        button.setFocusable(true);
        return button;
    }
}
