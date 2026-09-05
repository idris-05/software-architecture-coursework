package calculator.presentation.presenter;

import calculator.presentation.view.CalculatorView;
import calculator.service.CalculatorService;

public class CalculatorPresenter {
    private final CalculatorView view;
    private final CalculatorService service;
    private final Runnable onQuitRequested;

    public CalculatorPresenter(CalculatorView view, CalculatorService service, Runnable onQuitRequested) {
        this.view = view;
        this.service = service;
        this.onQuitRequested = onQuitRequested;
        bindActions();
    }

    private void bindActions() {
        view.setOnSum(this::handleSum);
        view.setOnMultiply(this::handleMultiply);
        view.setOnFactorial(this::handleFactorial);
        view.setOnRandom(this::handleRandom);
        view.setOnTrace(this::handleTrace);
        view.setOnQuit(this::handleQuit);
    }

    private void handleSum() {
        try {
            int a = parseInt(view.getFirstNumber(), "Nombre 1");
            int b = parseInt(view.getSecondNumber(), "Nombre 2");
            long result = service.add(a, b);
            view.setResult("Resultat: " + result);
        } catch (Exception e) {
            view.showError(messageFor(e));
        }
    }

    private void handleMultiply() {
        try {
            int a = parseInt(view.getFirstNumber(), "Nombre 1");
            int b = parseInt(view.getSecondNumber(), "Nombre 2");
            long result = service.multiply(a, b);
            view.setResult("Resultat: " + result);
        } catch (Exception e) {
            view.showError(messageFor(e));
        }
    }

    private void handleFactorial() {
        try {
            int n = parseInt(view.getFirstNumber(), "Nombre 1");
            long result = service.factorial(n);
            view.setResult("Resultat: " + result);
        } catch (Exception e) {
            view.showError(messageFor(e));
        }
    }

    private void handleRandom() {
        try {
            long result = service.random();
            view.setResult("Nombre aleatoire: " + result);
        } catch (Exception e) {
            view.showError(messageFor(e));
        }
    }

    private void handleTrace() {
        try {
            view.setTrace(service.loadTraceAsText());
        } catch (Exception e) {
            view.showError(messageFor(e));
        }
    }

    private void handleQuit() {
        view.close();
        onQuitRequested.run();
    }

    private int parseInt(String value, String fieldName) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " est obligatoire.");
        }
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " doit etre un entier valide.");
        }
    }

    private String messageFor(Exception exception) {
        if (exception.getMessage() != null && !exception.getMessage().isBlank()) {
            return exception.getMessage();
        }
        return "Entree invalide";
    }
}
