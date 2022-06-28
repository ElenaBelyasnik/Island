package ru.javarush.island.belyasnik.isLand;

import ru.javarush.island.belyasnik.isLand.entity.IslandMap;
import ru.javarush.island.belyasnik.isLand.servises.IslandMapWorker2;

//** Класс для запуска эмуляции
public class Runner {
    public static void main(String[] args) {
        try {
            System.out.println("Инициализация карты...");
            IslandMap islandMap = new IslandMap(IslandMap.fillLayers());
            System.out.println("Инициализация карты завершена.");
            IslandMapWorker2 islandMapWorker = new IslandMapWorker2(islandMap);
            islandMapWorker.start();


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}

