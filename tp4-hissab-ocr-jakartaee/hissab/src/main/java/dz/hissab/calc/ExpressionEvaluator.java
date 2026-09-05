package dz.hissab.calc;

import dz.hissab.exception.CalcException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;

class ExpressionEvaluator {
    double evaluate(String expression, CalcService calc) throws CalcException {
        return evaluateInternal(expression, calc, null).result();
    }

    CalculationDetails evaluateDetailed(String expression, CalcService calc) throws CalcException {
        List<String> steps = new ArrayList<>();
        CalculationDetails details = evaluateInternal(expression, calc, steps);
        return new CalculationDetails(details.result(), List.copyOf(steps));
    }

    private CalculationDetails evaluateInternal(String expression, CalcService calc, List<String> stepCollector)
            throws CalcException {
        List<String> tokens = tokenize(expression);
        if (tokens.isEmpty()) {
            throw new CalcException("Malformed expression: " + expression);
        }

        Deque<Double> values = new ArrayDeque<>();
        Deque<Character> operators = new ArrayDeque<>();

        try {
            for (String token : tokens) {
                if ("(".equals(token)) {
                    operators.push('(');
                } else if (")".equals(token)) {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        char op = operators.pop();
                        double b = values.pop();
                        double a = values.pop();
                        values.push(applyAndRecord(op, b, a, calc, stepCollector));
                    }
                    if (operators.isEmpty() || operators.pop() != '(') {
                        throw new CalcException("Malformed expression: " + expression);
                    }
                } else if (token.length() == 1 && "+-*/".indexOf(token.charAt(0)) >= 0) {
                    char currentOp = token.charAt(0);
                    while (!operators.isEmpty()
                            && operators.peek() != '('
                            && precedence(operators.peek()) >= precedence(currentOp)) {
                        char op = operators.pop();
                        double b = values.pop();
                        double a = values.pop();
                        values.push(applyAndRecord(op, b, a, calc, stepCollector));
                    }
                    operators.push(currentOp);
                } else {
                    values.push(Double.parseDouble(token));
                }
            }

            while (!operators.isEmpty()) {
                char op = operators.pop();
                if (op == '(' || op == ')') {
                    throw new CalcException("Malformed expression: " + expression);
                }
                double b = values.pop();
                double a = values.pop();
                values.push(applyAndRecord(op, b, a, calc, stepCollector));
            }
        } catch (NumberFormatException | NoSuchElementException e) {
            throw new CalcException("Malformed expression: " + expression, e);
        }

        if (values.size() != 1) {
            throw new CalcException("Malformed expression: " + expression);
        }
        return new CalculationDetails(values.pop(), List.of());
    }

    private List<String> tokenize(String expression) throws CalcException {
        List<String> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();

        for (char ch : expression.toCharArray()) {
            if (Character.isDigit(ch) || ch == '.') {
                number.append(ch);
            } else if (Character.isWhitespace(ch)) {
                if (!number.isEmpty()) {
                    tokens.add(number.toString());
                    number.setLength(0);
                }
            } else if ("+-*/()".indexOf(ch) >= 0) {
                if (!number.isEmpty()) {
                    tokens.add(number.toString());
                    number.setLength(0);
                }
                tokens.add(String.valueOf(ch));
            } else {
                throw new CalcException("Unknown character: " + ch);
            }
        }

        if (!number.isEmpty()) {
            tokens.add(number.toString());
        }

        return tokens;
    }

    private int precedence(char op) {
        if (op == '*' || op == '/') {
            return 2;
        }
        if (op == '+' || op == '-') {
            return 1;
        }
        return 0;
    }

    private double applyOp(char op, double b, double a, CalcService calc) throws CalcException {
        return switch (op) {
            case '+' -> calc.add(a, b);
            case '-' -> calc.subtract(a, b);
            case '*' -> calc.multiply(a, b);
            case '/' -> calc.divide(a, b);
            default -> throw new CalcException("Unknown operator: " + op);
        };
    }

    private double applyAndRecord(char op, double b, double a, CalcService calc, List<String> stepCollector)
            throws CalcException {
        double result = applyOp(op, b, a, calc);
        if (stepCollector != null) {
            stepCollector.add(formatNumber(a) + " " + op + " " + formatNumber(b) + " = " + formatNumber(result));
        }
        return result;
    }

    private String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}
