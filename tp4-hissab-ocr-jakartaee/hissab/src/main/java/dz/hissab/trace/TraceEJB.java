package dz.hissab.trace;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class TraceEJB implements TraceService {
    @PersistenceContext(unitName = "hissabPU")
    private EntityManager em;

    @Override
    public void save(ComputationTrace trace) {
        em.persist(trace);
    }

    @Override
    public List<ComputationTrace> findAll() {
        return em.createQuery(
                "SELECT t FROM ComputationTrace t ORDER BY t.computedAt DESC",
                ComputationTrace.class).getResultList();
    }

    @Override
    public List<ComputationTrace> searchByExpression(String keyword) {
        String normalized = "%" + keyword.toLowerCase() + "%";
        return em.createQuery(
                "SELECT t FROM ComputationTrace t " +
                        "WHERE LOWER(t.extractedExpression) LIKE :keyword " +
                        "OR LOWER(t.sanitizedExpression) LIKE :keyword " +
                        "ORDER BY t.computedAt DESC",
                ComputationTrace.class)
                .setParameter("keyword", normalized)
                .getResultList();
    }

    @Override
    public long countAll() {
        return em.createQuery("SELECT COUNT(t) FROM ComputationTrace t", Long.class).getSingleResult();
    }

    @Override
    public long countByStatus(String status) {
        return em.createQuery(
                "SELECT COUNT(t) FROM ComputationTrace t WHERE t.status = :status",
                Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }

    @Override
    public long countWithProvidedAnswer() {
        return em.createQuery(
                "SELECT COUNT(t) FROM ComputationTrace t " +
                        "WHERE t.providedAnswer IS NOT NULL AND TRIM(t.providedAnswer) <> ''",
                Long.class).getSingleResult();
    }

    @Override
    public ComputationTrace findById(Long id) {
        return em.find(ComputationTrace.class, id);
    }
}
