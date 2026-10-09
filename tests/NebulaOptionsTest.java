package com.jeremykenedy.nebuladrift;

public final class NebulaOptionsTest {
    public static void main(String[] args) {
        defaultsAreBalancedAndAnimated();
        numericOptionsAreClamped();
        randomizationChangesOnlyEnabledFields();
        randomizationIsRepeatableForTheSameShowing();
        System.out.println("NebulaOptions tests passed");
    }

    private static void defaultsAreBalancedAndAnimated() {
        NebulaOptions options = NebulaOptions.defaults();
        require(options.palette == 0 && options.form == 0, "Default palette and cloud form");
        require(
                options.speed == 2 && options.density == 3 && options.stars == 3,
                "Default visual balance");
        require(
                options.brightness == 3 && options.twinkle && options.meteors,
                "Default motion and lighting");
        require(options.randomMask == 0, "Defaults are stable, not randomized");
    }

    private static void numericOptionsAreClamped() {
        NebulaOptions options = new NebulaOptions(-1, 99, 0, 8, -5, 99, false, false, -1);
        require(options.palette == 0 && options.form == 3, "Palette and form bounds");
        require(options.speed == 1 && options.density == 5 && options.stars == 1, "Motion bounds");
        require(
                options.brightness == 5 && options.randomMask == 255,
                "Brightness and random mask bounds");
        require(NebulaOptions.clamp(3, 1, 5) == 3, "Clamp leaves an in-range value unchanged");
    }

    private static void randomizationChangesOnlyEnabledFields() {
        NebulaOptions original = new NebulaOptions(0, 1, 2, 3, 4, 2, false, true, 1 | 4 | 64);
        NebulaOptions resolved = original.resolve(784);
        require(resolved.randomMask == 0, "Resolved setting is fixed for one showing");
        require(
                resolved.palette >= 0 && resolved.palette < NebulaOptions.PALETTES.length,
                "Random palette range");
        require(
                resolved.form == original.form && resolved.density == original.density,
                "Disabled random values are preserved");
        require(
                resolved.stars == original.stars && resolved.brightness == original.brightness,
                "Unselected values stay fixed");
    }

    private static void randomizationIsRepeatableForTheSameShowing() {
        NebulaOptions options = new NebulaOptions(0, 0, 2, 3, 3, 3, true, true, 255);
        NebulaOptions first = options.resolve(9451);
        NebulaOptions again = options.resolve(9451);
        require(
                first.palette == again.palette && first.form == again.form,
                "Choice seed stabilizes categorical values");
        require(
                first.speed == again.speed && first.density == again.density,
                "Choice seed stabilizes numeric values");
        require(
                first.stars == again.stars && first.brightness == again.brightness,
                "Choice seed stabilizes density and brightness");
        require(
                first.twinkle == again.twinkle && first.meteors == again.meteors,
                "Choice seed stabilizes effects");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
