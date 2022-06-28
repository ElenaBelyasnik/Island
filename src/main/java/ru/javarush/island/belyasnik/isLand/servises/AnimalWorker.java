package ru.javarush.island.belyasnik.isLand.servises;

import ru.javarush.island.belyasnik.isLand.abstract_.Organism;
import ru.javarush.island.belyasnik.isLand.entity.Cell;
import ru.javarush.island.belyasnik.isLand.entity.IslandMap;
import ru.javarush.island.belyasnik.isLand.entity.IslandQueue;
import ru.javarush.island.belyasnik.isLand.enums.IslandParam;
import ru.javarush.island.belyasnik.isLand.interfaces.AnimalActions;
import ru.javarush.island.belyasnik.isLand.util.Randomizer;

import java.util.ArrayList;
import java.util.Deque;


public class AnimalWorker implements Runnable, AnimalActions {
    private final IslandMap islandMap;
    private Cell cell;
    private final Organism organism;
    private final int bioType; // код биологического вида и номер слоя карты

    public AnimalWorker(IslandMap islandMap, Cell cell, Organism organism) {
        this.islandMap = islandMap;
        this.cell = cell;
        this.organism = organism;
        this.bioType = this.cell.getLayerIndex();
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    @Override
    public void run() {
        this.eat();
        if (this.die()) {
            return; // умерло, так умерло...
        }
        // размножение
        if (this.canReproduce(organism.getMale())) {
            try {
                this.reproduce(); // если может размножиться, то размножается
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
        // движение
        this.move();
    }


    @Override
    public boolean die() {
        // если после еды уровень сытости животного = 0, то животное умирает
        boolean successfulRemoval;
        if (this.organism.isAte() & this.organism.fullnessLevel == 0.0d) {
            successfulRemoval = removeFromQueue(this.organism, this.cell);
        } else return false;
        return successfulRemoval;
    }


    // удаление организма из очереди
    public boolean removeFromQueue(Organism organism, Cell cell) {
        boolean successfulRemoval;
        synchronized (cell.getOrganisms().getMonitor()) {
            successfulRemoval = cell.getOrganisms().getDeque().remove(organism);
        }
        return successfulRemoval;
    }

    // проверяет: а имеет ли животное возможность размножиться?
    public boolean canReproduce(int male) {
        // если пол женский male = 0, то
        if (male == 0) {
            // если животное поело досыта, не умерло, и не голодает, то оно может размножиться
            if (!this.organism.isDead() & this.organism.isAte() & !this.organism.isHungry()) {
                Deque<Organism> deque = this.cell.getOrganisms().getDeque();
                // проверяем, а есть ли в очереди ячейки хоть 1 самец этого вида
                return deque
                        .stream()
                        .map(Organism::getMale)
                        .anyMatch(m -> (m == 1));
            }
        }
        return false;
    }

    public void reproduce() {
        // если животное поело досыта, не умерло, и не голодает, то оно может размножиться
        synchronized (this.cell.getOrganisms().getMonitor()) {
            Class<? extends Organism> clazz = this.organism.getClass();
            int col = this.cell.getCol();
            int row = this.cell.getRow();
            for (int n = 0; n < IslandParam.HOW_MANY_CHILDREN[this.bioType]; n++) {
                try {
                    int capacity = this.cell.getOrganisms().getCapacity();
                    int size = this.cell.getOrganisms().getSize();
                    if (capacity > size) {
                        // добавить организм в очередь ячейки
                        this.cell.getOrganisms().addNewOrganismInQueue(clazz, col, row, true);
                    }
                } catch (Exception ignored) {

                }
            }
        }
    }


    // TODO Проверить корректно ли двигается животное.
    @Override
    public void move() {
        // получить длину пути, на который переместится животное
        int pathLength = Randomizer.get(0, IslandParam.HOW_MANY_STEPS[this.bioType + 1]);
        Cell currentCell = this.cell;
        // пытаемся ходить, пока не закончился путь
        while (pathLength > 0) {
            Cell nextCell = getNextCell(currentCell);
            // если ходить некуда, то останавливаемся
            if (nextCell == null) {
                break;
            } else {
                currentCell = nextCell;
                pathLength--;
            }
            this.setCell(currentCell);
        }
    }


    // перейти в следующую ячейку пути
    public Cell getNextCell(Cell currentCell) {
        Cell nextCell = null;
        Cell cell;
        boolean transfer;
        // получить список возможных шагов
        ArrayList<Cell> arrayList = currentCell.getCellSteps();
        int size = arrayList.size();
        for (int step = 0; step < size; step++) {
            int direction = Randomizer.get(0, size);
            cell = arrayList.get(direction);
            transfer = transferToAnotherQueue(this.organism, cell);
            if (transfer) {
                nextCell = cell;
                break;
            }
        }
        return nextCell;
    }

    // перемещение организма в очередь другой ячейки
    public boolean transferToAnotherQueue(Organism organism, Cell cell) {
        boolean successfulAdding = false;
        boolean successfulRemoval = false;
        synchronized (cell.getMonitor()) {
            //Deque deque = cell.getOrganisms().getDeque();
            boolean canTransfer = cell.getOrganisms().getCapacity() > cell.getOrganisms().getSize();
            if (canTransfer) {
                successfulAdding = cell.getOrganisms()
                        .getDeque()
                        .offerLast(organism);
                if (successfulAdding) {
                    successfulRemoval = removeFromQueue(organism, cell);
                }
            }
        }
        return successfulRemoval & successfulAdding;
    }


    @Override
    public void eat() {
        // если животное ещё не ело и оно живо, то оно должно есть
        if (!(this.organism.isAte() & this.organism.isDead())) {
            // eat1(); // процесс еды по первому алгоритму
            eat2(); // процесс еды по второму алгоритму
        }
    }


    // по этому алгоритму сначала съедаются все животные, находящиеся первыми по списку в рационе,
    // потом съедаются все вторые по списку, и т.д.
    public void eat2() {
        // Создать массив с рационом для этого класса
        int[][] rationParam = IslandParam.RACION_PARAM[this.bioType];
        // последовательно просматриваем все виды, перечисленные в меню
        for (int[] ints : rationParam) {
            int vid = ints[0]; // код био-вида животного из рациона
            int probability = ints[1]; // вероятность съедания
            // получить ячейку с очередью организмов из рациона по коду био-вида
            Cell foodCell = this.getFoodCell(vid);
            // получить очередь поедаемых организмов
            IslandQueue<Organism> foodIslandQueue = foodCell.getOrganisms();
            //Последовательно перебираем очередь "поедаемых", если она не пуста
            // выполнять, пока показатель сытости не станет равным эталону
            while (this.organism.fullnessLevel < this.organism.kgFood && foodIslandQueue.getSize() > 0) {
                if (Randomizer.get(1, 100) < probability) {
                    // Извлекает и удаляет голову очереди, представленной этой двухсторонней очередью
                    // (другими словами, первый элемент этой двухсторонней очереди),
                    // или возвращает значение, null если эта двухсторонняя очередь пуста.
                    Organism foodOrganism = foodIslandQueue.pool();
                    if (foodOrganism != null) {
                        // процесс поедания добавляет к уровню сытости вес поедаемого организма
                        double eatingWeight = countNeedToEat(foodOrganism.getWeight(), this.organism.fullnessLevel, this.organism.kgFood);
                        this.organism.setFullnessLevel(this.organism.fullnessLevel + eatingWeight);
                        foodOrganism.setDead(true);
                        //System.out.println(this.animal.toString() + " съел " + foodOrganism.toString() + " уровень сытости = " + this.animal.fullnessLevel);
                    }
                }
            }
        }
        // расставим статусы сытости
        setEatenStatus(this.organism);
    }

    // расстановка статусов "сытости"
    public void setEatenStatus(Organism organism) {
        synchronized (this.organism.getMonitor()) {
            organism.setAte(true); // признак того, что животное обработано процессом "eat"
            if (organism.fullnessLevel > 0.0d & organism.fullnessLevel < organism.kgFood) {
                organism.setHungry(true); // если животному не удалось насытиться, то оно голодно
            }
        }
    }

    // Рассчитать: сколько нужно откусить от веса объекта для достижения положенного уровня сытости.
    public double countNeedToEat(double foodWeight, double fullnessLevel, double kgFood) {
        double needToEat;
        if (fullnessLevel < kgFood) {
            needToEat = kgFood - fullnessLevel;
        } else return 0d;

        return Math.min(foodWeight, needToEat);
    }

    // получить ячейку с очередью организмов из рациона по номеру слоя
    public Cell getFoodCell(int vid) {
        return this.islandMap.getLayers()[vid].getCells()[this.cell.getCol()][this.cell.getRow()];
    }


/*
    public boolean rationQueueIsEmpty(IslandQueue<Organism>[] foodIslandQueue) {
        int size = 0;
        for (IslandQueue<Organism> islandQueue : foodIslandQueue) {
            size += islandQueue.getSize();
        }
        return (size == 0);
    }

    // по этому алгоритму последовательно съедаются животное вида,
    // идущего первым в рационе (IslandParam.RATION_PARAM[bioTypeCode])
    // затем съедается животное вида, идущего 2-м по списку в "рационе" (IslandParam.RATION_PARAM[bioTypeCode]),
    // и т.д. до достижения уровня насыщения
    public void eat1() {

        // Создать массив с рационом для этого класса
        int[][] rationParam = IslandParam.RATION_PARAM[this.bioType];
        // массив очередей организмов из меню и вероятностей поедания
        IslandQueue[] foodIslandQueue = new IslandQueue[rationParam.length];
        // массив вероятностей поедания для организмов из рациона
        int[] probability = new int[rationParam.length];
        // заполняем массивы...
        for (int n = 0; n < rationParam.length; n++) {
            // получить ячейку с очередью организмов из рациона
            int vid = rationParam[n][0];
            Cell foodCell = this.getFoodCell(vid);
            // получить очередь поедаемых организмов
            foodIslandQueue[n] = foodCell.getOrganisms();
            probability[n] = rationParam[n][1]; // вероятность съедания
        }

        // выполнять, пока показатель сытости меньше предела насыщения и в очередях кто-то есть
        while (this.animal.fullnessLevel < this.animal.kgFood & !this.rationQueueIsEmpty(foodIslandQueue)) {

            // последовательно просматриваем очереди видов, перечисленныx в меню
            for (int n = 0; n < foodIslandQueue.length; n++) {
                synchronized (foodIslandQueue[n]) {
                    // если очередь не пустая
                    if (foodIslandQueue[n].getSize() > 0) {
                        // если выпала соответствующая вероятность,
                        // то съедаем очередной организм из очереди
                        if (Randomizer.get(1, 100) < probability[n]) {
                            Organism foodOrganism = (Organism) foodIslandQueue[n].pool();
                            synchronized (this.animal.getMonitor()) {
                                double eatingWeight = countNeedToEat(foodOrganism.weight, this.animal.fullnessLevel, this.animal.kgFood);
                                this.animal.fullnessLevel += eatingWeight;
                            }
                        }
                    }
                    // если достигнут показатель сытости
                    if (!(this.animal.fullnessLevel < this.animal.kgFood)) break;
                    // если очереди опустели
                    if (this.rationQueueIsEmpty(foodIslandQueue)) break;
                }
            }
        }
    }
*/
}
