package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRESeq;
import main.java.jdr299zdh5cew256ans96.ir.IRSeq;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.types.ArrayType;
import main.java.jdr299zdh5cew256ans96.types.IntType;
import main.java.jdr299zdh5cew256ans96.types.NullType;
import main.java.jdr299zdh5cew256ans96.types.RecordType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.types.UnitType;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * AST Node for a local declaration with an assign. It can be used anywhere
 * where a local definition can be used
 */
public class LocalDecAssign extends LocalDeclaration {

    /**
     * Expression to assign to identifier in local declaration
     */
    private Expression expression;

    /**
     * Constructor for creating a local declaration assign object
     * 
     * @param parameter  - left hand side identifier and type
     * @param expression - right hand side expression
     * @param pos        - position of assign statement in program file
     */
    public LocalDecAssign(Parameter parameter, Expression expression,
            String pos) {
        super(parameter, pos);
        this.expression = expression;
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
        Parameter parameter = getParameter();
        TypeNode nodeType = parameter.getType();
        Type type = nodeType.typeCheck(c);
        Type exprType = expression.typeCheck(c);

        if (exprType instanceof NullType) {
            if (type instanceof RecordType || type instanceof ArrayType) {
                UnitType unitType = new UnitType();
                setNodeType(unitType);
                return unitType;
            }
            throw new SemanticError(getParameter().getPos()+" error: "+
                    "Cannot assign null type to primitive type variables");
        }

        if (!exprType.equals(type)) {
            throw new SemanticError(expression.getPos() +
                    " error: " + "Expected " + type.toString() +
                    " but found " + exprType.toString());
        }
        c.put(parameter.getId(), type);

        UnitType unitType = new UnitType();
        setNodeType(unitType);
        return unitType;
    }

    @Override
    public IRStmt translate(IRNodeFactory factory) {
        IRExpr rightSideExpr = expression.translate(factory);
        IRExpr leftSideExpr = getParameter().translate(factory);
        if (rightSideExpr.isShortCircuit()) {
            IRESeq eseq = (IRESeq) rightSideExpr;
            String endLabel = factory.generateFreshEndLabel();

            return IRSeq.getShortCircuitSeq(eseq.stmt(), leftSideExpr, rightSideExpr,
                    endLabel, factory.getCurrentLabel());
        } else {
            return factory.IRMove(leftSideExpr, rightSideExpr);
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
        c.startUnifiedList();
        c.printAtom("=");
        getParameter().prettyPrint(c);
        expression.prettyPrint(c);
        c.endList();
        return c;
    }
}