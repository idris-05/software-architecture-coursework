package interpreter;

import java.util.Map;
import java.util.Stack;

/**
 * Builds and evaluates an expression tree from a Reverse Polish Notation input string.
 */
public class Evaluator implements Expression {

    private final Expression syntaxTree;

    public Evaluator(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression must not be empty.");
        }

        Stack<Expression> expressionStack = new Stack<>();

        for (String token : expression.trim().split("\\s+")) {

            if (token.equals("+")) {
                validateOperands(expressionStack, token);

                Expression right = expressionStack.pop();
                Expression left = expressionStack.pop();
                Expression subExpression = new Plus(left, right);

                expressionStack.push(subExpression);

            } else if (token.equals("-")) {
                validateOperands(expressionStack, token);

                Expression right = expressionStack.pop();
                Expression left = expressionStack.pop();

                Expression subExpression = new Minus(left, right);

                expressionStack.push(subExpression);

            } else if (token.equals("*")) {
                validateOperands(expressionStack, token);

                Expression right = expressionStack.pop();
                Expression left = expressionStack.pop();

                Expression subExpression = new Multiply(left, right);

                expressionStack.push(subExpression);

            } else {
                // Try to parse as a number literal, otherwise treat as variable
                try {
                    expressionStack.push(new Number(Long.parseLong(token)));
                } catch (NumberFormatException e) {
                    expressionStack.push(new Variable(token));
                }
            }
        }

        if (expressionStack.size() != 1) {
            throw new IllegalArgumentException("Invalid RPN expression: " + expression);
        }

        syntaxTree = expressionStack.pop();
    }

    public long interpret(Map<String, Expression> context) {
        return syntaxTree.interpret(context);
    }

    private void validateOperands(Stack<Expression> expressionStack, String operator) {
        if (expressionStack.size() < 2) {
            throw new IllegalArgumentException(
                    "Invalid RPN expression: operator '" + operator + "' is missing operands.");
        }
    }
}
