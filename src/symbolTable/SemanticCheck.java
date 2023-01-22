package symbolTable;

import errorHandler.*;
import parser.TreeNode;

import java.util.Collections;
import java.util.LinkedList;

public class SemanticCheck {
    public static boolean checkFunctionDeclaration(TreeNode initTrainer, TreeNode endTrainer, SymbolTableNode st) {
        boolean error = false;
        if(!initTrainer.getSymbol().equals(endTrainer.getSymbol())){
            //Error. Function must end as it begins.
            /*System.out.println(st.getScope() + ":" + initTrainer.getLine() + " - Error semántico. La función debe empezar y acabar con el mismo entrenador. \n" +
                    "En este caso empieza por \"" + initTrainer.getSymbol() + "\" y termina por \"" + endTrainer.getSymbol() + "\".");*/
            ErrorHandler.throwError(ErrorList.SemanticFuncTrainerError, initTrainer.getLine(), "Error at scope " + st.getScope() + ": Should start with \"" + initTrainer.getSymbol() +  "\" and end with \"" + endTrainer.getSymbol() + "\".");
            error = true;
        }
        if(alreadyDeclared(initTrainer,st)){
            //Error. Function already declared.
            //System.out.println(st.getScope() + ":" + initTrainer.getLine() + " - Error semántico. La función \"" + initTrainer.getSymbol() + "\" ya está declarada.");
            ErrorHandler.throwError(ErrorList.AlreadyDeclaredFuncError, initTrainer.getLine(), "Error at scope " + st.getScope() + ": Function \"" + initTrainer.getSymbol() + "\".");

            error = true;
        }
        if(!Dictionary.found(initTrainer.getSymbol())) {
            //Error. Trainer not found in dictionary.
            //System.out.println(st.getScope() + ":" + initTrainer.getLine() + " - Error semántico. El entrenador \"" + initTrainer.getSymbol() + "\" no definido en el diccionario.");
            ErrorHandler.throwError(ErrorList.SemanticUnknownTrainerError, initTrainer.getLine(), "Error at scope " + st.getScope() + ": Trainer undeclared is \"" + initTrainer.getSymbol() + "\".");

            error = true;
        }
        return error;
    }

    public static boolean checkFunctionDeclaration2(TreeNode initTrainer, TreeNode endTrainer, SymbolTableNode st) {
        boolean error = false;
        if(!initTrainer.getSymbol().equals(endTrainer.getSymbol())){
            //Error. Function must end as it begins.
            /*System.out.println(st.getScope() + ":" + initTrainer.getLine() + " - Error semántico. La función debe empezar y acabar con el mismo entrenador. \n" +
                    "En este caso empieza por \"" + initTrainer.getSymbol() + "\" y termina por \"" + endTrainer.getSymbol() + "\".");*/
            ErrorHandler.throwError(ErrorList.SemanticFuncTrainerError, initTrainer.getLine(), "Error at scope " + st.getScope() + ": Should start with \"" + initTrainer.getSymbol() +  "\" and end with \"" + endTrainer.getSymbol() + "\".");

            error = true;
        }

        if(!Dictionary.found(initTrainer.getSymbol())) {
            //Error. Trainer not found in dictionary.
            //System.out.println(st.getScope() + ":" + initTrainer.getLine() + " - Error semántico. El entrenador \"" + initTrainer.getSymbol() + "\" no definido en el diccionario.");
            ErrorHandler.throwError(ErrorList.SemanticUnknownTrainerError, initTrainer.getLine(), "Error at scope " + st.getScope() + ": Trainer undeclared is \"" + initTrainer.getSymbol() + "\".");
            error = true;
        }
        return error;
    }

    private static boolean alreadyDeclared(TreeNode node, SymbolTableNode st) {
        for(Symbol s : st.getSymbols()){
            if(s.getName().equals(node.getSymbol())){
                return true;
            }
        }
        if(st.getScope().equals("Global")){
            return false;
        }
        return alreadyDeclared(node,st.getParent());
    }

    public static boolean checkVarAvailable(TreeNode pokemonName, SymbolTableNode st) {
        if (!Dictionary.found(pokemonName.getSymbol())) {
            //System.out.println("Error semántico. El pokemon \"" + pokemonName.getSymbol() + "\" no esta definido en el diccionario.\n");
            ErrorHandler.throwError(ErrorList.SemanticUnknownPokemonError, pokemonName.getLine(), "Error at scope " + st.getScope() + ": Pokemon undeclared is \"" + pokemonName.getSymbol() + "\".");
            return true;
        }
        if (varAlreadyDeclared(pokemonName,st)){
            //System.out.println(st.getScope() + ":" + pokemonName.getLine() + " - Error semántico. Variable \"" + pokemonName.getSymbol() + "\" previamente declarada.");
            ErrorHandler.throwError(ErrorList.AlreadyDeclaredPokemonError, pokemonName.getLine(), "Error at scope " + st.getScope() + ": Pokemon \"" + pokemonName.getSymbol() + "\".");

            return true;
        }

        return false;
    }

