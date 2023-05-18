package main.java.jdr299zdh5cew256ans96.assembly;

public class GlobalPrefix extends Instruction {

    private String name;
    private int size;

    public GlobalPrefix(String name, int size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String toString() {
        int numBytes = size == 0 ? 8 : 8*size;
        return ".align 32\n\t.type "+name+", @object\n\t.size "+name+", "+numBytes ;
    }

    public boolean hasAbstractTemp() {
        return false;
    }
}
