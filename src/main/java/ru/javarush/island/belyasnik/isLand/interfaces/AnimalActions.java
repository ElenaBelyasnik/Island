package ru.javarush.island.belyasnik.isLand.interfaces;

import ru.javarush.island.belyasnik.isLand.entity.Cell;

import java.lang.reflect.InvocationTargetException;

public interface AnimalActions {
    //** Действия организма
    void goToCell(Cell cell);

    void eat();

    boolean die();

    void reproduct() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

}
