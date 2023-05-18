//package main.java.jdr299zdh5cew256ans96.ir;
//
//import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
//import main.java.jdr299zdh5cew256ans96.assembly.MemRegister;
//import main.java.jdr299zdh5cew256ans96.assembly.Mov;
//import main.java.jdr299zdh5cew256ans96.assembly.Register;
//import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
//import main.java.jdr299zdh5cew256ans96.ir.visit.CheckCanonicalIRVisitor;
//import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
//import main.java.jdr299zdh5cew256ans96.tiles.MoveExprToMemTile;
//import main.java.jdr299zdh5cew256ans96.tiles.MoveExprToTempTile;
//import main.java.jdr299zdh5cew256ans96.tiles.MoveMemToExprTile;
//import main.java.jdr299zdh5cew256ans96.tiles.MoveTempToExprTile;
//import main.java.jdr299zdh5cew256ans96.tiles.MoveTile;
//import main.java.jdr299zdh5cew256ans96.tiles.Tile;
//import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
//
//import java.util.ArrayList;
//
///** An intermediate representation for a move statement MOVE(target, expr) */
//public class IRMove extends IRStmt {
//    private IRExpr target;
//    private IRExpr src;
//
//    /**
//     * @param target the destination of this move
//     * @param src the expression whose value is to be moved
//     */
//    public IRMove(IRExpr target, IRExpr src) {
//        if (!target.isValid()) {
//            this.setValid(false);
//        }
//        this.target = target;
//        this.src = src;
//    }
//
//    public IRExpr target() {
//        return target;
//    }
//
//    public IRExpr source() {
//        return src;
//    }
//
//    @Override
//    public String label() {
//        return "MOVE";
//    }
//
//    @Override
//    public CheckCanonicalIRVisitor checkCanonicalEnter(CheckCanonicalIRVisitor v) {
//        return v.enterMove();
//    }
//
//    @Override
//    public IRNode visitChildren(IRVisitor v) {
//        IRExpr target = (IRExpr) v.visit(this, this.target);
//        IRExpr expr = (IRExpr) v.visit(this, src);
//
//        if (target != this.target || expr != src) return v.nodeFactory().IRMove(target, expr);
//
//        return this;
//    }
//
//    @Override
//    public IRStmt lower(IRNodeFactory factory) { // TODO: simplify using rules
//        // left side is temp
//        if (!target.isMem()) {
//            IRESeq eseq = src.lower(factory);
//
//            return factory.IRSeq(
//                    eseq.stmt(),
//                    factory.IRMove(target, eseq.expr())
//            );
//        } else { // left side is mem
//            IRMem leftMem = (IRMem) target;
//            IRExpr memExpr = leftMem.expr();
//
//            IRESeq eseq1 = memExpr.lower(factory);
//            IRStmt sideEffect1 = eseq1.stmt();
//            IRExpr pureExpr1 = eseq1.expr();
//
//            IRESeq eseq2 = src.lower(factory);
//            IRStmt sideEffect2 = eseq2.stmt();
//            IRExpr pureExpr2 = eseq2.expr();
//
//            IRTemp temp = factory.generateFreshTemp();
//
//            return factory.IRSeq(
//                    sideEffect1,
//                    factory.IRMove(temp,pureExpr1),
//                    sideEffect2,
//                    factory.IRMove(factory.IRMem(temp),pureExpr2)
//            );
//
//        }
//    }
//
//    @Override
//    public Assembly recursiveMunch(ArrayList<IRNode> recursiveChildren,
//                                   Tile tile) {
//        if (tile instanceof MoveTile) {
//            IRNode targetNode = recursiveChildren.get(0);
//            IRNode srcNode = recursiveChildren.get(1);
//
//            Assembly a1 = targetNode.munch();
//            Assembly a2 = srcNode.munch();
//            Assembly move = new Assembly(new Mov(a1.getShuttleTemp(),
//                    a2.getShuttleTemp()));
//
//            Assembly total = new Assembly();
//            total.addInstructions(a1);
//            total.addInstructions(a2);
//            total.addInstructions(move);
//            return total;
//        } else if (tile instanceof MoveExprToTempTile) {
//            IRNode srcNode = recursiveChildren.get(0);
//            Assembly a = srcNode.munch();
//            IRTemp tempTarget = (IRTemp) target;
//            String tempTargetName = tempTarget.name();
//
//            if (tempTargetName.contains("_RV")) {
//                int numRegTarget = Integer.parseInt(
//                        String.valueOf(
//                                tempTargetName.charAt(tempTargetName.length() - 1)));
//                if (numRegTarget > 2) {
//                    int offset = (numRegTarget - 3) * 8;
//                    tempTargetName = "qword ptr [rdi + "+offset+"]";
//
//                } else {
//                    if (numRegTarget == 1) {
//                        tempTargetName = "rax";
//                    } else {
//                        tempTargetName = "rdx";
//                    }
//                }
//            }
//
//
//            Assembly move = new Assembly(new Mov(tempTargetName,
//                    a.getShuttleTemp()));
//
//            Assembly total = new Assembly();
//            total.addInstructions(a);
//            total.addInstructions(move);
//            return total;
//        } else if (tile instanceof MoveTempToExprTile) {
//            IRNode targetNode = recursiveChildren.get(0);
//            Assembly a = targetNode.munch();
//            IRTemp tempSrc = (IRTemp) src;
//            Assembly move = new Assembly(new Mov(a.getShuttleTemp(),
//                    tempSrc.name()));
//            Assembly total = new Assembly();
//            total.addInstructions(a);
//            total.addInstructions(move);
//            return total;
//        } else if (tile instanceof MoveMemToExprTile) {
//            IRNode targetNode = recursiveChildren.get(0);
//            Assembly a = targetNode.munch();
//            IRMem memSrc = (IRMem) src;
//            Assembly memMunch = memSrc.expr().munch();
//            Assembly move =
//                    new Assembly(new Mov(new Register(a.getShuttleTemp()), new MemRegister(memMunch.getShuttleTemp())));
//
//            Assembly total = new Assembly();
//            total.addInstructions(a);
//            total.addInstructions(memMunch);
//            total.addInstructions(move);
//            return total;
//        } else if (tile instanceof MoveExprToMemTile) {
//            IRNode srcNode = recursiveChildren.get(0);
//            Assembly a = srcNode.munch();
//            IRMem memTarget = (IRMem) target;
//            Assembly memMunch = memTarget.expr().munch();
//            Assembly move =
//                    new Assembly(new Mov(new MemRegister(memMunch.getShuttleTemp()),new Register(a.getShuttleTemp())));
//
//            Assembly total = new Assembly();
//            total.addInstructions(a);
//            total.addInstructions(memMunch);
//            total.addInstructions(move);
//            return total;
//        }
//
//        System.out.println("no match");
//        return new Assembly();
//
//    }
//
//    @Override
//    public <T> T aggregateChildren(AggregateVisitor<T> v) {
//        T result = v.unit();
//        result = v.bind(result, v.visit(target));
//        result = v.bind(result, v.visit(src));
//        return result;
//    }
//
//    @Override
//    public void printSExp(CodeWriterSExpPrinter p) {
//        p.startList();
//        p.printAtom("MOVE");
//        target.printSExp(p);
//        src.printSExp(p);
//        p.endList();
//    }
//}

