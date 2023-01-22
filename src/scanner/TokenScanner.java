package scanner;

import parser.ParsingTable;
import symbolTable.Token;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;

public class TokenScanner {
    private static Scanner sc;
    private static boolean commentBlock;
    private static int line;
    private static Stack<String> inputLine;
    private static final String init_filename = "\\res\\fibonacci.txt";

    static {
        initTokenScanner(init_filename);
    }

    public static void initTokenScanner(String filename){
        try {
            line = 0;
            inputLine = new Stack<>();
            String f = new File("").getAbsolutePath();
            File file = new File(f.concat(filename));
            sc = new Scanner(file);
        }
        catch(FileNotFoundException e)
        {
            //e.printStackTrace();
            System.out.println("File \"" + init_filename + "\" not found!");
            System.exit(-1);
        }
    }

    public static Token getToken(){
        if(inputLine.isEmpty()){
            if(sc.hasNextLine()){
                //line++;
                while(inputLine.isEmpty()){
                    inputLine.addAll(Arrays.asList(sc.nextLine().split(" ")));
                    line++;
                }
                Collections.reverse(inputLine);
            } else {
                return new Token(null,line);
            }
        }

        String token = inputLine.pop();
        if(token.endsWith("*/") && commentBlock){
            commentBlock = false;
            return getToken();
        }
        if(token.startsWith("/*") && !commentBlock){
            commentBlock = true;
            return getToken();
        }
        if(commentBlock){
            return getToken();
        }
        if(ParsingTable.isReservedToken(token)){
            return new Token(token,line);
        }
        return getToken();
    }

    public static boolean hasNextToken() {
        return sc.hasNext();
    }
}
