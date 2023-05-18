package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRSeq;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import main.java.jdr299zdh5cew256ans96.types.RType;
import main.java.jdr299zdh5cew256ans96.types.ReturnType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.types.UnitType;
import main.java.jdr299zdh5cew256ans96.types.VoidType;

import java.util.ArrayList;

/**
 * AST Node for blocks. Contains a list of statements
 * which represents the statements inside of the block.
 * If the block has a return type, it needs to end with it,
 * so this is why the return statement is a separate attribute
 * rather than part of the statement list.
 */
public class Block extends Statement {

    /**
     * List of statements that are part of the block and a return statement
     * that must be at the end of the block
     */
    private ArrayList<Statement> statements;
    private Return returnStatement;
    private boolean returns;

    /**
     * Constructor for creating a block without a return statement
     * @param statements - list of statements in block
     * @param pos - position of block in the program file
     */
    public Block(ArrayList<Statement> statements, String pos) {
        super(pos);
        this.statements = statements;
        this.returnStatement = new Return(pos);
        returns = false;
    }

    /**
     * Constructor for creating a block with a return statement
     * @param statements - list of statements in block
     * @param returnStatement - return statement at the end of the block
     * @param pos - position of block in the program file
     */
    public Block(ArrayList<Statement> statements, Return returnStatement,
                 String pos) {
        super(pos);
        this.statements = statements;
        this.returnStatement = returnStatement;
        returns = true;
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
        ReturnType returnOutput = null;
        RType returnOutputType = null;
        int firstVoidIndex = 0;

        for (int i = 0; i < statements.size(); i++) {
            Type statementType = statements.get(i).typeCheck(c);
            RType stmtTypeCheck = (RType) statementType;

            if (stmtTypeCheck.equalsStr("void") || (stmtTypeCheck.equalsStr(
                    "unit") && !stmtTypeCheck.isEmpty())) {
                returnOutput = stmtTypeCheck.getReturns();
                returnOutputType = stmtTypeCheck;
                firstVoidIndex = i;
                break;
            }
        } 

        if (returnOutput != null) {
            for (int i = firstVoidIndex+1; i<statements.size(); i++) {
                Type statementType = statements.get(i).typeCheck(c);
                RType stmtTypeCheck = (RType) statementType;

                if (stmtTypeCheck.equalsStr("void") || (stmtTypeCheck.equalsStr(
                        "unit") && !stmtTypeCheck.isEmpty())) {
                    if (!returnOutput.equals(stmtTypeCheck.getReturns())) {
                        throw new SemanticError(statements.get(firstVoidIndex)
                                .getPos()+" error: return type mismatch in block");
                    }
                }
            }
        }

        if (returnOutput == null) {
            if (returns) {
                ReturnType returnType = (ReturnType) returnStatement.typeCheck(c);
                VoidType voidType = new VoidType(returnType);
                setNodeType(voidType);
                return voidType;
            } else {
                UnitType unitType = new UnitType();
                setNodeType(unitType);
                return unitType;
            }
        } else {
            if (returns) {
                ReturnType returnType = (ReturnType) returnStatement.typeCheck(c);
                if (!returnOutput.equals(returnType)) {
                    throw new SemanticError(returnStatement.getPos()+
                            " error: return type mismatch in block");
                }
            } else {
                if (returnOutputType.equalsStr("unit")
                        && !returnOutput.isEmpty()) {
                    throw new SemanticError(statements.get(firstVoidIndex)
                            .getPos()+" error: cannot have return statement" +
                            " here if there is not return type at the end of block");
                }
            }

            VoidType voidType = new VoidType(returnOutput);
            setNodeType(voidType);
            return voidType;
        }
        
    }

    public IRSeq translate(IRNodeFactory factory) {
        /**
         * go through the list of statements in the block and call
         * translate on each one - each should give you back an IR statement
         * node (could be an IR Seq node, which is still just an IR statement
         * node). Pass all of these IR statement nodes to IRSeq constructor
         *
         * check if returns, if it does then call translate on return object.
         * This will return an IRReturn object. Add this IRReturn object to the
         * end of the IR Seq node. Return IRSeq
         */
        ArrayList<IRStmt> irStmts = new ArrayList<>();
        for (Statement statement : statements) {
            IRStmt stmt = statement.translate(factory);
            if (stmt.isValid()) {
                irStmts.add(statement.translate(factory));
            }
        }

        if (returns) {
            irStmts.addAll(returnStatement.translate(factory).stmts());
        }
        return new IRSeq(irStmts);
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
        for (Statement s : statements) {
            s.prettyPrint(c);
        }
        if (returns)
            returnStatement.prettyPrint(c);
        c.endList();
        return c;
    }

}