package ru.javarush.island.belyasnik.isLand.enums;

import ru.javarush.island.belyasnik.isLand.abstract_.Organism;
import ru.javarush.island.belyasnik.isLand.bio.carnivores.*;
import ru.javarush.island.belyasnik.isLand.bio.herbivores.*;


public abstract class IslandParam {

    public static final int BIO_TYPES_TOTAL = BioTypes.values().length; // число биологических видов
    public static final int NUMBER_OF_COLUMNS = 10; // число ячеек по горизонтали
    public static final int NUMBER_OF_ROWS = 10; // число ячеек по вертикали
    public static final int NUMBER_OF_EXECUTOR_THREADS = 4;
    public static final Long TACT = 2000L;

    // Массив классов организмов
    public static Class<? extends Organism>[] classes = new Class[]{
            Plant.class, // 0
            Horse.class, // 1
            Deer.class,  // 2
            Rabbit.class, // 3
            Mouse.class, // 4
            Goat.class, // 5
            Sheep.class, // 6
            Boar.class, // 7
            Buffalo.class, // 8
            Duck.class, // 9
            Caterpillar.class, // 10
            Wolf.class, // 11
            Boa.class, // 12
            Fox.class, // 13
            Bear.class, // 14
            Eagle.class // 15
    };


    // Максимальное количество животных этого вида в одной ячейке карты
    public static int[] MAX_NUMBER_IN_CELL = new int[]{
            200,  // 0, PLANT
            20,   // 1, HORSE
            20,   // 2, DEER
            150,  // 3, RABBIT
            500,  // 4, MOUSE
            140,  // 5, GOAT
            140,  // 6, SHEEP
            50,   // 7, BOAR
            10,   // 8, BUFFALO
            200,  // 9, DUCK
            1000, // 10, CATERPILLAR
            30,   // 11, WOLF
            30,   // 12, BOA
            30,   // 13, FOX
            5,    // 14, BEAR
            20    // 15, EAGLE
    };

    // Собственный вес животных
    public static double[] WEIGHT = new double[]{
            1,  // 0, PLANT
            400,   // 1, HORSE
            300,   // 2, DEER
            2,  // 3, RABBIT
            0.05,  // 4, MOUSE
            60,  // 5, GOAT
            70,  // 6, SHEEP
            400,   // 7, BOAR
            700,   // 8, BUFFALO
            1,  // 9, DUCK
            0.01, // 10, CATERPILLAR
            50,   // 11, WOLF
            15,   // 12, BOA
            8,   // 13, FOX
            500,    // 14, BEAR
            6    // 15, EAGLE
    };


    // Скорость перемещения, не более чем, клеток за ход
    public static int[] SPEED = new int[]{
            0,  // 0, PLANT
            4,   // 1, HORSE
            4,   // 2, DEER
            2,  // 3, RABBIT
            1,  // 4, MOUSE
            3,  // 5, GOAT
            3,  // 6, SHEEP
            2,   // 7, BOAR
            3,   // 8, BUFFALO
            4,  // 9, DUCK
            0, // 10, CATERPILLAR
            3,   // 11, WOLF
            1,   // 12, BOA
            2,   // 13, FOX
            2,    // 14, BEAR
            3    // 15, EAGLE
    };

    // Сколько килограммов пищи нужно животному для полного насыщения
    public static double[] KG_FOOD = new double[]{
            0,  // 0, PLANT
            60,   // 1, HORSE
            50,   // 2, DEER
            0.45,  // 3, RABBIT
            0.01,  // 4, MOUSE
            10,  // 5, GOAT
            15,  // 6, SHEEP
            50,   // 7, BOAR
            100,   // 8, BUFFALO
            0.15,  // 9, DUCK
            0.0, // 10, CATERPILLAR
            8,   // 11, WOLF
            3,   // 12, BOA
            2,   // 13, FOX
            80,    // 14, BEAR
            1    // 15, EAGLE
    };

    // Параметры питания: {съедаемый вид, вероятность}
    public static int[][][] RATION_PARAM = new int[][][]{
            {{0, 0}},    // 0, PLANT
            {{0, 100}},  // 1, HORSE
            {{0, 100}},  // 2, DEER
            {{0, 100}},  // 3, RABBIT
            {{10, 90}, {0, 100}},  // 4, MOUSE
            {{0, 100}},  // 5, GOAT
            {{0, 100}},  // 6, SHEEP
            {{0, 100}, {10, 90}}, // 7, BOAR
            {{0, 100}},  // 8, BUFFALO
            {{10, 90}, {0, 100}},  // 9, DUCK
            {{0, 100}}, // 10, CATERPILLAR
            {{1, 10}, {2, 15}, {3, 60}, {4, 80}, {5, 60}, {6, 70}, {7, 15}, {8, 10}, {9, 40}},   // 11, WOLF
            {{13, 15}, {3, 20}, {4, 40}, {9, 10}},   // 12, BOA
            {{3, 70}, {4, 90}, {9, 60}, {10, 40}},   // 13, FOX
            {{12, 80}, {2, 80}, {3, 80}, {4, 90}, {5, 70}, {6, 70}, {7, 50}, {8, 20}, {9, 10}, {1, 40}}, // 14, BEAR
            {{3, 90}, {4, 90}, {9, 80}, {13, 10}}    // 15, EAGLE
    };

    // Максимальное количество детёнышей для каждого вида животных
    public static int[] HOW_MANY_CHILDREN = new int[]{
            0,  // 0, PLANT
            1,   // 1, HORSE
            1,   // 2, DEER
            10,  // 3, RABBIT
            12,  // 4, MOUSE
            2,  // 5, GOAT
            2,  // 6, SHEEP
            30,   // 7, BOAR
            1,   // 8, BUFFALO
            10,  // 9, DUCK
            200, // 10, CATERPILLAR
            5,   // 11, WOLF
            35,   // 12, BOA
            5,   // 13, FOX
            5,    // 14, BEAR
            3    // 15, EAGLE
    };

    // Максимальное количество шагов для каждого вида животных
    public static int[] HOW_MANY_STEPS = new int[]{
            0,  // 0, PLANT
            4,   // 1, HORSE
            4,   // 2, DEER
            2,  // 3, RABBIT
            1,  // 4, MOUSE
            3,  // 5, GOAT
            3,  // 6, SHEEP
            2,   // 7, BOAR
            3,   // 8, BUFFALO
            4,  // 9, DUCK
            0, // 10, CATERPILLAR
            3,   // 11, WOLF
            1,   // 12, BOA
            2,   // 13, FOX
            2,    // 14, BEAR
            3    // 15, EAGLE
    };

}
