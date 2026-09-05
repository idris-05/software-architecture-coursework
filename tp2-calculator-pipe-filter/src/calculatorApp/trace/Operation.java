package calculatorApp.trace;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Operation implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // Compact field names kept for serialized trace compatibility.
    char o;
    int g;
    int d;
    long r;
    // Capture save time once when the operation is created.
    String savedAt;

    public Operation(char o, int g, int d, long r) {
        this.o = o;
        this.g = g;
        this.d = d;
        this.r = r;
        this.savedAt = LocalDateTime.now().format(FORMATTER);
    }
}