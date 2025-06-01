import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalculatorTest {
    private final Calculator calculator = new Calculator();

    @Test
    void testGivenExamples() {
        assertEquals("5", calculator.calculate("2 + 3"));
        assertEquals("7", calculator.calculate("3 * 2 + 1"));
        assertEquals("0", calculator.calculate("3 * -2 + 6"));
    }

    @Test
    void testBasicOperations() {
        assertEquals("3.5", calculator.calculate("1.5 + 2"));
        assertEquals("-0.5", calculator.calculate("1.5 - 2"));
        assertEquals("3", calculator.calculate("1.5 * 2"));
        assertEquals("0.75", calculator.calculate("1.5 / 2"));
    }

    @Test
    void testDivisionAccuracy() {
        assertEquals("1", calculator.calculate("2 / 2")); // not 1.0
        assertEquals("1.5", calculator.calculate("3 / 2")); // not 1
    }

    @Test
    void testUnsupportedOperations() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate("3 * -2 ^ 6"));
    }

    @Test
    void testArithmeticOrder() {
        assertEquals("-9", calculator.calculate("3 + -2 * 6"));
        assertEquals("6", calculator.calculate("(3 + -2) * 6"));
    }

    @Test
    void testBrackets() {
        assertEquals("9", calculator.calculate("(1+2)*3"));
        assertEquals("3", calculator.calculate("((2)) + 1"));
        assertEquals("5", calculator.calculate("(2*(3+4))-9"));
    }

    @Test
    void testScientificNotation() {
        assertEquals("150000000", calculator.calculate("1.5e3 * 1e5"));
        assertEquals("1500000000", calculator.calculate("1.5e3 * 1e6"));
        assertEquals("1e+10", calculator.calculate("9999999999 + 1"));
        assertEquals("1.5e+10", calculator.calculate("1.5e3 * 1e7"));
        assertEquals("600", calculator.calculate("3 * 2E2"));
        assertEquals("6e+10", calculator.calculate("3 * 2E10"));
        assertEquals("0.15", calculator.calculate("1.5 / 10"));
        assertEquals("0.015", calculator.calculate("1.5 / 100"));
        assertEquals("0.0015", calculator.calculate("1.5 / 1000"));
        assertEquals("0.00015", calculator.calculate("1.5 / 10000"));
        assertEquals("1e-4", calculator.calculate("1 / 10000"));
        assertEquals("1.5e-5", calculator.calculate("1.5 / 100000"));
        assertEquals("1.5e-10", calculator.calculate("1.5 / 10000000000"));
    }

    @Test
    void testSpaces() {
        assertEquals("135", calculator.calculate("123 + 12"));
        assertEquals("135", calculator.calculate("123 + 1 2"));
        assertEquals("135", calculator.calculate("12 3 + 12"));
        assertEquals("135", calculator.calculate("12 3 + 1 2"));
        assertEquals("135", calculator.calculate("1 23 + 12"));
        assertEquals("135", calculator.calculate("1 23 + 1 2"));
        assertEquals("135", calculator.calculate("1 2 3 + 12"));
        assertEquals("135", calculator.calculate("1 2 3 + 1 2"));
    }

    @Test
    void testMixedSeparatorsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate("1.5 + 2,5"));
    }

    @Test
    void testUnaryMinus() {
        assertEquals("-3", calculator.calculate("-1 + -2"));
        assertEquals("1", calculator.calculate("-1 + 2"));
        assertEquals("-1", calculator.calculate("1 + -2"));
    }

    @Test
    void testSingleNumberThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate("42"));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate("1.5e3"));
    }

    @Test
    void testDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> calculator.calculate("1/0"));
    }

    @Test
    void testAutomaticSeparatorDetection() {
        assertEquals("3,5", calculator.calculate("1,5 + 2"));
        assertEquals("3.5", calculator.calculate("1.5 + 2"));
    }

    @Test
    void testOnlyOneNumber() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate("10"));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate("-1"));
    }

    @Test
    void testInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(null));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(""));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate("-"));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate("+"));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate("*"));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate("/"));
    }
}
