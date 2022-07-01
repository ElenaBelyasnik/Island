package ru.javarush.island.belyasnik.isLand.servises;

import ru.javarush.island.belyasnik.isLand.abstract_.Organism;
import ru.javarush.island.belyasnik.isLand.entity.*;
import ru.javarush.island.belyasnik.isLand.enums.IslandParam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;


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
        ExecutorService plantExecutor = Executors.newFixedThreadPool(IslandParam.NUMBER_OF_EXECUTOR_THREADS);
        ExecutorService animalExecutor = Executors.newFixedThreadPool(IslandParam.NUMBER_OF_EXECUTOR_THREADS);
        long tact = IslandParam.TACT;
        //перебор слоёв с организмами
        for (int layerIdx = 0; layerIdx < layers.length; layerIdx++) {
            Layer layer = this.layers[layerIdx]; // слой
            Cell[][] cells = layer.getCells(); // получить ячейки слоя
            // проходим по каждой ячейке слоя
            for (Cell[] value : cells) {
                for (Cell cell : value) {
                    if (layerIdx == 0) {
                        // для растений - свой процесс роста растений
                        growPlants(plantExecutor, cell);
                    } else {
                        // для животных свой процесс жизнедеятельности животных
                        submitAnimals(animalExecutor, cell);
                    }
                }
            }
        }

        // завершить все сервисы
        plantExecutor.shutdown();
        animalExecutor.shutdown();

        // если экзекутор остановлен, то выводим "FINISH"
        if (animalExecutor.isShutdown() & plantExecutor.isShutdown()) {
            System.out.println("FINISH");
        }
        //Собрать и вывести статистику по карте
        this.dispatcher.finishedOneElseAction();
        this.islandMap.printStat(this.dispatcher);
        this.islandMap.draw();
    }

    // высадка растений
    public void growPlants(ExecutorService executorService, Cell cell) {
        PlantWorker plantWorker = new PlantWorker(cell);
        executorService.submit(plantWorker);
    }

    // обработка жизнедеятельности животных
    public void submitAnimals(ExecutorService executorService, Cell cell) {
        // Добыть очередь организмов из ячейки
        IslandQueue<Organism> animalIslandQueue = cell.getOrganisms();
        //Последовательно перебираем все организмы
        Iterator<Organism> iterator = animalIslandQueue.getDeque().descendingIterator();
        while (iterator.hasNext()) {
            //List<AnimalWorker1> animalWorker1List = new ArrayList<>(); // список нитей
            List<Future<String>> futures = new ArrayList<>();
            AnimalWorker animalWorker;
            // добавляем в список задач по 100 задач
            for (int j = 0; j < 100 & iterator.hasNext(); j++) {
                Organism organism = iterator.next(); // получить очередной организм
                // новорождённых животных не обрабатываем в этом такте
                if (!organism.isNewBorn()) {
                    animalWorker = new AnimalWorker(islandMap, cell, organism);
                    futures.add(executorService.submit(animalWorker));
                }
            }
            executeFutures(futures); // выполнение списка запущенных задач
        }
    }

    // выполнение списка запущенных задач
    public void executeFutures(List<Future<String>> futures) {
        // обработка запущенных заданий
        boolean done = false;
        try {
            // проходим по списку futures и выводим их на консоль
            for (Future<String> future : futures) {
                // если задача не выполнена, пытаемся получить результат
                if (!future.isDone()) {
                    try {
                        future.get(); // получаем результат
                        //System.out.println(f.get());
                    } catch (CancellationException | ExecutionException | InterruptedException ignore) {
                    }
                }

            }
            done = true;
        } finally {
            // если  не сделано, то отменить
            if (!done)
                for (Future<String> future : futures)
                    future.cancel(true);
        }
    }
}
