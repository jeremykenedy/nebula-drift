package com.jeremykenedy.nebuladrift;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public final class PreviewActivity extends Activity {
    private NebulaSceneView scene;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(5894 | 512 | 1024);
        scene =
                new NebulaSceneView(
                        this, new NebulaPreferences(this).read().resolve(System.nanoTime()));
        scene.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
        setContentView(scene);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (scene != null) {
            scene.onResume();
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
