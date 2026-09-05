package interpreter;

import java.util.Map;

public class Multiply implements Expression {

    private final Expression leftOperand;
    private final Expression rightOperand;

    public Multiply(Expression left, Expression right) {
        leftOperand = left;
        rightOperand = right;
    }

    public long interpret(Map<String, Expression> variables) {
        return leftOperand.interpret(variables) *
                rightOperand.interpret(variables);
    }
}