    public static boolean checkVarDeclaration(TreeNode pokemonName, SymbolTableNode st) {
        if (!Dictionary.foundPokemon(pokemonName.getSymbol())) {
            //System.out.println("Error semántico. El pokemon \"" + pokemonName.getSymbol() + "\" no esta definido en el diccionario.\n");
            ErrorHandler.throwError(ErrorList.SemanticUnknownPokemonError, pokemonName.getLine(), "Error at scope " + st.getScope() + ": Pokemon undeclared is \"" + pokemonName.getSymbol() + "\".");

            return false;
        }
        return varAlreadyDeclared(pokemonName, st);
    }

    private static boolean varAlreadyDeclared(TreeNode pokemon, SymbolTableNode st) {
        LinkedList<Symbol> reversedSymbols = new LinkedList<>(st.getSymbols());
        Collections.reverse(reversedSymbols);
        // Reverse symbols to search from the last symbol declared.
        for(Symbol s : reversedSymbols){
            if(s.getName().equals(pokemon.getSymbol())){
                if(s.isDeclared()){
                    return true;
                } else {
                    return false;
                }
            }
        }
        if(st.getScope().equals("Global")){
            return false;
        }

        return varAlreadyDeclared(pokemon,st.getParent());
    }

    public static void checkOperationTypes(TreeNode pokemonF, TreeNode pokemonS, TreeNode pokemonT, TreeNode operand, SymbolTableNode st) {
        //first check if pokemons are declared
        TreeNode[] pokemons = { pokemonF, pokemonS, pokemonT };
        for (TreeNode pok: pokemons) {
            if (!checkVarDeclaration(pok, st)){
                //System.out.println(st.getScope() + ":" + pok.getLine() + " - Error semántico. Variable \"" + pok.getSymbol() + "\" no declarada.");
                ErrorHandler.throwError(ErrorList.UndeclaredPokemonError, pok.getLine(), "Error at scope " + st.getScope() + ": Pokemon undeclared is \"" + pok.getSymbol() + "\".");
                return;
            }
        }

        //check operand is right
        if (!Dictionary.isNumericalOp(operand.getSymbol())) {
            //System.out.println(st.getScope() + ":" + operand.getLine() + " - Error semántico. Operando no numerico.\n");
            ErrorHandler.throwError(ErrorList.NumericOperandExpected, operand.getLine(), "Error at scope " + st.getScope() + ": Operand incorrect is \"" + operand.getSymbol() + "\".");
        }

        //all three variables are declared, now check types are same
        String[] types = new String[pokemons.length];
        for (int i = 0; i < pokemons.length; i++) {
            if (Dictionary.getVarType(pokemons[i].getSymbol()) == null) {
                //System.out.println(st.getScope() + ":" + pokemons[i].getLine() + " - Error semántico. Variable \"" + pokemons[i].getSymbol() + "\" no definida en diccionario.\n");
                ErrorHandler.throwError(ErrorList.SemanticUnknownPokemonError, pokemons[i].getLine(), "Error at scope " + st.getScope() + ": Pokemon undeclared is \"" + pokemons[i].getSymbol() + "\".");
                return;
            } else {
                types[i] = Dictionary.getVarType(pokemons[i].getSymbol());
            }
        }
        assert types[0] != null;
        if (!(types[0].equals(types[1]) && types[0].equals(types[2]) && types[0].equals("INTEGER"))) {
            //System.out.println(st.getScope() + ":" + pokemonF.getLine() + " - Error semántico en operacion. Variables \"" + pokemonF.getSymbol() + "\", \"" + pokemonS.getSymbol() + "\", \"" + pokemonT.getSymbol() + "\" no coinciden en tipos.");
            ErrorHandler.throwError(ErrorList.SemanticErrorOperandTypes, pokemonF.getLine(), "Error at scope " + st.getScope() + "Pokemons \"" + pokemonF.getSymbol() + "\", \"" + pokemonS.getSymbol() + "\"and \"" + pokemonT.getSymbol() + "\" doesn't match type");
        }
    }

