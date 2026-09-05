package dz.hissab.util;

import dz.hissab.exception.CalcException;

public final class MathSanitizer {
    private MathSanitizer() {
    }

    public static String sanitize(String raw) throws CalcException {
        if (raw == null || raw.isBlank()) {
            throw new CalcException("Cannot sanitize null or empty expression");
        }

        String result = raw
                .replace('−', '-')
                .replace('–', '-')
                .replace('—', '-')
                .replace('×', '*')
                .replace('·', '*')
                .replace('÷', '/');

        result = result.replaceAll("\\s+", "");

        if (!result.matches("^[0-9+\\-*/().]+$")) {
            throw new CalcException("Expression contains invalid characters after sanitization: " + result);
        }

        if (!result.matches(".*[+\\-*/].*")) {
            throw new CalcException("No operator found in expression: " + result);
        }

        return result;
    }
}
