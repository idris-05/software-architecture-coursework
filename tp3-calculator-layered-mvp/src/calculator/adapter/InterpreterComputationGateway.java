package calculator.adapter;

import calculator.service.ComputationGateway;
import interpreter.Evaluator;
import interpreter.Expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InterpreterComputationGateway implements ComputationGateway {
    private final Map<String, Expression> context = new HashMap<>();
    private final Random random = new Random();

    @Override
    public long add(int a, int b) {
        return evaluate(a + " " + b + " +");
    }

    @Override
    public long multiply(int a, int b) {
        return evaluate(a + " " + b + " *");
    }

    @Override
    public long factorial(int n) {
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

    @Override
    public long random() {
        return random.nextInt(10000);
    }

    private long evaluate(String expression) {
        Evaluator evaluator = new Evaluator(expression);
        return evaluator.interpret(context);
    }
}