    public static void checkBoolAsig(TreeNode pokemon, TreeNode operand, SymbolTableNode st) {
        if (checkVarDeclaration(pokemon, st)) {
            //check variable is boolean
            String type = Dictionary.getVarType(pokemon.getSymbol());
            assert type != null;
            if (!type.equals("BOOLEAN")) {
                //System.out.println(st.getScope() + ":" + pokemon.getLine() + " - Error semántico. Variable \"" + pokemon.getSymbol() + "\" no booleana en asignacion booleana.");
                ErrorHandler.throwError(ErrorList.BooleanAssignationExpected, pokemon.getLine(), "Error at scope " + st.getScope() + "Pokemon \"" + pokemon.getSymbol() + "\" is not boolean (ghost type)");

                //return;
            }

            //check operand is boolean
            if (!Dictionary.isBooleanAsig(operand.getSymbol())) {
                //System.out.println("Error semántico. asignacion no booleana.\n");
                ErrorHandler.throwError(ErrorList.BooleanAssignationExpected);
            }
        } else {
            //System.out.println(st.getScope() + ":" + pokemon.getLine() + " - Error semántico. Variable \"" + pokemon.getSymbol() + "\" no declarada.");
            ErrorHandler.throwError(ErrorList.UndeclaredPokemonError, pokemon.getLine(), "Error at scope " + st.getScope() + ": Pokemon undeclared is \"" + pokemon.getSymbol() + "\".");

        }
    }

    public static void checkNumericalAsign(TreeNode pokemon, TreeNode value, SymbolTableNode st) {
        if (checkVarDeclaration(pokemon, st)) {
            //check variable is integer
            String type = Dictionary.getVarType(pokemon.getSymbol());
            assert type != null;
            if (!type.equals("INTEGER")) {
                //System.out.println(st.getScope() + ":" + value.getLine() + " - Error semántico. Variable \"" + pokemon.getSymbol() + "\" no numerica en asignacion numerica.");
                ErrorHandler.throwError(ErrorList.NumericOperandExpected, pokemon.getLine(), "Error at scope " + st.getScope() + "Pokemon \"" + pokemon.getSymbol() + "\" is not numeric type.");

                return;
            }

            //check value is numerical pokemon or numerical value
            if (value.getSymbol() != null) {
                try {
                    int num = Integer.parseInt(value.getSymbol());
                } catch (NumberFormatException e) {
                    if (checkVarDeclaration(value, st)) {
                        //check value variable is integer
                        String type2 = Dictionary.getVarType(value.getSymbol());
                        assert type2 != null;
                        if (!type2.equals("INTEGER")) {
                            //System.out.println(st.getScope() + ":" + value.getLine() + " - Error semántico. Variable \"" + value.getSymbol() + "\" no numerica en asignacion numerica.");
                            ErrorHandler.throwError(ErrorList.NumericOperandExpected, pokemon.getLine(), "Error at scope " + st.getScope() + "Pokemon \"" + pokemon.getSymbol() + "\" is not numeric type.");
                        }
                    } else {
                        //System.out.println(st.getScope() + ":" + value.getLine() + " - Error semántico. Variable \"" + value.getSymbol() + "\" no declarada.");
                        ErrorHandler.throwError(ErrorList.UndeclaredPokemonError, value.getLine(), "Error at scope " + st.getScope() + ": Pokemon undeclared is \"" + value.getSymbol() + "\".");
                    }
                }
            } else {
                //System.out.println(st.getScope() + ":" + value.getLine() + " - Error semántico. valor no valido.\n");
                ErrorHandler.throwError(ErrorList.InvalidValueError, value.getLine(), "Error at scope " + st.getScope() + ": Invalid value for \"" + value.getSymbol() + "\".");
            }
        } else {
            //System.out.println(st.getScope() + ":" + pokemon.getLine() + " - Error semántico. Variable \"" + pokemon.getSymbol() + "\" no declarada.");
            ErrorHandler.throwError(ErrorList.UndeclaredPokemonError, value.getLine(), "Error at scope " + st.getScope() + ": Pokemon undeclared is \"" + value.getSymbol() + "\".");

        }
    }

