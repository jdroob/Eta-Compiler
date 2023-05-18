package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRBinOp;
import main.java.jdr299zdh5cew256ans96.ir.IRData;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRMove;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;
import main.java.jdr299zdh5cew256ans96.types.ArrayType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * AST Node for string literals. It can be used anywhere
 * where an expression can be used.
 */
public class StringLiteral extends Expression {

    /**
     * string value for literal
     */
    private String val;
    private static int count;
    private String name;

    /**
     * Constructor for creating a string literal object
     * @param val - string literal value
     * @param pos - position of string literal in program file
     */
    public StringLiteral(String val, String pos) {
        super(pos);
        this.val = val;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Function to type check an AST node in the tree.
     *
     * @param c - Context that represents the symbol table of storing variables
     * @return the type associated with the AST node after type checking
     */
    @Override
    public Type typeCheck(Context c) {
        count++;
        String constName = "string_const"+count;
        setName(constName);
        IRData strLit = new IRData(constName,getAsciiChars());
        Program.addStringLiteral(getPos(),strLit);
        ArrayType arrayType = new ArrayType("int");
        setNodeType(arrayType);
        return arrayType;
    }

    public IRExpr translate(IRNodeFactory factory) {
        String strTempName = Program.getStringLiteral(getPos()).name();

        IRTemp lengthTemp = factory.generateFreshTemp();
        IRTemp firstCharTemp = factory.generateFreshTemp();

        IRMove lengthPos = factory.IRMove(lengthTemp,
                factory.IRName(strTempName));

        IRMove firstChar = factory.IRMove(firstCharTemp,
                factory.IRBinOp(
                        IRBinOp.OpType.ADD,
                        lengthTemp,
                        factory.IRConst(8)));

        return factory.IRESeq(
                factory.IRSeq(lengthPos,firstChar),
                firstCharTemp);
    }

    public long[] getAsciiChars() {
        long[] asciiChars = new long[val.length()+1];
        asciiChars[0] = val.length();
        for (int i=0;i<val.length();i++) {
            String character = val.charAt(i)+"";
            int unicodeVal =
                    Integer.parseInt(character.codePoints().mapToObj(Integer::toHexString).findFirst().get(), 16);
            if (unicodeVal == 92 && i != val.length() - 1) {
                char nextChar = val.charAt(i+1);
                if (nextChar == 'n') {
                    asciiChars[i+1] = 10;
                } else if (nextChar == '\'') {
                    asciiChars[i+1] = 39;
                } else if (nextChar == '\"') {
                    asciiChars[i+1] = 34;
                } else if (nextChar == '\\') {
                    asciiChars[i+1] = 92;
                }
                i++;
            } else {
                asciiChars[i+1] = unicodeVal;
            }
        }
        return asciiChars;
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
        c.printAtom("\""+val+"\"");
        return c;
    }
}