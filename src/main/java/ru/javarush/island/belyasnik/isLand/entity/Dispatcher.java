package ru.javarush.island.belyasnik.isLand.entity;

import java.util.concurrent.atomic.AtomicInteger;

//* Диспетчер считает, чтобы были выполнены все действия цикла:
// есть, умереть/размножиться, перейти в другую ячейку - после этого цикл завершится
public class Dispatcher {
    private final AtomicInteger countTact = new AtomicInteger(0); // сколько действий выполнено

    public Dispatcher() {
    }

    public AtomicInteger getCountTact() {
        return countTact;
    }

    // при завершении очередного действия, увеличить счётчик тактов
    public synchronized void finishedOneElseAction() {
        countTact.getAndIncrement();
    }

}
