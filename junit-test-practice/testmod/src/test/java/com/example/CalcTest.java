package com.example;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CalcTest {
    private final Calculator calc = new Calculator();

    @Test
    @DisplayName("Test addition of two positive numbers(happy path)")
    void addBasic() {
        // arrange and act
        int result = calc.add(2, 3);
        // assert
        assertEquals(5, result);

    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, -1})
    void add_with_zero(int x) {
        // arrange and act
        int result = calc.add(x, 0);
        // assert
        assertEquals(x, result);
    }

    @ParameterizedTest
    @CsvSource({
        "1, 2, 3",
        "-1, -2, -3",
        "0, 0, 0",
        "100, 200, 300"
    })

    void add_table(int a, int b, int expected) {
        // arrange and act
        int result = calc.add(a, b);
        // assert
        assertEquals(expected, result);
    }

    @Test
    void divide_throws_on_zero() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> calc.divide(10, 0));
        assertEquals("Division by zero is not allowed.", ex.getMessage());
    }
    
    @Nested
    class ClampTests {
        @Test
        void clamp_within_bounds() {
            int result = calc.clamp(5, 1, 10);
            assertEquals(5, result);
        }

        @Test
        void clamp_below_min() {
            int result = calc.clamp(-5, 0, 10);
            assertEquals(0, result);
        }

        @Test
        void clamp_above_max() {
            int result = calc.clamp(15, 0, 10);
            assertEquals(10, result);
        }

        @Test
        void clamp_min_greater_than_max() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> calc.clamp(5, 10, 1));
            assertEquals("Minimum value cannot be greater than maximum value.", ex.getMessage());
        }

        @Test
        void multiClamp() {
            int result = calc.clamp(calc.clamp(15, 0, 10), 5, 8);
            assertAll(
                () -> assertEquals(8, result),
                () -> assertEquals(10, calc.clamp(15, 0, 10)),
                () -> assertEquals(8, calc.clamp(8, 5, 8))
            );
        }
    }

}
