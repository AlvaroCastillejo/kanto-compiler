package assembler;

public class Register {
    private String type;
    private int number;
    private boolean used;

    public Register(String type, int number) {
        this.type = type;
        this.number = number;
        this.used = false;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isUsed(){
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
