package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRBinOp;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRMove;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;
import main.java.jdr299zdh5cew256ans96.types.ArrayType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

;

/**
 * AST Node for Array literals. It can be used anywhere
 * where an expression can be used. Contains a list of
 * Nodes which represent elements in the array literal.
 */
public class Array extends Expression {

    /**
     * A list of expressions representing the elements of this array literal
     */
    private ArrayList<Expression> arrayContents;

    private int length;

    /**
     * Constructor for creating an array literal with an expression list
     * 
     * @param arrayContents - expression list of array contents
     * @param pos           - position of AST node in program file
     */
    public Array(ArrayList<Expression> arrayContents, String pos) {
        super(pos);
        this.arrayContents = arrayContents;
        length = arrayContents.size();
    }

    public ArrayList<Expression> getArrayContents() {
        return arrayContents;
    }

    /**
     * Constructor for creating an empty array literal
     * 
     * @param pos - position of AST node in program file
     */
    public Array(String pos) {
        super(pos);
        arrayContents = new ArrayList<>();
    }

    public int length() {
        return length;
    }

    @Override
    public IRExpr translate(IRNodeFactory factory) {
        int n = length();
        ArrayList<IRStmt> movs = new ArrayList<>();
        IRTemp tm = factory.generateFreshTemp();
        IRTemp tn = factory.IRTemp("tn");

        IRMove malloc = factory.IRMove(
                tm,
                factory.IRCall(factory.IRName("_eta_alloc"),
                        factory.IRConst(n * 8 + 8)
                )
        );
        movs.add(malloc);
        // store the length of the array in memory
        IRMove movLength = factory.IRMove(
                factory.IRMem(tm),
                factory.IRConst(n));
        movs.add(movLength);

        // move array elements into memory
        for (int i = 0; i < arrayContents.size(); i++) {
            IRMove mov = factory.IRMove(
                    factory.IRMem(
                            factory.IRBinOp(
                            IRBinOp.OpType.ADD,
                                    tm,
                                    factory.IRConst((i + 1) * 8)
                        )
                    ),
                    arrayContents.get(i).translate(factory)
            );
            movs.add(mov);
        }

        return factory.IRESeq(
                factory.IRSeq(movs),
                // move the memory pointer up 8 bytes to point to the first element
                factory.IRBinOp(IRBinOp.OpType.ADD, tm, factory.IRConst(8)));
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
        Expression firstExpr = arrayContents.get(0);
        Type firstExprType = firstExpr.typeCheck(c);
        String firstExprTypeStr = firstExprType.toString();
        for (int i = 1; i < arrayContents.size(); i++) {
            Expression arrayContent = arrayContents.get(i);
            Type arrayContentType = arrayContent.typeCheck(c);
            if (!arrayContentType.equalsStr(firstExprTypeStr)) {
                throw new SemanticError(arrayContent.getPos() + " error: " +
                        "Arrays cannot have mismatched types. Expected " +
                        firstExprTypeStr + " but found " +
                        arrayContentType.toString());
            }
        }

        ArrayType arrayType = new ArrayType(firstExprTypeStr);
        setNodeType(arrayType);
        return arrayType;
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
        for (Expression n : arrayContents) {
            n.prettyPrint(c);
        }
        c.endList();
        return c;
    }

}