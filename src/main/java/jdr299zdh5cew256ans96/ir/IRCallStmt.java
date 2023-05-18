package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.Binop;
import main.java.jdr299zdh5cew256ans96.assembly.Call;
import main.java.jdr299zdh5cew256ans96.assembly.CallStack;
import main.java.jdr299zdh5cew256ans96.assembly.Mov;
import main.java.jdr299zdh5cew256ans96.assembly.Push;
import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.CheckCanonicalIRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An intermediate representation for a call statement. t_1, t_2, _, t_4 =
 * CALL(e_target, e_1, ...,
 * e_n) where n = n_returns.
 */
public class IRCallStmt extends IRStmt {
    protected IRExpr target;
    protected List<IRExpr> args;
    protected Long n_returns;

    /**
     * @param target address of the code for this function call
     * @param args   arguments of this function call
     */
    public IRCallStmt(IRExpr target, Long n_returns, IRExpr... args) {
        this(target, n_returns, Arrays.asList(args));
    }

    /**
     * @param target address of the code for this function call
     * @param args   arguments of this function call
     */
    public IRCallStmt(IRExpr target, Long n_returns, List<IRExpr> args) {
        this.target = target;
        this.args = args;
        this.n_returns = n_returns;
    }

    public IRExpr target() {
        return target;
    }

    public List<IRExpr> args() {
        return args;
    }

    public Long n_returns() {
        return n_returns;
    }

    @Override
    public String label() {
        return "CALL_STMT";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        IRExpr target = (IRExpr) v.visit(this, this.target);
        if (target != this.target)
            modified = true;

        List<IRExpr> results = new ArrayList<>(args.size());
        for (IRExpr arg : args) {
            IRExpr newExpr = (IRExpr) v.visit(this, arg);
            if (newExpr != arg)
                modified = true;
            results.add(newExpr);
        }

        if (modified)
            return v.nodeFactory().IRCallStmt(target, n_returns, results);

        return this;
    }

    @Override
    public Assembly munch() {
        IRName name = (IRName) target;
        Assembly pushArgs = new Assembly();
        int maxArgs = 6;
        int numSpilledArgs = 0;
        int numSpilledRVs = n_returns.intValue() > 2 ? n_returns.intValue() - 2 : 0;

        if (numSpilledRVs > 0) {
            // allocating space for multiple return values
            int val = 8 * (n_returns.intValue()-2);
            pushArgs.addInstruction(new Binop("sub", "rsp", val));
            pushArgs.addInstruction(new Mov("rdi", "rsp"));
            maxArgs = 5;
        }

        // compute number of spilled args
        numSpilledArgs = CallStack.size() > maxArgs ? CallStack.size() - maxArgs : 0;

        // save spilled args
        while (CallStack.size() > maxArgs) {
            pushArgs.addInstruction(new Push(CallStack.pop()));
        }

        String[] regs = { "r9", "r8", "rcx", "rdx", "rsi", "rdi" };
        if (numSpilledArgs > 0) {
            int i = 0;
            while (!CallStack.isEmpty()) {
                pushArgs.addInstruction(new Mov(regs[i], CallStack.pop()));
                i++;
            }
        } else {
            while (!CallStack.isEmpty()) {
                int i = maxArgs - CallStack.size();
                pushArgs.addInstruction(new Mov(regs[i], CallStack.pop()));
                i++;
            }
        }

        // Calculate n
        int n = numSpilledArgs + numSpilledRVs;
        if (n % 2 != 0) {
            pushArgs.addInstruction(new Push(0));
        }

        // Call objects store name of called function, # of spilled args,
        // # of spilled RVs
        pushArgs.addInstruction(new Call(name.name(), numSpilledArgs,
                numSpilledRVs));

        // Calculate n
        if (n % 2 != 0) {
            pushArgs.addInstruction(new Binop("add", "rsp", 8));
        }

        if (args.size() > maxArgs) {
            int decrement = 8 * (args.size() - maxArgs);
            pushArgs.addInstruction(new Binop("add", "rsp", decrement));

        }

        return pushArgs;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(target));
        for (IRExpr arg : args)
            result = v.bind(result, v.visit(arg));
        return result;
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return !v.inExpr();
    }

    @Override
    public IRStmt lower(IRNodeFactory factory) {
        ArrayList<IRStmt> seq = new ArrayList<>();
        ArrayList<IRExpr> freshTemps = new ArrayList<>();
        int i = 1;
        for (IRExpr arg : args) {
            IRESeq eseq = arg.lower(factory);
            IRStmt sideEffect = eseq.stmt();
            IRExpr pureExpr = eseq.expr();
            seq.add(sideEffect);
            IRTemp freshTemp = factory.generateFreshTemp();
            IRTemp argReg = factory.IRTemp("_ARG" + i);
            freshTemps.add(freshTemp);
            seq.add(factory.IRMove(freshTemp, pureExpr));
            seq.add(factory.IRMove(argReg, freshTemp));
            i += 1;
        }

        seq.add(factory.IRCallStmt(target, n_returns, freshTemps));
        return factory.IRSeq(seq);
    }

    public String targetString() {
        return "l_" + target.toString();
    }

    public String toString() {
        String s = "CALL_STMT_" + n_returns
                + "_" + target.toString();
        for (IRExpr arg : args) {
            s = s + "_" + arg.toString();
        }
        return s;

    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("CALL_STMT");
        p.printAtom(Long.toString(n_returns));
        target.printSExp(p);
        for (IRExpr arg : args)
            arg.printSExp(p);
        p.endList();
    }
}
