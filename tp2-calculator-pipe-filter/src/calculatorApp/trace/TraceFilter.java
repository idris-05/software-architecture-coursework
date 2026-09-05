package calculatorApp.trace;

import calculatorApp.pipe.CalculationResult;
import calculatorApp.pipe.Filter;
import calculatorApp.pipe.Pipe;
import calculatorApp.pipe.TraceRecord;

public class TraceFilter extends Filter<CalculationResult, TraceRecord> {
    public TraceFilter(Pipe<CalculationResult> inPipe, Pipe<TraceRecord> outPipe) {
        super(inPipe, outPipe);
    }

    @Override
    public void process() {
        CalculationResult result = inPipe.pull();
        // Persist operation immediately in the trace store.
        Trace.savop(result.request.operation, result.request.operand1, result.request.operand2, result.result);
        outPipe.push(new TraceRecord(result));
    }

    public String loadTrace() {
        return Trace.loadTraceString();
    }
}