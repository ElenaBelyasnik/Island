package ru.javarush.island.belyasnik.isLand.abstract_;


import ru.javarush.island.belyasnik.isLand.annotations.OrganismParam;

@OrganismParam(typeName = "Животное", emoji = "*", bioTypeCode = 0, weight = 0, maxNumberInCell = 0, speed = 0, kgFood = 0)
public abstract class Animal extends Organism {

    public Animal(int row, int col, boolean newBorn) {
        super(row, col, newBorn);
    }


    // Вернуть изображение emoji для биологического вида
    public static String getEmoji(Class cl) {
        if (cl.isAnnotationPresent(OrganismParam.class)) {
            OrganismParam organismParameters = (OrganismParam) cl.getAnnotation(OrganismParam.class);
            return organismParameters.emoji();
        }
        return "";
    }

    // Вернуть cколько килограммов пищи нужно животному для полного насыщения
    public static double extractKgFood(Class cl) {
        if (cl.isAnnotationPresent(OrganismParam.class)) {
            OrganismParam organismParameters = (OrganismParam) cl.getAnnotation(OrganismParam.class);
            return organismParameters.kgFood();
        }
        return 0;
    }


    // Вернуть вес животного
    public static double extractWeight(Class cl) {
        if (cl.isAnnotationPresent(OrganismParam.class)) {
            OrganismParam organismParameters = (OrganismParam) cl.getAnnotation(OrganismParam.class);
            return organismParameters.weight();
        }
        return 0;
    }


    // Вернуть наименование биологического вида
    public static String getTypeName(Class cl) {
        if (cl.isAnnotationPresent(OrganismParam.class)) {
            OrganismParam organismParameters = (OrganismParam) cl.getAnnotation(OrganismParam.class);
            return organismParameters.typeName();
        }
        return "";
    }


    // Вернуть код биологического вида
    public static int getBioTypeCode(Class cl) {
        if (cl.isAnnotationPresent(OrganismParam.class)) {
            OrganismParam organismParameters = (OrganismParam) cl.getAnnotation(OrganismParam.class);
            return organismParameters.bioTypeCode();
        }
        return 0;
    }


    // Вернуть максимальное количество животных этого вида на одной клетке
    public static int getMaxNumberInCell(Class cl) {
        if (cl.isAnnotationPresent(OrganismParam.class)) {
            OrganismParam organismParameters = (OrganismParam) cl.getAnnotation(OrganismParam.class);
            return organismParameters.maxNumberInCell();
        }
        return 0;
    }

    // Вернуть cкорость перемещения, не более чем, клеток за ход
    public static int getSpeed(Class cl) {
        if (cl.isAnnotationPresent(OrganismParam.class)) {
            OrganismParam organismParameters = (OrganismParam) cl.getAnnotation(OrganismParam.class);
            return organismParameters.speed();
        }
        return 0;
    }

}
