package calculator.presentation.view;

public interface CalculatorView {
    String getFirstNumber();

    String getSecondNumber();

    void setResult(String text);

    void setTrace(String text);

    void showError(String message);

    void setOnSum(Runnable action);

    void setOnMultiply(Runnable action);

    void setOnFactorial(Runnable action);

    void setOnRandom(Runnable action);

    void setOnTrace(Runnable action);

    void setOnQuit(Runnable action);

    void close();

    void display();
}
