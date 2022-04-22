package com.game.kalaha.converter;

import com.game.kalaha.repository.converter.BoardConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class BoardConverterTest {

    private final int[] oneDim = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
    private final int[][] twoDim = {{0, 1, 2, 3, 4, 5, 6}, {7, 8, 9, 10, 11, 12, 13}};

    @Test
    void convertToDatabaseColumn() {
        BoardConverter boardConverter = new BoardConverter();
        assertArrayEquals(oneDim, boardConverter.convertToDatabaseColumn(twoDim));
    }

    @Test
    void convertToEntityAttribute() {
        BoardConverter boardConverter = new BoardConverter();
        assertArrayEquals(twoDim, boardConverter.convertToEntityAttribute(oneDim));
    }
}