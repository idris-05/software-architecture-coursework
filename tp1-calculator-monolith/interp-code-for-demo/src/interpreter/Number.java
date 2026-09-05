package interpreter;

import java.util.Map;

public class Number implements Expression {

    private final long number;

    public Number(long number) {
        this.number = number;
    }

    public long interpret(Map<String, Expression> variables) {
        return number;
    }
}
