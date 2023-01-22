package parser;

import errorHandler.ErrorHandler;
import errorHandler.ErrorList;
import scanner.TokenScanner;
import symbolTable.Token;

import java.util.List;

public class Parser {
    public static Token toParse(TreeNode root){
        return recursiveParser("S", TokenScanner.getToken(), root);
    }

    private static Token recursiveParser(String stackTop, Token input, TreeNode root){
        if(stackTop.equals("$") && input.getSymbol() == null){
            return new Token("ññ");
        }
        if(input.getSymbol() == null){
            return new Token("ñ");
        }
        if(input.getSymbol().equals("ñ")){
            return input;
        }
        //MATCH
        if (stackTop.equals(input.getSymbol())) {
            return TokenScanner.getToken();
        }
        if(stackTop.equals("numeroEntero") && isInt(input.getSymbol())){
            return TokenScanner.getToken();
        }
        if(stackTop.equals("ε")){
            return input;
        }
        List<String> production = null;
        if(isInt(input.getSymbol())){
            production = ParsingTable.getProduction(stackTop, "numeroEntero");
        } else {
            production = ParsingTable.getProduction(stackTop, input.getSymbol());
        }
        if (production == null) {
            //System.out.println("Lexic error around line " + (input.getLine()));
            ErrorHandler.throwError(ErrorList.LexicError, input.getLine());
            return new Token("ñ");
        }

        for(String prod : production){
            TreeNode newSon = null;
            if(prod.equals("numeroEntero") && isInt(input.getSymbol())){
                newSon = new TreeNode(input.getSymbol(), input.getLine());
            } else {
                if(input == null){
                }
                newSon = new TreeNode(prod, input.getLine());
            }
            newSon.setDad(root);
            root.addSon(newSon);
            input = recursiveParser(prod,input,newSon);
        }
        return input;
    }

    public static boolean isInt(String str) {

        try {
            @SuppressWarnings("unused")
            int x = Integer.parseInt(str);
            return true; //String is an Integer
        } catch (NumberFormatException e) {
            return false; //String is not an Integer
        }

    }
}
