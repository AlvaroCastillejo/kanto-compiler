package threeAddressCode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.JSONObject;
import parser.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicBlock {
    //Si no tiene ningun hijo es el final
    //Si solo tiene un hijo es ese camino y ya
    //Si tiene un if(while/if) tiene los 2 hijos
    private LinkedList<Quadruple> quadruples;
    private BasicBlock dad;
    private LinkedList<BasicBlock> sons;
    private String label;
    private boolean written;
    private String endGoto;

    public BasicBlock() {
        quadruples = new LinkedList<>();
        sons = new LinkedList<>();
        this.written = false;
    }

    public static JSONObject toJson(BasicBlock bb) {
        AtomicInteger qCount = new AtomicInteger(0);

        JSONObject json = new JSONObject();
        json.put("label", bb.getLabel());

        for(Quadruple q : bb.quadruples){
            json.put("Q"+qCount.getAndIncrement(),q.toString());
        }
        if(bb.endGoto!=null){
            json.put("Q"+qCount.getAndIncrement(), "goto"+bb.endGoto);
        }

        List<JSONObject> children = new ArrayList<>();
        for(BasicBlock son : bb.sons) {
            children.add(toJson(son));
        }

        json.put("sons", children);
        return json;
    }

    public static String prettyJson(JSONObject jsonBB) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(jsonBB.toString());
        return gson.toJson(je);
    }

    public BasicBlock getDad() {
        return dad;
    }

    public void setDad(BasicBlock dad) {
        dad = new BasicBlock();
        this.dad = dad;
    }

    public boolean isWritten() {
        return written;
    }

    public void setWritten(boolean written) {
        this.written = written;
    }

    public String getEndGoto() {
        return endGoto;
    }

    public void setEndGoto(String endGoto) {
        this.endGoto = endGoto;
    }

    public void addQuad(Quadruple q){
        quadruples.add(q);
    }

    public void addSon(BasicBlock bb){
        sons.add(bb);
    }

    public LinkedList<Quadruple> getQuadruples() {
        return quadruples;
    }

    public void setQuadruples(LinkedList<Quadruple> quadruples) {
        this.quadruples = quadruples;
    }

    public LinkedList<BasicBlock> getSons() {
        return sons;
    }

    public void setSons(LinkedList<BasicBlock> sons) {
        this.sons = sons;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void markAsWritten(){ this.written = true; }

    public boolean hasBeenWritten(){ return written; }
}
