package com.jeremykenedy.nebuladrift;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class NebulaOptionsCoverageTest {
    @Test
    public void defaultsAndBoundsAreStable() {
        NebulaOptions options = NebulaOptions.defaults();
        assertEquals(0, options.palette);
        assertEquals(0, options.form);
        assertEquals(2, options.speed);
        assertEquals(3, options.density);
        assertEquals(3, options.stars);
        assertEquals(3, options.brightness);
        assertTrue(options.twinkle);
        assertTrue(options.meteors);
        assertEquals(0, options.randomMask);
        NebulaOptions bounded = new NebulaOptions(-1, 99, 0, 6, 0, 8, false, false, -1);
        assertEquals(0, bounded.palette);
        assertEquals(3, bounded.form);
        assertEquals(1, bounded.speed);
        assertEquals(5, bounded.density);
        assertEquals(1, bounded.stars);
        assertEquals(5, bounded.brightness);
        assertEquals(255, bounded.randomMask);
        assertEquals(3, NebulaOptions.clamp(3, 1, 5));
    }

    @Test
    public void onlyRandomFieldsChangeAndSeedRepeats() {
        NebulaOptions options = new NebulaOptions(0, 1, 2, 3, 4, 2, false, true, 1 | 4 | 64);
        NebulaOptions first = options.resolve(784);
        NebulaOptions second = options.resolve(784);
        assertEquals(first.palette, second.palette);
        assertEquals(first.speed, second.speed);
        assertEquals(first.twinkle, second.twinkle);
        assertEquals(first.meteors, second.meteors);
        assertEquals(0, first.randomMask);
        assertTrue(first.palette >= 0 && first.palette < NebulaOptions.PALETTES.length);
        assertTrue(first.speed >= 1 && first.speed <= 5);
        assertEquals(1, first.form);
        assertEquals(3, first.density);
        assertEquals(4, first.stars);
        assertEquals(2, first.brightness);
        assertTrue(first.meteors);
        NebulaOptions paletteOnly =
                new NebulaOptions(0, 0, 2, 3, 3, 3, true, false, 1).resolve(784);
        assertTrue(paletteOnly.twinkle);
        assertTrue(paletteOnly.meteors == false);
        NebulaOptions fixed = NebulaOptions.defaults();
        assertTrue(fixed == fixed.resolve(784));
    }

    @Test
    public void everyRandomChoiceUsesItsSupportedRange() {
        NebulaOptions all = new NebulaOptions(0, 0, 1, 1, 1, 1, false, false, 255);
        for (int seed = 0; seed < 32; seed++) {
            NebulaOptions resolved = all.resolve(seed);
            assertTrue(resolved.palette >= 0 && resolved.palette < NebulaOptions.PALETTES.length);
            assertTrue(resolved.form >= 0 && resolved.form < NebulaOptions.FORMS.length);
            assertTrue(resolved.speed >= 1 && resolved.speed <= 5);
            assertTrue(resolved.density >= 1 && resolved.density <= 5);
            assertTrue(resolved.stars >= 1 && resolved.stars <= 5);
            assertTrue(resolved.brightness >= 1 && resolved.brightness <= 5);
            assertEquals(0, resolved.randomMask);
        }
    }
}
