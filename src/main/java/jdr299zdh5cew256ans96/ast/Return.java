package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRESeq;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRSeq;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;
import main.java.jdr299zdh5cew256ans96.types.ReturnType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

/**
 * Object node in AST that represents a return statement in a block. Must be
 * the last statement in a block
 */
public class Return extends Node {

    /**
     * List of expressions in the return statement
     */
    private ArrayList<Expression> expressionList;

    /**
     * Constructing an empty return statement
     * @param pos - position of return statement in program file
     */
    public Return(String pos) {
        super(pos);
        expressionList = new ArrayList<>();
    }

    /**
     * Constructing a non-empty return statement
     * @param pos - position of return statement in program file
     */
    public Return(ArrayList<Expression> expressionList, String pos) {
        super(pos);
        this.expressionList = expressionList;
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
        ArrayList<Type> returnTypes = new ArrayList<>();
        for (Expression e: expressionList) {
            Type t = e.typeCheck(c);
            returnTypes.add(t);
        }

        ReturnType returnType = new ReturnType(returnTypes);
        setNodeType(returnType);
        return returnType;
    }

    public IRSeq translate(IRNodeFactory factory) {
        /**
         * go through list of expressions and call translate on each of them.
         * This will give us back a list of IRExpression nodes, which we
         * pass into IRReturn constructor
         */
        ArrayList<IRStmt> moveRegisterStatements = new ArrayList<>();

        ArrayList<IRExpr> irExprList = new ArrayList<>();

        for (Expression e : expressionList) {
            IRExpr irExpr = e.translate(factory);
            IRTemp regTemp = factory.generateFreshTemp();
            if (irExpr.isShortCircuit()) {
                IRESeq eseq = (IRESeq) irExpr;
                String endLabel = factory.generateFreshEndLabel();
                moveRegisterStatements.add(IRSeq.getShortCircuitSeq(
                        eseq.stmt(), regTemp, irExpr, endLabel,
                        factory.getCurrentLabel()
                    )
                );

            } else {
                moveRegisterStatements.add(factory.IRSeq(
                        factory.IRMove(regTemp, irExpr)
                ));

            }
            irExprList.add(regTemp);
        }

        for (int i=0;i<irExprList.size();i++) {
            IRTemp returnRegister = factory.IRTemp("_RV"+(i+1));
            moveRegisterStatements.add(factory.IRMove(returnRegister,
                    irExprList.get(i)));
        }

        moveRegisterStatements.add(factory.IRReturn(irExprList));

        return factory.IRSeq(moveRegisterStatements);
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
        c.startUnifiedList();
        c.printAtom("return");
        for (Expression e : expressionList) {
            e.prettyPrint(c);
        }
        c.endList();
        return c;
    }
}