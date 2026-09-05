package calculator;

import interpreter.Evaluator;
import interpreter.Expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Adapter between the GUI and the Interpreter.
 * Delegates calculations to the interpreter instead of computing directly.
 */
public class Calcul {

    private static final Map<String, Expression> CONTEXT = new HashMap<>();
    private static final Random RANDOM = new Random();

    public static long somme(int c, int d) {
        return evaluate(c + " " + d + " +");
    }

    public static long prod(int c, int d) {
        return evaluate(c + " " + d + " *");
    }

    public static long fact(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Factorial is undefined for negative numbers (n=" + n + ").");
        }
        if (n <= 1) {
            return 1;
        }

        StringBuilder expression = new StringBuilder("1");
        for (int i = 2; i <= n; i++) {
            expression.append(" ").append(i).append(" *");
        }

        return evaluate(expression.toString());
    }

    public static long random() {
        return RANDOM.nextInt(10000);
    }

    /**
     * Centralizes the call to the interpreter so the GUI only depends on this adapter.
     */
    private static long evaluate(String expression) {
        Evaluator evaluator = new Evaluator(expression);
        return evaluator.interpret(CONTEXT);
    }
}
