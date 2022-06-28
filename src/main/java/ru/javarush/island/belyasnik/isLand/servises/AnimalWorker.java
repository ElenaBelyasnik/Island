package ru.javarush.island.belyasnik.isLand.servises;

import ru.javarush.island.belyasnik.isLand.abstract_.Animal;
import ru.javarush.island.belyasnik.isLand.abstract_.Organism;
import ru.javarush.island.belyasnik.isLand.entity.Cell;
import ru.javarush.island.belyasnik.isLand.entity.IslandMap;
import ru.javarush.island.belyasnik.isLand.entity.IslandQueue;
import ru.javarush.island.belyasnik.isLand.entity.Layer;
import ru.javarush.island.belyasnik.isLand.enums.IslandParam;
import ru.javarush.island.belyasnik.isLand.interfaces.AnimalActions;
import ru.javarush.island.belyasnik.isLand.util.Randomizer;

import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;


public class AnimalWorker implements Runnable, AnimalActions {
    private final IslandMap islandMap;
    private final Cell cell;
    private final Animal animal;
    private final int bioType; // код биологического вида и номер слоя карты
    private final Iterator<Animal> iterator;
    private IslandQueue islandQueue;

    public AnimalWorker(IslandMap islandMap, Cell cell, Animal animal, Iterator<Animal> iterator) {
        this.islandMap = islandMap;
        this.cell = cell;
        this.animal = animal;
        this.bioType = this.cell.getLayerIndex();
        this.iterator = iterator;
        this.islandQueue = this.cell.getOrganisms();

    }

