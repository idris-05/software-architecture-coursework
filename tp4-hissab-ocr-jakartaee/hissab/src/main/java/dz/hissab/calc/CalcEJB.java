package dz.hissab.calc;

import dz.hissab.exception.CalcException;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;

@Stateless
@Local(CalcService.class)
public class CalcEJB implements CalcService {
    @Override
    public double add(double a, double b) {
        return a + b;
    }

    @Override
    public double subtract(double a, double b) {
        return a - b;
    }

    @Override
    public double multiply(double a, double b) {
        return a * b;
    }

    @Override
    public double divide(double a, double b) throws CalcException {
        if (b == 0.0) {
            throw new CalcException("Division by zero");
        }
        return a / b;
    }

    @Override
    public double evaluate(String expression) throws CalcException {
        if (expression == null || expression.isBlank()) {
            throw new CalcException("Expression is null or empty");
        }
        return new ExpressionEvaluator().evaluate(expression, this);
    }

    @Override
    public CalculationDetails evaluateDetailed(String expression) throws CalcException {
        if (expression == null || expression.isBlank()) {
            throw new CalcException("Expression is null or empty");
        }
        return new ExpressionEvaluator().evaluateDetailed(expression, this);
    }
}
