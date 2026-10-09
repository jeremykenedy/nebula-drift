package com.jeremykenedy.nebuladrift;

import android.service.dreams.DreamService;

public final class NebulaDreamService extends DreamService {
    private NebulaSceneView scene;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(false);
        setFullscreen(true);
        setScreenBright(true);
        scene =
                new NebulaSceneView(
                        this, new NebulaPreferences(this).read().resolve(System.nanoTime()));
        setContentView(scene);
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        if (scene != null) {
            scene.configure(new NebulaPreferences(this).read().resolve(System.nanoTime()));
            scene.onResume();
            scene.startAnimation();
        }
    }

    @Override
    public void onDreamingStopped() {
        stopScene();
        super.onDreamingStopped();
    }

    @Override
    public void onDetachedFromWindow() {
        stopScene();
        scene = null;
        super.onDetachedFromWindow();
    }

    private void stopScene() {
        if (scene != null) {
            scene.stopAnimation();
            scene.onPause();
        }
    }
}