    @Override
    public void run() {
        this.eat();
        if (this.die()) {
            return; // умерло, так умерло...
        }
        // размножение
        if (this.canReproduct(animal.getMale())) {
            try {
                this.reproduct(); // если может размножиться, то размножается
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean die() {
        // если после еды уровень сытости животного = 0 то животное умирает
        boolean successfulRemoval = false;
        if (this.animal.isAte() & this.animal.fullnessLevel == 0.0d) {
            Deque<Organism> deque = this.islandQueue.getDeque();
            successfulRemoval = deque.remove(animal);
            if (successfulRemoval) {
                synchronized (this.islandQueue) {
                    this.islandQueue.setDeque((LinkedBlockingDeque) deque);
                }
            }
        } else return false;
        return successfulRemoval;
    }

    // проверяет: а имеет ли животное возможность размножиться?
    public boolean canReproduct(int male) {
        // если пол женский male = 0, то
        if (male == 0) {
            // если животное поело досыта, не умерло, и не голодает, то оно может размножиться
            if (!this.animal.isDead() & this.animal.isAte() & !this.animal.isHungry()) {
                Deque<Organism> deque = this.islandQueue.getDeque();
                // проверяем, а есть ли в очереди ячейки хоть 1 самец этого вида
                boolean hasMale = deque
                        .stream()
                        .map(organism -> organism.getMale())
                        .anyMatch(m -> (m == 1));
                return hasMale;
            }
        }
        return false;
    }

    @Override
    public void reproduct() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // если животное поело досыта, не умерло, и не голодает, то оно может размножиться
        synchronized (this.islandQueue) {
            Class clazz = this.animal.getClass();
            int col = this.cell.getCol();
            int row = this.cell.getRow();
            for (int n = 0; n < IslandParam.HOW_MANY_CHILDREN[this.bioType]; n++) {
                // добавить организм в очередь ячейки
                this.islandQueue.addNewOrganismInQueue(clazz, col, row, true);
            }
        }
    }


    @Override
    public void goToCell(Cell cell) {

    }

    @Override
    public void eat() {
        // если животное ещё не ело и оно живо, то оно должно есть
        if (!(this.animal.isAte() & this.animal.isDead())) {
            // eat1(); // процесс еды по первому алгоритму
            eat2(); // процесс еды по второму алгоритму
        }
    }


    // по этому алгоритму сначала съедаются все животные, находящиеся первыми по списку в рационе,
    // потом съедаются все вторые по списку, и т.д.
    public void eat2() {
        // Создать массив с рационом для этого класса
        int[][] racionParam = IslandParam.RACION_PARAM[this.bioType];
        // последовательно просматриваем все виды, перечисленные в меню
        for (int n = 0; n < racionParam.length; n++) {
            int vid = racionParam[n][0]; // код био-вида животного из рациона
            int probability = racionParam[n][1]; // вероятность съедания
            // получить ячейку с очередью ораганизмов из рациона по коду био-вида
            Cell foodCell = this.getFoodCell(vid);
            // получить очередь поедаемых организмов
            IslandQueue foodIslandQueue = foodCell.getOrganisms();
            //Последовательно перебираем очередь "поедаемых", если она не пуста
            // ВОПРОС: НУЖНО ЛИ СИНХРОНИЗИРОВАТЬ LinkedBlockingDeque
            // выполнять, пока показатель сытости не станет равным эталону
            while (this.animal.fullnessLevel < this.animal.kgFood && foodIslandQueue.getSize() > 0) {
                if (Randomizer.get(1, 100) < probability) {
                    // Извлекает и удаляет голову очереди, представленной этой двухсторонней очередью
                    // (другими словами, первый элемент этой двухсторонней очереди),
                    // или возвращает значение, nullесли эта двухсторонняя очередь пуста.
                    Organism foodOrganism = (Organism) foodIslandQueue.pool();
                    if (foodOrganism != null) {
                        // процесс поедания добавляет к уровню сытости вес поедаемого организма
                        double eatingWeight = countNeedToEat(foodOrganism.weight, this.animal.fullnessLevel, this.animal.kgFood);
                        this.animal.setFullnessLevel(this.animal.fullnessLevel + eatingWeight);
                        foodOrganism.setDead(true);
                        //System.out.println(this.animal.toString() + " съел " + foodOrganism.toString() + " уровень сытости = " + this.animal.fullnessLevel);
                    }
                }
            }
        }
        // расставим статусы сытости
        setEatenStatus(this.animal);
/*
        if (animal.getClass().getSimpleName().equals("Wolf")) {
            System.out.println( this.animal.toString()
                    + " Ate = " + this.animal.isAte() + " уровень сытости = " + this.animal.getFullnessLevel());
        }
*/

/*
        if (animal.fullnessLevel >= animal.kgFood) {
            System.out.println(this.animal.toString() + " уровень сытости = " + this.animal.getFullnessLevel());
        }
*/
    }

    // расстановка статусов "сытости"
    public void setEatenStatus(Animal animal) {
        synchronized (this.animal.getMonitor()) {
            //cell.getLock().lock(); // блокируем ячейку
            try {
                animal.setAte(true); // признак того, что животное обработано процессом "eat"
                if (animal.fullnessLevel > 0.0d & animal.fullnessLevel < animal.kgFood) {
                    animal.setHungry(true); // если животному не удалось насытится, то оно голодно
                }
/*
            if (animal.getClass().getSimpleName().equals("Wolf")) {
                System.out.println("Ate = " + animal.isAte());
            }
*/
            } finally {
                //       cell.getLock().unlock();
            }
        }
    }

    // Расчитать: сколько нужно откусить от веса объекта для достижения положенного уровня сытости.
    public double countNeedToEat(double foodWeight, double fullnessLevel, double kgFood) {
        double needToEat = 0;
        if (fullnessLevel < kgFood) {
            needToEat = kgFood - fullnessLevel;
        } else return 0d;

        if (foodWeight > needToEat) {
            return needToEat;
        } else return foodWeight;
    }

    public boolean racionQueueIsEmpty(IslandQueue[] foodIslandQueue) {
        int size = 0;
        for (int n = 0; n < foodIslandQueue.length; n++) {
            size += foodIslandQueue[n].getSize();
        }
        return (size == 0);
    }

    // получить ячейку с очередью организмов из рациона по номеру слоя
    public Cell getFoodCell(int vid) {
        Layer layer = this.islandMap.getLayers()[vid];
        int col = this.cell.getCol();
        int row = this.cell.getRow();
        Cell cell = layer.getCells()[col][row];
        return cell;
    }

    // по этому алгоритму последовательно съедаются животное вида,
    // идущего первым в рационе (IslandParam.RACION_PARAM[bioTypeCode])
    // затем съедается животное вида, идущего 2-м по списку в "рационе" (IslandParam.RACION_PARAM[bioTypeCode]),
    // и т.д. до достижения уровня насыщения
    public void eat1() {

        // Создать массив с рационом для этого класса
        int[][] racionParam = IslandParam.RACION_PARAM[this.bioType];
        // массив очередей организмов из меню и вероятностей поедания
        IslandQueue[] foodIslandQueue = new IslandQueue[racionParam.length];
        // массив вероятностей поедания для организмов из рациона
        int[] probability = new int[racionParam.length];
        // заполняем массивы...
        for (int n = 0; n < racionParam.length; n++) {
            // получить ячейку с очередью ораганизмов из рациона
            int vid = racionParam[n][0];
            Cell foodCell = this.getFoodCell(vid);
            // получить очередь поедаемых организмов
            foodIslandQueue[n] = foodCell.getOrganisms();
            probability[n] = racionParam[n][1]; // вероятность съедания
        }

        // выполнять, пока показатель сытости меньше предела насыщения и в очередях кто-то есть
        while (this.animal.fullnessLevel < this.animal.kgFood & !this.racionQueueIsEmpty(foodIslandQueue)) {

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
/*
                            System.out.println(this.animal.toString() + " съел " + foodOrganism.toString() + " уровень сытости = " + this.animal.fullnessLevel);
*/
                        }
                    }
                    // если достигнут показатель сытости
                    if (!(this.animal.fullnessLevel < this.animal.kgFood)) break;
                    // если очереди опустели
                    if (this.racionQueueIsEmpty(foodIslandQueue)) break;
                }
            }
        }
/*
        if (this.animal.fullnessLevel >= animal.kgFood) {
            System.out.println(this.animal.toString() + " уровень сытости = " + this.animal.getFullnessLevel());
        }
*/
    }


}
