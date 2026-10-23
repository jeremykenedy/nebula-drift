package com.jeremykenedy.nebuladrift;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.format.DateFormat;

import java.util.Date;

final class ClockOverlay {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final java.text.DateFormat format;
    private final Date now = new Date();

    ClockOverlay(Context context) {
        format = DateFormat.getTimeFormat(context);
        paint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setColor(0xfff5f5f5);
    }

    void draw(Canvas canvas, int width, int height, float seconds) {
        now.setTime(System.currentTimeMillis());
        paint.setTextSize(height * 0.055f);
        paint.setShadowLayer(height * 0.008f, 0f, height * 0.002f, 0xcc000000);
        float x = width * (0.92f + 0.015f * (float) Math.sin(seconds / 31f));
        float y = height * (0.88f + 0.018f * (float) Math.cos(seconds / 43f));
        canvas.drawText(format.format(now), x, y, paint);
    }
}
