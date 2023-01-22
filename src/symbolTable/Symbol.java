package symbolTable;

import parser.TreeNode;

import java.util.LinkedList;

public class Symbol {
    private boolean declared;
    private String name;
    private String type;
    private SymbolTableNode scope;
    private SymbolTableNode subScope;
    private LinkedList<TreeNode> returnParams;

    public Symbol(boolean declared, String name, String type, SymbolTableNode scope) {
        this.declared = declared;
        this.name = name;
        this.type = type;
        this.scope = scope;
        subScope = null;
        if(type.equals("func") || type.equals("loop") || type.equals("ifCode") || type.equals("if")){
            subScope = new SymbolTableNode(name, scope);
            returnParams = new LinkedList<>();
        }
    }

    public void addReturnParam(TreeNode param){
        returnParams.add(param);
    }

    public LinkedList<TreeNode> getReturnParams() {
        return returnParams;
    }

    public SymbolTableNode getSubScope() {
        return subScope;
    }

    public boolean isDeclared() {
        return declared;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public SymbolTableNode getScope() {
        return scope;
    }
}
