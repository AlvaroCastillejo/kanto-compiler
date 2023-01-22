package symbolTable;

public class Token {

    public static final int EPSILON = 0;
    public static final int VARIABLE = 1;
    public static final int EQUAL = 2;
    public static final int NUM_OPERATOR = 3;
    public static final int INDECREMENT = 4;
    public static final int LOGICALOPERATOR = 5;
    public static final int LOOP = 6;
    public static final int IF = 7;
    public static final int ELSE = 8;
    public static final int DELIMITER = 9;
    public static final int FUNCTION = 10;
    public static final int MAIN = 11;
    public static final int RETURN = 12;
    public static final int COMPARATORS = 13;
    public static final int DECLARATION = 14;
    public static final int LOGICAL_ASIGNATION = 15;
    public static final int AFIRMATION = 16;

    public int token;
    public String sequence;

    private String symbol;
    private int line;

    public Token(String symbol, int line) {
        this.symbol = symbol;
        this.line = line;
    }

    public Token(int token, String sequence){
        this.token = token;
        this.sequence = sequence;
    }

    public Token(String s) {
        symbol = s;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getLine() {
        return line;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
}
