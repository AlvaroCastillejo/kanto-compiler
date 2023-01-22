package parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TreeNode {
    private LinkedList<TreeNode> sons;
    private TreeNode dad;
    private String symbol;
    private int line;
    private boolean calledFunc;

    public TreeNode() {
        sons = new LinkedList<>();
        this.symbol = "S";
    }

    public TreeNode(String symbol) {
        sons = new LinkedList<>();
        this.symbol = symbol;
        calledFunc = false;
    }

    public TreeNode(String symbol, int line) {
        sons = new LinkedList<>();
        this.symbol = symbol;
        this.line = line;
        calledFunc = false;
    }

    public TreeNode(TreeNode params) {
            sons = new LinkedList<>();
            for(TreeNode son : params.getSons()){
                sons.add(new TreeNode(son));
            }

            dad = (TreeNode) params.dad;
            symbol = params.symbol;
            line = params.line;
            calledFunc = params.calledFunc;

    }

    public static JSONObject toJson(TreeNode root) {
        JSONObject json = new JSONObject();
        json.put("symbol", root.symbol);

        List<JSONObject> children = new ArrayList<>();
        for(TreeNode subnode : root.sons) {
            children.add(toJson(subnode));
        }

        json.put("sons", children);
        return json;
    }

    public static String prettyJson(JSONObject json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json.toString());
        return gson.toJson(je);
    }

    public void addSons(List<String> production) {
        for(String prod : production){
            sons.add(new TreeNode(prod));
        }
    }

    public void setSons(LinkedList<TreeNode> sons) {
        this.sons = sons;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void addSon(TreeNode newSon) {
        sons.add(newSon);
    }

    public String getSymbol() {
        return symbol;
    }

    public LinkedList<TreeNode> getSons() {
        return sons;
    }

    public TreeNode getDad() {
        return dad;
    }

    public void setDad(TreeNode dad) {
        this.dad = dad;
    }

    public int getLine() {
        return line;
    }

    public boolean isLeaf(){
        if(sons == null || sons.isEmpty()) {
            return true;
        }
        return false;
    }


    public boolean isCalledFunc() {
        return calledFunc;
    }

    public void setCalledFunc(boolean calledFunc) {
        this.calledFunc = calledFunc;
    }
}



