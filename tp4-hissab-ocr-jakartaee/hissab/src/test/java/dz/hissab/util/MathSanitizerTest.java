package dz.hissab.util;

import dz.hissab.exception.CalcException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MathSanitizerTest {
    @Test
    void sanitizeShouldNormalizeSymbolsAndWhitespace() throws CalcException {
        String sanitized = MathSanitizer.sanitize(" 10 × 2 − 3 ÷ 1 ");
        assertEquals("10*2-3/1", sanitized);
    }

    @Test
    void sanitizeShouldRejectNullOrBlankInput() {
        CalcException nullEx = assertThrows(CalcException.class, () -> MathSanitizer.sanitize(null));
        assertEquals("Cannot sanitize null or empty expression", nullEx.getMessage());

        CalcException blankEx = assertThrows(CalcException.class, () -> MathSanitizer.sanitize(" "));
        assertEquals("Cannot sanitize null or empty expression", blankEx.getMessage());
    }

    @Test
    void sanitizeShouldRejectInvalidCharacters() {
        CalcException ex = assertThrows(CalcException.class, () -> MathSanitizer.sanitize("12 + abc"));
        assertEquals("Expression contains invalid characters after sanitization: 12+abc", ex.getMessage());
    }

    @Test
    void sanitizeShouldRejectExpressionWithoutOperator() {
        CalcException ex = assertThrows(CalcException.class, () -> MathSanitizer.sanitize("12345"));
        assertEquals("No operator found in expression: 12345", ex.getMessage());
    }
}
