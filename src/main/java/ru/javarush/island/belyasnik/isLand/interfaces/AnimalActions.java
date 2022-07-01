package ru.javarush.island.belyasnik.isLand.interfaces;

import java.lang.reflect.InvocationTargetException;

public interface AnimalActions {
    //** Действия организма
    void move();

    void eat();

    boolean die();

    void reproduce() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

}
