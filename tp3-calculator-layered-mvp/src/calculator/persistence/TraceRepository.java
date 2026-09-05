package calculator.persistence;

import calculator.domain.TraceEntry;

public interface TraceRepository {
    void save(TraceEntry entry);

    String readAllAsText();
}