package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.Const;
import main.java.jdr299zdh5cew256ans96.assembly.MemBinop;
import main.java.jdr299zdh5cew256ans96.assembly.MemRegister;
import main.java.jdr299zdh5cew256ans96.assembly.Mov;
import main.java.jdr299zdh5cew256ans96.assembly.Operand;
import main.java.jdr299zdh5cew256ans96.assembly.Register;
import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.CheckCanonicalIRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
import main.java.jdr299zdh5cew256ans96.tiles.MoveExprToMemTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveExprToTempTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveMemToExprTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveMemToTempTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveTempToExprTile;
import main.java.jdr299zdh5cew256ans96.tiles.MoveTile;
import main.java.jdr299zdh5cew256ans96.tiles.Tile;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

/** An intermediate representation for a move statement MOVE(target, expr) */
public class IRMove extends IRStmt {
    private IRExpr target;
    private IRExpr src;

    /**
     * @param target the destination of this move
     * @param src    the expression whose value is to be moved
     */
    public IRMove(IRExpr target, IRExpr src) {
        // if (!target.isValid()) {
        // this.setValid(false);
        // }
        this.target = target;
        this.src = src;
    }

    public IRExpr target() {
        return target;
    }

    public IRExpr source() {
        return src;
    }

    @Override
    public String label() {
        return "MOVE";
    }

    @Override
    public CheckCanonicalIRVisitor checkCanonicalEnter(CheckCanonicalIRVisitor v) {
        return v.enterMove();
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr target = (IRExpr) v.visit(this, this.target);
        IRExpr expr = (IRExpr) v.visit(this, src);

        if (target != this.target || expr != src)
            return v.nodeFactory().IRMove(target, expr);

        return this;
    }

