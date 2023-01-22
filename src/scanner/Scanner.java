package scanner;

/**
 * Clase Scanner:
 *
 * Lee el contenido del fichero con el código a compilar,
 * generando tokens y guardándolos en una estructura Diccionario
 */

import errorHandler.*;

import java.io.*;
import java.util.LinkedList;

public class Scanner {

    private static String path;
    private static java.util.Scanner sc;

    //Constructor de la clase
    public Scanner (String filePath){
        path = filePath;
    }

    private void openFile() throws FileNotFoundException{
        File f = new File(path);
        sc = new java.util.Scanner(f);
    }

    public void startScanner(){
        LinkedList<String> tokens = null;

        try{

            openFile();

        } catch(FileNotFoundException fnfe){

            ErrorHandler.throwError(ErrorList.ScannerFileNotFound, "File: " + path);

        }

    }

    public static String getToken(){
        if (sc.hasNext()) {
            String aux = sc.next();
        }
        return null;
    }
}

