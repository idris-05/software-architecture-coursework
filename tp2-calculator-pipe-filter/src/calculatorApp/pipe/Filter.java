package calculatorApp.pipe;

// Generic Pipe&Filter processing stage: pull from inPipe, push to outPipe.
public abstract class Filter<IN, OUT> {
    protected Pipe<IN> inPipe;
    protected Pipe<OUT> outPipe;

    public Filter(Pipe<IN> inPipe, Pipe<OUT> outPipe) {
        this.inPipe = inPipe;
        this.outPipe = outPipe;
    }

    // Process one item from input to output.
    public abstract void process();
}