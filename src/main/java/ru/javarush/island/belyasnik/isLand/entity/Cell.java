package ru.javarush.island.belyasnik.isLand.entity;

import ru.javarush.island.belyasnik.isLand.abstract_.Organism;
import ru.javarush.island.belyasnik.isLand.enums.IslandParam;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Cell {
    private final int col;
    private final int row;
    private final IslandQueue<Organism> organisms;
    private final int layerIndex;
    private ArrayList<Cell> cellSteps; // список соседних ячеек для ходьбы

    public Cell(int x, int y, IslandQueue<Organism> organisms, int layerIndex) {
        this.col = x;
        this.row = y;
        this.organisms = organisms;
        this.layerIndex = layerIndex;
    }

    public ArrayList<Cell> getCellSteps() {
        return cellSteps;
    }

    public void setCellSteps(ArrayList<Cell> cellSteps) {
        this.cellSteps = cellSteps;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "col=" + col +
                ", row=" + row +
                ", layerIndex=" + layerIndex +
                '}';
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public IslandQueue<Organism> getOrganisms() {
        return organisms;
    }

    public int getLayerIndex() {
        return layerIndex;
    }

    public Object getMonitor() {
        return this;
    }

    public void createInOneCell() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends Organism> clazz = IslandParam.classes[this.layerIndex];
        //int amount = Randomizer.get(0, this.getMaxPositionsToAdd());
        int amount = this.getMaxPositionsToAdd();
        for (int n = 0; n < amount; n++) {
            this.addNewOrganismInQueue(clazz);
        }
    }

    // Посчитать оставшуюся ёмкость очереди ячейки
    public int getMaxPositionsToAdd() {
        // максимально допустимое число организмов в ячейке для данного класса
        int maxNumberInCell = IslandParam.MAX_NUMBER_IN_CELL[this.layerIndex];
        // сколько ещё можно добавить организмов до максимального заполнения очереди
        return maxNumberInCell - this.organisms.getDeque().size();
    }

    // добавить новый организм класса clazz в очередь ячейки
    public void addNewOrganismInQueue(Class<? extends Organism> clazz) {
        try {
            Class<?>[] params = {int.class, int.class, boolean.class};
            this.organisms.add(clazz.getConstructor(params).newInstance(this.row, this.col, false));
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

