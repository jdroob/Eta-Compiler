package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRESeq;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.ir.IRBinOp;

/**
 * AST Node for while statements. They are also statements that must be
 * inside of blocks, but they can span multiple lines.
 */
public class While extends Multiline {

    /**
     * Constructor for creating while statement
     * @param expression - guard expression of while statement
     * @param body - body of while statement
     * @param pos - position of while statement in program file
     */
    public While(Expression expression, Statement body, String pos) {
        super(expression,body,pos);
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
        Expression guard = getExpression();
        Node whileBody = getBody();
        Type exprType = guard.typeCheck(c);

        if (!exprType.equalsStr("bool")) {
            throw new SemanticError(guard.getPos()+" error: Expected bool in " +
                    "guard expression, but found "+exprType.toString());
        }
        c.push();
        Type whileReturnType = whileBody.typeCheck(c);
        c.pop();

        setNodeType(whileReturnType);
        return whileReturnType;
    }

    /**
     *
     * @return while as the name of the multi line
     */
    public String getName() {
        return "while";
    }

    @Override
    public IRStmt translate(IRNodeFactory factory) {
        IRExpr guardExpr = getExpression().translate(factory);
        IRExpr invertedGuardExpr = factory.IRBinOp(IRBinOp.OpType.XOR, factory.IRConst(1),guardExpr);
        guardExpr.setLabel(factory.getCurrentLabel(), factory);

        IRStmt irBody = getBody().translate(factory);
        String headerLabel = factory.generateFreshHeaderLabel();
        String endLabel = factory.generateFreshEndLabel();
        // For Basic Block purposes
        String trueLabel = factory.generateFreshTrueLabel();
        if (guardExpr.isShortCircuit()) {
            IRESeq eseq = (IRESeq) guardExpr;
            if (guardExpr.isOr()) { // or / not
                return factory.IRSeq(
                        factory.IRLabel(headerLabel),
                        eseq.stmt(),
                        factory.IRJump(factory.IRName(endLabel)),
                        factory.IRLabel(guardExpr.getLabel()),
                        irBody,
                        factory.IRJump(factory.IRName(headerLabel)),
                        factory.IRLabel(endLabel)
                );
            }
            else {  // and
                return factory.IRSeq(
                        factory.IRLabel(headerLabel),
                        eseq.stmt(),
                        irBody,
                        factory.IRJump(factory.IRName(headerLabel)),
                        factory.IRLabel(guardExpr.getLabel())
                );
            }
        } else {
            String falseLabel = factory.generateFreshLabel();
            return factory.IRSeq(
                    factory.IRLabel(headerLabel),
                    factory.IRCJump(invertedGuardExpr, falseLabel),
                    factory.IRLabel(trueLabel),
                    irBody,
                    factory.IRJump(factory.IRName(headerLabel)),
                    factory.IRLabel(falseLabel)
            );
        }
    }
}