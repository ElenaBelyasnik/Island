package ru.javarush.island.belyasnik.isLand.servises;

import ru.javarush.island.belyasnik.isLand.abstract_.Animal;
import ru.javarush.island.belyasnik.isLand.bio.herbivores.Plant;
import ru.javarush.island.belyasnik.isLand.entity.Cell;
import ru.javarush.island.belyasnik.isLand.entity.IslandMap;
import ru.javarush.island.belyasnik.isLand.entity.IslandQueue;
import ru.javarush.island.belyasnik.isLand.entity.Layer;
import ru.javarush.island.belyasnik.isLand.enums.IslandParam;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IslandMapWorker1 extends Thread {
    private final IslandMap islandMap;
    private final Layer[] layers;
    ExecutorService animalExecutor = Executors.newFixedThreadPool(IslandParam.NUMBER_OF_EXECUTOR_THREADS);
    ExecutorService plantExecutor = Executors.newFixedThreadPool(IslandParam.NUMBER_OF_EXECUTOR_THREADS);


    public IslandMapWorker1(IslandMap islandMap) {
        this.islandMap = islandMap;
        this.layers = this.islandMap.getLayers();
    }


    @Override
    public void run() {

        try {
            processAnimal();
        } catch (InterruptedException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void processAnimal() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        //перебор слоёв с животными

        for (int layerIdx = 1; layerIdx < layers.length; layerIdx++) {
            // Экзекутор для роста растений
            this.createPlantInAllCell();

            Layer layer = this.layers[layerIdx]; // слой
            Cell[][] cells = layer.getCells(); // получить ячейки слоя
            // проходим по каждой ячейке слоя
            for (int col = 0; col < cells.length; col++) {
                for (int row = 0; row < cells[col].length; row++) {
                    Cell cell = cells[col][row];
                    // Добыть очередь организмов в ячейке
                    IslandQueue animalIslandQueue = cell.getOrganisms();
                    //Последовательно перебираем все организмы
                    Iterator<Animal> iterator = animalIslandQueue.getDeque().descendingIterator();
                    for (int i = 0; ; ++i) {
                        if (iterator.hasNext()) {
                            Animal animal = iterator.next(); // получить очередной организм
                            // новорождённых не обрабатывать в этом такте!
                            if (!animal.isNewBorn()) {
                                AnimalWorker animalWorker = new AnimalWorker(islandMap, cell, animal, iterator);
                                animalExecutor.submit(animalWorker);
                            }
                        } else break;
                    }
                }
            }
            Thread.sleep(500);
        }
        this.plantExecutor.shutdown();
        this.animalExecutor.shutdown();
        //this.scheduledPlantPool.shutdown();
    }


    public void createPlantInAllCell() throws NoSuchFieldException, IllegalAccessException {
        // Экзекутор для роста растений
        Cell[][] cells = this.layers[Plant.bioTypeCode].getCells();
        //int threadNumber = IslandParam.NUMBER_OF_EXECUTOR_THREADS;

        //System.out.println("Ежедневный рост растений...");
        // последовательно создаём нити для каждой ячейки слоя растений
        for (int col = 0; col < cells.length; col++) {
            for (int row = 0; row < cells[col].length; row++) {
                PlantWorker plantWorker = new PlantWorker(cells[col][row]);
                this.plantExecutor.submit(plantWorker);
                plantWorker.run();
            }
        }
    }

}




