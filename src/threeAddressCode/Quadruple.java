package threeAddressCode;

import org.json.JSONObject;

public class Quadruple {
    private String result;
    private String arg1;
    private String arg2;
    private String operator;
    private String scope;

    public Quadruple(String result, String arg1, String arg2, String operator){
        this.result = result;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.operator = operator;
    }

    public Quadruple(String result, String arg1, String operator){
        this.result = result;
        this.arg1 = arg1;
        this.operator = operator;
        this.arg2 = null;
    }

    public static JSONObject toJson(Quadruple q) {
        JSONObject json = new JSONObject();
        if(q.result!=null) json.put("result", q.result);
        if(q.arg1!=null) json.put("arg1", q.arg1);
        if(q.arg2!=null) json.put("operator", q.arg2);
        if(q.operator!=null) json.put("arg2", q.operator);
        return json;
    }

    public static JSONObject endGotoToJson(String endGoto) {
        JSONObject json = new JSONObject();
        json.put("result", "goto ");
        json.put("arg1", endGoto);
        return json;
    }

    public String getResult() { return result;}

    public String getArg1() { return arg1;}

    public String getArg2() { return arg2;}

    public String getOperator() { return operator;}

    public void setArg1(String arg1) { this.arg1 = arg1;}

    public void setArg2(String arg2) { this.arg2 = arg2;}

    public void setResult(String result) { this.result = result;}

    public void setOperator(String operator) { this.operator = operator;}

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        if(this.arg2 == null) {
            return this.result + " " + this.arg1;
        } else {
            return this.result + " " + this.arg1 + " " + this.operator + " " + this.arg2;
        }
    }
}
