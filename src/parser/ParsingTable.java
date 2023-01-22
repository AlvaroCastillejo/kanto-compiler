package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 *   table[1][3]:   //primero row, luego column
 *
 *   |___|___|___|___|
 *   |___|___|___|_x_|
 *   |___|___|___|___|
 *
 *   El formato del txt de importación es el siguiente:
 *   numColumns,numRows                        //de contenido, sin contar los nombres de las filas ni las columnas.
 *   colName1,colName2,colName3,colName4
 *   S,,,,,S ::= code $,S ::= code $,S ::= code $,S ::= code $,S ::= code $,S ::= code $,S ::= code $,S ::= code $,S ::= code $
 *   symbol,,symbol ::= declaration,symbol ::= operation,,,,,,,,,,ñ     //añadir una ñ al final si termina en ",".
 *
 */
public class ParsingTable {
    private static int colsNumber = -1;
    private static int rowsNumber = -1;
    private static String[][] table;
    private static final HashMap<String,Integer> rowIndexes;
    private static final HashMap<String,Integer> colIndexes;

    /**
     * Constructor de la clase. Es estático e inicializa las variables.
     */
    static {
        rowIndexes = new HashMap<>();
        colIndexes = new HashMap<>();
        Scanner sc = null;
        try {
            String f = new File("").getAbsolutePath();
            File file = new File(f.concat("\\src\\Parser\\parsingTable_full.txt"));
            sc = new Scanner(file);
            String line;
            if(sc.hasNextLine()){
                line = sc.nextLine();
                String[] tok = line.split(",");
                colsNumber = Integer.parseInt(tok[0]);
                rowsNumber = Integer.parseInt(tok[1]);
            }
            table = new String[rowsNumber][colsNumber];

            if (sc.hasNextLine()) {
                line = sc.nextLine();
                String[] tok = line.split(",");
                for (int i = 0; i < colsNumber; i++) {
                    colIndexes.put(tok[i], i);
                }
            }
            for (int row = 0; sc.hasNextLine(); row++) {
                line = sc.nextLine();
                String[] tok = line.split(",");
                if(tok[tok.length-1].equals("ñ")) tok[tok.length-1] = "";
                rowIndexes.put(tok[0],row);
                for(int col = 0; col < colsNumber; col++){
                    table[row][col] = tok[col+1];
                }
                rowsNumber++;
            }
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        } finally {
            if (sc != null) sc.close();
        }
    }


    /**
     * Obtiene los token que se podrian combinar con la expresion para obtener una produccion valida. Usaremos la funcion para control de errores.
     * @param exp Expresion sobre la que queramos hacer la consulta.
     * @return Devuelve una lista de tokens que funcionan con la expresion dada.
     */
    public static List<String> getFollow(String exp) {
        List<String> tokenList = new LinkedList<>();
        for(String token : table[rowIndexes.get(exp)]){
            if(!token.equals("")) tokenList.add(token);
        }
        return tokenList;
    }

    /**
     * Obtiene la producción a aplicar según los parámetros.
     * @param context La fila de la tabla de parse. Corresponde al token del Stack.
     * @param newToken LA columan de la tabla de parse. Corresponde al token de Input.
     * @return Retorna la producción (Output).
     */
    public static List<String> getProduction(String context, String newToken){
        try {
            int col = colIndexes.get(newToken);
            int row = rowIndexes.get(context);
            String prod = table[row][col];
            if(prod.equals("")) return null;
            String[] t = prod.split("::= ");
            return Arrays.asList(t[1].split(" "));
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static boolean isReservedToken(String token){
        if(colIndexes.containsKey(token)){
            return true;
        }
        return Parser.isInt(token);
    }
}
