package calculatorApp.gui;

import calculatorApp.pipe.TraceRecord;

// UI adapter that maps pipeline output records to view updates.
public class GuiOutputAdapter {

    private final MainGUI view;

    public GuiOutputAdapter(MainGUI view) {
        this.view = view;
    }

    public void display(TraceRecord record) {
        if (record != null) {
            view.showResult(record.result.result);
        }
    }
}