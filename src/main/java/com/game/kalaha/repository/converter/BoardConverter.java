package com.game.kalaha.repository.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BoardConverter implements AttributeConverter<int[][], int[]> {

    @Override
    public int[] convertToDatabaseColumn(int[][] attribute) {
        int[] column = new int[2 * attribute[0].length];
        System.arraycopy(attribute[0], 0, column, 0, attribute[0].length);
        System.arraycopy(attribute[1], 0, column, attribute[0].length, attribute[0].length);
        return column;
    }

    @Override
    public int[][] convertToEntityAttribute(int[] dbData) {
        int[][] entity = new int[2][dbData.length / 2];
        System.arraycopy(dbData, 0, entity[0], 0, entity[0].length);
        System.arraycopy(dbData, entity[0].length, entity[1], 0, entity[0].length);
        return entity;
    }
}
