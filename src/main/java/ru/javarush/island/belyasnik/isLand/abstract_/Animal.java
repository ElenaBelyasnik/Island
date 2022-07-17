package ru.javarush.island.belyasnik.isLand.abstract_;


import ru.javarush.island.belyasnik.isLand.annotations.OrganismParam;

@OrganismParam(typeName = "Животное", emoji = "*", weight = 0, maxNumberInCell = 0, speed = 0, kgFood = 0)
public abstract class Animal extends Organism {

    public Animal(int row, int col, boolean newBorn) {
        super(row, col, newBorn);
    }


}
