package calculatorApp.pipe;

public class CalculationRequest {
    // Operation code: s(sum), p(product), f(factorial), r(random).
    public final char operation;
    public final int operand1;
    public final int operand2;

    public CalculationRequest(char operation, int operand1, int operand2) {
        this.operation = operation;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }
}