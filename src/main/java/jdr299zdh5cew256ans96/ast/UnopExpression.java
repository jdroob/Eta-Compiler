package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRBinOp;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRESeq;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.types.BoolType;
import main.java.jdr299zdh5cew256ans96.types.IntType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * AST Node for unary expressions. Can be used anywhere where an expression
 * can be used
 */
public class UnopExpression extends Expression {

    /**
     * Unary operator and expression next to unary operator
     */
    private Operator.UnaryOperator operator;
    private Expression expression;

    /**
     * Constructor for creating unary expression
     * @param expression - unary expression
     * @param operator - unary operator for expression
     * @param pos - position of unary expression in program file
     */
    public UnopExpression(Expression expression, Operator.UnaryOperator operator,
                          String pos) {
        super(pos);
        this.expression = expression;
        this.operator = operator;
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
        Type expressionType = expression.typeCheck(c);
        String operatorType = operator.toString();
        if(operatorType.equals("!")){
            if (expressionType.equalsStr("bool")){
                BoolType boolType = new BoolType();
                setNodeType(boolType);
                return boolType;
            }
            else{
                throw new SemanticError(expression.getPos()+
                        " error: Operand of ! must be bool");
            }
        }
        if(operatorType.equals("-")){
            if (expressionType.equalsStr("int")){
                IntType intType = new IntType();
                setNodeType(intType);
                return intType;
            } else{
                throw new SemanticError(expression.getPos()+
                        " error: Operand of - must be int");
            }
        }

        throw new SemanticError(expression.getPos()+
                " error: Invalid unary operator");
    }

    public IRExpr translate(IRNodeFactory factory) {
        /**
         * express minus as SUB(CONST(0), expression.translate)
         * express not as XOR(CONST(1), expression.translate)
         */
        IRExpr iRExpr = expression.translate(factory);
        IRExpr invertedIRExpr = factory.IRBinOp(IRBinOp.OpType.XOR, factory.IRConst(1),iRExpr);

        switch (operator) {
            case MINUS:
                return factory.IRBinOp(IRBinOp.OpType.SUB,
                        factory.IRConst(0),
                        iRExpr);
            case NOT:
                IRExpr notExpr = IRESeq.getShortCircuitForm(invertedIRExpr, null,
                        factory.generateFreshLabel(),
                        factory.IRBinOp(IRBinOp.OpType.XOR, factory.IRConst(1), iRExpr),
                        factory
                );
                notExpr.setShortCircuit("not");
                notExpr.setShortCircuitFTVal(0);
                return notExpr;
            default: return null;
        }
    }

    /**
     * Pretty printing function to print parsed AST node to file. Different
     * AST nodes pretty print differently, so this method is just a stub
     *
     * @param c - printer object that is used to pretty print node
     * @return the printer object after it is modified with node's pretty print
     */
    @Override
    public CodeWriterSExpPrinter prettyPrint(CodeWriterSExpPrinter c) {
        c.startList();
        c.printAtom(operator.toString());
        expression.prettyPrint(c);
        c.endList();
        return c;
    }

}