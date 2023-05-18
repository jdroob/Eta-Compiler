package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.Mov;
import main.java.jdr299zdh5cew256ans96.assembly.Push;
import main.java.jdr299zdh5cew256ans96.assembly.TempStack;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java.util.List;
import java.util.ArrayList;

/** An intermediate representation for a temporary register TEMP(name) */
public class IRTemp extends IRExpr_c {
    private String name;

    /** @param name name of this temporary register */
    public IRTemp(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String label() {
        return "TEMP(" + name + ")";
    }

    @Override
    public Assembly munch() {
        String shuttleTemp = Assembly.generateFreshTemp();
        Assembly a = new Assembly(new Mov(shuttleTemp, name));
        a.setShuttleTemp(shuttleTemp);
        return a;
    }

    @Override
    public int valueNumbering(int i) {
        System.out.println("IRTemp: " + toString() + " value number " + i);
        return setNumber(i);
    }

    @Override
    public IRExpr get(IRExpr t) {
        if (t.toString() == toString()) {
            return this;
        } else {
            return null;
        }

    }

    @Override
    public boolean contains(IRExpr t) {
        if (get(t) == null) {
            return false;
        }
        return true;

    }

    @Override
    public List<IRTemp> temps() {
        ArrayList<IRTemp> l = new ArrayList<>();
        l.add(this);
        return l;

    }

    public String toString() {
        return "T_" + name;
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("TEMP");
        p.printAtom(name);
        p.endList();
    }
}