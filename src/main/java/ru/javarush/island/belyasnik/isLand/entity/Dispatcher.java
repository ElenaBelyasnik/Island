package ru.javarush.island.belyasnik.isLand.entity;

import java.util.concurrent.atomic.AtomicInteger;

//* Диспетчер считает, чтобы были выполнены все действия цикла:
// есть, умереть/размножиться, перейти в другую ячейку - после этого цикл завершится
public class Dispatcher {
    private final int totalAction;
    private final AtomicInteger countTacts = new AtomicInteger(0); // сколько действий выполнено

    public Dispatcher(int totalAction) {
        this.totalAction = totalAction;
    }

    public AtomicInteger getCountTacts() {
        return countTacts;
    }

    // при завершении очередного действия, добавить счётчик
    public synchronized void finishedOneElseAction() {
        countTacts.getAndIncrement();
    }

    public boolean continueAction() {
        return countTacts.get() < this.totalAction;
    }

    public boolean finishDay() {
        return countTacts.get() == this.totalAction;
    }
}
