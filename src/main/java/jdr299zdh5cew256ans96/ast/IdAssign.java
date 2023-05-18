package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRESeq;
import main.java.jdr299zdh5cew256ans96.ir.IRSeq;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.types.ArrayType;
import main.java.jdr299zdh5cew256ans96.types.NullType;
import main.java.jdr299zdh5cew256ans96.types.RecordType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.types.UnitType;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * AST Node for assigning values to identifiers. These are statements that
 * would appear inside blocks. They contain an expression that gets
 * assigned to the given identifier
 */
public class IdAssign extends SingleLine {

    /**
     * id for assignment - will be either an identifier or an array
     * access enforced by the grammar along with an expression to assign the
     * id to
     */
    private Expression expression;
    private Expression id;

    /**
     * Constructor for creating a new id assign object
     * 
     * @param expression - expression to assign to identifier / array access
     * @param id         - identifier to assign expression to
     * @param pos        - position of id assign in program file
     */
    public IdAssign(Expression expression, Expression id, String pos) {
        super(pos);
        this.expression = expression;
        this.id = id;
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
        Type idType = id.typeCheck(c);
        Type exprType = expression.typeCheck(c);

        if (exprType instanceof NullType) {
            if (idType instanceof RecordType || idType instanceof ArrayType) {
                UnitType unitType = new UnitType();
                setNodeType(unitType);
                return unitType;
            }
            throw new SemanticError(id.getPos()+" error: "+
                    "Cannot assign null type to primitive type variables");
        }

        if (idType.equalsStr("unit")) { // if id is underscore
            UnitType unitType = new UnitType();
            setNodeType(unitType);
            return unitType;
        }
        if (!idType.equals(exprType)) {
            throw new SemanticError(expression.getPos() + " error: expected " +
                    "type " + idType.toString() + " but got " + exprType.toString());
        }

        UnitType unitType = new UnitType();
        setNodeType(unitType);
        return unitType;
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
        id.prettyPrint(c);
        expression.prettyPrint(c);
        c.endList();
        return c;
    }

    @Override
    public IRStmt translate(IRNodeFactory factory) {
        if (id.getStoredType().toString().contains("access")) {
            return aaTranslate(factory, (ArrayAccess) id, expression);
        }

        IRExpr rightSideExpr = expression.translate(factory);
        IRExpr leftSideExpr = id.translate(factory);
        if (rightSideExpr.isShortCircuit()) {
            IRESeq eseq = (IRESeq) rightSideExpr;
            String endLabel = factory.generateFreshEndLabel();

            return IRSeq.getShortCircuitSeq(eseq.stmt(), leftSideExpr, rightSideExpr,
                    endLabel, factory.getCurrentLabel());

        } else {
            return factory.IRMove(leftSideExpr, rightSideExpr);
        }
    }

}