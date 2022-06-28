package ru.javarush.island.belyasnik.isLand.servises;

import ru.javarush.island.belyasnik.isLand.abstract_.Animal;
import ru.javarush.island.belyasnik.isLand.entity.*;
import ru.javarush.island.belyasnik.isLand.enums.IslandParam;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IslandMapWorker2 extends Thread {
    private final IslandMap islandMap;
    private final Layer[] layers;
    private final Long tact = IslandParam.TACT;
    private Dispatcher dispatcher;
    // запускаемый по расписанию пул потоков
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(IslandParam.NUMBER_OF_EXECUTOR_THREADS);


    public IslandMapWorker2(IslandMap islandMap) {
        this.islandMap = islandMap;
        this.layers = this.islandMap.getLayers();
        this.dispatcher = new Dispatcher(16);
    }

    @Override
    public void run() {

        scheduledThreadPool.scheduleAtFixedRate(
                () -> {


                    try {

                        this.islandMap.resetStatus(); // обнулить статусы всех организмов
                        emulation();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }

                , 0, tact, TimeUnit.MILLISECONDS);

    }

    public void emulation() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        ExecutorService animalExecutor = Executors.newFixedThreadPool(IslandParam.NUMBER_OF_EXECUTOR_THREADS);
        ExecutorService plantExecutor = Executors.newFixedThreadPool(IslandParam.NUMBER_OF_EXECUTOR_THREADS);
        //перебор слоёв с организмами
        for (int layerIdx = 0; layerIdx < layers.length; layerIdx++) {
            Layer layer = this.layers[layerIdx]; // слой
            Cell[][] cells = layer.getCells(); // получить ячейки слоя
            // проходим по каждой ячейке слоя
            for (int col = 0; col < cells.length; col++) {
                for (int row = 0; row < cells[col].length; row++) {
                    Cell cell = cells[col][row];
                    if (layerIdx == 0) {
                        // для растений - свой процесс роста растений
                        PlantWorker plantWorker = new PlantWorker(cells[col][row]);
                        plantExecutor.submit(plantWorker);
                    } else {
                        // Добыть очередь организмов в ячейке
                        IslandQueue animalIslandQueue = cell.getOrganisms();
                        //Последовательно перебираем все организмы
                        Iterator<Animal> iterator = animalIslandQueue.getDeque().descendingIterator();
                        for (int i = 0; ; ++i) {
                            if (iterator.hasNext()) {
                                Animal animal = iterator.next(); // получить очередной организм
                                // новорождённых не обрабатываем в этом такте
                                if (!animal.isNewBorn()) {
                                    AnimalWorker animalWorker = new AnimalWorker(islandMap, cell, animal);
                                    animalExecutor.submit(animalWorker);
                                }
                            } else break;
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
/*
        try {
            if (plantExecutor.awaitTermination(tact, TimeUnit.MILLISECONDS)
                    | animalExecutor.awaitTermination(tact, TimeUnit.MILLISECONDS)) {
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
*/
        //Thread.sleep(1000);
        //Собрать и вывести статистику по карте
        this.dispatcher.finishedOneElseAction();
        this.islandMap.printStat(this.dispatcher);
    }
}
