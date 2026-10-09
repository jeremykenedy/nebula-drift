package com.jeremykenedy.nebuladrift;

import java.util.Random;

final class NebulaOptions {
    static final String[] PALETTES = {"Violet", "Blue", "Emerald", "Crimson"};
    static final String[] FORMS = {"Veils", "Pillars", "Supernova", "Dark matter"};
    static final int MIN_SPEED = 1;
    static final int MAX_SPEED = 5;
    static final int MIN_DENSITY = 1;
    static final int MAX_DENSITY = 5;
    static final int MIN_STARS = 1;
    static final int MAX_STARS = 5;

    final int palette;
    final int form;
    final int speed;
    final int density;
    final int stars;
    final int brightness;
    final boolean twinkle;
    final boolean meteors;
    final int randomMask;

    NebulaOptions(
            int palette,
            int form,
            int speed,
            int density,
            int stars,
            int brightness,
            boolean twinkle,
            boolean meteors,
            int randomMask) {
        this.palette = clamp(palette, 0, PALETTES.length - 1);
        this.form = clamp(form, 0, FORMS.length - 1);
        this.speed = clamp(speed, MIN_SPEED, MAX_SPEED);
        this.density = clamp(density, MIN_DENSITY, MAX_DENSITY);
        this.stars = clamp(stars, MIN_STARS, MAX_STARS);
        this.brightness = clamp(brightness, 1, 5);
        this.twinkle = twinkle;
        this.meteors = meteors;
        this.randomMask = randomMask & 0xff;
    }

    static NebulaOptions defaults() {
        return new NebulaOptions(0, 0, 2, 3, 3, 3, true, true, 0);
    }

    NebulaOptions resolve(long seed) {
        if (randomMask == 0) return this;
        Random random = new Random(seed);
        int resolvedPalette = choose(random, randomMask, 1, palette, 0, PALETTES.length - 1);
        int resolvedForm = choose(random, randomMask, 2, form, 0, FORMS.length - 1);
        int resolvedSpeed = choose(random, randomMask, 4, speed, MIN_SPEED, MAX_SPEED);
        int resolvedDensity = choose(random, randomMask, 8, density, MIN_DENSITY, MAX_DENSITY);
        int resolvedStars = choose(random, randomMask, 16, stars, MIN_STARS, MAX_STARS);
        int resolvedBrightness = choose(random, randomMask, 32, brightness, 1, 5);
        boolean resolvedTwinkle = (randomMask & 64) == 0 ? twinkle : random.nextBoolean();
        boolean resolvedMeteors = (randomMask & 128) == 0 ? meteors : random.nextBoolean();
        return new NebulaOptions(
                resolvedPalette,
                resolvedForm,
                resolvedSpeed,
                resolvedDensity,
                resolvedStars,
                resolvedBrightness,
                resolvedTwinkle,
                resolvedMeteors,
                0);
    }

    private static int choose(
            Random random, int mask, int bit, int current, int minimum, int maximum) {
        return (mask & bit) == 0 ? current : minimum + random.nextInt(maximum - minimum + 1);
    }

    static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
