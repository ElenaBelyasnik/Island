package ru.javarush.island.belyasnik.isLand.entity;

import ru.javarush.island.belyasnik.isLand.abstract_.Organism;
import ru.javarush.island.belyasnik.isLand.enums.IslandParam;

import java.util.Iterator;


public class IslandMap {
    private final Layer[] layers;
    // layersStat[bioType] - сколько всего животных данного вида ()
    private final int[] layersStat = new int[IslandParam.BIO_TYPES_TOTAL];
    // массивы для рисования карты с итогом очередного такта
    private int[][] drawingCount = new int[IslandParam.NUMBER_OF_COLUMNS][IslandParam.NUMBER_OF_ROWS];
    private String[][] drawingEmoji = new String[IslandParam.NUMBER_OF_COLUMNS][IslandParam.NUMBER_OF_ROWS];

    public IslandMap(Layer[] layers) {
        this.layers = layers;
    }

    public Layer[] getLayers() {
        return this.layers;
    }

    public int[][] getDrawingCount() {
        return drawingCount;
    }

    public void setDrawingCount(int[][] drawingCount) {
        this.drawingCount = drawingCount;
    }

    public String[][] getDrawingEmoji() {
        return drawingEmoji;
    }

    public void setDrawingEmoji(String[][] drawingEmoji) {
        this.drawingEmoji = drawingEmoji;
    }

    // Проходим по всем слоям и заполняем их организмами
    public static Layer[] fillLayers() throws NoSuchFieldException, IllegalAccessException {
        Layer[] layers = new Layer[IslandParam.BIO_TYPES_TOTAL];
        System.out.println("Всего создано: ");
        for (int bioTypeIndex = 0; bioTypeIndex < layers.length; bioTypeIndex++) {
            layers[bioTypeIndex] = IslandMap.init(IslandParam.classes[bioTypeIndex], bioTypeIndex);
            // инициализация списка соседних ячеек, для каждой ячейки
            layers[bioTypeIndex].getCellStepsList();
        }
        return layers;
    }

    // инициализация одного слоя карты организмами одного биологического вида
    public static <T> Layer init(Class<T> clazz, int layerIndex) throws NoSuchFieldException, IllegalAccessException {
        // заполняем массив пустых ячеек слоя layer
        Cell[][] cells = new Cell[IslandParam.NUMBER_OF_COLUMNS][IslandParam.NUMBER_OF_ROWS];
        for (int col = 0; col < cells.length; col++) {
            for (int row = 0; row < cells[col].length; row++) {
                // создать очередь организмов для каждой ячейки
                int maxNumberInCell = (int) getFieldValue(clazz, "maxNumberInCell");
                IslandQueue<Organism> islandQueueOrganism = new IslandQueue<>(maxNumberInCell);
                // заполнить очередь организмами в количестве maxNumberInCell/5
                for (int n = 0; n < maxNumberInCell / 5; n++) {
                    // добавить организм в очередь ячейки
                    islandQueueOrganism.addNewOrganismInQueue(clazz, col, row, false);
                    //queueOrganism.add(clazz.getConstructor(params).newInstance(col, row, false));
                }
                // создать объект ячейки и присвоить его массиву ячеек слоя,
                // передав туда координаты, очередь организмов и индекс слоя
                Cell cell = new Cell(col, row, islandQueueOrganism, layerIndex);

                cells[col][row] = cell;
            }
        }
        int counter = (int) clazz.getDeclaredField("counter").get(null);
        printMess(clazz, counter);

        // получить код биологического вида
        int bioTypeCode = (int) getFieldValue(clazz, "bioTypeCode");
        return new Layer(bioTypeCode, cells);
    }

    public static Object getFieldValue(Class<?> clazz, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return clazz.getDeclaredField(fieldName).get(null);
    }


    public static void printMess(Class<?> clazz, int counter) throws NoSuchFieldException, IllegalAccessException {
        String emoji = (String) getFieldValue(clazz, "emoji");
        String typeName = (String) clazz.getDeclaredField("typeName").get(null);
        System.out.println(emoji + " " + typeName + " - " + counter);
    }

