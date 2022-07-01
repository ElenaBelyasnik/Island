package ru.javarush.island.belyasnik.isLand.entity;


import ru.javarush.island.belyasnik.isLand.enums.IslandParam;

import java.util.ArrayList;

public class Layer {
    private final int bioTypeCode; // биологический вид, соответствующий этому слою карты
    // слой ячеек карты
    private final Cell[][] cells;

    // массив статистики по каждой ячейке слоя
    private final int[][] cellStat = new int[IslandParam.NUMBER_OF_ROWS][IslandParam.NUMBER_OF_COLUMNS];

    public Layer(int bioTypeCode, Cell[][] cells) {
        this.bioTypeCode = bioTypeCode;
        this.cells = cells;
    }

    public int getBioTypeCode() {
        return bioTypeCode;
    }

    public int[][] getCellStat() {
        return cellStat;
    }

    public Cell[][] getCells() {
        return cells;
    }


    // сбор статистики по всем ячейкам слоя (био-вида)
    // сбор статистики по каждому слою (био-виду) целиком
    public int getLayerStat(IslandMap islandMap) {
        Cell[][] cells = this.getCells();
        int count = 0;
        for (int col = 0; col < cells.length; col++) {
            for (int row = 0; row < cells[col].length; row++) {
                cellStat[col][row] = this.getOneCellStat(cells[col][row]);
                count += this.getOneCellStat(cells[col][row]);
            }
        }
        return count;
    }

    // сбор статистики по одной ячейке слоя
    public int getOneCellStat(Cell cell) {
        return cell.getOrganisms().getSize();
    }

    // инициализация списка соседних ячеек, для каждой ячейки
    public void getCellStepsList() {
        Cell[][] cells = this.getCells();
        int maxCol = cells.length;
        int minCol = 0;
        int maxRow = cells[0].length;
        int minRow = 0;
        Cell cell1, cell2, cell3, cell4;

        for (int col = 0; col < cells.length; col++) {
            for (int row = 0; row < cells[col].length; row++) {
                ArrayList<Cell> stepList = new ArrayList<>();

                if ((col + 1) < maxCol) {
                    cell1 = cells[col + 1][row];
                    stepList.add(cell1);
                }
                if ((row - 1) >= minRow) {
                    cell2 = cells[col][row - 1];
                    stepList.add(cell2);
                }
                if ((col - 1) >= minCol) {
                    cell3 = cells[col - 1][row];
                    stepList.add(cell3);
                }
                if ((row + 1) < maxRow) {
                    cell4 = cells[col][row + 1];
                    stepList.add(cell4);
                }
                this.getCells()[col][row].setCellSteps(stepList);
            }
        }
    }
}