    public static void checkComparation(TreeNode pokemonF, TreeNode operand, TreeNode pokemonS, SymbolTableNode st, String typeObj) {
        if (checkVarDeclaration(pokemonF, st)) {
            String type = Dictionary.getVarType(pokemonF.getSymbol());
            assert type != null;
            if (!type.equals(typeObj)) {
                if (typeObj.equals("INTEGER"))
                    //System.out.println(st.getScope() + ":" + pokemonF.getLine() + " - Error semántico. Variable \"" + pokemonF.getSymbol() + "\" no numerica en comparacion numerica.");
                    ErrorHandler.throwError(ErrorList.NumericComparisonExpected, pokemonF.getLine(), "Error at scope " + st.getScope() + ": Pokemon received is \"" + pokemonF.getSymbol() + "\".");

                else
                    //System.out.println(st.getScope() + ":" + pokemonF.getLine() + " - Error semántico. Variable \"" + pokemonF.getSymbol() + "\" no literal en comparacion literal.");
                    ErrorHandler.throwError(ErrorList.LiteralComparisonExpected, pokemonF.getLine(), "Error at scope " + st.getScope() + ": Pokemon received is \"" + pokemonF.getSymbol() + "\".");

                return;
            }
        } else {
            //System.out.println(st.getScope() + ":" + pokemonF.getLine() + " - Error semántico. Variable \"" + pokemonF.getSymbol() + "\" no declarada.");
            ErrorHandler.throwError(ErrorList.UndeclaredPokemonError, pokemonF.getLine(), "Error at scope " + st.getScope() + ": Pokemon undeclared is \"" + pokemonF.getSymbol() + "\".");

        }

        if (checkVarDeclaration(pokemonS, st)) {
            String type = Dictionary.getVarType(pokemonS.getSymbol());
            assert type != null;
            if (!type.equals(typeObj)) {
                if (typeObj.equals("INTEGER"))
                    //System.out.println(st.getScope() + ":" + pokemonF.getLine() + " - Error semántico. Variable \"" + pokemonF.getSymbol() + "\" no numerica en comparacion numerica.");
                    ErrorHandler.throwError(ErrorList.NumericComparisonExpected, pokemonF.getLine(), "Error at scope " + st.getScope() + ": Pokemon received is \"" + pokemonF.getSymbol() + "\".");

                else
                    //System.out.println(st.getScope() + ":" + pokemonF.getLine() + " - Error semántico. Variable \"" + pokemonF.getSymbol() + "\" no literal en comparacion literal.");
                    ErrorHandler.throwError(ErrorList.LiteralComparisonExpected, pokemonF.getLine(), "Error at scope " + st.getScope() + ": Pokemon received is \"" + pokemonF.getSymbol() + "\".");

                //return;
            }
        } else {
            //System.out.println(st.getScope() + ":" + pokemonS.getLine() + " - Error semántico. Variable \"" + pokemonS.getSymbol() + "\" no declarada.");
            ErrorHandler.throwError(ErrorList.UndeclaredPokemonError, pokemonS.getLine(), "Error at scope " + st.getScope() + ": Pokemon undeclared is \"" + pokemonS.getSymbol() + "\".");

        }

        if (!Dictionary.isBooleanComp(operand.getSymbol())) {
            //System.out.println(st.getScope() + ":" + operand.getLine() + " - Error semántico. Operador \"" + operand.getSymbol() + "\" no es un comparador.\n");
            ErrorHandler.throwError(ErrorList.ComparatorExpected, operand.getLine(), "Error at scope " + st.getScope() + ": Symbol \"" + operand.getSymbol() + "\" is not a comparator.");

        }
    }

    public static void checkBooleanCheck(TreeNode pokemon, TreeNode operand, SymbolTableNode st) {
        if (checkVarDeclaration(pokemon, st)) {
            String type = Dictionary.getVarType(pokemon.getSymbol());
            assert type != null;
            if (!type.equals("BOOLEAN")) {
                //System.out.println(st.getScope() + ":" + pokemon.getLine() + " - Error semántico. Variable \"" + pokemon.getSymbol() + "\" no booleana en comprobacion booleana.");
                ErrorHandler.throwError(ErrorList.ComparatorExpected, pokemon.getLine(), "Error at scope " + st.getScope() + ": Symbol \"" + pokemon.getSymbol() + "\" is not a boolean comparison.");

                return;
            }
        } else {
            //System.out.println(st.getScope() + ":" + pokemon.getLine() + " - Error semántico. Variable \"" + pokemon.getSymbol() + "\" no declarada.");
            ErrorHandler.throwError(ErrorList.UndeclaredPokemonError, pokemon.getLine(), "Error at scope " + st.getScope() + ": Pokemon undeclared is \"" + pokemon.getSymbol() + "\".");

        }

        if (!Dictionary.isBooleanAfir(operand.getSymbol())) {
            //System.out.println(st.getScope() + ":" + operand.getLine() + " - Error semántico. afirmacion no booleana.\n");
            ErrorHandler.throwError(ErrorList.BooleanAffirmationExpected, operand.getLine(), "Error at scope " + st.getScope() + ": Wrong operand is \"" + operand.getSymbol() + "\".");


        }
    }

