package ru.javarush.island.belyasnik.isLand.bio.herbivores;

import ru.javarush.island.belyasnik.isLand.abstract_.Animal;
import ru.javarush.island.belyasnik.isLand.annotations.OrganismParam;
import ru.javarush.island.belyasnik.isLand.enums.IslandParam;

@OrganismParam(typeName = "Овца", emoji = "\uD83D\uDC11", bioTypeCode = 6, weight = 70, maxNumberInCell = 140, speed = 3, kgFood = 15)
public class Sheep extends Animal {
    public static final String emoji; //!!!
    public static final String typeName; //!!!
    public static final int bioTypeCode;
    public static final int maxNumberInCell; // максимальное количество в ячейке
    public static final int speed;
    public static int counter = 0; // счётчик всех созданных экземпляров класса
    public static final int[][] menu; // рацион (вид, вероятность, предел насыщения)/d


    static {
        Class<Sheep> cl = Sheep.class;
        emoji = Animal.getEmoji(cl); //!!!
        typeName = Animal.getTypeName(cl); //!!!
        bioTypeCode = Animal.getBioTypeCode(cl);
        maxNumberInCell = Animal.getMaxNumberInCell(cl);
        speed = Animal.getSpeed(cl);
        menu = IslandParam.RATION_PARAM[bioTypeCode];
    }

    public Sheep(int row, int col, boolean newBorn) {
        super(row, col, newBorn);
        this.weight = IslandParam.WEIGHT[bioTypeCode];
        this.kgFood = IslandParam.KG_FOOD[bioTypeCode];
        this.number = ++counter;
        this.name = emoji + " " + typeName + "_" + number;
    }

    /*public Sheep(int row, int col, boolean newBorn) {
        super(row, col, IslandParam.WEIGHT[bioTypeCode], IslandParam.KG_FOOD[bioTypeCode]);
        this.setNewBorn(newBorn);
        this.number = ++counter;
        this.name = emoji + " " + typeName + "_" + number; // !!!
    }*/

/*
    public Sheep(int x, int y) {
        super(x, y, IslandParam.WEIGHT[bioTypeCode], IslandParam.KG_FOOD[bioTypeCode]);
        this.number = ++counter;
        this.name = emoji + " " + typeName + "_" + number; // !!!
        //System.out.println("Создание организма " + this.name);
    }
*/

    // Вернуть счётчик объектов
    public static int getCounter() {
        return counter;
    }
}