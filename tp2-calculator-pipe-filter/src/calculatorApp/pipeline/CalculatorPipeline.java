package calculatorApp.pipeline;

import calculatorApp.calculator.ComputeFilter;
import calculatorApp.gui.GuiOutputAdapter;
import calculatorApp.gui.MainGUI;
import calculatorApp.pipe.CalculationRequest;
import calculatorApp.pipe.CalculationResult;
import calculatorApp.pipe.Pipe;
import calculatorApp.pipe.TraceRecord;
import calculatorApp.trace.TraceFilter;

public class CalculatorPipeline {
    // Request channel shared between source and compute filters.
    private final Pipe<CalculationRequest> requestPipe = new Pipe<>();
    // Internal channel shared between adjacent filters.
    private final Pipe<CalculationResult> resultPipe = new Pipe<>();
    // Output channel shared between trace filter and GUI output adapter.
    private final Pipe<TraceRecord> tracePipe = new Pipe<>();

    private final ComputeFilter computeFilter;
    private final TraceFilter traceFilter;
    private final GuiOutputAdapter outputAdapter;

    public CalculatorPipeline(MainGUI view) {
        // Adjacency wiring: out of one filter is the in of the next.
        computeFilter = new ComputeFilter(requestPipe, resultPipe);
        traceFilter = new TraceFilter(resultPipe, tracePipe);
        // note: output adapter is not a filter, so it doesn't have input/output pipes.
        // It directly consumes from tracePipe.
        outputAdapter = new GuiOutputAdapter(view);
    }

    public void run() {
        // Execute one pipeline cycle on the data currently in requestPipe.
        computeFilter.process();
        traceFilter.process();
        outputAdapter.display(tracePipe.pull());
    }

    public String loadTrace() {
        return traceFilter.loadTrace();
    }

    public Pipe<CalculationRequest> getInputPipe() {
        return requestPipe;
    }

}