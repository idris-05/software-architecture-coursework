package dz.hissab.trace;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "computation_trace")
public class ComputationTrace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_name", length = 255, nullable = false)
    private String imageName;

    @Column(name = "extracted_expression", length = 500)
    private String extractedExpression;

    @Column(name = "sanitized_expression", length = 500)
    private String sanitizedExpression;

    @Column(name = "result", length = 100)
    private String result;

    @Column(name = "provided_answer", length = 100)
    private String providedAnswer;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt;

    @Transient
    private List<String> breakdownSteps = List.of();

    public ComputationTrace() {
        this.computedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getExtractedExpression() {
        return extractedExpression;
    }

    public void setExtractedExpression(String extractedExpression) {
        this.extractedExpression = extractedExpression;
    }

    public String getSanitizedExpression() {
        return sanitizedExpression;
    }

    public void setSanitizedExpression(String sanitizedExpression) {
        this.sanitizedExpression = sanitizedExpression;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public String getProvidedAnswer() {
        return providedAnswer;
    }

    public void setProvidedAnswer(String providedAnswer) {
        this.providedAnswer = providedAnswer;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getComputedAt() {
        return computedAt;
    }

    public void setComputedAt(LocalDateTime computedAt) {
        this.computedAt = computedAt;
    }

    public List<String> getBreakdownSteps() {
        return breakdownSteps;
    }

    public void setBreakdownSteps(List<String> breakdownSteps) {
        this.breakdownSteps = breakdownSteps;
    }
}
