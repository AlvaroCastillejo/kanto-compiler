package symbolTable;

public class Dictionary {
    private static HashMapCustom<String, Token> dictionary;

    static {
        Dictionary.dictionary = new HashMapCustom<String, Token>(17);
        defineOperators();
        defineLogicalOperators();
        definePokemons();
        defineTrainers();
    }

    private static void definePokemons() {
        dictionary.add("Charmander", new Token(Token.VARIABLE, "INTEGER"));
        dictionary.add("Charmeleon", new Token(Token.VARIABLE, "INTEGER"));
        dictionary.add("Charizard", new Token(Token.VARIABLE, "INTEGER"));
        dictionary.add("Torchic", new Token(Token.VARIABLE, "INTEGER"));
        dictionary.add("Chimchar", new Token(Token.VARIABLE, "INTEGER"));
        dictionary.add("Monferno", new Token(Token.VARIABLE, "INTEGER"));
        dictionary.add("Blaziken", new Token(Token.VARIABLE, "INTEGER"));
        dictionary.add("Combusken", new Token(Token.VARIABLE, "INTEGER"));
        dictionary.add("Infernape", new Token(Token.VARIABLE, "INTEGER"));

        dictionary.add("Gastly", new Token(Token.VARIABLE, "BOOLEAN"));
        dictionary.add("Gengar", new Token(Token.VARIABLE, "BOOLEAN"));
        dictionary.add("Dusclops", new Token(Token.VARIABLE, "BOOLEAN"));

        dictionary.add("Bulbasaur", new Token(Token.VARIABLE, "LITERAL"));
        dictionary.add("Ivysaur", new Token(Token.VARIABLE, "LITERAL"));
        dictionary.add("Venusaur", new Token(Token.VARIABLE, "LITERAL"));
        dictionary.add("Chikorita", new Token(Token.VARIABLE, "LITERAL"));
        dictionary.add("Bayleef", new Token(Token.VARIABLE, "LITERAL"));
        dictionary.add("Meganium", new Token(Token.VARIABLE, "LITERAL"));
        dictionary.add("Treecko", new Token(Token.VARIABLE, "LITERAL"));
        dictionary.add("Grovyle", new Token(Token.VARIABLE, "LITERAL"));
        dictionary.add("Sceptile", new Token(Token.VARIABLE, "LITERAL"));

        dictionary.add("Rayquaza", new Token(Token.LOOP, "LEGENDARIO"));
        dictionary.add("Groudon", new Token(Token.LOOP, "LEGENDARIO"));
        dictionary.add("Kyogre", new Token(Token.LOOP, "LEGENDARIO"));
        dictionary.add("Zapdos", new Token(Token.LOOP, "LEGENDARIO"));
        dictionary.add("Articuno", new Token(Token.LOOP, "LEGENDARIO"));
        dictionary.add("Moltres", new Token(Token.LOOP, "LEGENDARIO"));
        dictionary.add("Regirock", new Token(Token.LOOP, "LEGENDARIO"));
        dictionary.add("Regice", new Token(Token.LOOP, "LEGENDARIO"));
        dictionary.add("Registeel", new Token(Token.LOOP, "LEGENDARIO"));
    }

    private static void defineTrainers() {
        dictionary.add("Ash", new Token(Token.MAIN, "MAIN"));
        dictionary.add("Alder", new Token(Token.FUNCTION, "FUNCTION"));
        dictionary.add("Blue", new Token(Token.FUNCTION, "FUNCTION"));
        dictionary.add("Cynthia", new Token(Token.FUNCTION, "FUNCTION"));
        dictionary.add("Diantha", new Token(Token.FUNCTION, "FUNCTION"));
        dictionary.add("Drake", new Token(Token.FUNCTION, "FUNCTION"));
    }

    private static void defineOperators() {
        dictionary.add("fights", new Token(Token.EQUAL, "="));
        dictionary.add("with", new Token(Token.NUM_OPERATOR, "+"));
        dictionary.add("without", new Token(Token.NUM_OPERATOR, "-"));
        dictionary.add("spreading", new Token(Token.NUM_OPERATOR, "*"));
        dictionary.add("splitting", new Token(Token.NUM_OPERATOR, "/"));
        dictionary.add("healed", new Token(Token.INDECREMENT, "+="));
        dictionary.add("lost", new Token(Token.INDECREMENT, "-="));
        dictionary.add("has", new Token(Token.DECLARATION, "="));
    }

    private static void defineLogicalOperators() {
        dictionary.add("and", new Token(Token.LOGICALOPERATOR, "&&"));
        dictionary.add("or", new Token(Token.LOGICALOPERATOR, "||"));
        dictionary.add("appeared.", new Token(Token.LOGICAL_ASIGNATION, "true"));
        dictionary.add("disappeared.", new Token(Token.LOGICAL_ASIGNATION, "false"));
        dictionary.add("stronger", new Token(Token.COMPARATORS, ">"));
        dictionary.add("weaker", new Token(Token.COMPARATORS, "<"));
        dictionary.add("equal", new Token(Token.COMPARATORS, "=="));
        dictionary.add("differs", new Token(Token.COMPARATORS, "!="));
        dictionary.add("visible", new Token(Token.AFIRMATION, "true"));
        dictionary.add("invisible", new Token(Token.AFIRMATION, "false"));
    }

    public static String translate(String str){
        return dictionary.get(str).getSequence();
    }

    public static boolean found(String string){
        return dictionary.get(string) != null;
    }

    public static boolean foundPokemon(String string) {
        return (dictionary.get(string) != null && dictionary.get(string).getToken() == Token.VARIABLE);
    }

    public static String getVarType(String string) {
        if (found(string) && dictionary.get(string).getToken() == Token.VARIABLE) {
            return dictionary.get(string).getSequence();
        }

        return null;
    }

    public static boolean isNumericalOp(String operand) {
        int token = dictionary.get(operand).getToken();

        return token == Token.NUM_OPERATOR;
    }

    public static boolean isIndecrementOp(String operand) {
        int token = dictionary.get(operand).getToken();

        return token == Token.INDECREMENT;
    }

    public static boolean isFunction(String scope) {
        Token f = dictionary.get(scope);
        if (f == null) return false;
        return f.getToken() == Token.FUNCTION || f.getToken() == Token.MAIN;
    }

    public boolean isBooleanOp(String operand) {
        int token = dictionary.get(operand).getToken();

        return token == Token.LOGICALOPERATOR;
    }

    public static boolean isBooleanAsig(String operand) {
        int token = dictionary.get(operand).getToken();

        return token == Token.LOGICAL_ASIGNATION;
    }

    public static boolean isBooleanAfir(String operand) {
        int token = dictionary.get(operand).getToken();

        return token == Token.AFIRMATION;
    }

    public static boolean isBooleanComp(String operand) {
        int token = dictionary.get(operand).getToken();

        return token == Token.COMPARATORS;
    }
}
