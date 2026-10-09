package com.jeremykenedy.nebuladrift;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;

final class NebulaSceneView extends GLSurfaceView {
    private final NebulaRenderer renderer;
    private final Handler frameHandler = new Handler(Looper.getMainLooper());
    private final Runnable frameRequest =
            new Runnable() {
                @Override
                public void run() {
                    requestRender();
                    if (running) frameHandler.postDelayed(this, 33L);
                }
            };
    private volatile boolean running;

    NebulaSceneView(Context context, NebulaOptions options) {
        super(context);
        setEGLContextClientVersion(2);
        renderer = new NebulaRenderer(options);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    void configure(NebulaOptions options) {
        renderer.configure(options);
        requestRender();
    }

    void startAnimation() {
        if (running) return;
        running = true;
        frameHandler.post(frameRequest);
    }

    void stopAnimation() {
        running = false;
        frameHandler.removeCallbacks(frameRequest);
    }
}
