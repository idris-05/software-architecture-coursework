package calculator.app;

import calculator.adapter.InterpreterComputationGateway;
import calculator.persistence.FileTraceRepository;
import calculator.persistence.TraceRepository;
import calculator.presentation.presenter.CalculatorPresenter;
import calculator.presentation.view.CalculatorView;
import calculator.presentation.view.SwingCalculatorView;
import calculator.service.CalculatorApplicationService;
import calculator.service.CalculatorService;
import calculator.service.ComputationGateway;

public final class CalculatorApplication {
    private CalculatorApplication() {
    }

    public static void main(String[] args) {
        SwingCalculatorView.runOnUiThread(() -> {
            TraceRepository traceRepository = new FileTraceRepository();
            ComputationGateway computationGateway = new InterpreterComputationGateway();
            CalculatorService calculatorService = new CalculatorApplicationService(computationGateway, traceRepository);
            CalculatorView view = new SwingCalculatorView();

            new CalculatorPresenter(view, calculatorService, () -> System.exit(0));
            view.display();
        });
    }
}