    // сбор статистики по всей карте
    public void getStat() {
        for (int bioType = 0; bioType < layersStat.length; bioType++) {
            Layer layer = this.layers[bioType];
            layersStat[bioType] = layer.getLayerStat(this);
        }
    }

    // вывести статистику по оставшимся биологическим видам
    public void printStat(Dispatcher dispatcher) throws NoSuchFieldException, IllegalAccessException {
        System.out.println();
        System.out.println("Завершение " + dispatcher.getCountTacts() + " такта эмуляции, статистика: ");
        getStat();
        for (int bioTypeCode = 0; bioTypeCode < layersStat.length; bioTypeCode++) {
            if (layersStat[bioTypeCode] > 0) {
                System.out.println(this.getStatMess(IslandParam.classes[bioTypeCode], layersStat[bioTypeCode]));
            }
        }
    }

    public <T> String getStatMess(Class<T> clazz, int counter) throws NoSuchFieldException, IllegalAccessException {
        String emoji = (String) getFieldValue(clazz, "emoji");
        String typeName = (String) clazz.getDeclaredField("typeName").get(null);
        return emoji + " " + typeName + " - " + counter;
    }

    // обнуление статусов всех организмов для следующего цикла
    public void resetStatus() {
        Layer[] layers = this.getLayers();

        for (Layer layer : layers) {
            Cell[][] cells = layer.getCells();
            for (Cell[] value : cells) { // строки
                for (Cell cell : value) { // столбцы
                    IslandQueue<Organism> islandQueueOrganism = cell.getOrganisms();
                    //Последовательно перебираем все организмы
                    Iterator<Organism> iterator = islandQueueOrganism.getDeque().descendingIterator();
                    while (iterator.hasNext()) {
                        Organism organism = iterator.next(); // получить очередной организм
                        organism.setDead(false); // живой
                        organism.setAte(false); // ещё не ел в этом такте
                        organism.setHungry(false); // ещё не голоден
                        organism.setNewBorn(false); // не новорождённый
                        organism.setFullnessLevel(0.0d); // ничего не съел
                    }
                }
            }
        }
    }

    //**TODO создать матрицу по размеру карты, каждая ячейка которой содержит:
    // - код самого многочисленного вида животных в этой ячейке
    // - если животных нет, то показывать растения
    // - число особей каждого вида в ячейке
    public void keepStatForDrawing() throws NoSuchFieldException, IllegalAccessException {
        int[][] drawingCount = this.getDrawingCount();
        String[][] drawingEmoji = this.getDrawingEmoji();
        Layer[] layers = this.getLayers();

        for (int col = 0; col < drawingCount.length; col++) {
            for (int row = 0; row < drawingCount[col].length; row++) {
                drawingCount[col][row] = 0;
                drawingEmoji[col][row] = "";

            }
        }

        for (int col = 0; col < drawingCount.length; col++) {
            for (int row = 0; row < drawingCount[col].length; row++) {
                for (int l = 1; l < layers.length; l++) {
                    int bioType = layers[l].getBioTypeCode();
                    Class<?> clazz = IslandParam.classes[bioType];
                    String emoji = (String) getFieldValue(clazz, "emoji");
                    int[][] cellStat = layers[l].getCellStat();
                    if (drawingCount[col][row] < cellStat[col][row]) {
                        drawingCount[col][row] = cellStat[col][row];
                        drawingEmoji[col][row] = emoji;

                    }
                    if (drawingCount[col][row] == 0) {
                        drawingEmoji[col][row] = (String) getFieldValue(IslandParam.classes[0], "emoji");
                    }
                }
            }
        }
        this.setDrawingCount(drawingCount);
        this.setDrawingEmoji(drawingEmoji);
    }

    public void draw() throws NoSuchFieldException, IllegalAccessException {
        this.keepStatForDrawing();

        int[][] drawingCount = this.getDrawingCount();
        String[][] drawingEmoji = this.getDrawingEmoji();

        System.out.println();
        for (int col = 0; col < drawingCount.length; col++) {
            for (int row = 0; row < drawingCount[col].length; row++) {
                System.out.print("[" + drawingEmoji[col][row] + "]");
            }
            System.out.println();
        }
    }


}



