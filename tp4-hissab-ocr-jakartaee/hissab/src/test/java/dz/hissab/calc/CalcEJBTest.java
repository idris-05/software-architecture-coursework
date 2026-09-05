package dz.hissab.calc;

import dz.hissab.exception.CalcException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalcEJBTest {
    private final CalcEJB calc = new CalcEJB();

    @Test
    void primitiveOperationsShouldWork() throws CalcException {
        assertEquals(9.0, calc.add(4.0, 5.0));
        assertEquals(1.0, calc.subtract(4.0, 3.0));
        assertEquals(12.0, calc.multiply(4.0, 3.0));
        assertEquals(2.0, calc.divide(6.0, 3.0));
    }

    @Test
    void divideShouldThrowOnZero() {
        CalcException ex = assertThrows(CalcException.class, () -> calc.divide(8.0, 0.0));
        assertEquals("Division by zero", ex.getMessage());
    }

    @Test
    void evaluateShouldRespectOperatorPrecedence() throws CalcException {
        assertEquals(14.0, calc.evaluate("5 + 2 * 6 - 3"));
    }

    @Test
    void evaluateShouldHandleParentheses() throws CalcException {
        assertEquals(21.0, calc.evaluate("(5 + 2) * 3"));
    }

    @Test
    void evaluateDetailedShouldReturnResultAndSteps() throws CalcException {
        CalculationDetails details = calc.evaluateDetailed("(5 + 2) * 3");
        assertEquals(21.0, details.result());
        assertEquals(2, details.steps().size());
        assertEquals("5 + 2 = 7", details.steps().get(0));
        assertEquals("7 * 3 = 21", details.steps().get(1));
    }

    @Test
    void evaluateShouldRejectNullOrBlankExpression() {
        CalcException nullEx = assertThrows(CalcException.class, () -> calc.evaluate(null));
        assertEquals("Expression is null or empty", nullEx.getMessage());

        CalcException blankEx = assertThrows(CalcException.class, () -> calc.evaluate("   "));
        assertEquals("Expression is null or empty", blankEx.getMessage());
    }

    @Test
    void evaluateShouldRejectUnknownCharacters() {
        CalcException ex = assertThrows(CalcException.class, () -> calc.evaluate("5 & 2"));
        assertEquals("Unknown character: &", ex.getMessage());
    }

    @Test
    void evaluateShouldRejectMalformedExpression() {
        CalcException ex = assertThrows(CalcException.class, () -> calc.evaluate("5 +"));
        assertTrue(ex.getMessage().startsWith("Malformed expression: "));
    }
}
