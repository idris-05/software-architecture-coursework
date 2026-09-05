package calculator.domain;

public enum OperationType {
    SUM('s', "Somme"),
    PRODUCT('p', "Produit"),
    FACTORIAL('f', "Factoriel"),
    RANDOM('r', "Aleatoire");

    private final char code;
    private final String displayName;

    OperationType(char code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public char getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }
}