    public static void checkCallFunction(TreeNode caller, TreeNode called, TreeNode params, SymbolTableNode st) {
        //check if caller is actual context
        String scope = st.getFunctionScope();
        if (!caller.getSymbol().equals(scope)) {
            //System.out.println(st.getScope() + ":" + caller.getLine() + " - Error semántico. Funcion \"" + caller.getSymbol() + "\" no es contexto actual.");
            ErrorHandler.throwError(ErrorList.WrongContextError, caller.getLine(), "Error at scope " + st.getScope() + ": Wrong function is \"" + caller.getSymbol() + "\".");

            return;
        }

        //check if called is declared
        if (!alreadyDeclared(called, st) && !called.getSymbol().equals(scope)) {
            //System.out.println(st.getScope() + ":" + called.getLine() + " - Error semántico. Funcion \"" + called.getSymbol() + "\" no declarada.");
            ErrorHandler.throwError(ErrorList.UndeclaredTrainerError, called.getLine(), "Error at scope " + st.getScope() + ": Trainer undeclared is \"" + called.getSymbol() + "\".");

            return;
        }

        //check params valid
        if (params != null){
            for(TreeNode param : params.getSons()){
                if (!checkVarDeclaration(param, st)) {
                    //System.out.println(st.getScope() + ":" + param.getLine() + " - Error semántico. Parametro \"" + param.getSymbol() + "\" no declarado.");
                    ErrorHandler.throwError(ErrorList.UndeclaredPokemonError, param.getLine(), "Error at scope " + st.getScope() + ": Parameter undeclared is \"" + param.getSymbol() + "\".");

                }
            }
        }
    }

    public static void checkModifOper(TreeNode pokemon, TreeNode operand, TreeNode value, SymbolTableNode st) {
        if (checkVarDeclaration(pokemon, st)) {
            String type = Dictionary.getVarType(pokemon.getSymbol());
            assert type != null;
            if (!type.equals("INTEGER")) {
                //System.out.println(st.getScope() + ":" + pokemon.getLine() + " - Error semántico. Variable \"" + pokemon.getSymbol() + "\" no numerica en modificacion numerica.");
                ErrorHandler.throwError(ErrorList.NumericOperandExpected, operand.getLine(), "Error at scope " + st.getScope() + ": Variable incorrect is \"" + operand.getSymbol() + "\".");
                return;
            }
        } else {
            //System.out.println(st.getScope() + ":" + pokemon.getLine() + " - Error semántico. Variable \"" + pokemon.getSymbol() + "\" no declarada.");
            ErrorHandler.throwError(ErrorList.UndeclaredPokemonError, pokemon.getLine(), "Error at scope " + st.getScope() + ": Pokemon undeclared is \"" + pokemon.getSymbol() + "\".");

        }

        if (!Dictionary.isIndecrementOp(operand.getSymbol())) {
            //System.out.println(st.getScope() + ":" + operand.getLine() + " - Error semántico. Operador \"" + operand.getSymbol() + "\" no es healed o lost.\n");
            ErrorHandler.throwError(ErrorList.LostOrHealedExpected, operand.getLine(), "Error at scope " + st.getScope() + ": Symbol received is \"" + operand.getSymbol() + "\".");

            return;
        }

        //check value is numerical pokemon or numerical value
        if (value.getSymbol() != null) {
            try {
                int num = Integer.parseInt(value.getSymbol());
            } catch (NumberFormatException e) {
                if (checkVarDeclaration(value, st)) {
                    //check value variable is integer
                    String type2 = Dictionary.getVarType(value.getSymbol());
                    assert type2 != null;
                    if (!type2.equals("INTEGER")) {
                        System.out.println(st.getScope() + ":" + value.getLine() + " - Error semántico. Variable \"" + value.getSymbol() + "\" no numerica en modificacion numerica.");
                    }
                } else {
                    //System.out.println(st.getScope() + ":" + value.getLine() + " - Error semántico. Variable \"" + value.getSymbol() + "\" no declarada.");
                    ErrorHandler.throwError(ErrorList.UndeclaredPokemonError, value.getLine(), "Error at scope " + st.getScope() + ": Pokemon undeclared is \"" + value.getSymbol() + "\".");

                }
            }
        } else {
            //System.out.println(st.getScope() + ":" + value.getLine() + " - Error semántico. Valor no valido.");
            ErrorHandler.throwError(ErrorList.InvalidValueError, value.getLine(), "Error at scope " + st.getScope() + ": Invalid value \"" + value.getSymbol() + "\".");

        }
    }

