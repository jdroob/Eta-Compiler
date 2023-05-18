package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.Mov;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/** An intermediate representation for a 64-bit integer constant. CONST(n) */
public class IRConst extends IRExpr_c {
    private long value;

    /** @param value value of this constant */
    public IRConst(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }

    @Override
    public String label() {
        return "CONST(" + value + ")";
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public long constant() {
        return value;
    }

    @Override
    public Assembly munch() {
        String shuttleTemp = Assembly.generateFreshTemp();
        Assembly a = new Assembly(new Mov(shuttleTemp, (int)value));
        a.setShuttleTemp(shuttleTemp);
        return a;
    }


    public String toString() {
        return "C_" + value;
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("CONST");
        p.printAtom(String.valueOf(value));
        p.endList();
    }
}