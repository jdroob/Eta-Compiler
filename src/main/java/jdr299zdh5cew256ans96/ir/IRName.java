package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.Binop;
import main.java.jdr299zdh5cew256ans96.assembly.MemBinop;
import main.java.jdr299zdh5cew256ans96.assembly.Mov;
import main.java.jdr299zdh5cew256ans96.assembly.Register;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/** An intermediate representation for named memory address NAME(n) */
public class IRName extends IRExpr_c {
    private String name;

    /** @param name name of this memory address */
    public IRName(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String label() {
        return "NAME(" + name + ")";
    }

    public String toString() {
        return name;

    }

    @Override
    public Assembly munch() {
        String shuttleTemp = Assembly.generateFreshTemp();

        Assembly a = new Assembly(new Binop("lea", new Register(shuttleTemp),
                new MemBinop(new Register("rip"), "+", new Register(name))));
        a.setShuttleTemp(shuttleTemp);
        return a;
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("NAME");
        p.printAtom(name);
        p.endList();
    }
}
