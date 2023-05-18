package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRConst;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.
        CodeWriterSExpPrinter;
import main.java.jdr299zdh5cew256ans96.types.IntType;
import main.java.jdr299zdh5cew256ans96.types.Type;


/**
 * AST Node for character literals. It can be used anywhere
 * where an expression can be used. Value needs to be stored as a
 * String here to account for \x unicode cases.
 */
public class CharLiteral extends Expression {

    /**
     * value of char literal
     */
    private String val;

    /**
     * Constructor for creating a character literal
     * @param val - char value
     * @param pos - position of literal in program file
     */
    public CharLiteral(String val, String pos) {
        super(pos);
        this.val = val;
    }

    /**
     * Function to type check an AST node in the tree.
     *
     * @param c - Context that represents the symbol table of storing variables
     * @return the type associated with the AST node after type checking
     */
    @Override
    public Type typeCheck(Context c) {
        IntType intType = new IntType();
        setNodeType(intType);
        return intType;
    }

    public IRConst translate(IRNodeFactory factory) {
        /**
         * return IRConst object with ascii value of the character
         * literal converted to a long
         */
        int unicodeVal = Integer.parseInt(val.codePoints().mapToObj(Integer::toHexString).findFirst().get(), 16);
        return factory.IRConst(unicodeVal);
    }

    @Override
    public long[] getLiteralValue() {
        long[] data = new long[1];
        data[0] = Integer.parseInt(val.codePoints().mapToObj(Integer::toHexString).findFirst().get(), 16);
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
        c.printAtom("'" +val+ "'");
        return c;
    }
}