package errorHandler;

import java.util.LinkedList;
import java.util.List;

public class ErrorHandler {

    private static final List<Integer> errorList;
    private static boolean testEnvironment = false;

    /*
     * Constructor estático.
     */
    static {
        errorList = new LinkedList<>();
    }

    /**
     * Recibe un código de error y se encarga de gestionarlo.
     * @param errorCode código de error sobre el que tiene que trabajar.
     */
    public static void throwError(int errorCode) {
        String msg = ErrorList.getErrorMessage(errorCode);

        // Si nos devuelve null, no conocemos el error
        if(msg == null) {
            errorList.add(ErrorList.UnknownError);
            if(!testEnvironment) System.out.println("Error("+errorCode+") Unknown.");
            return;
        }
        errorList.add(errorCode);
        if(!testEnvironment) System.out.println("Error("+errorCode+"). Section " + msg);
    }

    /**
     * Recibe un código de error junto con la linea.
     * @param errorCode código de error sobre el que tiene que trabajar.
     * @param line numero de linea donde se ha producido el error.
     */
    public static void throwError(int errorCode, int line) {
        if(line < 0){
            throwError(errorCode);
        }

        String msg = ErrorList.getErrorMessage(errorCode);

        // Si nos devuelve null, no conocemos el error
        if(msg == null) {
            errorList.add(ErrorList.UnknownError);
            if(!testEnvironment) System.out.println("Error("+errorCode+"). At line " + line + " Unknown type.");
            return;
        }
        errorList.add(errorCode);
        if(!testEnvironment) System.out.println("Error("+errorCode+"). At line " + line + " with Section " + msg);
    }

    /**
     * Recibe un código de error junto con un mensaje extra que se quiera añadir para especificar cualquier problematica extra.
     * @param  errorCode coding de error sobre el que tiene que trabajar.
     * @param extraMSG texto extra que se quiera añadir al final del error.
     */
    public static void throwError(int errorCode, String extraMSG) {
        if(extraMSG == null){
            throwError(errorCode);
            return;
        }

        String msg = ErrorList.getErrorMessage(errorCode);

        // Si nos devuelve null, no conocemos el error
        if(msg == null) {
            errorList.add(ErrorList.UnknownError);
            if(!testEnvironment) System.out.println("Error("+errorCode+"). Unknown type. " + extraMSG);
            return;
        }
        errorList.add(errorCode);
        if(!testEnvironment) System.out.println("Error("+errorCode+"). With Section " + msg + " " + extraMSG);
    }

    /**
     * Muestra por pantalla un error completo, con el código de error, la linea y un mensaje extra.
     * @param  errorCode código de error sobre el que tiene que trabajar.
     * @param line linea donde ha ocurrido el error.
     * @param extraMSG texto extra que se quiera añadir al final del error.
     */
    public static void throwError(int errorCode, int line, String extraMSG) {
        if(extraMSG == null){
            throwError(errorCode, line);
            return;
        }
        if(line < 0){
            throwError(errorCode, extraMSG);
            return;
        }

        String msg = ErrorList.getErrorMessage(errorCode);

        // Si nos devuelve null, no conocemos el error
        if(msg == null) {
            errorList.add(ErrorList.UnknownError);
            if(!testEnvironment) System.out.println("Error("+errorCode+"). At line " + line + ". Unknown Section. " + extraMSG);
            return;
        }
        errorList.add(errorCode);
        if(!testEnvironment) System.out.println("Error("+errorCode+"): At line " + line + ". "+ msg + " " + extraMSG);
    }

    public static void printErrorList(){
        if(errorList.isEmpty()) {
            System.out.println("No errors were found");
            return;
        }

        for (int errorCode : errorList) {
            System.out.println("Error (" + errorCode + "): " + ErrorList.getErrorMessage(errorCode));
        }
    }

    // Functions just for testing
    public static void clearErrorList() {
        errorList.clear();
    }

    public static boolean errorListEmpty() {
        return errorList.isEmpty();
    }

    public static void setTestEnvironment(){
        testEnvironment = true;
    }
}
