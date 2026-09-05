package calculator.service;

public interface CalculatorService {
    long add(int a, int b);

    long multiply(int a, int b);

    long factorial(int n);

    long random();

    String loadTraceAsText();
}
