package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRESeq;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.ir.IRBinOp;
import main.java.jdr299zdh5cew256ans96.types.Type;


/**
 * AST Node for if statements. They are also statements that must be
 * inside of blocks, but they can span multiple lines.
 */
public class If extends Multiline {

    private IRExpr guard;

    /**
     * Constructor for creating if statement
     * 
     * @param e   - guard expression of if statement
     * @param b   - body of if statement
     * @param pos - position of if statement in program file
     */
    public If(Expression e, Statement b, String pos) {
        super(e, b, pos);
    }

    /**
     * Function to type check an AST node in the tree.
     *
     * @param c - Context that represents the symbol table of storing variables
     * @return the type associated with the AST node after type checking
     * @throws SemanticError if the AST node does not type check
     */
    @Override
    public Type typeCheck(Context c) throws SemanticError {
        Expression guardExpr = getExpression();
        Statement ifBody = getBody();
        Type type = guardExpr.typeCheck(c);
        Type ifReturnType;
        if (type.equalsStr("bool")) {
            c.push();
            ifReturnType = ifBody.typeCheck(c);
            c.pop();
        } else {
            throw new SemanticError(guardExpr.getPos()
                    + " error: Expected bool in guard expression, " +
                    "but found " + type.toString());
        }

        setNodeType(ifReturnType);
        return ifReturnType;
    }

    /**
     *
     * @return if as the name of the multi line
     */
    public String getName() {
        return "if";
    }

    public void setIRGuard(IRExpr guard) {
        this.guard = guard;
    }

    public IRExpr getIRGuard() {
        return guard;
    }

    @Override
    public IRStmt translate(IRNodeFactory factory) {
        IRExpr guardExpr = getExpression().translate(factory);
        setIRGuard(guardExpr);
        IRExpr invertedGuardExpr = factory.IRBinOp(IRBinOp.OpType.XOR, factory.IRConst(1),guardExpr);

        guardExpr.setLabel(factory.getCurrentLabel(), factory);

        IRStmt irBody = getBody().translate(factory);
        String endLabel = factory.generateFreshEndLabel();
        if (guardExpr.isShortCircuit()) {
            IRESeq eseq = (IRESeq) guardExpr;
            if (guardExpr.isOr()) { // or / not
                return factory.IRSeq(
                        eseq.stmt(),
                        factory.IRJump(factory.IRName(endLabel)),
                        factory.IRLabel(guardExpr.getLabel()),
                        irBody,
                        factory.IRLabel(endLabel)
                );
            } else { // and
                return factory.IRSeq(
                        eseq.stmt(),
                        irBody,
                        factory.IRJump(factory.IRName(endLabel)),
                        factory.IRLabel(guardExpr.getLabel()),
                        factory.IRLabel(endLabel)
                );
            }


        } else {
            String falseLabel = factory.generateFreshLabel();
            // For Basic Block purposes
            String trueLabel = factory.generateFreshTrueLabel();
            return factory.IRSeq(
                    factory.IRCJump(invertedGuardExpr, falseLabel),
                    factory.IRLabel(trueLabel),
                    irBody,
                    factory.IRJump(factory.IRName(endLabel)),
                    factory.IRLabel(falseLabel),
                    factory.IRLabel(endLabel)
            );
        }

    }
}