package dev.Volatile.Chopper.AntiBan;

import dev.Volatile.Chopper.Main;
import java.util.concurrent.ThreadLocalRandom;
import static org.osbot.rs07.script.MethodProvider.random;

public class AntiBan {
    private final int[] coOrdinates = new int[] {
            255,
            52,
            345,
            28,
            209,
            377,
            25,
            70,
            353,
            25,
            695,
            58
    };

    /**
     *
     * @param main Main method
     * @param type Type of Anti ban movement
     */
    public void run(Main main, String type) {
        switch (type) {
            case "MOUSE" :
                main.getMouse().move(getRandomElement(coOrdinates), getRandomElement(coOrdinates));
                break;

            case "KEYBOARD":
                main.getCamera().moveYaw(random(0, 360));
                break;
        }
    }

    /**
     * Gets random element from Array.
     * @param arr Integer Array of elements
     * @return Array
     */
    public static int getRandomElement(int[] arr) {
        return arr[ThreadLocalRandom.current().nextInt(arr.length)];
    }
}
