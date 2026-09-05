package calculator;

import java.io.Serializable;

public class Operation implements Serializable {
    private static final long serialVersionUID = 1L;
    char o;
    int g;
    int d;
    long r;

    public Operation(char o, int g, int d, long r) {
        this.o = o;
        this.g = g;
        this.d = d;
        this.r = r;
    }
}
