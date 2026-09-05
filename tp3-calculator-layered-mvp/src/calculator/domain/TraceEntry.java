package calculator.domain;

import java.io.Serializable;

public class TraceEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private final OperationType type;
    private final int leftOperand;
    private final int rightOperand;
    private final long result;

    public TraceEntry(OperationType type, int leftOperand, int rightOperand, long result) {
        this.type = type;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.result = result;
    }

    public OperationType getType() {
        return type;
    }

    public int getLeftOperand() {
        return leftOperand;
    }

    public int getRightOperand() {
        return rightOperand;
    }

    public long getResult() {
        return result;
    }

    public String getLeftOperandText() {
        switch (type) {
            case RANDOM:
                return "-";
            default:
                return Integer.toString(leftOperand);
        }
    }

    public String getOperatorText() {
        switch (type) {
            case SUM:
                return "+";
            case PRODUCT:
                return "*";
            case FACTORIAL:
                return "!";
            case RANDOM:
                return "rand";
            default:
                return Character.toString(type.getCode());
        }
    }

    public String getRightOperandText() {
        switch (type) {
            case SUM:
            case PRODUCT:
                return Integer.toString(rightOperand);
            default:
                return "-";
        }
    }

    public String getResultText() {
        return Long.toString(result);
    }

    public String toDisplayLine(int operationWidth, int leftWidth, int operatorWidth, int rightWidth, int resultWidth) {
        String lineFormat = "%-" + operationWidth + "s | %"
                + leftWidth + "s | %-"
                + operatorWidth + "s | %"
                + rightWidth + "s | %"
                + resultWidth + "s";

        return String.format(
                lineFormat,
                type.getDisplayName(),
                getLeftOperandText(),
                getOperatorText(),
                getRightOperandText(),
                getResultText()
        );
    }
}
