package calculator.presentation.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

public class SwingCalculatorView extends JFrame implements CalculatorView {
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

    public SwingCalculatorView() {
        setTitle("Calculatrice - GUI");
        setSize(900, 560);
        setMinimumSize(new Dimension(820, 500));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("Nombre 1:"));
        field1 = new JTextField();
        inputPanel.add(field1);
        inputPanel.add(new JLabel("Nombre 2:"));
        field2 = new JTextField();
        inputPanel.add(field2);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
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

        JPanel bottomPanel = new JPanel(new BorderLayout());
        resultLabel = new JLabel("Resultat: ");
        bottomPanel.add(resultLabel, BorderLayout.NORTH);
        traceArea = new JTextArea();
        traceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        traceArea.setEditable(false);
        bottomPanel.add(new JScrollPane(traceArea), BorderLayout.CENTER);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 5, 10);

        gbc.gridy = 0;
        gbc.weighty = 0.15;
        add(inputPanel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.25;
        add(buttonPanel, gbc);

        gbc.gridy = 2;
        gbc.weighty = 0.6;
        add(bottomPanel, gbc);
    }

    public static void runOnUiThread(Runnable task) {
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
            return;
        }
        SwingUtilities.invokeLater(task);
    }

    @Override
    public String getFirstNumber() {
        return field1.getText();
    }

    @Override
    public String getSecondNumber() {
        return field2.getText();
    }

    @Override
    public void setResult(String text) {
        resultLabel.setText(text);
    }

    @Override
    public void setTrace(String text) {
        traceArea.setText(text);
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void setOnSum(Runnable action) {
        bindAction(btnSomme, action);
    }

    @Override
    public void setOnMultiply(Runnable action) {
        bindAction(btnProduit, action);
    }

    @Override
    public void setOnFactorial(Runnable action) {
        bindAction(btnFact, action);
    }

    @Override
    public void setOnRandom(Runnable action) {
        bindAction(btnRandom, action);
    }

    @Override
    public void setOnTrace(Runnable action) {
        bindAction(btnTrace, action);
    }

    @Override
    public void setOnQuit(Runnable action) {
        bindAction(btnQuit, action);
    }

    @Override
    public void close() {
        dispose();
    }

    @Override
    public void display() {
        setVisible(true);
    }

    private void bindAction(JButton button, Runnable action) {
        for (ActionListener listener : button.getActionListeners()) {
            button.removeActionListener(listener);
        }
        button.addActionListener(e -> action.run());
    }
}