    /**
     * Validates the abstract syntax tree. It performs three traversals. The first one (left-right) declares the function
     * scopes, the second (right-left) fills the scopes with the parameters from the calls. The third (left-right) checks
     * the semantic of the code. TODO: The IF needs to be validated too.
     *
     * @param ast The root node from the AST tree
     * @param st The symbol table to maintain
     */
    public static void validateTree(TreeNode ast, SymbolTableNode st) {
        //First traverse
        firstTraverse(ast, st);
        //Second traverse
        secondTraverse(ast,st);
        //Third traverse
        thirdTraverse(ast,st);
    }

    /**
     * Traverses the AST declaring function scopes.
     * @param ast AST root.
     * @param st Symbol table to build.
     */
    private static void firstTraverse(TreeNode ast, SymbolTableNode st) {
        for(TreeNode function : ast.getSons()){
            TreeNode firstTrainer = new TreeNode(function.getSons().getFirst().getSymbol(),function.getSons().getFirst().getLine());
            TreeNode endTrainer = new TreeNode(function.getSons().getFirst().getSymbol(),function.getSons().getFirst().getLine());
            if(!SemanticCheck.checkFunctionDeclaration(firstTrainer,endTrainer,st)){
                st.addSymbol(new Symbol(true,function.getSons().getFirst().getSymbol(),"func",st));
            }
        }
    }

/*    private static void secondTraverse(TreeNode ast, SymbolTableNode st) {
        for(int i = ast.getSons().size()-1; i >= 0; i--){
            // Check if function has code
            if(ast.getSons().get(i).getSons().size() == 3){
                for(TreeNode statement : ast.getSons().get(i).getSons().get(1).getSons()){
                    // It only looks for function calls
                    if(statement.getSymbol().equals("llamarFuncion")){
                        // Check if function call uses parameters
                        if((statement.getSons().size() == 3 && statement.getSons().get(2).getSons().isEmpty()) || (statement.getSons().size() == 2)){
                            // No params
                        } else {
                            // Params
                            for(TreeNode param : statement.getSons().get(2).getSons()){
                                st.addSymbolToScope(param,statement.getSons().get(1));
                            }
                        }
                    }
                }
            }
        }
    }*/

    private static void secondTraverse(TreeNode ast, SymbolTableNode st) {
        if(ast.getSymbol().equals("llamarFuncion")){
            // Check if function call uses parameters
            if((ast.getSons().size() == 3 && ast.getSons().get(2).getSons().isEmpty()) || (ast.getSons().size() == 2)){
                // No params
            } else {
                // Params
                for(TreeNode param : ast.getSons().get(2).getSons()){
                    st.addSymbolToScope(param,ast.getSons().get(1));
                }
            }
            return;
        }
        if(ast.getSymbol().equals("IE")){
            for(TreeNode t : ast.getSons().getLast().getSons()){
                secondTraverse(t,st);
            }
            return;
        }
        if(ast.getSymbol().equals("W")){
            // TODO: mirar el ast para ver qué hijo pasarle a la llamada recursiva

        }

        if(ast.getSymbol().equals("program")){
            for(int i = ast.getSons().size()-1; i >= 0; i--){
                secondTraverse(ast.getSons().get(i),st);
            }
        } else {
            for(int i = 0; i < ast.getSons().size(); i++){
                secondTraverse(ast.getSons().get(i),st);
            }
        }

    }

