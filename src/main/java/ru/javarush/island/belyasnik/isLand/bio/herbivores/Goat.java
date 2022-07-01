package ru.javarush.island.belyasnik.isLand.bio.herbivores;

import ru.javarush.island.belyasnik.isLand.abstract_.Animal;
import ru.javarush.island.belyasnik.isLand.annotations.OrganismParam;
import ru.javarush.island.belyasnik.isLand.enums.IslandParam;

@OrganismParam(typeName = "Коза", emoji = "\uD83D\uDC0F", bioTypeCode = 5, weight = 60, maxNumberInCell = 140, speed = 3, kgFood = 10)
public class Goat extends Animal {
    public static final String emoji; //!!!
    public static final String typeName; //!!!
    public static final int bioTypeCode;
    public static final int maxNumberInCell; // максимальное количество в ячейке
    public static final int speed;
    public static int counter = 0; // счётчик всех созданных экземпляров класса
    public static final int[][] menu; // рацион (вид, вероятность, предел насыщения)/d

    static {
        Class<Goat> cl = Goat.class;
        emoji = Animal.getEmoji(cl); //!!!
        typeName = Animal.getTypeName(cl); //!!!
        bioTypeCode = Animal.getBioTypeCode(cl);
        maxNumberInCell = Animal.getMaxNumberInCell(cl);
        speed = Animal.getSpeed(cl);
        menu = IslandParam.RATION_PARAM[bioTypeCode];
    }

    public Goat(int row, int col, boolean newBorn) {
        super(row, col, newBorn);
        this.weight = IslandParam.WEIGHT[bioTypeCode];
        this.kgFood = IslandParam.KG_FOOD[bioTypeCode];
        this.number = ++counter;
        this.name = emoji + " " + typeName + "_" + number;
    }


    // Вернуть счётчик объектов
    public static int getCounter() {
        return counter;
    }
}