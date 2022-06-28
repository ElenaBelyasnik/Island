package ru.javarush.island.belyasnik.isLand.entity;


import ru.javarush.island.belyasnik.isLand.enums.IslandParam;

public class Layer {
    private int bioTypeCode; // биологический вид, соответствующий этому слою карты
    // слой ячеек карты
    private Cell[][] cells = new Cell[IslandParam.NUMBER_OF_COLUMNS][IslandParam.NUMBER_OF_ROWS];

    // массив статистики по каждой ячейке слоя
    private int[][] cellStat = new int[IslandParam.NUMBER_OF_COLUMNS][IslandParam.NUMBER_OF_ROWS];

    public Layer(int bioTypeCode, Cell[][] cells) {
        this.bioTypeCode = bioTypeCode;
        this.cells = cells;
    }


    public Cell[][] getCells() {
        return cells;
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }


    // сбор статистики по всем ячейкам слоя
    public int getLayerStat() {
        Cell[][] cells = this.getCells();
        int count = 0;
        for (int row = 0; row < cells.length; row++) {
            for (int col = 0; col < cells[row].length; col++) {

                cellStat[row][col] = this.getCellStat(cells[row][col]);
                count += this.getCellStat(cells[row][col]);
            }
        }
        return count;
    }

    // сбор статистики по одной ячейке слоя
    public int getCellStat(Cell cell) {
        return cell.getOrganisms().getSize();
    }

    // инициализация списка соседних ячеек, для каждой ячейки
    public void getCellStapsList() {
        Cell[][] cells = this.getCells();
        int maxCol = cells.length;
        int minCol = 0;
        int maxRow = cells[0].length;
        int minRow = 0;
        Cell cell1, cell2, cell3, cell4;

        for (int col = 0; col < cells.length; col++) {
            for (int row = 0; row < cells[col].length; row++) {
                IslandQueue<Cell> deque = new IslandQueue<>(4);

                if ((col + 1) < cells.length) {
                    cell1 = cells[col + 1][row];
                    deque.add(cell1);
                }
                if ((row - 1) >= 0) {
                    cell2 = cells[col][row - 1];
                    deque.add(cell2);
                }
                if ((col - 1) >= 0) {
                    cell3 = cells[col - 1][row];
                    deque.add(cell3);
                }
                if ((row + 1) < cells[col].length) {
                    cell4 = cells[col][row + 1];
                    deque.add(cell4);
                }
                this.getCells()[col][row].setCellSteps(deque);
            }
        }
    }
}
