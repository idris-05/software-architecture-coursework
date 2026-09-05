package calculatorApp.pipe;

public class TraceRecord {
    // Payload consumed by GUI sink to update the displayed result.
    public final CalculationResult result;

    public TraceRecord(CalculationResult result) {
        this.result = result;
    }
}