package calculatorApp.gui;

import calculatorApp.pipe.CalculationRequest;
import calculatorApp.pipe.Filter;
import calculatorApp.pipe.Pipe;
import calculatorApp.pipeline.CalculatorPipeline;

// Source filter: converts GUI actions into CalculationRequest messages.
public class GuiSourceFilter extends Filter<Void, CalculationRequest> {

    private final MainGUI view;
    private final CalculatorPipeline pipeline;
    private char operation;

    public GuiSourceFilter(MainGUI view, CalculatorPipeline pipeline, Pipe<CalculationRequest> outPipe) {
        super(null, outPipe);
        this.view = view;
        this.pipeline = pipeline;
        bindActions();
    }

    private void bindActions() {
        view.getBtnSomme().addActionListener(e -> {
            operation = 's';
            process();
        });
        view.getBtnProduit().addActionListener(e -> {
            operation = 'p';
            process();
        });
        view.getBtnFact().addActionListener(e -> {
            operation = 'f';
            process();
        });
        view.getBtnRandom().addActionListener(e -> {
            operation = 'r';
            process();
        });

        view.getBtnTrace().addActionListener(e -> view.showTrace(pipeline.loadTrace()));
        view.getBtnQuit().addActionListener(e -> System.exit(0));
    }

    @Override
    public void process() {
        try {
            int left = (operation == 'r') ? 0 : view.getFirstValue();
            int right = (operation == 's' || operation == 'p') ? view.getSecondValue() : 0;

            outPipe.push(new CalculationRequest(operation, left, right));
            pipeline.run();
        } catch (Exception ex) {
            view.showError(ex);
        }
    }
}