package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRConst;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.
        CodeWriterSExpPrinter;
import main.java.jdr299zdh5cew256ans96.types.BoolType;
import main.java.jdr299zdh5cew256ans96.types.Type;

/**
 * AST Node for boolean literals. It can be used anywhere
 * where an expression can be used.
 */
public class BooleanLiteral extends Expression {

    /**
     * value of boolean literal
     */
    private boolean boolVal;

    /**
     * Constructor for creating boolean literal
     * @param boolVal - bool value
     * @param pos - position of literal in program file
     */
    public BooleanLiteral(boolean boolVal, String pos) {
        super(pos);
        this.boolVal = boolVal;
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
        BoolType boolType = new BoolType();
        setNodeType(boolType);
        return boolType;
    }

    public IRConst translate(IRNodeFactory factory) {
        /**
         * return IRConst object. Pass in a 1 if the boolVal is true
         * and 0 if the boolVal is false
         */
        if (boolVal) {
            return factory.IRConst(1);
        }

        return factory.IRConst(0);
    }

    @Override
    public long[] getLiteralValue() {
        long[] data = new long[1];
        data[0] = boolVal ? 1 : 0;
        return data;
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
        c.printAtom(String.valueOf(boolVal));
        return c;
    }
}