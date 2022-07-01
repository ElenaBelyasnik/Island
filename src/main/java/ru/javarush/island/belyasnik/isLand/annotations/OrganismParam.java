package ru.javarush.island.belyasnik.isLand.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OrganismParam {
    String emoji() default "";

    String typeName() default "";

    int bioTypeCode() default 0; // код вида

    double weight() default 1;  // вес животного

    int speed() default 1;   // Скорость перемещения, не более чем, клеток за ход

    double kgFood() default 1;  // Сколько килограммов пищи нужно животному для полного насыщения

    int maxNumberInCell() default 1; // Максимальное количество животных этого вида на одной клетке

}
