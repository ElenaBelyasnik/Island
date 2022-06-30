package ru.javarush.island.belyasnik.isLand.servises;

import ru.javarush.island.belyasnik.isLand.abstract_.Organism;
import ru.javarush.island.belyasnik.isLand.entity.*;
import ru.javarush.island.belyasnik.isLand.enums.IslandParam;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IslandMapWorker extends Thread {
    private final IslandMap islandMap;
    private final Layer[] layers;
    private final Dispatcher dispatcher;
    // запускаемый по расписанию пул потоков
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(IslandParam.NUMBER_OF_EXECUTOR_THREADS);


    public IslandMapWorker(IslandMap islandMap) {
        this.islandMap = islandMap;
        this.layers = this.islandMap.getLayers();
        this.dispatcher = new Dispatcher();
    }

    @Override
    public void run() {
        Long tact = IslandParam.TACT;
        scheduledThreadPool.scheduleAtFixedRate(
                () -> {
                    try {
                        this.islandMap.resetStatus(); // обнулить статусы всех организмов
                        emulation();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException();
                    }
                }
                , 0, tact, TimeUnit.MILLISECONDS);

    }

    public void emulation() throws NoSuchFieldException, IllegalAccessException {
        long tact = IslandParam.TACT;
        ExecutorService animalExecutor = Executors.newFixedThreadPool(IslandParam.NUMBER_OF_EXECUTOR_THREADS);
        ExecutorService plantExecutor = Executors.newFixedThreadPool(IslandParam.NUMBER_OF_EXECUTOR_THREADS);
        //перебор слоёв с организмами
        for (int layerIdx = 0; layerIdx < layers.length; layerIdx++) {
            Layer layer = this.layers[layerIdx]; // слой
            Cell[][] cells = layer.getCells(); // получить ячейки слоя
            // проходим по каждой ячейке слоя
            for (Cell[] value : cells) {
                for (Cell cell : value) {
                    if (layerIdx == 0) {
                        // для растений - свой процесс роста растений
                        PlantWorker plantWorker = new PlantWorker(cell);
                        plantExecutor.submit(plantWorker);
                    } else {
                        // Добыть очередь организмов в ячейке
                        IslandQueue<Organism> animalIslandQueue = cell.getOrganisms();
                        //Последовательно перебираем все организмы
                        Iterator<Organism> iterator = animalIslandQueue.getDeque().descendingIterator();
                        while (iterator.hasNext()) {
                            Organism organism = iterator.next(); // получить очередной организм
                            // новорождённых не обрабатываем в этом такте
                            if (!organism.isNewBorn()) {
                                AnimalWorker animalWorker = new AnimalWorker(islandMap, cell, organism);
                                animalExecutor.submit(animalWorker);
                            }
                        }
                    }
                }
            }
            //  Thread.sleep(1000);
        }
        // завершить все сервисы
        plantExecutor.shutdown();
        animalExecutor.shutdown();
        // ещё раз завершить сервисы
        try {
            if (plantExecutor.awaitTermination(tact, TimeUnit.MILLISECONDS)
                    | animalExecutor.awaitTermination(tact, TimeUnit.MILLISECONDS)) {
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //Собрать и вывести статистику по карте
        this.dispatcher.finishedOneElseAction();
        this.islandMap.printStat(this.dispatcher);
        this.islandMap.draw();


    }
}
