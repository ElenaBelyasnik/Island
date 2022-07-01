package ru.javarush.island.belyasnik.isLand.abstract_;


import ru.javarush.island.belyasnik.isLand.annotations.OrganismParam;
import ru.javarush.island.belyasnik.isLand.util.Randomizer;

@OrganismParam(weight = 0, maxNumberInCell = 0, speed = 0, kgFood = 0)
public abstract class Organism {
    public int row;
    public int col;
    public int number;
    public String name;
    public double weight;   // вес организма
    public double kgFood;   // сколько кг пищи нужно для насыщения
    public boolean dead = false;    // признак того, что организм умер
    public double fullnessLevel = 0.0d; // уровень сытости
    public boolean hungry = false;  // признак того, что организм голодает
    public boolean ate = false;     // признак того, что организм выполнил процесс еды
    public final int male = Randomizer.get(0, 2); // признак мужского пола (1 - М, 0 - Ж)
    boolean newBorn; // признак новорождённого

    public Organism(int row, int col, boolean newBorn) {
        this.row = row;
        this.col = col;
        this.newBorn = newBorn;
    }

    public boolean isNewBorn() {
        return newBorn;
    }

    public void setNewBorn(boolean newBorn) {
        this.newBorn = newBorn;
    }

    // вернуть признак пола: 0 - самка, 1 - самец
    public int getMale() {
        return male;
    }

    // Вернуть изображение emoji для биологического вида
    public static String getEmoji(Class<? extends Organism> cl) {
        if (cl.isAnnotationPresent(OrganismParam.class)) {
            OrganismParam organismParameters = cl.getAnnotation(OrganismParam.class);
            return organismParameters.emoji();
        }
        return "";
    }

    // Вернуть наименование биологического вида
    public static String getTypeName(Class<? extends Organism> cl) {
        if (cl.isAnnotationPresent(OrganismParam.class)) {
            OrganismParam organismParameters = cl.getAnnotation(OrganismParam.class);
            return organismParameters.typeName();
        }
        return "";
    }


    // Вернуть код биологического вида
    public static int getBioTypeCode(Class<? extends Organism> cl) {
        if (cl.isAnnotationPresent(OrganismParam.class)) {
            OrganismParam organismParameters = cl.getAnnotation(OrganismParam.class);
            return organismParameters.bioTypeCode();
        }
        return 0;
    }


    // Вернуть максимальное количество животных этого вида на одной клетке
    public static int getMaxNumberInCell(Class<? extends Organism> cl) {
        if (cl.isAnnotationPresent(OrganismParam.class)) {
            OrganismParam organismParameters = cl.getAnnotation(OrganismParam.class);
            return organismParameters.maxNumberInCell();
        }
        return 0;
    }

    // Вернуть скорость перемещения, не более чем, клеток за ход
    public static int getSpeed(Class<? extends Organism> cl) {
        if (cl.isAnnotationPresent(OrganismParam.class)) {
            OrganismParam organismParameters = cl.getAnnotation(OrganismParam.class);
            return organismParameters.speed();
        }
        return 0;
    }


    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getKgFood() {
        return kgFood;
    }

    public void setKgFood(double kgFood) {
        this.kgFood = kgFood;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public void setFullnessLevel(double fullnessLevel) {
        this.fullnessLevel = fullnessLevel;
    }

    public boolean isHungry() {
        return hungry;
    }

    public void setHungry(boolean hungry) {
        this.hungry = hungry;
    }

    public boolean isAte() {
        return ate;
    }

    public void setAte(boolean ate) {
        this.ate = ate;
    }

    public Object getMonitor() {
        return this;
    }

    @Override
    public String toString() {
        return name;
    }

}