    private static void thirdTraverse(TreeNode ast, SymbolTableNode st) {
        if(ast.getSymbol().equals("declararFuncion") ||  ast.getSymbol().equals("AshFunction")){
            TreeNode initTrainer = ast.getSons().getFirst();
            TreeNode endTrainer = ast.getSons().getLast();
            if(SemanticCheck.checkFunctionDeclaration2(initTrainer,endTrainer,st)){
                //semantic error
                //return;
            }
            // Check if function has code
            if(ast.getSons().get(1).getSymbol().equals("code")){
                SymbolTableNode newScope = st;
                for(Symbol stn : st.getSymbols()){
                    if(stn.getName().equals(initTrainer.getSymbol())){
                        newScope = stn.getSubScope();
                    }
                }
                for(TreeNode statement : ast.getSons().get(1).getSons()){
                    thirdTraverse(statement,newScope);
                }
                //Check if function has return statement
                if(ast.getSons().size() > 3){
                    for(TreeNode param : ast.getSons().get(2).getSons()){
                        if(!SemanticCheck.varAlreadyDeclared(param,newScope)){
                            //System.out.println(newScope.getScope() + ":" + param.getLine() + " - Error semántico. Variable \"" + param.getSymbol() + "\" no declarada.");
                            ErrorHandler.throwError(ErrorList.UndeclaredPokemonError, param.getLine(), "Error at scope " + st.getScope() + ": Pokemon undeclared is \"" + param.getSymbol() + "\".");

                        } else {
                            for(Symbol s : st.getSymbols()){
                                if(s.getName().equals(newScope.getScope())){
                                    s.addReturnParam(param);
                                }
                            }
                        }
                    }
                }
            }

            return;
        }
        if(ast.getSymbol().equals("operation")){
            TreeNode pokemonF = ast.getSons().getFirst();
            TreeNode pokemonS = ast.getSons().get(1);
            TreeNode pokemonT = ast.getSons().getLast();
            TreeNode operand = ast.getSons().get(2);
            SemanticCheck.checkOperationTypes(pokemonF, pokemonS, pokemonT, operand, st);
            return;
        }
        if(ast.getSymbol().equals("modif")){
            TreeNode pokemon = ast.getSons().getFirst();
            TreeNode operand = ast.getSons().get(1);
            TreeNode amount = ast.getSons().getLast();
            SemanticCheck.checkModifOper(pokemon, operand, amount, st);
            return;
        }
        if(ast.getSymbol().equals("varDecl")){
            TreeNode pokemonName = ast.getSons().getFirst();
            if(SemanticCheck.checkVarAvailable(pokemonName,st)){
                //System.out.println("Error semántico. Variable \"" + pokemonName.getSymbol() + "\" previamente declarada.\n");
                //Error
            } else {
                Symbol newVar = new Symbol(true, pokemonName.getSymbol(), "var",st);
                st.addSymbol(newVar);
            }
            return;
        }
        if(ast.getSymbol().equals("LiteralAssignation")){

            return;
        }
        if(ast.getSymbol().equals("BooleanAssignation")){
            TreeNode pokemon = ast.getSons().getFirst();
            TreeNode operand = ast.getSons().getLast();
            SemanticCheck.checkBoolAsig(pokemon, operand, st);
            return;
        }
        if(ast.getSymbol().equals("NumericAssignation")){
            TreeNode pokemon = ast.getSons().getFirst();
            TreeNode value = ast.getSons().getLast();
            SemanticCheck.checkNumericalAsign(pokemon, value, st);
            return;
        }
        if(ast.getSymbol().equals("llamarFuncion")){
            TreeNode caller = ast.getSons().getFirst();
            TreeNode called = ast.getSons().get(1);
            TreeNode params = ast.getSons().getLast();
            SemanticCheck.checkCallFunction(caller, called, params, st);
            // Check if its recursive call
            if(called.getSymbol().equals(caller.getSymbol())){
                // Roll back to check which params will be returned so they won't be undeclared.
                TreeNode aux = ast.getDad();
                while(true){
                    if(aux.getSymbol().equals("declararFuncion")){
                        break;
                    }
                    aux = aux.getDad();
                }
                // Check if function returns something
                LinkedList<TreeNode> sons = aux.getSons();
                int i = 0;
                boolean found = false;
                for (; i < sons.size(); i++) {
                    TreeNode node = sons.get(i);
                    if (node.getSymbol().equals("returned")) {
                        found = true;
                        break;
                    }
                }
                if(found){
                    TreeNode paramsCopy = new TreeNode(params);
                    TreeNode returnParams = sons.get(i);
                    LinkedList<TreeNode> paramsSons = params.getSons();
                    for (int j = 0; j < paramsSons.size(); j++) {
                        TreeNode n = paramsSons.get(j);
                        for (TreeNode m : returnParams.getSons()) {
                            if (n.getSymbol().equals(m.getSymbol())) {
                                paramsCopy.getSons().remove(j);
                            }
                        }
                    }
                    undeclareVars(paramsCopy,st,called);
                } else {
                    undeclareVars(params,st,called);
                }

            } else {
                undeclareVars(params,st,called);
            }
            return;
        }
        if(ast.getSymbol().equals("W")){
            TreeNode pokemonF;
            TreeNode pokemonS;
            switch (ast.getSons().get(1).getSymbol()){
                case "condition:numeric":
                    pokemonF = ast.getSons().get(1).getSons().getFirst();
                    TreeNode operand = ast.getSons().get(1).getSons().get(1);
                    pokemonS = ast.getSons().get(1).getSons().getLast();
                    SemanticCheck.checkComparation(pokemonF, operand, pokemonS, st, "INTEGER");
                    break;
                case "condition:boolean":
                    TreeNode pokemon = ast.getSons().get(1).getSons().getFirst();
                    TreeNode condition = ast.getSons().get(1).getSons().getLast();
                    SemanticCheck.checkBooleanCheck(pokemon, condition, st);
                    break;
                case "condition:literal":
                    pokemonF = ast.getSons().get(1).getSons().getFirst();
                    TreeNode comp = null;
                    pokemonS = ast.getSons().get(1).getSons().getLast();
                    //SemanticCheck.checkComparation(pokemonF, comp, pokemonS, st, "LITERAL");
                    ErrorHandler.throwError(ErrorList.CompBetweenLiterals, pokemonF.getLine(), "Tried to compare " + pokemonF.getSymbol() + " with " + pokemonS.getSymbol() + ".");
                    break;
            }
            Symbol newWhile = new Symbol(true, ast.getSons().getFirst().getSymbol(), "loop",st);
            // Check if the "while" has code inside.
            if(ast.getSons().get(2).getSymbol().equals("codeW")){
                thirdTraverse(ast.getSons().get(2),newWhile.getSubScope());
            }
            st.addSymbol(newWhile);
            return;
        }

        if(ast.getSymbol().equals("IE")){
            TreeNode pokemonF;
            TreeNode pokemonS;
            switch (ast.getSons().get(0).getSymbol()){
                case "condition:numeric":
                    pokemonF = ast.getSons().get(0).getSons().getFirst();
                    TreeNode operand = ast.getSons().get(0).getSons().get(1);
                    pokemonS = ast.getSons().get(0).getSons().getLast();
                    SemanticCheck.checkComparation(pokemonF, operand, pokemonS, st, "INTEGER");
                    break;
                case "condition:boolean":
                    TreeNode pokemon = ast.getSons().get(0).getSons().getFirst();
                    TreeNode condition = ast.getSons().get(0).getSons().getLast();
                    SemanticCheck.checkBooleanCheck(pokemon, condition, st);
                    break;
                case "condition:literal":
                    pokemonF = ast.getSons().get(0).getSons().getFirst();
                    TreeNode comp = ast.getSons().get(0).getSons().get(1);
                    pokemonS = ast.getSons().get(0).getSons().getLast();
                    SemanticCheck.checkComparation(pokemonF, comp, pokemonS, st, "LITERAL");
                    break;
            }
            Symbol newIf = new Symbol(true, "IE", "if",st);
            if(!ast.getSons().get(1).getSons().get(0).getSons().isEmpty()){
                // True case has code.
                Symbol newIfTrueCode = new Symbol(true, "IETrueCode", "ifCode", newIf.getSubScope());
                thirdTraverse(ast.getSons().get(1).getSons().get(0),newIfTrueCode.getSubScope());
                newIf.getSubScope().addSymbol(newIfTrueCode);
            }
            if(ast.getSons().get(1).getSons().size() > 1){
                // Has "else" condition
                if(!ast.getSons().get(1).getSons().get(1).getSons().isEmpty()){
                    // Else case has code
                    Symbol newIfElseCode = new Symbol(true,"IEElseCode", "ifCode", newIf.getSubScope());
                    thirdTraverse(ast.getSons().get(1).getSons().get(1),newIfElseCode.getSubScope());
                    newIf.getSubScope().addSymbol(newIfElseCode);
                }
            }
            st.addSymbol(newIf);
            return;
        }

        for(TreeNode node : ast.getSons()){
            thirdTraverse(node,st);
        }
    }

