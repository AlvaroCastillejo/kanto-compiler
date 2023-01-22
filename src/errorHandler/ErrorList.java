package errorHandler;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Clase ErrorList:
 *
 * Contiene un diccionario con todos los errores que podría tener nuestro sistema.
 * El objetivo de esta clase es ir añadiendo los errores a lo largo del tiempo sin tener
 * que modificar el ErrorHandler para hacerlo.
 *
 * Para añadir un error:
 * Añadir en la section pertinente, el código de error y el mensaje de error, por ejemplo.
 *     public static final Integer ERROR = 202;
 *     private static final String ErrorMSG = "Mensaje de error";
 * En los comentarios está indicado qué rango de valores tienes en cada sección de errores.
 * Luego, ir a la función pertinente a la seccion de donde provenga el error y añadirlo junto con
 * la sección. Por ejemplo.
 *      dictionary.put(ERROR, SECTION + ErrorMSG);
 * Donde SECTION seria el string de la sección del error, por ejemplo, SCANNER, PARSER, OPTIMIZER, TAC...
 *
 */

public class ErrorList {
    private static final Dictionary <Integer, String> dictionary = new Hashtable<>();

    // General errors
    public static final Integer UnknownError = 0;

    // Scanner error messages [200, 300)
    private static final String SCANNER = "Scanner: ";
    public static final Integer ScannerFileNotFound = 201;
    private static final String ScannerFileNotFoundMSG = "File not found. ";
    public static final Integer ScannerFileNotRead = 202;
    private static final String ScannerFileNotReadMSG = "File could not be read.";

    // Parser error messages [300, 400)
    private static final String PARSER = "Parser: ";
    public static final Integer LexicError = 301;
    private static final String LexicErrorMSG = "Lexic error";

    // Optimizer error messages [400, 500)
    private static final String OPTIMIZER = "Optimizer: ";

    // TAC error messages [500, 600)
    private static final String TAC = "TAC: ";
    public static final Integer SemanticFuncTrainerError = 501;
    private static final String SemanticFuncTrainerErrorMSG = "The function must start and end with the same trainer.";
    public static final Integer AlreadyDeclaredFuncError = 502;
    private static final String AlreadyDeclaredFuncErrorMSG = "The function has already been declared. Can't declare it twice.";
    public static final Integer SemanticUnknownTrainerError = 503;
    private static final String SemanticUnknownTrainerErrorMSG = "There is an unknown trainer in the code.";
    public static final Integer SemanticUnknownPokemonError = 504;
    private static final String SemanticUnknownPokemonErrorMSG = "There is an unknown pokemon in the code.";
    public static final Integer AlreadyDeclaredPokemonError = 505;
    private static final String AlreadyDeclaredPokemonErrorMSG = "The pokemon has already been declared in this scope. Can't declare it twice.";
    public static final Integer UndeclaredPokemonError = 506;
    private static final String UndeclaredPokemonErrorMSG = "The pokemon hasn't been declared yet in this scope.";
    public static final Integer NumericOperandExpected = 507;
    private static final String NumericOperandExpectedMSG = "Expected numeric operand. Type found incorrect.";
    public static final Integer SemanticErrorOperandTypes = 508;
    private static final String SemanticErrorOperandTypesMSG= "Pokemons doesn't match type.";
    public static final Integer BooleanOperandExpected = 509;
    private static final String BooleanOperandExpectedMSG= "Expected boolean operand. Type found incorrect.";
    public static final Integer BooleanAssignationExpected = 510;
    private static final String BooleanAssignationExpectedMSG = "Non boolean pokemon in boolean assignation error.";
    public static final Integer UndeclaredTrainerError = 511;
    private static final String UndeclaredTrainerErrorMSG = "The trainer (function) hasn't been defined.";
    public static final Integer SameParameterTwiceError = 512;
    private static final String SameParameterTwiceErrorMSG = "Trainer (function) is receiving the same pokemon (parameter) multiple times.";
    public static final Integer InvalidValueError = 513;
    private static final String InvalidValueErrorMSG = "An invalid value has been found.";
    public static final Integer NumericComparisonExpected = 514;
    private static final String NumericComparisonExpectedMSG = "Expected a numeric pokemon for a numeric comparison.";
    public static final Integer LiteralComparisonExpected = 515;
    private static final String LiteralComparisonExpectedMSG = "Expected a literal pokemon for a literal comparison.";
    public static final Integer ComparatorExpected = 516;
    private static final String ComparatorExpectedMSG = "Expected a comparator but received other thing.";
    public static final Integer BooleanAffirmationExpected = 517;
    private static final String BooleanAffirmationExpectedMSG = "Expected a boolean affirmation but received other.";
    public static final Integer LostOrHealedExpected = 518;
    private static final String LostOrHealedExpectedMSG = "Expected lost or healed but received other.";
    public static final Integer WrongContextError = 519;
    private static final String WrongContextErrorMSG = "Function from other context used. Wrong context (actual).";
    public static final Integer CallBackError = 520;
    private static final String CallBackErrorMSG = "Fatal error while trying to retrieve returned parameters from a function.";

