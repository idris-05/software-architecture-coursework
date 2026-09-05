package calc.soap;

import calc.calculator.Calculator;
import calc.model.HistoryRecord;
import calc.persistence.HistoryManager;

import jakarta.jws.WebService;
import java.util.List;

@WebService(endpointInterface = "calc.soap.CalculatorSoapService")
public class CalculatorSoapServiceImpl implements CalculatorSoapService {

    @Override
    public double add(double a, double b) {
        double result = Calculator.add(a, b);
        HistoryManager.getInstance().save(a, "+", b, result, "SOAP");
        return result;
    }

    @Override
    public double subtract(double a, double b) {
        double result = Calculator.subtract(a, b);
        HistoryManager.getInstance().save(a, "-", b, result, "SOAP");
        return result;
    }

    @Override
    public double multiply(double a, double b) {
        double result = Calculator.multiply(a, b);
        HistoryManager.getInstance().save(a, "*", b, result, "SOAP");
        return result;
    }

    @Override
    public double divide(double a, double b) throws Exception {
        double result;
        try {
            result = Calculator.divide(a, b);
        } catch (ArithmeticException e) {
            throw new Exception("Division by zero");
        }
        HistoryManager.getInstance().save(a, "/", b, result, "SOAP");
        return result;
    }

    @Override
    public List<HistoryRecord> getHistory() {
        return HistoryManager.getInstance().getAll();
    }
}
