package com.game.kalaha.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameProcessorTest {

    @Test
    void boardSetup() {
        int[][] board = GameProcessor.boardSetup(6, 6);
        int[][] expected = {
                {0, 6, 6, 6, 6, 6, 6},
                {6, 6, 6, 6, 6, 6, 0}
        };
        assertArrayEquals(expected, board);
    }

    @Test
    void sowOrdinal() {
        int[][] init = {
                {0, 6, 6, 6, 6, 6, 6},
                {6, 6, 6, 6, 6, 6, 0}
        };
        int[][] expected = {
                {1, 0, 6, 6, 6, 6, 6},
                {7, 7, 7, 7, 7, 6, 0}
        };
        assertTrue(GameProcessor.sow(init, 0, 1));
        assertArrayEquals(expected, init);
        int[][] initOpp = {
                {0, 6, 6, 6, 6, 6, 6},
                {6, 6, 6, 6, 6, 6, 0}
        };
        int[][] expectedOpp = {
                {0, 6, 7, 7, 7, 7, 7},
                {6, 6, 6, 6, 6, 0, 1}
        };
        assertTrue(GameProcessor.sow(initOpp, 1, 5));
        assertArrayEquals(expectedOpp, initOpp);
    }

    @Test
    void sowExtraTurnRule() {
        int[][] init = {
                {0, 6, 6, 3, 6, 6, 6},
                {6, 6, 6, 6, 6, 6, 0}
        };
        int[][] expected = {
                {1, 7, 7, 0, 6, 6, 6},
                {6, 6, 6, 6, 6, 6, 0}
        };
        assertFalse(GameProcessor.sow(init, 0, 3));
        assertArrayEquals(expected, init);
        int[][] initOpp = {
                {0, 6, 6, 6, 6, 6, 6},
                {6, 6, 6, 3, 6, 6, 0}
        };
        int[][] expectedOpp = {
                {0, 6, 6, 6, 6, 6, 6},
                {6, 6, 6, 0, 7, 7, 1}
        };
        assertFalse(GameProcessor.sow(initOpp, 1, 3));
        assertArrayEquals(expectedOpp, initOpp);
    }

    @Test
    void sowSkipOpponentsPitRule() {
        int[][] init = {
                {0, 10, 6, 6, 6, 6, 6},
                {6, 6, 6, 6, 6, 10, 0}
        };
        int[][] expected = {
                {1, 0, 6, 6, 7, 7, 7},
                {7, 7, 7, 7, 7, 11, 0}
        };
        assertTrue(GameProcessor.sow(init, 0, 1));
        assertArrayEquals(expected, init);
        int[][] initOpp = {
                {0, 10, 6, 6, 6, 6, 6},
                {6, 6, 6, 6, 6, 10, 0}
        };
        int[][] expectedOpp = {
                {0, 11, 7, 7, 7, 7, 7},
                {7, 7, 7, 6, 6, 0, 1}
        };
        assertTrue(GameProcessor.sow(initOpp, 1, 5));
        assertArrayEquals(expectedOpp, initOpp);
    }

    @Test
    void sowCaptureRule() {
        int[][] init = {
                {10, 0, 1, 6, 6, 6, 6},
                {6, 6, 6, 6, 6, 6, 0}
        };
        int[][] expected = {
                {17, 0, 0, 6, 6, 6, 6},
                {0, 6, 6, 6, 6, 6, 0}
        };
        assertTrue(GameProcessor.sow(init, 0, 2));
        assertArrayEquals(expected, init);
        int[][] initOpp = {
                {0, 6, 6, 6, 6, 6, 6},
                {6, 6, 6, 6, 1, 0, 0}
        };
        int[][] expectedOpp = {
                {0, 6, 6, 6, 6, 6, 0},
                {6, 6, 6, 6, 0, 0, 7}
        };
        assertTrue(GameProcessor.sow(initOpp, 1, 4));
        assertArrayEquals(expectedOpp, initOpp);
    }

    @Test
    void gameOver() {
        int[][] continues = {
                {0, 10, 6, 6, 6, 6, 6},
                {6, 6, 6, 6, 6, 10, 0}
        };
        int[][] expected = {
                {0, 10, 6, 6, 6, 6, 6},
                {6, 6, 6, 6, 6, 10, 0}
        };
        assertFalse(GameProcessor.gameOver(continues));
        assertArrayEquals(expected, continues);
        int[][] runOut = {
                {10, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 10}
        };
        int[][] runOutExpected = {
                {10, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 10}
        };
        assertTrue(GameProcessor.gameOver(runOut));
        assertArrayEquals(runOutExpected, runOut);
        int[][] beginnerRunsOut = {
                {0, 0, 0, 0, 0, 0, 0},
                {6, 6, 6, 6, 6, 10, 0}
        };
        int[][] beginnerRunsOutExpected = {
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 40}
        };
        assertTrue(GameProcessor.gameOver(beginnerRunsOut));
        assertArrayEquals(beginnerRunsOutExpected, beginnerRunsOut);
        int[][] opponentRunsOut = {
                {0, 10, 6, 6, 6, 6, 6},
                {0, 0, 0, 0, 0, 0, 10}
        };
        int[][] opponentRunsOutExpected = {
                {40, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 10}
        };
        assertTrue(GameProcessor.gameOver(opponentRunsOut));
        assertArrayEquals(opponentRunsOutExpected, opponentRunsOut);
    }

    @Test
    void compareScore() {
        int[][] beginner = {
                {10, 0, 1, 6, 6, 6, 6},
                {6, 6, 6, 6, 6, 6, 1}
        };
        assertEquals(9, GameProcessor.compareScore(beginner));
        int[][] draw = {
                {10, 0, 1, 6, 6, 6, 6},
                {6, 6, 6, 6, 6, 6, 10}
        };
        assertEquals(0, GameProcessor.compareScore(draw));
        int[][] opponent = {
                {0, 0, 1, 6, 6, 6, 6},
                {6, 6, 6, 6, 6, 6, 10}
        };
        assertEquals(-10, GameProcessor.compareScore(opponent));
    }
}