package calculatorApp.pipe;

public class CalculationResult {
    // Keep original request for downstream trace formatting/persistence.
    public final CalculationRequest request;
    public final long result;

    public CalculationResult(CalculationRequest request, long result) {
        this.request = request;
        this.result = result;
    }
}