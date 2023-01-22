import assembler.Assembler;
import parser.Parser;
import parser.TreeNode;
import symbolTable.*;
import org.json.JSONObject;
import threeAddressCode.BasicBlock;
import threeAddressCode.TAC;

import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        long timeInG = System.currentTimeMillis();

        // Lexic analysis
        TreeNode root = new TreeNode();
        Token accepted = Parser.toParse(root);
        if(accepted.getSymbol().equals("ñ")){
            System.out.println("Syntax analysis completed with errors");
            return;
        } else {
            //System.out.println("Syntax analysis done. All ok.");
        }
        long timeOutL = System.currentTimeMillis();

        // AST
        TreeNode ast = new TreeNode("program");
        AST.getAST(ast,root);
        long timeOutS = System.currentTimeMillis();

        // Semantic analysis
        SymbolTableNode st = new SymbolTableNode();
        SemanticCheck.validateTree(ast, st);
        long timeOutSem = System.currentTimeMillis();

        //3@Code generator
        AST.reverseFunctions(ast);
        BasicBlock bb = new BasicBlock();
        TAC.generateQuadruplesList(bb, ast);
        long timeOutTAC = System.currentTimeMillis();

        //MIPS generator
        Assembler.createMainFile(bb,st,ast);
        long timeOutG = System.currentTimeMillis();

        generateStats(root,ast,st,bb,timeInG,timeOutL,timeOutS,timeOutSem,timeOutTAC,timeOutG);
    }

    private static void generateStats(TreeNode root, TreeNode ast, SymbolTableNode st, BasicBlock bb, long timeInG, long timeOutL, long timeOutS, long timeOutSem, long timeOutTAC, long timeOutG) {
        JSONObject json = TreeNode.toJson(root);
        String prettyJson = TreeNode.prettyJson(json);
        writeJSONtoFile(prettyJson,"parsingTree.json");

        JSONObject jsonAST = TreeNode.toJson(ast);
        String prettyJsonAST = TreeNode.prettyJson(jsonAST);
        writeJSONtoFile(prettyJsonAST,"abstractSyntaxTree.json");

        JSONObject jsonST = SymbolTableNode.toJson(st);
        String prettyJsonST = SymbolTableNode.prettyJson(jsonST);
        writeJSONtoFile(prettyJsonST,"symbolTable.json");

        JSONObject jsonBB = BasicBlock.toJson(bb);
        String prettyJsonBB = BasicBlock.prettyJson(jsonBB);
        writeJSONtoFile(prettyJsonBB, "basicBlocks.json");

        String timeStats = ("Global elapsed time: " + (timeOutG-timeInG) + "ms"
                + "\n\tLexic analysis elapsed time: " + (timeOutL-timeInG) + "ms"
                + "\n\tAbstract Syntax Tree build elapsed time: " + (timeOutS-timeOutL) + "ms"
                + "\n\tSemantic analysis elapsed time: " + (timeOutSem-timeOutS) + "ms"
                + "\n\t3@C generator elapsed time: " + (timeOutTAC-timeOutSem) + "ms"
                + "\n\tMIPS generator elapsed time: " + (timeOutG-timeOutTAC) + "ms");
        timeStats += "\nAbstract Syntax Tree compression factor: " + AST.getCompressionFactor(root,ast) + "%";
        writeJSONtoFile(timeStats,"stats.txt");
    }

    private static void writeJSONtoFile(String json,String fileName){
        try (FileWriter file = new FileWriter("stats/"+fileName)) {
            file.write(json); // data is a JSON string here
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
