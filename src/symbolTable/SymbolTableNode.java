package symbolTable;

import com.sun.source.tree.Tree;
import errorHandler.ErrorHandler;
import errorHandler.ErrorList;
import parser.TreeNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SymbolTableNode {
    private LinkedList<Symbol> symbols;
    private String scope;
    private SymbolTableNode parent;

    public SymbolTableNode() {
        symbols = new LinkedList<Symbol>();
        scope = "Global";
        this.parent = null;
    }

    public SymbolTableNode(String name, SymbolTableNode parent) {
        symbols = new LinkedList<Symbol>();
        this.scope = name;
        this.parent = parent;
    }

    public static JSONObject toJson(SymbolTableNode st) {
        JSONObject json = new JSONObject();
        List<JSONObject> children = new ArrayList<>();
        for(Symbol s : st.getSymbols()){
            if(s.getSubScope() != null){
                JSONObject a = toJson(s.getSubScope());
                a.put("name",s.getName());
                a.put("type",s.getType());
                children.add(a);
            } else {
                JSONObject b = new JSONObject();
                b.put("name",s.getName());
                b.put("type",s.getType());
                b.put("declared",s.isDeclared());
                children.add(b);
            }
        }
        json.put("sons",children);
        return json;
    }

    public static String prettyJson(JSONObject json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json.toString());
        return gson.toJson(je);
    }

    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
    }

    public LinkedList<Symbol> getSymbols() {
        return symbols;
    }

    public String getScope() {
        if(Dictionary.isFunction(this.scope) || this.scope.equals("Global")) return scope;
        return parent.getScope();
    }

    public SymbolTableNode getParent() {
        return parent;
    }

    public String getFunctionScope() {
        if (getParent().getScope().equals("Global")) {
            return getScope();
        }

        return getParent().getFunctionScope();
    }

    public void addSymbolToScope(TreeNode param, TreeNode scopeTarget) {
        String contextFunction = "";
        TreeNode aux = param;
        while(true){
            if(aux.getSymbol().equals("declararFuncion")){
                contextFunction = aux.getSons().getFirst().getSymbol();
                break;
            }
            aux = aux.getDad();
        }
        if(contextFunction.equals(scopeTarget.getSymbol())){
            return;
        }
        if(this.getParent() != null){
            this.getParent().addSymbolToScope(param, scopeTarget.getDad());
            return;
        }
        // Global scope reached
        for(Symbol scope : this.getSymbols()){
            if(scope.getName().equals(scopeTarget.getSymbol())){
                // Target scope
                if(!SemanticCheck.checkVarAvailable(param,scope.getSubScope())){
                    scope.getSubScope().addSymbol(new Symbol(true, param.getSymbol(),"var", this));
                } else {
                    //System.out.println("Este error puede deberse a que la función \"" + scopeTarget.getSymbol() + "\" está recibiendo dos veces el mismo parámetro.");
                    ErrorHandler.throwError(ErrorList.SameParameterTwiceError, scopeTarget.getLine(), "Error at scope " + scope.getScope() + ": Function is \"" + scopeTarget.getSymbol() + "\". Pokemon is \"" + param.getSymbol() + "\".");
                }
            }
        }
    }

    //Constructor
/*    public SymbolTable() {
        symbolTable = new HashMap<>();
    }

    public void insert(String name, String type){
        this.st.add(name, type);
    }

    public void remove(String name){
        this.st.remove(name);
    }

    public String search(String name){
        return this.st.get(name);
    }*/
}
