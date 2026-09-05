package calc.model;

public class HistoryRecord {

    private int id;
    private String timestamp;
    private double operandA;
    private double operandB;
    private String operator;
    private double result;
    private String via;

    public HistoryRecord() {
    }

    public HistoryRecord(int id,
                         String timestamp,
                         double operandA,
                         String operator,
                         double operandB,
                         double result,
                         String via) {
        this.id = id;
        this.timestamp = timestamp;
        this.operandA = operandA;
        this.operandB = operandB;
        this.operator = operator;
        this.result = result;
        this.via = via;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getOperandA() {
        return operandA;
    }

    public void setOperandA(double operandA) {
        this.operandA = operandA;
    }

    public double getOperandB() {
        return operandB;
    }

    public void setOperandB(double operandB) {
        this.operandB = operandB;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String toFileLine() {
        return id + "|" + timestamp + "|" + operandA + "|" + operator + "|" + operandB + "|" + result + "|" + via;
    }

    public static HistoryRecord fromFileLine(String line) {
        if (line == null) {
            return null;
        }
        String[] parts = line.split("\\|");
        if (parts.length != 7) {
            return null;
        }
        try {
            int id = Integer.parseInt(parts[0].trim());
            String timestamp = parts[1].trim();
            double operandA = Double.parseDouble(parts[2].trim());
            String operator = parts[3].trim();
            double operandB = Double.parseDouble(parts[4].trim());
            double result = Double.parseDouble(parts[5].trim());
            String via = parts[6].trim();
            return new HistoryRecord(id, timestamp, operandA, operator, operandB, result, via);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