    @Override
    public IRStmt lower(IRNodeFactory factory) {
        // left side is temp
        if (!target.isMem()) {
            IRESeq eseq = src.lower(factory);

            return factory.IRSeq(
                    eseq.stmt(),
                    factory.IRMove(target, eseq.expr()));
        } else { // left side is mem
            IRMem leftMem = (IRMem) target;
            IRExpr memExpr = leftMem.expr();

            IRESeq eseq1 = memExpr.lower(factory);
            IRStmt sideEffect1 = eseq1.stmt();
            IRExpr pureExpr1 = eseq1.expr();

            IRESeq eseq2 = src.lower(factory);
            IRStmt sideEffect2 = eseq2.stmt();
            IRExpr pureExpr2 = eseq2.expr();

            IRTemp temp = factory.generateFreshTemp();

            return factory.IRSeq(
                    sideEffect1,
                    factory.IRMove(temp, pureExpr1),
                    sideEffect2,
                    factory.IRMove(factory.IRMem(temp), pureExpr2));

        }
    }

    @Override
    public Assembly recursiveMunch(ArrayList<IRNode> recursiveChildren,
            Tile tile) {
        if (tile instanceof MoveTile) {
            System.out.println("MoveTile");
            IRNode targetNode = recursiveChildren.get(0);
            IRNode srcNode = recursiveChildren.get(1);

            Assembly a1 = targetNode.munch();
            Assembly a2 = srcNode.munch();
            Assembly move = new Assembly(new Mov(a1.getShuttleTemp(),
                    a2.getShuttleTemp()));

            Assembly total = new Assembly();
            total.addInstructions(a1);
            total.addInstructions(a2);
            total.addInstructions(move);
            return total;
        } else if (tile instanceof MoveExprToTempTile) {
            IRNode srcNode = recursiveChildren.get(0);
            Assembly a = srcNode.munch();
            IRTemp tempTarget = (IRTemp) target;
            String tempTargetName = tempTarget.name();
            Operand tempTargetOperand = new Register(tempTargetName);

            if (tempTargetName.contains("_RV")) {
                int numRegTarget = Integer.parseInt(
                        String.valueOf(
                                tempTargetName.charAt(tempTargetName.length() - 1)));
                if (numRegTarget > 2) {
                    int offset = (numRegTarget - 3) * 8;
                    tempTargetOperand = new MemBinop(new Register("rdi"), "+", new Const(offset));
                } else {
                    if (numRegTarget == 1) {
                        tempTargetOperand = new Register("rax");
                    } else {
                        tempTargetOperand = new Register("rdx");
                    }
                }
            }
            Assembly move = new Assembly(new Mov(tempTargetOperand,
                    new Register(a.getShuttleTemp())));
            Assembly total = new Assembly();
            total.addInstructions(a);
            total.addInstructions(move);
            return total;
        } else if (tile instanceof MoveTempToExprTile) {
            IRNode targetNode = recursiveChildren.get(0);
            Assembly a = targetNode.munch();
            IRTemp tempSrc = (IRTemp) src;
            Assembly move = new Assembly(new Mov(a.getShuttleTemp(),
                    tempSrc.name()));
            Assembly total = new Assembly();
            total.addInstructions(a);
            total.addInstructions(move);
            return total;
        } else if (tile instanceof MoveMemToExprTile) {
            IRNode targetNode = recursiveChildren.get(0);
            Assembly a = targetNode.munch();
            IRMem memSrc = (IRMem) src;
            Assembly memMunch = memSrc.expr().munch();
            Assembly move = new Assembly(new Mov(new Register(a.getShuttleTemp()),
                    new MemRegister(memMunch.getShuttleTemp())));

            Assembly total = new Assembly();
            total.addInstructions(a);
            total.addInstructions(memMunch);
            total.addInstructions(move);
            return total;
        } else if (tile instanceof MoveExprToMemTile) {
            IRNode srcNode = recursiveChildren.get(0);
            Assembly a = srcNode.munch();
            IRMem memTarget = (IRMem) target;
            Assembly memMunch = memTarget.expr().munch();
            Assembly move = new Assembly(new Mov(new MemRegister(memMunch.getShuttleTemp()),
                    new Register(a.getShuttleTemp())));

            Assembly total = new Assembly();
            total.addInstructions(a);
            total.addInstructions(memMunch);
            total.addInstructions(move);
            return total;
        } else if (tile instanceof MoveMemToTempTile) {
            IRNode srcNode = recursiveChildren.get(0);
            IRTemp tempTarget = (IRTemp) target;
            Assembly a = srcNode.munch();
            Assembly total = new Assembly();
            total.addInstructions(a);
            total.addInstruction(new Mov(new Register(tempTarget.name()),
                    new MemRegister(a.getShuttleTemp())));
            return total;
        }

        System.out.println("no match");
        return new Assembly();

    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(target));
        result = v.bind(result, v.visit(src));
        return result;
    }

    public String targetString() {
        return "targetString() in IRMove";
    }

    public String toString() {
        return "MOVE_" + target.toString() + "_" + src.toString();
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("MOVE");
        target.printSExp(p);
        src.printSExp(p);
        p.endList();
    }
}