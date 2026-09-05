package dz.hissab.calc;

import dz.hissab.exception.CalcException;
import jakarta.ejb.Local;

@Local
public interface CalcService {
    double add(double a, double b);

    double subtract(double a, double b);

    double multiply(double a, double b);

    double divide(double a, double b) throws CalcException;

    double evaluate(String expression) throws CalcException;

    CalculationDetails evaluateDetailed(String expression) throws CalcException;
}
