package com.jeremykenedy.nebuladrift;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class NebulaActivity extends Activity {
    private NebulaSceneView scene;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(48, 32, 48, 32);
        scene =
                new NebulaSceneView(
                        this, new NebulaPreferences(this).read().resolve(System.nanoTime()));
        root.addView(scene, new LinearLayout.LayoutParams(-1, 0, 1f));
        TextView title = new TextView(this);
        title.setText("Nebula Drift");
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));
        TextView summary = new TextView(this);
        summary.setText("Animated nebula fields, stars, and drifting light");
        summary.setTextSize(18);
        summary.setGravity(Gravity.CENTER);
        root.addView(summary, new LinearLayout.LayoutParams(-1, -2));
        Button preview = button("Open full-screen preview");
        preview.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(NebulaActivity.this, PreviewActivity.class));
                    }
                });
        root.addView(preview, new LinearLayout.LayoutParams(-1, -2));
        Button settings = button("Settings");
        settings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(NebulaActivity.this, SettingsActivity.class));
                    }
                });
        root.addView(settings, new LinearLayout.LayoutParams(-1, -2));
        setContentView(root);
    }

    private Button button(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextSize(18);
        button.setFocusable(true);
        return button;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (scene != null) {
            scene.onResume();
            scene.configure(new NebulaPreferences(this).read().resolve(System.nanoTime()));
            scene.startAnimation();
        }
    }

    @Override
    protected void onPause() {
        if (scene != null) {
            scene.stopAnimation();
            scene.onPause();
        }
        super.onPause();
    }
}
