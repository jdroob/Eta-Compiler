package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.Label;
import main.java.jdr299zdh5cew256ans96.ir.visit.InsnMapsBuilder;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/** An intermediate representation for naming a memory address */
public class IRLabel extends IRStmt {
    private String name;

    /** @param name name of this memory address */
    public IRLabel(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String label() {
        return "LABEL(" + name + ")";
    }

    @Override
    public InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v) {
        v.addNameToCurrentIndex(name);
        return v;
    }

    @Override
    public Assembly munch() {
        if (name.contains("_I")) {
            return new Assembly();
        }
        return new Assembly(new Label(name));
    }

    public String targetString() {
        return name;
    }

    public String toString() {
        return "LABEL_" + name;
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("LABEL");
        p.printAtom(name);
        p.endList();
    }
}
