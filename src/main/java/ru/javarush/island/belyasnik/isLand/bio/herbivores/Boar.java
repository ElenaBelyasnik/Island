package ru.javarush.island.belyasnik.isLand.bio.herbivores;

import ru.javarush.island.belyasnik.isLand.abstract_.Animal;
import ru.javarush.island.belyasnik.isLand.abstract_.Organism;
import ru.javarush.island.belyasnik.isLand.annotations.OrganismParam;
import ru.javarush.island.belyasnik.isLand.enums.IslandParam;

@OrganismParam(typeName = "Кабан", emoji = "\uD83D\uDC17", bioTypeCode = 7, weight = 400, maxNumberInCell = 50, speed = 2, kgFood = 50)
public class Boar extends Animal {
    public static final String emoji; //!!!
    public static final String typeName; //!!!
    public static final int bioTypeCode;
    public static final int maxNumberInCell; // максимальное количество в ячейке
    public static final int speed;
    public static int counter = 0; // счётчик всех созданных экземпляров класса
    public static final int[][] menu; // рацион (вид, вероятность, предел насыщения)/d

    static {
        Class<Boar> cl = Boar.class;
        emoji = Organism.getEmoji(cl); //!!!
        typeName = Organism.getTypeName(cl); //!!!
        bioTypeCode = Organism.getBioTypeCode(cl);
        maxNumberInCell = Organism.getMaxNumberInCell(cl);
        speed = Organism.getSpeed(cl);
        menu = IslandParam.RATION_PARAM[bioTypeCode];
    }

    public Boar(int row, int col, boolean newBorn) {
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