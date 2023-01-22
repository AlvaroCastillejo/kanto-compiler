package assembler;

import errorHandler.ErrorHandler;
import errorHandler.ErrorList;
import parser.Parser;
import parser.TreeNode;
import symbolTable.SymbolTableNode;
import threeAddressCode.BasicBlock;
import threeAddressCode.Quadruple;
import threeAddressCode.TAC;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Assembler {
    //Constant valor 0
    private final String ZERO = "$zero";

    //Temporal per l'ensamblador
    private final String AT = "$at";

    //Valors retornats en funcions
    private final String V0 = "$v0";
    private final String V1 = "$v1";

    //Arguments en funcions
    private final String A0 = "$a0";
    private final String A1 = "$a1";
    private final String A2 = "$a2";
    private final String A3 = "$a3";

    //Temporals
    private final String T0 = "$t0";
    private final String T1 = "$t1";
    private final String T2 = "$t2";
    private final String T3 = "$t3";
    private final String T4 = "$t4";
    private final String T5 = "$t5";
    private final String T6 = "$t6";
    private final String T7 = "$t7";

    //Temporals salvats
    private final String S0 = "$s0";
    private final String S1 = "$s1";
    private final String S2 = "$s2";
    private final String S3 = "$s3";
    private final String S4 = "$s4";
    private final String S5 = "$s5";
    private final String S6 = "$s6";
    private final String S7 = "$s7";

    //Temporals
    private final String T8 = "$t8";
    private final String T9 = "$t9";

    //Punters
    private final String GP = "$gp";
    private final String SP = "$sp";
    private final String FP = "$fp";
    private final String RA = "$ra";

    //Paraules reservades que usem
    private final String MAIN = "main:\n";
    private static final String DATA = ".data\n";
    private static final String TEXT = ".text\n";
    private final String ONE_BYTE = ".byte";
    private final String TWO_BYTES = ".halfword";
    private final String FOUR_BYTES = ".word";
    private final String FOUR_BYTESf = ".float";
    private final String EIGHT_BYTES = ".double";

    //Salts en el codi ensamblador
    private final String END_IF = "END_IF";
    private final String START_LOOP = "START_LOOP";
    private final String END_LOOP = "END_LOOP";

    //Carrega la direccio
    private final String LW = "lw";
    //Carrega contingut
    private final String LI = "li";
    //SUMA
    private final String ADD = "add";
    //RESTA
    private final String SUB = "sub";
    //MULTIPLICA
    private final String MUL = "mul";
    //DIVIDEIX
    private final String DIV = "div";
    //LOGICA ==
    private final String BEQ = "beq";
    //LOGICA !=
    private final String BNE = "bne";
    //LOGICA <
    private final String BLT = "blt";
    //LOGICA >
    private final String BGT = "bgt";
    //LOGICA <=
    private final String BLE = "ble";
    //LOGICA >=
    private final String BGE = "bge";

    private static SymbolTableNode symbolTable;

    public static void createMainFile(BasicBlock bb, SymbolTableNode st, TreeNode ast){
        symbolTable = st;
        try{
            Writer assembler = new FileWriter("main.asm", false);
            assembler.write(DATA);
            assembler.write(TEXT);
            // Code for main function
            generateCode(assembler, bb);
            assembler.write("li   $v0, 10          # system call for exit\n" +
                    "      syscall\n");
            // Code for other functions
            for(int i = 1; i < ast.getSons().size(); i++){
                if(!ast.getSons().get(i).isCalledFunc()) continue;
                RegisterManager.resetRegister();
                BasicBlock funcBB = new BasicBlock();
                TAC.generateQuadruplesList(funcBB, ast.getSons().get(i));
                BasicBlock returnBB = new BasicBlock();
                returnBB.setDad(funcBB);
                Quadruple q = new Quadruple(null,null,null,"return");
                q.setScope("return");
                returnBB.addQuad(q);
                funcBB.addSon(returnBB);
                generateCode(assembler, funcBB);
            }
            assembler.close();
        }catch(IOException e){
            ErrorHandler.throwError(ErrorList.AssemblerMainNotFound);
            e.printStackTrace();
        }
    }

    private static void generateCode(Writer assembler, BasicBlock bb) throws IOException{
        LinkedList<String> code = new LinkedList<>();

        if(bb.hasBeenWritten()){
            return;
        }
        if(bb.getLabel() != null){
            try{
                assembler.write(bb.getLabel() + ": \n");
            }catch(IOException io){
                ErrorHandler.throwError(ErrorList.AssemblerLabelError);
            }
        }
        for(Quadruple quad : bb.getQuadruples()) {
            switch (quad.getScope()) {
                case "varDecl":
                    varDeclaration(assembler,quad);
                    break;
                case "operation":
                    operacio(assembler, quad);
                    break;
                case "modif":
                    break;
                case "BooleanAssignation":
                    boolAssignation(assembler, quad);
                    break;
                case "NumericAssignation":
                    assignacio(assembler, quad, 0);
                    break;
                case "allocate":
                    allocateTemporaryRegisters(assembler);
                    break;
                case "pushparam":
                    pushParam(assembler,quad);
                    break;
                case "call":
                    callFunction(assembler,quad);
                    break;
                case "pullReturnedParam":
                    retrieveReturnedParam(assembler,quad);
                    break;
                case "reallocate":
                    reallocateTemporaryRegisters(assembler);
                    break;
                case "param":
                    declareParam(assembler,quad);
                    break;
                case "IE":
                    ifElse(assembler, quad);
                    break;
                case "llamarFuncion":
                    break;
                case "goto":
                    goTo(assembler, quad);
                    break;
                case "W":
                    bucle(assembler, quad);
                    break;
                case "returnParam":
                    pushReturnedParams(assembler,quad);
                    break;
                case "return":
                    returnFunc(assembler, quad);
                    break;
            }
        }
        if(bb.getEndGoto()!=null){
            assembler.write("j " + bb.getEndGoto() + "\n");
        }
        bb.markAsWritten();
        //Recursive function
        for(BasicBlock node : bb.getSons()){
            generateCode(assembler, node);
        }
    }

    private static void reallocateTemporaryRegisters(Writer assembler) throws IOException {
        ArrayList<String> commands = RegisterManager.getRestoreRegisters();
        for(String command : commands){
            assembler.write(command);
        }
    }

    private static void pushReturnedParams(Writer assembler, Quadruple quad) throws IOException {
        String auxArg1 = RegisterManager.getRegisterByArg(quad.getArg1(),null);
        String registerArg1 = null;

        if(auxArg1.equals("FULL")){
            String[] commands = RegisterManager.reallocateRegisters(quad.getArg1(), String.valueOf(0));
            assembler.write(commands[0]);
            assembler.write(commands[1]);
            registerArg1 = commands[2];
        }else{
            registerArg1 = auxArg1;
        }

        assembler.write("move $v" + quad.getArg2() + ", $" + registerArg1 + "\n");
    }

    private static void declareParam(Writer assembler, Quadruple quad) throws IOException {
        String auxArg1 = RegisterManager.getRegisterByArg(quad.getArg1(),null);
        String registerArg1 = null;

        if(auxArg1.equals("FULL")){
            String[] commands = RegisterManager.reallocateRegisters(quad.getArg1(), String.valueOf(0));
            assembler.write(commands[0]);
            assembler.write(commands[1]);
            registerArg1 = commands[2];
        }else{
            registerArg1 = auxArg1;
        }

        assembler.write("move $" + registerArg1 + ", $a" + quad.getArg2() + "\n");
    }

    private static void varDeclaration(Writer assembler, Quadruple quad) throws IOException {
        String registerToUse = RegisterManager.getRegisterByArg(quad.getArg1(),null);
        if(registerToUse.equals("FULL")) {
            String[] commands = RegisterManager.reallocateRegisters(quad.getArg1(), String.valueOf(0));
            assembler.write(commands[0]);
            assembler.write(commands[1]);
        } else {
            assembler.write("li $" + registerToUse + ", 0\n");
        }
    }

    private static void retrieveReturnedParam(Writer assembler, Quadruple quad) throws IOException {
        String auxArg1 = RegisterManager.getRegisterByArg(quad.getArg1(),null);
        String registerArg1 = null;

        if(auxArg1.equals("FULL")){
            String[] commands = RegisterManager.reallocateRegisters(quad.getArg1(), String.valueOf(0));
            assembler.write(commands[0]);
            assembler.write(commands[1]);
            registerArg1 = commands[2];
        }else{
            registerArg1 = auxArg1;
        }

        assembler.write("move $" + registerArg1 + ", $v" + quad.getArg2() + "\n");
    }

    private static void callFunction(Writer assembler, Quadruple quad) throws IOException {
        assembler.write("jal " + quad.getArg1() + "\n");
    }

    private static void pushParam(Writer assembler, Quadruple quad) throws IOException {
        String auxArg1 = RegisterManager.getRegisterByArg(quad.getArg1(),null);
        String registerArg1 = null;

        if(auxArg1.equals("FULL")){
            String[] commands = RegisterManager.reallocateRegisters(quad.getArg1(), String.valueOf(0));
            assembler.write(commands[0]);
            assembler.write(commands[1]);
            registerArg1 = commands[2];
        }else{
            registerArg1 = auxArg1;
        }

        assembler.write("move $a"+ quad.getArg2() +", $" + registerArg1 + "\n");
        assembler.write(RegisterManager.freeRegister(quad.getArg1()));
        RegisterManager.freeRegister(quad.getArg1());
    }

    private static void allocateTemporaryRegisters(Writer assembler) throws IOException {
        ArrayList<String> commands = RegisterManager.emptyTempRegisters();
        for(String command : commands){
            assembler.write(command);
        }
    }

    private static void returnFunc(Writer assembler, Quadruple quad) throws IOException {
        assembler.write("jr $ra\n");
    }

    private static void ifElse(Writer assembler, Quadruple quad) throws IOException {
        String auxArg1 = RegisterManager.getRegisterByArg(quad.getArg1(),null);
        String registerArg1 = null;

        if(auxArg1.equals("FULL")){
            String[] commands = RegisterManager.reallocateRegisters(quad.getArg1(), String.valueOf(0));
            assembler.write(commands[0]);
            assembler.write(commands[1]);
            registerArg1 = commands[2];
        }else{
            registerArg1 = auxArg1;
        }

        String auxArg2 = RegisterManager.getRegisterByArg(quad.getArg2(),null);
        String registerArg2 = null;

        if(auxArg2.equals("FULL")){
            String[] commands = RegisterManager.reallocateRegisters(quad.getArg2(), String.valueOf(0));
            assembler.write(commands[0]);
            assembler.write(commands[1]);
            registerArg2 = commands[2];
        }else{
            registerArg2 = auxArg2;
        }

        String auxAux = RegisterManager.getRegisterByArg("aux",null); // auxAux KEKW
        String registerAux = null;

        if(auxAux.equals("FULL")){
            String[] commands = RegisterManager.reallocateRegisters("aux", String.valueOf(0));
            assembler.write(commands[0]);
            assembler.write(commands[1]);
            registerAux = commands[2];
        }else{
            registerAux = auxAux;
        }

        String command = "null";
        switch (quad.getOperator()){
            case "stronger-e":
                assembler.write("slt $" + registerAux + ", $" + registerArg1 + ", $" + registerArg2 + "\n");
                assembler.write("beq $" + registerAux + ", $zero, " + quad.getResult() + "\n");
                break;
            case "weaker-e":
                assembler.write("slt $" + registerAux + ", $" + registerArg2 + ", $" + registerArg1 + "\n");
                assembler.write("beq $" + registerAux + ", $zero, " + quad.getResult() + "\n");
                break;
            case "nEqual":
                command = "bne";
                assembler.write(command + " $" + registerArg1 + ", $" + registerArg2 + ", " + quad.getResult() + "\n");
                break;
            case "visible":
                command = "beq";
                assembler.write(command + " $" + registerArg1 + ", 1" + ", " + quad.getResult() + "\n");
                break;
            case "invisible":
                command = "bne";
                assembler.write(command + " $" + registerArg1 + ", 0" + ", " + quad.getResult() + "\n");
                break;
        }
        //assembler.write(command + " $" + RegisterManager.getRegisterByArg(quad.getArg1(),null) + ", $" + RegisterManager.getRegisterByArg(quad.getArg2(),null) + ", " + quad.getResult() + "\n");
    }

    private static void boolAssignation(Writer assembler,Quadruple quadruple) throws IOException {
        String registerToUse = RegisterManager.getRegisterByArg(quadruple.getResult(),symbolTable);
        switch (quadruple.getArg1()) {
            case "appeared.":
                if(registerToUse.equals("FULL")){
                    String[] commands = RegisterManager.reallocateRegisters(quadruple.getResult(), "1");
                    assembler.write(commands[0]);
                    assembler.write(commands[1]);
                } else {
                    assembler.write("li $"+ registerToUse + ", " + "1" + "\n");
                }
                break;
            case "disappeared.":
                if(registerToUse.equals("FULL")){
                    String[] commands = RegisterManager.reallocateRegisters(quadruple.getResult(), "0");
                    assembler.write(commands[0]);
                    assembler.write(commands[1]);
                } else {
                    assembler.write("li $"+ registerToUse + ", " + "0" + "\n");
                }
                break;
        }
    }

    private static void assignacio(Writer assembler, Quadruple quadruple, Integer option) throws IOException{

        switch(option){
            //Case INT
            case 0:
                String registerToUse = RegisterManager.getRegisterByArg(quadruple.getResult(),symbolTable);
                if(registerToUse.equals("FULL")){
                    String[] commands = RegisterManager.reallocateRegisters(quadruple.getResult(), quadruple.getArg1());
                    assembler.write(commands[0]);
                    assembler.write(commands[1]);
                }else{
                    if(RegisterManager.doesExistMemory(quadruple.getResult())){
                        //Exemple: lw $t2, 0($s0)
                        assembler.write("lw $t" + registerToUse + ", " + RegisterManager.getMemoryAddress(quadruple.getResult()) + "($sp)");
                        assembler.write(RegisterManager.freeRegister(quadruple.getResult()));
                    }else{
                        if(Parser.isInt(quadruple.getArg1())){
                            assembler.write("li $"+ registerToUse + ", " + quadruple.getArg1() + "\n");
                        } else {
                            assembler.write("move $"+ registerToUse + ", $" + RegisterManager.getRegisterByArg(quadruple.getArg1(),null) + "\n");
                        }
                    }
                }
                break;
        }
    }

    private static void operacio(Writer assembler, Quadruple quadruple) throws IOException{
        String auxResult = RegisterManager.getRegisterByArg(quadruple.getResult(),symbolTable);
        String auxArg1 = "";
        String auxArg2 = "";

        String registerResult = "";
        String registerArg1 = "";
        String registerArg2 = "";

        if(auxResult.equals("FULL")){
            String[] commands = RegisterManager.reallocateRegisters(quadruple.getResult(), String.valueOf(0));
            assembler.write(commands[0]);
            assembler.write(commands[1]);
            registerResult = commands[2];
        }else{
            registerResult = auxResult;
        }
        if(quadruple.getResult().equals(quadruple.getArg1())){
            registerArg1 = registerResult;
        }else{
            auxArg1 = RegisterManager.getRegisterByArg(quadruple.getArg1(),symbolTable);
            if(auxArg1.equals("FULL")){
                String[] commands = RegisterManager.reallocateRegisters(quadruple.getResult(), String.valueOf(0));
                assembler.write(commands[0]);
                assembler.write(commands[1]);
                registerArg1 = commands[2];
            }else{
                registerArg1 = auxArg1;
            }
        }
        if(quadruple.getResult().equals(quadruple.getArg2()) || quadruple.getArg1().equals(quadruple.getArg2())){
            if(quadruple.getResult().equals(quadruple.getArg2())){
                registerArg2 = registerResult;
            }else{
                registerArg2 = registerArg1;
            }
        }else{
            auxArg2 = RegisterManager.getRegisterByArg(quadruple.getArg2(),symbolTable);
            if(auxArg2.equals("FULL")){
                String[] commands = RegisterManager.reallocateRegisters(quadruple.getResult(), String.valueOf(0));
                assembler.write(commands[0]);
                assembler.write(commands[1]);
                registerArg2 = commands[2];
            }else{
                registerArg2 = auxArg2;
            }
        }

        //Buscar registres de result, arg1 i arg2
        switch(quadruple.getOperator()){
            case "with":
                // +
                assembler.write("add $" + registerResult + ", $" + registerArg1 + ", $" + registerArg2 + "\n");
                break;
            case "without":
                // -
                assembler.write("sub $" + registerResult + ", $" + registerArg1 + ", $" + registerArg2 + "\n");
                break;

            case "spreading":
                // *
                assembler.write("div $" + registerResult + ", $" + registerArg1 + ", $" + registerArg2 + "\n");
                break;

            case "splitting":
                // /
                assembler.write("mul $" + registerResult + ", $" + registerArg1 + ", $" + registerArg2 + "\n");
                break;

            case "increase":
                try {
                    Integer.parseInt(quadruple.getArg2());
                    assembler.write("add $" + registerResult + ", $" + registerArg1 + ", " + quadruple.getArg2() + "\n");
                } catch (NumberFormatException nfe){
                    assembler.write("add $" + registerResult + ", $" + registerArg1 + ", $" + registerArg2 + "\n");
                }
                break;
            case "decrease":
                try {
                    Integer.parseInt(quadruple.getArg2());
                    assembler.write("sub $" + registerResult + ", $" + registerArg1 + ", " + quadruple.getArg2()+ "\n");
                } catch (NumberFormatException nfe){
                    assembler.write("add $" + registerResult + ", $" + registerArg1 + ", $" + registerArg2 + "\n");
                }
                break;
        }
    }

    private static void bucle(Writer assembler, Quadruple quadruple) throws IOException{
        String command = null;

        String auxArg1 = RegisterManager.getRegisterByArg(quadruple.getArg1(),null);
        String registerArg1 = null;

        if(auxArg1.equals("FULL")){
            String[] commands = RegisterManager.reallocateRegisters(quadruple.getArg1(), String.valueOf(0));
            assembler.write(commands[0]);
            assembler.write(commands[1]);
            registerArg1 = commands[2];
        }else{
            registerArg1 = auxArg1;
        }

        String auxArg2 = RegisterManager.getRegisterByArg(quadruple.getArg2(),null);
        String registerArg2 = null;

        if(auxArg2.equals("FULL")){
            String[] commands = RegisterManager.reallocateRegisters(quadruple.getArg2(), String.valueOf(0));
            assembler.write(commands[0]);
            assembler.write(commands[1]);
            registerArg2 = commands[2];
        }else{
            registerArg2 = auxArg2;
        }

        switch (quadruple.getOperator()) {
            case "stronger":
                command = "bgt";
                break;
            case "weaker":
                command = "blt";
                break;
            case "equal":
                command = "beq";
                break;
            case "visible":
                command = "beq";
                assembler.write(command + " $" + registerArg1 + ", 1" + ", " + quadruple.getResult() + "\n");
                return;
            case "invisible":
                command = "bne";
                assembler.write(command + " $" + registerArg1 + ", 0" + ", " + quadruple.getResult() + "\n");
                return;
        }

        assembler.write(command + " $" + registerArg1 + ", $" + registerArg2 + ", " + quadruple.getResult() + "\n");
    }

    private static void goTo(Writer assembler, Quadruple quadruple) throws IOException{
        assembler.write("j " + quadruple.getResult() + "\n");
    }
}
