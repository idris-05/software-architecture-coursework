package calculator.service;

import calculator.domain.TraceEntry;
import calculator.domain.OperationType;
import calculator.persistence.TraceRepository;

public class CalculatorApplicationService implements CalculatorService {
    private final ComputationGateway computationGateway;
    private final TraceRepository traceRepository;

    public CalculatorApplicationService(ComputationGateway computationGateway, TraceRepository traceRepository) {
        this.computationGateway = computationGateway;
        this.traceRepository = traceRepository;
    }

    @Override
    public long add(int a, int b) {
        long result = computationGateway.add(a, b);
        traceRepository.save(new TraceEntry(OperationType.SUM, a, b, result));
        return result;
    }

    @Override
    public long multiply(int a, int b) {
        long result = computationGateway.multiply(a, b);
        traceRepository.save(new TraceEntry(OperationType.PRODUCT, a, b, result));
        return result;
    }

    @Override
    public long factorial(int n) {
        long result = computationGateway.factorial(n);
        traceRepository.save(new TraceEntry(OperationType.FACTORIAL, n, 0, result));
        return result;
    }

    @Override
    public long random() {
        long result = computationGateway.random();
        traceRepository.save(new TraceEntry(OperationType.RANDOM, 0, 0, result));
        return result;
    }

    @Override
    public String loadTraceAsText() {
        return traceRepository.readAllAsText();
    }
}
