package ru.javarush.island.belyasnik.isLand.bio.herbivores;


import ru.javarush.island.belyasnik.isLand.abstract_.Organism;
import ru.javarush.island.belyasnik.isLand.annotations.OrganismParam;
import ru.javarush.island.belyasnik.isLand.enums.IslandParam;
import ru.javarush.island.belyasnik.isLand.interfaces.PlantAction;

@OrganismParam(typeName = "Растение", emoji = "\uD83C\uDF3F", maxNumberInCell = 200, speed = 0, kgFood = 0)
public class Plant extends Organism implements PlantAction {
    public static final String emoji; //!!!
    public static final String typeName; //!!!
    public static final int bioTypeCode;
    public static final int maxNumberInCell; // максимальное количество в ячейке
    public static final int speed;
    public static int counter = 0; // счётчик всех созданных экземпляров класса
    public static final int[][] menu; // рацион (вид, вероятность, предел насыщения)/d


    static {
        Class<Plant> cl = Plant.class;
        emoji = Organism.getEmoji(cl); //!!!
        typeName = Organism.getTypeName(cl); //!!!
        bioTypeCode = Organism.getBioTypeCode(cl);
        maxNumberInCell = Organism.getMaxNumberInCell(cl);
        speed = Organism.getSpeed(cl);
        menu = IslandParam.RATION_PARAM[bioTypeCode];
    }

    public Plant(int row, int col, boolean newBorn) {
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

    @Override
    public void grow() {

    }
}