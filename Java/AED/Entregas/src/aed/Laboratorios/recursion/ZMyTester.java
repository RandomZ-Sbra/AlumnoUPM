package aed.Laboratorios.recursion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import es.upm.aedlib.indexedlist.ArrayIndexedList;

public class ZMyTester {
    
    @Test
    public void test_01() {
        assertEquals(4, Utils.multiply(2, 2));
        assertEquals(8, Utils.multiply(4, 2));
        assertEquals(8, Utils.multiply(2, 4));
        assertEquals(21, Utils.multiply(3, 7));
        assertEquals(0, Utils.multiply(2, 0));
        assertEquals(9, Utils.multiply(9, 1));
    }

    @Test
    public void test_02() {
        assertEquals(-4, Utils.multiply(-2, 2));
        assertEquals(-8, Utils.multiply(4, -2));
        assertEquals(8, Utils.multiply(-2, -4));
        assertEquals(-21, Utils.multiply(-3, 7));
        assertEquals(0, Utils.multiply(-2, 0));
        assertEquals(9, Utils.multiply(-9, -1));
    }

    @Test
    public void test_03() {
        Integer[] arr1 = {1, 2, 3, 4    };
        assertEquals(0, Utils.findBottom(new ArrayIndexedList<>(arr1)));
        Integer[] arr2 = {3, 2, 1, 1, 2, 3, 4, 5, 6, 7, 6, 5};
        try {
            assertEquals(11, Utils.findBottom(new ArrayIndexedList<>(arr2)));
        } catch (AssertionError e) {
            try {
                assertEquals(3, Utils.findBottom(new ArrayIndexedList<>(arr2)));
            } catch (AssertionError e2) {
                assertEquals(2, Utils.findBottom(new ArrayIndexedList<>(arr2)));
            }
        }
    }
}
