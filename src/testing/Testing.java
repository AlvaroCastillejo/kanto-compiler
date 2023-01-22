package testing;

import assembler.Assembler;
import errorHandler.ErrorHandler;
import parser.Parser;
import parser.TreeNode;
import scanner.TokenScanner;
import symbolTable.AST;
import symbolTable.SemanticCheck;
import symbolTable.SymbolTableNode;
import symbolTable.Token;
import threeAddressCode.BasicBlock;
import threeAddressCode.TAC;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Testing {
    private static final int MAX_NUM_TESTS = 100;
    public static void main(String [] args) {
        String fileSource = "\\src\\testing\\tests\\test";
        String fileDest = "\\src\\testing\\results\\test";
        String fileExt = ".txt";


        clearResultsDirectory();
        //ErrorHandler.setTestEnvironment();

        for(int i = 1; i <= MAX_NUM_TESTS; i++){
            // Checks if there aren't any more tests
            if(!fileNameValidator(fileSource + i + fileExt))
                break;

            // Test
            System.out.print("TEST " + i + ": ");
            executeTest(fileSource + i + fileExt);
            saveResult(fileDest + i + "_asm" + fileExt);
            if(ErrorHandler.errorListEmpty()){
                System.out.println("\033[0;32m" + "Test passed." + "\033[0m");
            } else {
                System.out.println("\033[0;31m" + "Test not passed." + "\033[0m");
            }
            ErrorHandler.clearErrorList();
        }
    }

    private static boolean fileNameValidator(String filename){
        String f = new File("").getAbsolutePath();
        File file = new File(f.concat(filename));
        try {
            Scanner sc = new Scanner(file);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private static void clearResultsDirectory(){
        String f = new File("").getAbsolutePath();
        File folder = new File(f.concat("\\src\\testing\\results"));
        deleteFolder(folder);

        if(!folder.mkdir()){
            System.out.println("Folder results failed to re-create.\n");
        }
    }

    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    private static void executeTest(String filename) {
        TokenScanner.initTokenScanner(filename);
        runCode();
    }

    private static void saveResult(String filename) {
        String f = new File("").getAbsolutePath();

        File sourceFile = new File(f.concat("\\main.asm"));
        File destFile = new File(f.concat(filename));

        if (sourceFile.renameTo(destFile)) {
            //System.out.println("File moved successfully");
        } else {
            //System.out.println("Failed to move file");
        }
    }

    private static void runCode() {
        long timeInG = System.currentTimeMillis();

        // Lexic analysis
        TreeNode root = new TreeNode();
        Token accepted = Parser.toParse(root);
        if(accepted.getSymbol().equals("ñ")){
            //System.out.println("Syntax analysis completed with errors");
            return;
        }

        // AST
        TreeNode ast = new TreeNode("program");
        AST.getAST(ast,root);

        // Semantic analysis
        SymbolTableNode st = new SymbolTableNode();
        SemanticCheck.validateTree(ast, st);

        //3@Code generator
        AST.reverseFunctions(ast);
        BasicBlock bb = new BasicBlock();
        TAC.generateQuadruplesList(bb, ast);

        //MIPS generator
        Assembler.createMainFile(bb,st,ast);
    }
}


