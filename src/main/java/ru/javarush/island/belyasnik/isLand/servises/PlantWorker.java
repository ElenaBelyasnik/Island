package ru.javarush.island.belyasnik.isLand.servises;


import ru.javarush.island.belyasnik.isLand.bio.herbivores.Plant;
import ru.javarush.island.belyasnik.isLand.entity.Cell;
import ru.javarush.island.belyasnik.isLand.interfaces.PlantAction;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

//* нить, которая выращивает растения
public class PlantWorker implements Runnable, PlantAction {
    private final Cell cell;
    private static final String emoji = Plant.emoji;
    private static final String typeName = Plant.typeName;
    private final String threadName;
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1); // номер группы

    public PlantWorker(Cell cell) {
        this.cell = cell;
        this.threadName = Thread.currentThread().getThreadGroup().getName() + "-pool-" + POOL_NUMBER.getAndIncrement() + "-" + Thread.currentThread().getName();
    }

    // ежедневное создание растений
    //@Override
    public void run() {
        grow();
    }


    @Override
    public void grow() {
        synchronized (this.cell.getMonitor()) {
            try {
                this.cell.createInOneCell();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}