    private static void undeclareVars(TreeNode params, SymbolTableNode st, TreeNode called) {
        LinkedList<TreeNode> returnedParams = getReturnedParams(called, st);
        if(returnedParams == null){
            //System.out.println("Fatal error occurred trying to retrieve returned parameters from \"" + called.getSymbol() + "\" function.");
            ErrorHandler.throwError(ErrorList.CompBetweenLiterals, called.getLine(), "Error at scope " + st.getScope() + ": Function is \"" + called.getSymbol() + "\".");

            return;
        }
        for(TreeNode param: params.getSons()){
            boolean match = false;
            for(TreeNode retParam: returnedParams){
                if(retParam.getSymbol().equals(param.getSymbol())){
                    match = true;
                }
            }
            if(!match) st.addSymbol(new Symbol(false,param.getSymbol(),"var",st));
        }
        for(TreeNode retParam: returnedParams){
            if(!varAlreadyDeclared(retParam,st)){
                st.addSymbol(new Symbol(true, retParam.getSymbol(), "var", st));
            }

        }
    }

    private static LinkedList<TreeNode> getReturnedParams(TreeNode called, SymbolTableNode st) {
        // Search the scope being called to check the returned params.
        // First go back untill "Global" scope is reached.
        while (!st.getScope().equals("Global")){
            st = st.getParent();
        }
        for(Symbol function: st.getSymbols()){
            if(function.getName().equals(called.getSymbol())){
                return function.getReturnParams();
            }
        }
        return null;
    }
}
