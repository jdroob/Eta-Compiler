package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.Binop;
import main.java.jdr299zdh5cew256ans96.assembly.Cmp;
import main.java.jdr299zdh5cew256ans96.assembly.Jmp;
import main.java.jdr299zdh5cew256ans96.assembly.Label;
import main.java.jdr299zdh5cew256ans96.assembly.MemBinop;
import main.java.jdr299zdh5cew256ans96.assembly.Mov;
import main.java.jdr299zdh5cew256ans96.assembly.Register;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;
import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.CheckConstFoldedIRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
import main.java.jdr299zdh5cew256ans96.tiles.Tile;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.InternalCompilerError;

import java.util.ArrayList;
import java.util.List;

/** An intermediate representation for a binary operation OP(left, right) */
public class IRBinOp extends IRExpr_c {

    /** Binary operators */
    public enum OpType {
        ADD,
        SUB,
        MUL,
        HMUL,
        DIV,
        MOD,
        AND,
        OR,
        XOR,
        LSHIFT,
        RSHIFT,
        ARSHIFT,
        EQ,
        NEQ,
        LT,
        ULT,
        GT,
        LEQ,
        GEQ;

        public String toString() {
            switch (this) {
                case ADD:
                    return "ADD";
                case SUB:
                    return "SUB";
                case MUL:
                    return "MUL";
                case HMUL:
                    return "HMUL";
                case DIV:
                    return "DIV";
                case MOD:
                    return "MOD";
                case AND:
                    return "AND";
                case OR:
                    return "OR";
                case XOR:
                    return "XOR";
                case LSHIFT:
                    return "LSHIFT";
                case RSHIFT:
                    return "RSHIFT";
                case ARSHIFT:
                    return "ARSHIFT";
                case EQ:
                    return "EQ";
                case NEQ:
                    return "NEQ";
                case LT:
                    return "LT";
                case ULT:
                    return "ULT";
                case GT:
                    return "GT";
                case LEQ:
                    return "LEQ";
                case GEQ:
                    return "GEQ";
            }
            throw new InternalCompilerError("Unknown op type");
        }
    };

    private OpType type;
    private IRExpr left, right;

