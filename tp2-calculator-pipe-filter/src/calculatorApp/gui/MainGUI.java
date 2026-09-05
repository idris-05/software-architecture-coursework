package calculatorApp.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Presentation layer of the application.
 * It collects user input, delegates computations through the pipeline, and
 * displays results.
 */
public class MainGUI extends JFrame {

    private final JTextField field1;
    private final JTextField field2;
    private final JTextArea traceArea;
    private final JLabel resultLabel;
    private final JButton btnSomme;
    private final JButton btnProduit;
    private final JButton btnFact;
    private final JButton btnRandom;
    private final JButton btnTrace;
    private final JButton btnQuit;

    public MainGUI() {

        setTitle("Calculatrice - GUI");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== TOP PANEL (INPUTS) =====
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 5, 5));

        inputPanel.add(new JLabel("Nombre 1:"));
        field1 = new JTextField();
        inputPanel.add(field1);

        inputPanel.add(new JLabel("Nombre 2:"));
        field2 = new JTextField();
        inputPanel.add(field2);

        // ===== CENTER PANEL (BUTTONS) =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 2, 10, 10));

        btnSomme = new JButton("Somme");
        btnProduit = new JButton("Produit");
        btnFact = new JButton("Factoriel");
        btnRandom = new JButton("Nombre Aleatoire");
        btnTrace = new JButton("Trace");
        btnQuit = new JButton("Quitter");

        buttonPanel.add(btnSomme);
        buttonPanel.add(btnProduit);
        buttonPanel.add(btnFact);
        buttonPanel.add(btnRandom);
        buttonPanel.add(btnTrace);
        buttonPanel.add(btnQuit);

        // ===== BOTTOM PANEL (RESULT + TRACE) =====
        JPanel bottomPanel = new JPanel(new BorderLayout());

        resultLabel = new JLabel("Resultat: ");
        bottomPanel.add(resultLabel, BorderLayout.NORTH);

        traceArea = new JTextArea();
        traceArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(traceArea);
        bottomPanel.add(scroll, BorderLayout.CENTER);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 5, 10);

        // ===== TOP PANEL (15%) =====
        gbc.gridy = 0;
        gbc.weighty = 0.15;
        add(inputPanel, gbc);

        // ===== CENTER PANEL (25%) =====
        gbc.gridy = 1;
        gbc.weighty = 0.25;
        add(buttonPanel, gbc);

        // ===== BOTTOM PANEL (60%) =====
        gbc.gridy = 2;
        gbc.weighty = 0.6;
        add(bottomPanel, gbc);

        // Wiring is intentionally done outside this class (see Main).
    }

    public int getFirstValue() {
        return Integer.parseInt(field1.getText());
    }

    public int getSecondValue() {
        return Integer.parseInt(field2.getText());
    }

    public void showResult(long result) {
        resultLabel.setText("Resultat: " + result);
    }

    public void showTrace(String traceText) {
        traceArea.setText(traceText);
    }

    public void showError(Exception exception) {
        String message = "Entree invalide";
        if (exception != null && exception.getMessage() != null && !exception.getMessage().isBlank()) {
            message = exception.getMessage();
        }

        JOptionPane.showMessageDialog(this,
                message,
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
    }

    public JButton getBtnSomme() {
        return btnSomme;
    }

    public JButton getBtnProduit() {
        return btnProduit;
    }

    public JButton getBtnFact() {
        return btnFact;
    }

    public JButton getBtnRandom() {
        return btnRandom;
    }

    public JButton getBtnTrace() {
        return btnTrace;
    }

    public JButton getBtnQuit() {
        return btnQuit;
    }

}
