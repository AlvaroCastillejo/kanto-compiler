package symbolTable;

import parser.TreeNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class AST {
    /**
     * Gets the Abstract Syntax Tree from a given Parser Tree. It works by traversing the Parse Tree looking for
     * those symbols that need semantic analysis, for instance a function declaration or an arithmetic operation.
     * However, there are some things that don't need this analysis, so they are not included in this tree. For instance
     * the IF declaration, as the grammar is very strict in this kind of specifications and the error would be thrown in
     * the syntactic analysis. TODO: The IF needs semantic validation too, so it needs to be added in the AST.
     *
     * @param ast The root node to build the Abstract Syntax Tree on.
     * @param pt The Parse Tree from which extract the Abstract Syntax Tree.
     *
     */
    public static void getAST(TreeNode ast, TreeNode pt){
        if(pt.getSymbol().equals("declararFuncion") ||  pt.getSymbol().equals("AshFunction")){

            TreeNode declFuncNode = new TreeNode("declararFuncion",pt.getLine());
            TreeNode initTrainer = (new TreeNode(pt.getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine()));
            TreeNode endTrainer = null;
            TreeNode returned = null;
            // Two options: has return statement or not.
            if(pt.getSons().get(1).getSons().get(1).getSons().get(0).getSymbol().equals("returnProd")){
                // Return statement
                returned = new TreeNode("returned",pt.getLine());
                getParams(returned,pt.getSons().get(1).getSons().get(1).getSons().get(1).getSons().get(0));
                returned.setDad(declFuncNode);
                endTrainer = new TreeNode(pt.getSons().get(1).getSons().get(1).getSons().get(1).getSons().get(1).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            } else {
                endTrainer = (new TreeNode(pt.getSons().get(1).getSons().get(1).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine()));
            }
            initTrainer.setDad(declFuncNode);
            endTrainer.setDad(declFuncNode);

            //Semantic check
            /*if(SemanticCheck.checkFunctionDeclaration(initTrainer,endTrainer,st)){
                //semantic error
                //return;
            }*/

            declFuncNode.addSon(initTrainer);
            // Fill the node "code" with the code inside the function.
            TreeNode codeSon = new TreeNode("code",pt.getLine());
            for(TreeNode node : pt.getSons().get(1).getSons().get(0).getSons()){
                getAST(codeSon,node);
            }
            if(!codeSon.getSons().isEmpty()){
                codeSon.setDad(declFuncNode);
                declFuncNode.addSon(codeSon);
            }
            if(returned != null) declFuncNode.addSon(returned);
            declFuncNode.addSon(endTrainer);
            declFuncNode.setDad(ast);
            ast.addSon(declFuncNode);

            return;
        }
        if(pt.getSymbol().equals("operation")){
            TreeNode operationNode = new TreeNode("operation",pt.getLine());
            // pokemonF = pokemonS operand pokemonT
            TreeNode pokemonF = new TreeNode(pt.getDad().getDad().getDad().getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            TreeNode pokemonS = new TreeNode(pt.getSons().get(1).getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            TreeNode operand = new TreeNode(pt.getSons().get(1).getSons().get(1).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            TreeNode pokemonT = new TreeNode(pt.getSons().get(1).getSons().get(1).getSons().get(1).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            pokemonF.setDad(operationNode);
            pokemonS.setDad(operationNode);
            operand.setDad(operationNode);
            pokemonT.setDad(operationNode);
            operationNode.setSons(new LinkedList<TreeNode>(Arrays.asList(pokemonF,pokemonS,operand,pokemonT)));
            operationNode.setDad(ast);
            ast.addSon(operationNode);
        }
        if(pt.getSymbol().equals("modif")){
            TreeNode modifNode = new TreeNode("modif",pt.getLine());
            TreeNode pokemon = new TreeNode(pt.getDad().getDad().getDad().getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            TreeNode operand = new TreeNode(pt.getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            TreeNode amount = null;
            if(pt.getSons().get(1).getSons().get(0).getSons().get(0).getSymbol().equals("PokemonVar")){
                amount = new TreeNode(pt.getSons().get(1).getSons().get(0).getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            } else {
                amount = new TreeNode(pt.getSons().get(1).getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            }
            pokemon.setDad(modifNode);
            operand.setDad(modifNode);
            amount.setDad(modifNode);
            modifNode.setSons(new LinkedList<TreeNode>(Arrays.asList(pokemon,operand,amount)));
            modifNode.setDad(ast);
            ast.addSon(modifNode);

            //SemanticCheck.checkModifOper(pokemon, operand, amount, st);
        }
        if(pt.getSymbol().equals("varDecl")){
            TreeNode pokemonName = new TreeNode(pt.getDad().getDad().getDad().getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());

            /*if(SemanticCheck.checkVarAvailable(pokemonName,st)){
                //System.out.println("Error semántico. Variable \"" + pokemonName.getSymbol() + "\" previamente declarada.\n");
                //Error
            }*/

            TreeNode varDeclNode = new TreeNode("varDecl",pt.getLine());
            pokemonName.setDad(varDeclNode);
            varDeclNode.addSon(pokemonName);
            ast.addSon(varDeclNode);
            /*Symbol newVar = new Symbol(true, pokemonName.getSymbol(), "var",st);
            st.addSymbol(newVar);*/
            return;
        }
        if(pt.getSymbol().equals("LiteralAssignation")){
            TreeNode literalAssignationNode = new TreeNode("LiteralAssignation",pt.getLine());
            TreeNode pokemon = new TreeNode(pt.getDad().getDad().getDad().getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            pokemon.setDad(literalAssignationNode);
            literalAssignationNode.addSon(pokemon);
            literalAssignationNode.setDad(ast);
            ast.addSon(literalAssignationNode);
            return;
        }
        if(pt.getSymbol().equals("BooleanAssignation")){
            TreeNode booleanAssignation = new TreeNode("BooleanAssignation",pt.getLine());
            TreeNode pokemon = new TreeNode(pt.getDad().getDad().getDad().getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            TreeNode operand = new TreeNode(pt.getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());

            pokemon.setDad(booleanAssignation);
            operand.setDad(booleanAssignation);
            booleanAssignation.setSons(new LinkedList<TreeNode>(Arrays.asList(pokemon,operand)));
            booleanAssignation.setDad(ast);
            ast.addSon(booleanAssignation);

            //SemanticCheck.checkBoolAsig(pokemon, operand, st);
            return;
        }
        if(pt.getSymbol().equals("NumericAssignation")){
            TreeNode numericAssignation = new TreeNode("NumericAssignation",pt.getLine());
            TreeNode pokemon = new TreeNode(pt.getDad().getDad().getDad().getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            TreeNode value;
            // Two options, integer or pokemon:
            // Charizard = 10
            // Charizard = Charmeleon
            // For the AST: ignoring the "=".
            if(pt.getSons().get(1).getSons().get(0).getSons().get(0).getSymbol().equals("PokemonVar")){
                //pokemon value
                value = new TreeNode(pt.getSons().get(1).getSons().get(0).getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            } else {
                value = new TreeNode(pt.getSons().get(1).getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            }
            pokemon.setDad(numericAssignation);
            value.setDad(numericAssignation);
            numericAssignation.setSons(new LinkedList<TreeNode>(Arrays.asList(pokemon,value)));
            numericAssignation.setDad(ast);
            ast.addSon(numericAssignation);

            /*SemanticCheck.checkNumericalAsign(pokemon, value, st);*/
            return;
        }
        if(pt.getSymbol().equals("llamarFuncion")){
            TreeNode llamarFuncion = new TreeNode("llamarFuncion",pt.getLine());
            TreeNode caller;
            // Caller can be Ash or a trainer, different productions = different paths.
            if(pt.getSons().get(1).getSons().get(0).getSons().get(0).getSymbol().equals("entrenador")){
                caller = new TreeNode(pt.getSons().get(1).getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            } else {
                caller = new TreeNode(pt.getSons().get(1).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            }
            TreeNode called = new TreeNode(pt.getSons().get(1).getSons().get(1).getSons().get(1).getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            caller.setDad(llamarFuncion);
            called.setDad(llamarFuncion);
            llamarFuncion.addSon(caller);
            llamarFuncion.addSon(called);
            TreeNode params = null;
            // Two options, with or without parameters.
            if(pt.getSons().get(1).getSons().get(1).getSons().get(1).getSons().get(1).getSons().get(0).getSymbol().equals("chosingProd")){
                // With parameters
                params = new TreeNode("params",pt.getLine());
                getParams(params,pt.getSons().get(1).getSons().get(1).getSons().get(1).getSons().get(1).getSons().get(1));
                params.setDad(llamarFuncion);
                llamarFuncion.addSon(params);
            }
            llamarFuncion.setDad(ast);
            ast.addSon(llamarFuncion);

            /*SemanticCheck.checkCallFunction(caller, called, params, st);*/
        }
        if(pt.getSymbol().equals("W")){
            // The while creates a new scope. It has minimum 3 sons and probably 4: init, condition, code, end
            // Four search blocks.
            TreeNode wNode = new TreeNode("W",pt.getLine());

            TreeNode initW = new TreeNode(pt.getSons().get(0).getSons().get(1).getSons().get(1).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            TreeNode conditionW = new TreeNode();{
                // The condition is a comparation. Can be three tipes: numericComparation, booleanCheck, literalComparation
                TreeNode comparacion = pt.getSons().get(1).getSons().get(0).getSons().get(1).getSons().get(1).getSons().get(1).getSons().get(0).getSons().get(0);
                switch (comparacion.getSymbol()) {
                    case "numericComparation" -> {
                        conditionW.setSymbol("condition:numeric");
                        TreeNode pokemonF = new TreeNode(comparacion.getDad().getSons().get(0).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
                        TreeNode operand = new TreeNode(comparacion.getDad().getSons().get(0).getSons().get(1).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
                        TreeNode pokemonS = new TreeNode(comparacion.getDad().getSons().get(0).getSons().get(1).getSons().get(1).getSons().get(0).getSymbol(),pt.getLine());
                        pokemonF.setDad(conditionW);
                        operand.setDad(conditionW);
                        pokemonS.setDad(conditionW);
                        conditionW.setSons(new LinkedList<TreeNode>(Arrays.asList(pokemonF, operand, pokemonS)));

                        /*SemanticCheck.checkComparation(pokemonF, operand, pokemonS, st, "INTEGER");*/
                    }
                    case "booleanCheck" -> {
                        conditionW.setSymbol("condition:boolean");
                        TreeNode pokemon = new TreeNode(comparacion.getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
                        TreeNode condition = new TreeNode(comparacion.getSons().get(1).getSons().get(0).getSymbol(),pt.getLine());
                        pokemon.setDad(conditionW);
                        condition.setDad(conditionW);
                        conditionW.setSons(new LinkedList<TreeNode>(Arrays.asList(pokemon, condition)));

                        /*SemanticCheck.checkBooleanCheck(pokemon, condition, st);*/
                    }
                    case "literalComparation" -> {
                        conditionW.setSymbol("condition:literal");
                        TreeNode pokemonF = new TreeNode(comparacion.getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
                        TreeNode comp = new TreeNode(comparacion.getSons().get(1).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
                        TreeNode pokemonS = new TreeNode(comparacion.getSons().get(1).getSons().get(1).getSons().get(0).getSymbol(),pt.getLine());
                        pokemonF.setDad(conditionW);
                        pokemonS.setDad(conditionW);
                        conditionW.setSons(new LinkedList<TreeNode>(Arrays.asList(pokemonF, pokemonS)));

                        /*SemanticCheck.checkComparation(pokemonF, comp, pokemonS, st, "LITERAL");*/
                    }
                }
            }

            TreeNode codeW = new TreeNode("codeW",pt.getLine());{
                getAST(codeW,pt.getSons().get(1).getSons().get(1).getSons().get(0));
            }
            TreeNode endW = new TreeNode(pt.getSons().get(1).getSons().get(1).getSons().get(1).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());

            initW.setDad(wNode);
            conditionW.setDad(wNode);
            codeW.setDad(wNode);
            endW.setDad(wNode);
            wNode.setSons(new LinkedList<TreeNode>(Arrays.asList(initW,conditionW,codeW,endW)));
            wNode.setDad(ast);
            ast.addSon(wNode);

            return;
        }
        if(pt.getSymbol().equals("IE")){
            // The while creates a new scope. It has minimum 3 sons and probably 4: init, condition, code, end
            // Four search blocks.
            TreeNode ieNode = new TreeNode("IE",pt.getLine());

            TreeNode conditionIE = new TreeNode();{
                // The condition is a comparation. Can be three tipes: numericComparation, booleanCheck, literalComparation
                TreeNode comparacion = pt.getSons().get(1).getSons().get(0).getSons().get(0).getSons().get(0);
                switch (comparacion.getSymbol()) {
                    case "numericComparation" -> {
                        conditionIE.setSymbol("condition:numeric");
                        TreeNode pokemonF = new TreeNode(comparacion.getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
                        TreeNode operand = new TreeNode(comparacion.getSons().get(1).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
                        TreeNode pokemonS = new TreeNode(comparacion.getSons().get(1).getSons().get(1).getSons().get(0).getSymbol(),pt.getLine());
                        pokemonF.setDad(conditionIE);
                        operand.setDad(conditionIE);
                        pokemonS.setDad(conditionIE);
                        conditionIE.setSons(new LinkedList<TreeNode>(Arrays.asList(pokemonF, operand, pokemonS)));

                        /*SemanticCheck.checkComparation(pokemonF, operand, pokemonS, st, "INTEGER");*/
                    }
                    case "booleanCheck" -> {
                        conditionIE.setSymbol("condition:boolean");
                        TreeNode pokemon = new TreeNode(comparacion.getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
                        TreeNode condition = new TreeNode(comparacion.getSons().get(1).getSons().get(0).getSymbol(),pt.getLine());
                        pokemon.setDad(conditionIE);
                        condition.setDad(conditionIE);
                        conditionIE.setSons(new LinkedList<TreeNode>(Arrays.asList(pokemon, condition)));

                        /*SemanticCheck.checkBooleanCheck(pokemon, condition, st);*/
                    }
                    case "literalComparation" -> {
                        conditionIE.setSymbol("condition:literal");
                        TreeNode pokemonF = new TreeNode(comparacion.getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
                        TreeNode comp = new TreeNode(comparacion.getSons().get(1).getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
                        TreeNode pokemonS = new TreeNode(comparacion.getSons().get(1).getSons().get(1).getSons().get(0).getSymbol(),pt.getLine());
                        pokemonF.setDad(conditionIE);
                        comp.setDad(conditionIE);
                        pokemonS.setDad(conditionIE);
                        conditionIE.setSons(new LinkedList<TreeNode>(Arrays.asList(pokemonF, comp, pokemonS)));

                        /*SemanticCheck.checkComparation(pokemonF, comp, pokemonS, st, "LITERAL");*/
                    }
                }
            }

            TreeNode codeIE = new TreeNode("codeIE", pt.getLine());
            TreeNode codeIETrue = new TreeNode("codeIETrue",pt.getLine());{
                getAST(codeIETrue,pt.getSons().get(1).getSons().get(1).getSons().get(0));
            }
            codeIETrue.setDad(codeIE);
            codeIE.addSon(codeIETrue);
            // Two options: has "else" or not.
            if(pt.getSons().get(1).getSons().get(1).getSons().get(1).getSons().get(0).getSymbol().equals("else")){
                TreeNode codeIEFalse = new TreeNode("codeIEFalse", pt.getLine());{
                    getAST(codeIEFalse,pt.getSons().get(1).getSons().get(1).getSons().get(1).getSons().get(0).getSons().get(1).getSons().get(0));
                }
                codeIEFalse.setDad(codeIE);
                codeIE.addSon(codeIEFalse);
            }


            conditionIE.setDad(ieNode);
            codeIE.setDad(ieNode);
            ieNode.setSons(new LinkedList<TreeNode>(Arrays.asList(conditionIE,codeIE)));
            ieNode.setDad(ast);
            ast.addSon(ieNode);

            return;
        }

        for(TreeNode node : pt.getSons()){
            getAST(ast,node);
        }
    }

    // Starting point: chosingProd node
    private static void getParams(TreeNode params, TreeNode pt) {
        if(pt.getSymbol().equals("PokemonVar")){
            TreeNode param = new TreeNode(pt.getSons().get(0).getSons().get(0).getSymbol(),pt.getLine());
            param.setDad(params);
            params.addSon(param);
            return;
        }
        for(TreeNode n : pt.getSons()){
            getParams(params, n);
        }
    }

    public static void reverseFunctions(TreeNode ast){
        Collections.reverse(ast.getSons());
    }

    public static int getCompressionFactor(TreeNode root, TreeNode ast) {
        int ptCount = countNodes(root);
        int astCount = countNodes(ast);

        return astCount*100/ptCount;
    }

    private static int countNodes(TreeNode root) {
        if(root.isLeaf()){
            return 1;
        }
        int i = 0;
        for(TreeNode son : root.getSons()){
            i += countNodes(son);
        }
        return i;
    }
}
