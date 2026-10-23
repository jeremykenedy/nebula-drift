package com.jeremykenedy.nebuladrift;

import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.FrameLayout;

final class NebulaSceneView extends FrameLayout {
    private final GLSurfaceView surface;
    private final NebulaRenderer renderer;
    private final ClockView clock;
    private final Handler frameHandler = new Handler(Looper.getMainLooper());
    private final Runnable frameRequest =
            new Runnable() {
                @Override
                public void run() {
                    surface.requestRender();
                    if (clock.getVisibility() == View.VISIBLE) clock.invalidate();
                    if (running) frameHandler.postDelayed(this, 33L);
                }
            };
    private boolean running;

    NebulaSceneView(Context context, NebulaOptions options) {
        super(context);
        surface = new GLSurfaceView(context);
        surface.setEGLContextClientVersion(2);
        renderer = new NebulaRenderer(options);
        surface.setRenderer(renderer);
        surface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        addView(surface, new LayoutParams(-1, -1));
        clock = new ClockView(context);
        addView(clock, new LayoutParams(-1, -1));
        updateClock();
    }

    void configure(NebulaOptions options) {
        renderer.configure(options);
        updateClock();
        surface.requestRender();
    }

    private void updateClock() {
        clock.setVisibility(
                new NebulaPreferences(getContext()).showClock() ? View.VISIBLE : View.GONE);
    }

    void onResume() {
        surface.onResume();
    }

    void onPause() {
        surface.onPause();
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

    static final class ClockView extends View {
        private final ClockOverlay overlay;
        private final long started = SystemClock.uptimeMillis();

        ClockView(Context context) {
            super(context);
            overlay = new ClockOverlay(context);
            setFocusable(false);
            setClickable(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            overlay.draw(
                    canvas,
                    getWidth(),
                    getHeight(),
                    (SystemClock.uptimeMillis() - started) / 1000f);
        }
    }
}
