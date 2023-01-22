package assembler;

import symbolTable.SymbolTableNode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class RegisterManager {
    //private static HashMap<String, Register> registerMap;
    //private static Boolean[] usedRegisters;

    private static Register[] temporaryRegisters; //Checks which registers are currently being used
    private static HashMap<String, Integer> registerMap; //Stores all the variables stored inside registers and their current position
    private static HashMap<String, Integer> memoryMap; //Stores all the variables stored in memory alongside their address
    private static int registerPointer; //Points to the next temporary register that will be reallocated into memory if they all are occupied
    private static int currentAddress;
    private static ArrayList<String> restoreRegisters;

    static {
        registerMap = new HashMap<>();
        memoryMap = new HashMap<>();
        temporaryRegisters = new Register[]{new Register("t",0),new Register("t",1),new Register("t",2),new Register("t",3),new Register("t",4),new Register("t",5),new Register("t",6),new Register("t",7),new Register("t",8),new Register("t",9)};
        registerPointer = 0;
        currentAddress = 0;
        restoreRegisters = new ArrayList<>();
    }

    public static void resetRegister(){
        registerMap = new HashMap<>();
        memoryMap = new HashMap<>();
        temporaryRegisters = new Register[]{new Register("t",0),new Register("t",1),new Register("t",2),new Register("t",3),new Register("t",4),new Register("t",5),new Register("t",6),new Register("t",7),new Register("t",8),new Register("t",9)};
        registerPointer = 0;
        currentAddress = 0;
        restoreRegisters = new ArrayList<>();
    }

    public static String getRegisterByArg(String request, SymbolTableNode symbolTable) {
        //If variable is already stored in a temporary register
        if(registerMap.containsKey(request)){
            return temporaryRegisters[registerMap.get(request)].getType()+ temporaryRegisters[registerMap.get(request)].getNumber();
        }
        int r = registerAvailable();
        if(r == -1){
            // No registers available
            return "FULL";
        }else{
            // Register number r is available
            registerMap.put(request,r);
            temporaryRegisters[r].setUsed(true);
        }
        return temporaryRegisters[r].getType()+ temporaryRegisters[r].getNumber();
    }

    private static int registerAvailable() {
        for (int i = 0; i < temporaryRegisters.length; i++) {
            Register r = temporaryRegisters[i];
            if (!r.isUsed()) return i;
        }
        return -1;
    }

    public static String[] reallocateRegisters(String request, String value){
        String[] commands = new String[3];
        String oldVar = "";
        String aux;

        //We first search for the variable that is stored in the register we are going to replace
        for (String i : registerMap.keySet()) {
            int register = registerMap.get(i);
            if(register == registerPointer){
                oldVar = i;
                break;
            }
        }

        //Then we need to know if the requested variable is already stored in our memory
        //Once we've got it, we store its info to return to the Assembler class
        /** REMEMBER
         *  lw (loads from memory to register): lw reg.dest, offset(reg.source) Example: lw $t2, 0($s0)
         *  li (loads value to register): li $s2, 4
         *  sw (loads from register to memory): sw reg.source, offset(reg.dest) Example: sw $t1, 16($s0)
         *  First load from register to memory (sw) then load from memory to register (lw) or load immediate (li)
         */
        aux = "sw $t" + registerPointer + ", " + currentAddress + "($sp)\n";
        commands[0] = aux;

        if(doesExistMemory(request)){

            int address = memoryMap.get(request);
            aux = "lw $t" + registerPointer + ", " + address + "($sp)\n";
            memoryMap.remove(request);
            memoryMap.put(oldVar, address);

        }else{

            aux = "li $t" + registerPointer + ", " + value + "\n";
            memoryMap.put(oldVar, currentAddress);

        }
        commands[1] = aux;

        registerMap.remove(oldVar);
        registerMap.put(request, registerPointer);

        commands[2] = "t" + registerPointer;


        if(registerPointer < 9) {
            registerPointer++;
        }else{
            registerPointer = 0;
        }

        currentAddress += 4;
        return commands;
    }

    public static ArrayList<String> emptyTempRegisters(){
        //We store the 10 temporary registers and the "ra" register
        ArrayList<String> commands = new ArrayList<>();
        restoreRegisters = new ArrayList<>();
        String oldVar = "";
        String aux;

        for (int i = 0; i < 10; i++) {
            if(temporaryRegisters[i].isUsed()) {
                aux = "sw $t" + i + ", " + currentAddress + " ($sp)\n";
                String counterAux = "lw $t" + i + ", " + currentAddress + " ($sp)\n";
                commands.add(aux);
                restoreRegisters.add(counterAux);
                /*for (String key : registerMap.keySet()) {
                    int register = registerMap.get(key);
                    if (register == i) {
                        oldVar = key;
                        break;
                    }
                }
                registerMap.remove(oldVar);
                memoryMap.put(oldVar, currentAddress);*/
                currentAddress += 4;
            }
        }
        aux = "sw $ra, " + currentAddress + "($sp)\n";
        String counterAux = "lw $ra, " + currentAddress + "($sp)\n";
        commands.add(aux);
        restoreRegisters.add(counterAux);
        currentAddress += 4;

        int numVars = commands.size();
        commands.add(0, "sub $sp,$sp, " + (numVars * 4) + "\n");
        restoreRegisters.add("addi $sp,$sp, " + (numVars * 4) + "\n");
        return commands;
    }

    public static ArrayList<String> getRestoreRegisters() {
        return restoreRegisters;
    }

    public static String callFunction(String functionName){
        return "jal " + functionName + "\n";
    }

    public static boolean doesExistRegister(String request){
        return registerMap.containsKey(request);
    }

    public static boolean doesExistMemory(String request){
        return memoryMap.containsKey(request);
    }

    public static int getMemoryAddress(String request){
        return memoryMap.get(request);
    }

    public static String freeRegister(String request){
        String command = "";
        int value = 0;
        char type = 'E';

        for (String i : registerMap.keySet()) {
            if(i==null) break;
            if(i.equals(request)){
                value = registerMap.get(i);
                type = 'T';
                temporaryRegisters[value].setUsed(false);
                break;
            }
        }
        for (String i : memoryMap.keySet()){
            if(i==null) break;
            if(i.equals(request)){
                value = memoryMap.get(i);
                type = 'M';
                break;
            }
        }

        if(type == 'T'){
            command = "li $t" + value + ", 0\n";
            registerMap.remove(request);
        }else{
            command = "sw $zero, " + value + "($sp)\n";
            memoryMap.remove(request);
        }

        return command;
    }


}
