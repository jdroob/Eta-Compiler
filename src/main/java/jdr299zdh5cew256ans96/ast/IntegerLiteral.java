package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRConst;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.types.IntType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.math.BigInteger;

/**
 * AST Node for integer literals. It can be used anywhere
 * where an expression can be used.
 */
public class IntegerLiteral extends Expression {

    /**
     * Integer value int literal node
     */
    private BigInteger intVal;

    /**
     * Constructor for creating integer literal object
     * 
     * @param val - value of integer
     * @param pos - position of integer literal in program file
     */
    public IntegerLiteral(String val, String pos) {
        super(pos);
        BigInteger INT_MIN = new BigInteger("-9223372036854775808"); // INT_MIN = -2^63
        BigInteger INT_MAX = new BigInteger("9223372036854775807"); // INT_MAX = 2^63 -1
        BigInteger testBelow = new BigInteger(val).subtract(INT_MIN); // testBelow < 0 if val < INT_MIN
        BigInteger testAbove = new BigInteger(val).subtract(INT_MAX); // testAbove > 0 if val > INT_MAX

        // val < INT_MIN
        if (testBelow.signum() == -1) {
            // overflow = testBelow mod -INT_MIN
            BigInteger overflow = new BigInteger(String.valueOf(testBelow.remainder(INT_MIN.negate())));
            // Roll over to (INT_MAX+1) + overflow
            intVal = new BigInteger(String.valueOf(overflow.add(INT_MAX.add(BigInteger.ONE))));
        }
        // val > INT_MAX
        else if (testAbove.signum() == 1) {
            // overflow = testAbove mod INT_MAX
            BigInteger overflow = new BigInteger(String.valueOf(testAbove.remainder(INT_MAX)));
            // Roll over to (INT_MIN-1) + overflow
            intVal = new BigInteger(String.valueOf(overflow.add(INT_MIN.subtract(BigInteger.ONE))));
        } else {
            intVal = new BigInteger(val);
        }
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

    @Override
    public long[] getLiteralValue() {
        long[] data = new long[1];
        data[0] = intVal.longValue();
        return data;
    }

    public long getVal() {
        return intVal.longValue();
    }

    public IRConst translate(IRNodeFactory factory) {
        /**
         * return IRConst object with value of integer literal
         * converted to a long
         */
        return factory.IRConst(intVal.longValue());
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
        if (intVal.signum() == -1) {
            c.startList();
            c.printAtom("-");
            c.printAtom(intVal.abs().toString());
            c.endList();
        } else {
            c.printAtom(intVal.toString());
        }
        return c;
    }

}