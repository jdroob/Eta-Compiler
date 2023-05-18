package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRSeq;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.types.RType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.types.UnitType;
import main.java.jdr299zdh5cew256ans96.types.VoidType;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.List;

/**
 * AST Node for If-else statements. They extend from if statements
 * since they also contain an expression guard and a body, but also
 * contains a body for the else part.
 */
public class Ifelse extends If {

    /**
     * Body of the else branch in the if-else statement
     */
    Statement bodyElse;

    /**
     * Constructor for creating if-else statement
     * @param e - guard expression for if statement
     * @param b - body of if statement
     * @param bodyElse - body of else statement
     * @param pos - position of if statement in program file
     */
    public Ifelse(Expression e, Statement b, Statement bodyElse, String pos) {
        super(e, b, pos);
        this.bodyElse = bodyElse;
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
        Expression guardExpr = getExpression();
        Node ifBody = getBody();

        Type exprType = guardExpr.typeCheck(c);
        Type ifBodyType;
        Type elseBodyType;
            if (exprType.equalsStr("bool")) {
                c.push();
                ifBodyType = ifBody.typeCheck(c);
                c.pop();
                c.push();
                elseBodyType = bodyElse.typeCheck(c);
                c.pop();
            } else {
                throw new SemanticError(guardExpr.getPos() + " error: " +
                        "Expected bool in guard expression, but found " +
                        exprType.toString());
            }

        RType ifBody1 = (RType) ifBodyType;
        RType elseBody1 = (RType) elseBodyType;

        if (ifBody1.equalsStr("unit") && elseBody1.equalsStr("unit")) {
            UnitType unitType = new UnitType();
            setNodeType(unitType);
            return unitType;
        }

        if (ifBody1.equalsStr("unit") && elseBody1.equalsStr("void")) {
            UnitType unitType = new UnitType(elseBody1.getReturns());
            setNodeType(unitType);
            return unitType;
        }

        if (ifBody1.equalsStr("void") && elseBody1.equalsStr("unit")) {
            UnitType unitType = new UnitType(ifBody1.getReturns());
            setNodeType(unitType);
            return unitType;
        }

        if (ifBody1.equalsStr("void") && elseBody1.equalsStr("void")) {
            if (!ifBody1.getReturns().equals(elseBody1.getReturns())) {
                // TODO: fix error statement
                throw new SemanticError("ERROR IF STATEMENT");
            }
            VoidType voidType = new VoidType(ifBody1.getReturns());
            setNodeType(voidType);
            return voidType;
        }

        UnitType unitType = new UnitType();
        setNodeType(unitType);
        return unitType;
    }

    @Override
    public IRStmt translate(IRNodeFactory factory) {
        IRSeq ifIrGen = (IRSeq) super.translate(factory);
        List<IRStmt> ifStmts = ifIrGen.stmts();
        IRStmt elseBody = bodyElse.translate(factory);
        if (getIRGuard().isOr()) {
            String newEnd = factory.generateFreshEndLabel();
            ifStmts.add(ifStmts.size()-1,
                    factory.IRJump(factory.IRName(newEnd)));
            ifStmts.add(elseBody);                                 // put the else body b/w the if body and the end label
            ifStmts.add(factory.IRLabel(newEnd));
        } else {
            IRStmt endLabel = ifStmts.remove(ifStmts.size()-1);
            ifStmts.add(elseBody);                                 // put the else body b/w the if body and the end label
            ifStmts.add(endLabel);
        }
        return factory.IRSeq(ifStmts);
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
        c.printAtom("if");
        getExpression().prettyPrint(c);
        getBody().prettyPrint(c);
        bodyElse.prettyPrint(c);
        c.endList();
        return c;
    }
}