package calculatorApp;

import javax.swing.SwingUtilities;

import calculatorApp.gui.GuiSourceFilter;
import calculatorApp.gui.MainGUI;
import calculatorApp.pipeline.CalculatorPipeline;

public class Main {
    public static void main(String[] args) {
        // Compose the app on Swing's UI thread.
        SwingUtilities.invokeLater(() -> {
            MainGUI view = new MainGUI();
            CalculatorPipeline pipeline = new CalculatorPipeline(view);
            new GuiSourceFilter(view, pipeline, pipeline.getInputPipe());

            view.setVisible(true);
        });
    }
}