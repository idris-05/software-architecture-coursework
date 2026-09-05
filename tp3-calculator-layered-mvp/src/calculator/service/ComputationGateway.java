package calculator.service;

public interface ComputationGateway {
    long add(int a, int b);

    long multiply(int a, int b);

    long factorial(int n);

    long random();
}