    public IRBinOp(OpType type, IRExpr left, IRExpr right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    public OpType opType() {
        return type;
    }

    public IRExpr left() {
        return left;
    }

    public IRExpr right() {
        return right;
    }

    @Override
    public String label() {
        return type.toString();
    }

    /** IRBinop valueNumbering */
    @Override
    public int valueNumbering(int i) {

        i = left.valueNumbering(i);
        System.out.println("IRBinOp: " + left.toString() + " value number " + left.number());
        i = right.valueNumbering(i);
        System.out.println("IRBinOp: " + right.toString() + " value number " + right.number());
        System.out.println("IRBinOp: " + toString() + " value number " + i);
        return setNumber(i);
    }

    @Override
    // TODO: make this also get whole binop expressions
    public IRExpr get(IRExpr t) {
        if (left.contains(t)) {
            return left.get(t);
        }
        if (right.contains(t)) {
            return right.get(t);
        } else {
            return null;
        }
    }

    @Override
    public boolean contains(IRExpr t) {
        if (left.get(t) == null && right.get(t) == null) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public List<IRTemp> temps() {
        ArrayList<IRTemp> l = new ArrayList<>();
        l.addAll(left.temps());
        l.addAll(right.temps());
        return l;

    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr left = (IRExpr) v.visit(this, this.left);
        IRExpr right = (IRExpr) v.visit(this, this.right);

        if (left != this.left || right != this.right)
            return v.nodeFactory().IRBinOp(type, left, right);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(left));
        result = v.bind(result, v.visit(right));
        return result;
    }

    @Override
    public boolean isConstFolded(CheckConstFoldedIRVisitor v) {
        if (isConstant()) {
            switch (type) {
                case DIV:
                case MOD:
                    return right.constant() == 0;
                default:
                    return false;
            }
        }
        return true;
    }

    @Override
    public boolean isConstant() {
        return left.isConstant() && right.isConstant();
    }

    @Override
    public IRESeq lower(IRNodeFactory factory) {
        IRESeq loweredLeft = left.lower(factory);
        IRESeq loweredRight = right.lower(factory);
        IRStmt sideEffect1 = loweredLeft.stmt();
        IRExpr pureExpr1 = loweredLeft.expr();

        IRStmt sideEffect2 = loweredRight.stmt();
        IRExpr pureExpr2 = loweredRight.expr();

        ArrayList<IRStmt> seq = new ArrayList<>();
        IRTemp temp = factory.generateFreshTemp();
        seq.add(sideEffect1);
        seq.add(factory.IRMove(temp, pureExpr1));
        seq.add(sideEffect2);

        return factory.IRESeq(
                factory.IRSeq(seq),
                factory.IRBinOp(type, temp, pureExpr2));
    }

    @Override
    public Assembly recursiveMunch(ArrayList<IRNode> recursiveChildren, Tile tile) {
        IRNode leftNode = recursiveChildren.get(0);
        IRNode rightNode = recursiveChildren.get(1);

        Assembly a1 = leftNode.munch();
        Assembly a2 = rightNode.munch();

        String newShuttleTemp = Assembly.generateFreshTemp();
        String leftShuttle = a1.getShuttleTemp();
        String rightShuttle = a2.getShuttleTemp();

        Assembly binop = new Assembly();

        // System.out.println("Got here too: "+type+" "+opType());
        switch (type) {
            case ADD:
                binop.addInstruction(new Binop("lea",
                        new Register(newShuttleTemp),
                        new MemBinop(new Register(leftShuttle), "+",
                                new Register(rightShuttle))));
                break;
            case MUL:
                binop.addInstruction(new Mov(newShuttleTemp, leftShuttle));
                binop.addInstruction(new Binop("imul", newShuttleTemp, rightShuttle));
                break;
            case HMUL:
                // System.out.println("got here");
                // step 1: save rax and rdx
                binop.addInstruction(new Mov("r13", "rax"));
                binop.addInstruction(new Mov("r14", "rdx"));

                // step 2: move multiplicand into rax
                binop.addInstruction(new Mov("rax", leftShuttle));

                // step 3: move multiplier into r12
                // save r12
                binop.addInstruction(new Mov("r15", "r12"));
                binop.addInstruction(new Mov("r12", rightShuttle));

                // step 4: RDX:RAX = RAX * R12
                binop.addInstruction(new Binop("imul", "r12"));

                // step 5: save upper 64 bits
                binop.addInstruction(new Mov(newShuttleTemp, "rdx"));
                // step 5: save upper 64 bits
                binop.addInstruction(new Mov(newShuttleTemp, "rdx"));

                // step 6: restore registers
                binop.addInstruction(new Mov("rax", "r13"));
                binop.addInstruction(new Mov("rdx", "r14"));
                binop.addInstruction(new Mov("r12", "r15"));
                break;
            case DIV:
                // How this works (I think)
                // Divisor stored in RDX:RAX
                // Dividend stored in arbitrary register (use r12 for now?)
                // idiv r12 ; does (rdx:rax)/r12
                // quotient stored in rax
                // remainder stored in rdx

                // step 1: save rax and rdx
                binop.addInstruction(new Mov("r13", "rax"));
                binop.addInstruction(new Mov("r14", "rdx"));

                // step 2: move divisor into rax
                binop.addInstruction(new Mov("rax", leftShuttle));

                // step 3: zero out upper 64 bits of rdx:rax
                // this is accomplished by xor'ing rdx by itself
                binop.addInstruction(new Binop("xor", "rdx", "rdx"));

                // step 4: move dividend into r12
                // save r12
                binop.addInstruction(new Mov("r15", "r12"));
                binop.addInstruction(new Mov("r12", rightShuttle));
                binop.addInstruction(new Binop("idiv", "r12"));

                // step 5: store quotient
                // Only difference b/w div and mod is that below will be
                // rdx instead of rax
                binop.addInstruction(new Mov(newShuttleTemp, "rax"));

                // step 6: restore registers
                binop.addInstruction(new Mov("rax", "r13"));
                binop.addInstruction(new Mov("rdx", "r14"));
                binop.addInstruction(new Mov("r12", "r15"));
                break;
            case MOD:
                // How this works (I think)
                // Divisor stored in RDX:RAX
                // Dividend stored in arbitrary register (use r12 for now?)
                // idiv r12 ; does (rdx:rax)/r12
                // quotient stored in rax
                // remainder stored in rdx

                // step 1: save rax and rdx
                binop.addInstruction(new Mov("r13", "rax"));
                binop.addInstruction(new Mov("r14", "rdx"));

                // step 2: move divisor into rax
                binop.addInstruction(new Mov("rax", leftShuttle));

                // step 3: zero out upper 64 bits of rdx:rax
                // this is accomplished by xor'ing rdx by itself
                binop.addInstruction(new Binop("xor", "rdx", "rdx"));

                // step 4: move dividend into r12
                // save r12
                binop.addInstruction(new Mov("r15", "r12"));
                binop.addInstruction(new Mov("r12", rightShuttle));
                binop.addInstruction(new Binop("idiv", "r12"));

                // step 5: store remainder
                // Only difference b/w mod and div is that below will be
                // rax instead of rdx
                binop.addInstruction(new Mov(newShuttleTemp, "rdx"));

                // step 6: restore registers
                binop.addInstruction(new Mov("rax", "r13"));
                binop.addInstruction(new Mov("rdx", "r14"));
                binop.addInstruction(new Mov("r12", "r15"));
                break;
            case AND:
            case OR:
            case XOR:
            case SUB:
                binop.addInstruction(new Mov(newShuttleTemp, leftShuttle));
                binop.addInstruction(new Binop(type.toString().toLowerCase(), newShuttleTemp, rightShuttle));
                break;
            case EQ:
            case NEQ:
            case LT:
            case ULT:
            case GT:
            case LEQ:
            case GEQ:
                String jump = getJump();
                String equalLabel = Assembly.generateFreshLabel();
                String endLabel = Assembly.generateFreshLabel();
                binop.addInstruction(new Cmp(leftShuttle, rightShuttle));
                binop.addInstruction(new Jmp(jump, equalLabel));
                binop.addInstruction(new Mov(newShuttleTemp, 0));
                binop.addInstruction(new Jmp("jmp", endLabel));
                binop.addInstruction(new Label(equalLabel));
                binop.addInstruction(new Mov(newShuttleTemp, 1));
                binop.addInstruction(new Label(endLabel));
                break;
            default:

        }

        Assembly total = new Assembly();
        total.addInstructions(a1);
        total.addInstructions(a2);

        total.addInstructions(binop);
        total.setShuttleTemp(newShuttleTemp);
        return total;
    }

    private String getJump() {
        return switch (type) {
            case EQ -> "jz";
            case NEQ -> "jnz";
            case LT, ULT -> "jl";
            case GT -> "jg";
            case LEQ -> "jle";
            case GEQ -> "jge";
            default -> "";
        };
    }

    private String getOpStr() {
        return switch (type) {
            case ADD -> "+";
            case SUB -> "-";
            case MUL -> "*";
            default -> "";
        };
    }

    public String getTargetName() {
        // TODO: Handle case where left is an IRMem
        if (left instanceof IRTemp) {
            return ((IRTemp) left).name();
        }
        return "TARGET_NOT_TEMP";
    }

    public String toString() {
        return type.toString() + "_"
                + left.toString() + "_" + right.toString();
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom(type.toString());
        left.printSExp(p);
        right.printSExp(p);
        p.endList();
    }

    // public IRExpr negate() {
    // IRBinOp expr = (IRBinOp) copy();
    // switch (type) {
    // case LT:
    // expr.type = OpType.GE;
    // break;
    // case GT:
    // expr.type = OpType.LE;
    // break;
    // case LE:
    // expr.type = OpType.GT;
    // break;
    // case GE:
    // expr.type = OpType.LT;
    // break;
    // case ULT:
    // expr.type = OpType.UGE;
    // break;
    // case UGT:
    // expr.type = OpType.ULE;
    // break;
    // case ULE:
    // expr.type = OpType.UGT;
    // break;
    // case UGE:
    // expr.type = OpType.ULT;
    // break;
    // case EQ:
    // expr.type = OpType.NEQ;
    // break;
    // case NEQ:
    // expr.type = OpType.EQ;
    // break;
    // default:
    // return super.negate();
    // }
    // return expr;
    // }
    //
    // public IRBinOp swapArgs() {
    // IRBinOp expr = (IRBinOp) copy();
    // expr.left = right;
    // expr.right = left;
    // switch (type) {
    // case LT:
    // expr.type = OpType.GT;
    // break;
    // case GT:
    // expr.type = OpType.LT;
    // break;
    // case LE:
    // expr.type = OpType.GE;
    // break;
    // case GE:
    // expr.type = OpType.LE;
    // break;
    // case ULT:
    // expr.type = OpType.UGT;
    // break;
    // case UGT:
    // expr.type = OpType.ULT;
    // break;
    // case ULE:
    // expr.type = OpType.UGE;
    // break;
    // case UGE:
    // expr.type = OpType.ULE;
    // break;
    // case EQ:
    // expr.type = OpType.EQ;
    // break;
    // case NEQ:
    // expr.type = OpType.NEQ;
    // break;
    // case ADD:
    // expr.type = OpType.ADD;
    // break;
    // case MUL:
    // expr.type = OpType.MUL;
    // break;
    // case AND:
    // expr.type = OpType.AND;
    // break;
    // case OR:
    // expr.type = OpType.OR;
    // break;
    // case LOGAND:
    // expr.type = OpType.LOGAND;
    // break;
    // case LOGOR:
    // expr.type = OpType.LOGOR;
    // break;
    // default:
    // return null;
    // }
    // return expr;
    // }
    //
    // public boolean isComparison() {
    // switch (type) {
    // case EQ:
    // case NEQ:
    // case LT:
    // case GT:
    // case LE:
    // case GE:
    // case ULT:
    // case UGT:
    // case ULE:
    // case UGE:
    // return true;
    // default:
    // return false;
    // }
    // }
    //
    // // eval the value based on op type
    // public static long eval(OpType opType, long leftVal, long rightVal) {
    // switch (opType) {
    // case ADD:
    // return leftVal + rightVal;
    // case AND:
    // case LOGAND:
    // return leftVal & rightVal;
    // case DIV:
    // if (rightVal == 0)
    // throw new InternalCompilerException("Division by zero "
    // + "during constant folding -- please fix your program!");
    // return leftVal / rightVal;
    // case EQ:
    // return leftVal == rightVal ? 1 : 0;
    // case GE:
    // return leftVal >= rightVal ? 1 : 0;
    // case GT:
    // return leftVal > rightVal ? 1 : 0;
    // case LE:
    // return leftVal <= rightVal ? 1 : 0;
    // case AR_LSHIFT:
    // return leftVal << rightVal;
    // case LSHIFT:
    // return leftVal << rightVal;
    // case LT:
    // return leftVal < rightVal ? 1 : 0;
    // case MOD:
    // if (rightVal == 0)
    // throw new InternalCompilerException("Division by zero "
    // + "during constant folding -- please fix your program!");
    // return leftVal % rightVal;
    // case MUL:
    // return leftVal * rightVal;
    // case NEQ:
    // return leftVal != rightVal ? 1 : 0;
    // case OR:
    // case LOGOR:
    // return leftVal | rightVal;
    // case AR_RSHIFT:
    // return leftVal >> rightVal;
    // case RSHIFT:
    // return leftVal >>> rightVal;
    // case SUB:
    // return leftVal - rightVal;
    // case XOR:
    // return leftVal ^ rightVal;
    // case UGT:
    // return leftVal > rightVal ^ leftVal < 0 != rightVal < 0 ? 1 : 0;
    // case ULT:
    // return leftVal < rightVal ^ leftVal < 0 != rightVal < 0 ? 1 : 0;
    // case ULE:
    // return leftVal == rightVal
    // || leftVal < rightVal ^ leftVal < 0 != rightVal < 0 ? 1 : 0;
    // case UGE:
    // return leftVal == rightVal
    // || leftVal > rightVal ^ leftVal < 0 != rightVal < 0 ? 1 : 0;
    // default:
    // throw new InternalCompilerException("Unknow op type "
    // + opType.toString());
    // }
    // }
}
