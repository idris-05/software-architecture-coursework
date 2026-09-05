package calculatorApp.calculator;

import calculatorApp.pipe.CalculationRequest;
import calculatorApp.pipe.CalculationResult;
import calculatorApp.pipe.Filter;
import calculatorApp.pipe.Pipe;

// Transforms a CalculationRequest from input pipe into CalculationResult on output pipe.
public class ComputeFilter extends Filter<CalculationRequest, CalculationResult> {
    public ComputeFilter(Pipe<CalculationRequest> inPipe, Pipe<CalculationResult> outPipe) {
        super(inPipe, outPipe);
    }

    @Override
    public void process() {
        CalculationRequest request = inPipe.pull();
        long result;

        // Route the request to the corresponding calculator operation.
        switch (request.operation) {
            case 's':
                result = Calcul.somme(request.operand1, request.operand2);
                break;
            case 'p':
                result = Calcul.prod(request.operand1, request.operand2);
                break;
            case 'f':
                result = Calcul.fact(request.operand1);
                break;
            case 'r':
                result = Calcul.random();
                break;
            default:
                throw new IllegalArgumentException("Unknown operation: " + request.operation);
        }

        // Publish computation output for the next filter.
        outPipe.push(new CalculationResult(request, result));
    }
}