    // Assembler error messages [600, 700)
    private static final String ASSEMBLER = "Assembler: ";
    public static final Integer AssemblerFileNotFound = 601;
    private static final String AssemblerFileNotFoundMSG = "File not found.";
    public static final Integer AssemblerIOError = 602;
    private static final String AssemblerIOErrorMSG = "Input/Output Error when writing file.";
    public static final Integer AssemblerMainNotFound = 603;
    private static final String AssemblerMainNotFoundMSG = "Main could not be found.";
    public static final Integer AssemblerLabelError = 604;
    private static final String AssemblerLabelErrorMSG = "Label could not be written.";

    // SymbolTable error messages [700, 800)
    private static final String SYMBOLTABLE = "SymbolTable: ";
    public static final Integer CompBetweenLiterals = 701;
    private static final String CompBetweenLiteralsMSG = "Comparison between two literals not allowed.";

    // Constructor de la lista de errores
    static {
        // Añadimos un error unknown para cuando mostremos la lista de errores
        dictionary.put(UnknownError, "Error type and section Unknown.");

        // Añadimos el resto de errores
        putScannerErrors();
        putParserErrors();
        putOptimizerErrors();
        putTACErrors();
        putAssemblerErrors();
        putSymbolTableErrors();
    }

    // Puts Scanner Errors inside the dictionary
    private static void putScannerErrors() {
        dictionary.put(SemanticFuncTrainerError, SCANNER + SemanticFuncTrainerErrorMSG);
    }

    // Puts Parser Errors inside the dictionary
    private static void putParserErrors() {
        dictionary.put(LexicError, PARSER + LexicErrorMSG);
    }

    // Puts Optimizer Errors inside the dictionary
    private static void putOptimizerErrors() {

    }

    // Puts TAC Errors inside the dictionary
    private static void putTACErrors() {
        dictionary.put(ScannerFileNotRead, TAC + ScannerFileNotReadMSG);
        dictionary.put(AlreadyDeclaredFuncError, TAC + AlreadyDeclaredFuncErrorMSG);
        dictionary.put(SemanticUnknownTrainerError, TAC + SemanticUnknownTrainerErrorMSG);
        dictionary.put(SemanticUnknownPokemonError, TAC + SemanticUnknownPokemonErrorMSG);
        dictionary.put(AlreadyDeclaredPokemonError, TAC + AlreadyDeclaredPokemonErrorMSG);
        dictionary.put(UndeclaredPokemonError, TAC + UndeclaredPokemonErrorMSG);
        dictionary.put(NumericOperandExpected, TAC + NumericOperandExpectedMSG);
        dictionary.put(SemanticErrorOperandTypes, TAC + SemanticErrorOperandTypesMSG);
        dictionary.put(BooleanOperandExpected, TAC + BooleanOperandExpectedMSG);
        dictionary.put(BooleanAssignationExpected, TAC + BooleanAssignationExpectedMSG);
        dictionary.put(UndeclaredTrainerError, TAC + UndeclaredTrainerErrorMSG);
        dictionary.put(SameParameterTwiceError, TAC + SameParameterTwiceErrorMSG);
        dictionary.put(InvalidValueError, TAC + InvalidValueErrorMSG);
        dictionary.put(NumericComparisonExpected, TAC + NumericComparisonExpectedMSG);
        dictionary.put(LiteralComparisonExpected, TAC + LiteralComparisonExpectedMSG);
        dictionary.put(ComparatorExpected, TAC + ComparatorExpectedMSG);
        dictionary.put(BooleanAffirmationExpected, TAC + BooleanAffirmationExpectedMSG);
        dictionary.put(LostOrHealedExpected, TAC + LostOrHealedExpectedMSG);
        dictionary.put(WrongContextError, TAC + WrongContextErrorMSG);
        dictionary.put(CallBackError, TAC + CallBackErrorMSG);
    }

    // Puts Assembler Errors inside the dictionary
    private static void putAssemblerErrors() {
        dictionary.put(AssemblerFileNotFound, ASSEMBLER + AssemblerFileNotFoundMSG);
        dictionary.put(AssemblerIOError, ASSEMBLER + AssemblerIOErrorMSG);
        dictionary.put(AssemblerMainNotFound, ASSEMBLER + AssemblerMainNotFoundMSG);
        dictionary.put(AssemblerLabelError, ASSEMBLER + AssemblerLabelErrorMSG);
    }

    // Puts SymbolTable Errors inside the dictionary
    private static void putSymbolTableErrors() {
        dictionary.put(CompBetweenLiterals, SYMBOLTABLE + CompBetweenLiteralsMSG);
    }

    //Public methods
    public static String getErrorMessage(Integer id) {
        if(id == null) {
            return null;
        }
        return dictionary.get(id);
    }
}
