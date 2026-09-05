package calculator;

import javax.swing.*;
import java.awt.*;

/**
 * Presentation layer of the application.
 * It collects user input, delegates computations to Calcul, and owns trace logging.
 */
public class MainGUI extends JFrame {

    private JTextField field1;
    private JTextField field2;
    private JTextArea traceArea;
    private JLabel resultLabel;

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

        JButton btnSomme = new JButton("Somme");
        JButton btnProduit = new JButton("Produit");
        JButton btnFact = new JButton("Factoriel");
        JButton btnRandom = new JButton("Nombre Aleatoire");
        JButton btnTrace = new JButton("Trace");
        JButton btnQuit = new JButton("Quitter");

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

        // ===== BUTTON ACTIONS =====

        btnSomme.addActionListener(e -> {
            try {
                int a = Integer.parseInt(field1.getText());
                int b = Integer.parseInt(field2.getText());
                long r = Calcul.somme(a, b);
                showResultAndSaveTrace("Resultat: ", 's', a, b, r);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnProduit.addActionListener(e -> {
            try {
                int a = Integer.parseInt(field1.getText());
                int b = Integer.parseInt(field2.getText());
                long r = Calcul.prod(a, b);
                showResultAndSaveTrace("Resultat: ", 'p', a, b, r);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnFact.addActionListener(e -> {
            try {
                int a = Integer.parseInt(field1.getText());
                long r = Calcul.fact(a);
                showResultAndSaveTrace("Resultat: ", 'f', a, 0, r);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnRandom.addActionListener(e -> {
            long r = Calcul.random();
            showResultAndSaveTrace("Nombre aleatoire: ", 'r', 0, 0, r);
        });

        btnTrace.addActionListener(e -> {
            traceArea.setText("");
            Trace.lireopToTextArea(traceArea);
        });

        btnQuit.addActionListener(e -> System.exit(0));
    }

    private void showError(Exception exception) {
        String message = "Entree invalide";
        if (exception != null && exception.getMessage() != null && !exception.getMessage().isBlank()) {
            message = exception.getMessage();
        }

        JOptionPane.showMessageDialog(this,
                message,
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Trace persistence stays in the GUI so Calcul remains focused on computation delegation.
     */
    private void showResultAndSaveTrace(String labelPrefix, char operation, int left, int right, long result) {
        resultLabel.setText(labelPrefix + result);
        Trace.savop(operation, left, right, result);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}
