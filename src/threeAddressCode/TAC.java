package threeAddressCode;

import parser.TreeNode;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class TAC {
    //hashtable<string,TACNode>
    private static HashMap<String,BasicBlock> bbDictionary = new HashMap<>();
    private static final AtomicInteger labelCount = new AtomicInteger(0);
    private static final AtomicInteger temporalCount = new AtomicInteger(0);

    public static BasicBlock generateQuadruplesList(BasicBlock bb, TreeNode ast) {
        if(ast.getSymbol().equals("declararFuncion")){
            if(ast.getSons().get(0).getSymbol().equals("Ash")){
                bb.setLabel(new String("main"));
                bbDictionary.put(bb.getLabel(),bb);
                // Two options: function has code or not
                if(ast.getSons().get(1).getSymbol().equals("code")){
                    // Has code
                    generateQuadruplesList(bb,(ast.getSons().get(1)));
                }
            } else {
                bb.setLabel(ast.getSons().get(0).getSymbol());
                bbDictionary.put(bb.getLabel(),bb);
                if(ast.getSons().getLast().getSymbol().equals("params")){
                    int i = 0;
                    for(TreeNode param : ast.getSons().getLast().getSons()){
                        Quadruple q = new Quadruple(null,param.getSymbol(),String.valueOf(i++),"param");
                        q.setScope("param");
                        bb.addQuad(q);
                    }
                }
                if(ast.getSons().get(1).getSymbol().equals("code")){
                    // Has code
                    generateQuadruplesList(bb,(ast.getSons().get(1)));
                }
                if(ast.getSons().size() > 2 && ast.getSons().get(2).getSymbol().equals("returned")){
                    int i = 0;
                    BasicBlock returnParamsBB = new BasicBlock();
                    returnParamsBB.setDad(bb);
                    for(TreeNode returnedParam : ast.getSons().get(2).getSons()){
                        Quadruple q = new Quadruple(null,returnedParam.getSymbol(),String.valueOf(i++),"returnParam");
                        q.setScope("returnParam");
                        returnParamsBB.addQuad(q);
                    }
                    bb.addSon(returnParamsBB);
                }
            }
            return null;
        }
        if(ast.getSymbol().equals("varDecl")){
            Quadruple q = new Quadruple(null,ast.getSons().getFirst().getSymbol(),null,null);
            q.setScope("varDecl");
            bb.addQuad(q);
            return null;
        }
        if(ast.getSymbol().equals("operation")){
            Quadruple q = new Quadruple(ast.getSons().get(0).getSymbol(),ast.getSons().get(1).getSymbol(),ast.getSons().get(3).getSymbol(),ast.getSons().get(2).getSymbol());
            q.setScope("operation");
            bb.addQuad(q);
            return null;
        }
        //modif
        if(ast.getSymbol().equals("modif")){
            Quadruple q;
            if(ast.getSons().get(1).getSymbol().equals("healed")) {
                q = new Quadruple(ast.getSons().get(0).getSymbol(),
                        ast.getSons().get(0).getSymbol(),
                        ast.getSons().get(2).getSymbol(),
                        "increase");
            }
            else {
                q = new Quadruple(ast.getSons().get(0).getSymbol(),
                        ast.getSons().get(0).getSymbol(),
                        ast.getSons().get(2).getSymbol(),
                        "decrease");
            }
            q.setScope("operation");
            bb.addQuad(q);
            return null;
        }

        if(ast.getSymbol().equals("NumericAssignation")){
            Quadruple q = new Quadruple(ast.getSons().get(0).getSymbol(),ast.getSons().get(1).getSymbol(),null,"=");
            q.setScope("NumericAssignation");
            bb.addQuad(q);
            return null;
        }
        //BooleanAssignation
        if(ast.getSymbol().equals("BooleanAssignation")){
            Quadruple q = new Quadruple(ast.getSons().get(0).getSymbol(),ast.getSons().get(1).getSymbol(),null,"=");
            q.setScope("BooleanAssignation");
            bb.addQuad(q);
        }
        if(ast.getSymbol().equals("llamarFuncion")){
            TreeNode f = getFunctionToCall(ast.getSons().get(1).getSymbol(),ast);
            TreeNode params = new TreeNode("params");
            if(ast.getSons().size()>2){
                // Function call has params
                for(TreeNode param: ast.getSons().get(2).getSons()){
                    params.addSon(param);
                }
            }
            int paramIndex = 0;
            for(TreeNode param : params.getSons()){
                Quadruple p = new Quadruple(null,param.getSymbol(),String.valueOf(paramIndex++),"pushparam");
                p.setScope("pushparam");
                bb.addQuad(p);
            }

            assert f != null;
            f.addSon(params);
            Quadruple fase1 = new Quadruple(null,null,null,"allocate");
            fase1.setScope("allocate");
            bb.addQuad(fase1);


            Quadruple call = new Quadruple(null,f.getSons().getFirst().getSymbol(),null,"call");
            call.setScope("call");
            bb.addQuad(call);
            f.setCalledFunc(true);

            if(f.getSons().size() > 2 && f.getSons().get(2).getSymbol().equals("returned")){
                paramIndex = 0;
                for(TreeNode returnedParam : f.getSons().get(2).getSons()){
                    Quadruple p = new Quadruple(null,returnedParam.getSymbol(),String.valueOf(paramIndex++),"pullReturnedParam");
                    p.setScope("pullReturnedParam");
                    bb.addQuad(p);
                }
            }

            Quadruple realloc = new Quadruple(null,null,null,"reallocate");
            realloc.setScope("reallocate");
            bb.addQuad(realloc);
            return null;
        }
        //W
        if(ast.getSymbol().equals("W")){
            BasicBlock whileDecl = new BasicBlock();
            if(bb.getEndGoto()!=null){
                whileDecl.setEndGoto(bb.getEndGoto());
                bb.setEndGoto(null);
            }
            String whileLabel = new String("L" + labelCount.getAndIncrement());
            whileDecl.setLabel(whileLabel);
            whileDecl.setDad(bb);
            bb.addSon(whileDecl);

            String labelAfterW = new String("L" + labelCount.getAndIncrement());
            Quadruple q = null;
            if(ast.getSons().get(1).getSymbol().equals("condition:numeric")){
                q = new Quadruple(labelAfterW,ast.getSons().get(1).getSons().get(0).getSymbol(),ast.getSons().get(1).getSons().get(2).getSymbol(),ast.getSons().get(1).getSons().get(1).getSymbol());
            }
            if(ast.getSons().get(1).getSymbol().equals("condition:boolean")){
                q = new Quadruple(labelAfterW,ast.getSons().get(1).getSons().get(0).getSymbol(),null,ast.getSons().get(1).getSons().get(1).getSymbol());
            }

            q.setScope("W");
            whileDecl.addQuad(q);

            //BB after while
            BasicBlock bbAfterW = new BasicBlock();
            if(whileDecl.getEndGoto()!=null){
                bbAfterW.setEndGoto(whileDecl.getEndGoto());
                whileDecl.setEndGoto(null);
            }
            bbAfterW.setLabel(labelAfterW);
            bbAfterW.setDad(whileDecl);

            //BB while code
            BasicBlock bbWhileCode = new BasicBlock();
            bbWhileCode.setEndGoto(whileLabel);
            if(bb.getEndGoto()!=null){
                bbWhileCode.setEndGoto(bb.getEndGoto());
                bb.setEndGoto(null);
            }
            bbWhileCode.setDad(whileDecl);
            generateQuadruplesList(bbWhileCode,ast.getSons().get(2));
            whileDecl.addSon(bbWhileCode);


            whileDecl.addSon(bbAfterW);
            return bbAfterW;
        }
        if(ast.getSymbol().equals("IE")){
            String falseLabel = new String("L"+getLabelNumber());
            reverseCondition(ast.getSons().get(0));
            String afterIELabel = new String("L"+getLabelNumber());
            Quadruple ifDecl = null;
            if(ast.getSons().get(0).getSymbol().equals("condition:numeric")){
                if(ast.getSons().get(1).getSons().size()>1){
                    ifDecl = new Quadruple(falseLabel,ast.getSons().get(0).getSons().get(0).getSymbol(),ast.getSons().get(0).getSons().get(2).getSymbol(),ast.getSons().get(0).getSons().get(1).getSymbol());
                } else {
                    ifDecl = new Quadruple(afterIELabel,ast.getSons().get(0).getSons().get(0).getSymbol(),ast.getSons().get(0).getSons().get(2).getSymbol(),ast.getSons().get(0).getSons().get(1).getSymbol());
                }
            }
            if(ast.getSons().get(0).getSymbol().equals("condition:boolean")){
                if(ast.getSons().get(1).getSons().size()>1){
                    ifDecl = new Quadruple(falseLabel,ast.getSons().get(0).getSons().get(0).getSymbol(),null,ast.getSons().get(0).getSons().get(1).getSymbol());
                } else {
                    ifDecl = new Quadruple(afterIELabel,ast.getSons().get(0).getSons().get(0).getSymbol(),null,ast.getSons().get(0).getSons().get(1).getSymbol());
                }
            }
            if(ast.getSons().get(0).getSymbol().equals("condition:literal")){
                return null;
            }
            assert ifDecl != null;
            ifDecl.setScope("IE");
            bb.addQuad(ifDecl);

            //BB for code after IE
            BasicBlock bbAfterIE = new BasicBlock();
            bbAfterIE.setLabel(afterIELabel);
            if(bb.getEndGoto()!=null){
                bbAfterIE.setEndGoto(bb.getEndGoto());
                bb.setEndGoto(null);
            }

            //BB "otherwise" code (if there is)

            //BB for true condition code
            BasicBlock bbTrueCode = new BasicBlock();
            bbTrueCode.setDad(bb);
            bbTrueCode.setEndGoto(afterIELabel);
                /*if(bb.getEndGoto()!=null){
                    bbElse.setEndGoto(bb.getEndGoto());
                    bb.setEndGoto(null);
                } else {
                    if(bbElse.getEndGoto()!=null){
                        bbTrueCode.setEndGoto(bbElse.getEndGoto());
                    }
                }*/

            bb.addSon(bbTrueCode);

            BasicBlock bbElse = new BasicBlock();
            if(ast.getSons().get(1).getSons().size()>1){
                //Has "otherwise" condition
                    /*if(bb.getEndGoto()!=null){
                        bbElse.setEndGoto(bb.getEndGoto());
                        bb.setEndGoto(null);
                    }*/
                bbElse.setLabel(falseLabel);
                bbElse.setDad(bb);
                bb.addSon(bbElse);
                generateQuadruplesList(bbElse,ast.getSons().get(1).getSons().get(1));
            }
            bb.addSon(bbAfterIE);
            generateQuadruplesList(bbTrueCode,ast.getSons().get(1).getSons().get(0));

            return bbAfterIE;
        }
        for(TreeNode s : ast.getSons()){
            // Optimization: unnecessary statements are ignored.
            /*if(s.getSymbol().equals("varDecl")){
                continue;
            }*/
            BasicBlock newBB = generateQuadruplesList(bb,s);
            if(newBB != null){
                bb = newBB;
            }

            // Optimization: only the main function is checked, functions are created as they are called, if a function is declared but not called
            //it is not converted to TAC.
            if(s.getSymbol().equals("declararFuncion")){
                return null;
            }
        }
        return null;
    }

    private static void reverseCondition(TreeNode treeNode) {
        switch (treeNode.getSymbol()){
            case "condition:numeric":
                switch (treeNode.getSons().get(1).getSymbol()){
                    case "stronger":
                        treeNode.getSons().get(1).setSymbol("weaker-e");
                        break;
                    case "weaker":
                        treeNode.getSons().get(1).setSymbol("stronger-e");
                        break;
                    case "equal":
                        treeNode.getSons().get(1).setSymbol("nEqual");
                        break;
                }
                break;
            case "condition:boolean":
                switch (treeNode.getSons().get(1).getSymbol()) {
                    case "visible":
                        treeNode.getSons().get(1).setSymbol("invisible");
                        break;
                    case "invisible":
                        treeNode.getSons().get(1).setSymbol("visible");
                        break;
                }
                break;
        }
    }

    private static TreeNode getFunctionToCall(String symbol, TreeNode ast) {
        while (true){
            if(ast.getSymbol().equals("program")){
                break;
            }
            ast = ast.getDad();
        }
        for(TreeNode t: ast.getSons()){
            if(t.getSons().get(0).getSymbol().equals(symbol)){
                return t;
            }
        }
        return null;

    }

    private static int getLabelNumber(){
        return labelCount.getAndIncrement();
    }
    private static int getTemporalNumber(){
        return temporalCount.getAndIncrement();
    }
}
