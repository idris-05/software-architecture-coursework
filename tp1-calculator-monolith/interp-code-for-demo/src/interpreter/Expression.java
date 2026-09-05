package interpreter;

import java.util.Map;

public interface Expression {
    long interpret(Map<String, Expression> variables);
}
