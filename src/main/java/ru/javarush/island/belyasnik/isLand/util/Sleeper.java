package ru.javarush.island.belyasnik.isLand.util;

public class Sleeper {
    public Sleeper() {
    }

    public static void sleep(int timeout) {
        try {
            Thread.sleep(timeout / 100);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }
}
