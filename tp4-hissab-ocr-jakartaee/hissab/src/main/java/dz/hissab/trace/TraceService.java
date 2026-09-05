package dz.hissab.trace;

import jakarta.ejb.Local;

import java.util.List;

@Local
public interface TraceService {
    void save(ComputationTrace trace);

    List<ComputationTrace> findAll();

    List<ComputationTrace> searchByExpression(String keyword);

    long countAll();

    long countByStatus(String status);

    long countWithProvidedAnswer();

    ComputationTrace findById(Long id);
}
