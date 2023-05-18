package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRCall;
import main.java.jdr299zdh5cew256ans96.ir.IRESeq;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.types.UnitType;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

/**
 * AST Node for local multi assignments. It can be used anywhere where a
 * single line statement can be used.
 */
public class LocalMultiAssign extends SingleLine {

    /**
     * Expression lists for left and right hand side of multi assign statement
     */
    private ArrayList<Expression> RHS;
    private ArrayList<Expression> LHS;

    /**
     * Constructor for creating a local multi assign object
     * 
     * @param parameterList  - expression list for left hand side of assignment
     * @param expressionList - expression list for right hand side of assignment
     * @param pos            - position of local multi assign in program file
     */
    public LocalMultiAssign(ArrayList<Expression> parameterList,
            ArrayList<Expression> expressionList, String pos) {
        super(pos);
        this.RHS = expressionList;
        this.LHS = parameterList;
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
        StringBuilder expectedTypeList = new StringBuilder();

        for (Expression e : LHS) {

            // adding things to context if parameter
            boolean hasType = e.hasType();
            if (hasType) {
                Parameter p = (Parameter) e;
                Type type = p.typeCheck(c);
                c.put(p.getId(), type);
                expectedTypeList.append(type.toString()).append(" ");
            } else {
                Type lType = e.typeCheck(c);
                expectedTypeList.append(lType.toString()).append(" ");
            }
        }

        StringBuilder actualTypeList = new StringBuilder();
        for (Expression e : RHS) {
            Type rhsType = e.typeCheck(c);      // if not in context, will catch here
            actualTypeList.append(rhsType.toString()).append(" ");
        }

        String[] expectedTypeListWords = expectedTypeList.toString().split(" ");
        String[] actualTypeListWords = actualTypeList.toString().split(" ");

        if (!compareTypeLists(actualTypeListWords,expectedTypeListWords) && RHS.size() != 1) {
            throw new SemanticError(getPos() +
                    " error: expected arg types " +
                    expectedTypeList + "but found " + actualTypeList);
        }
        else {
            if (RHS.size() == 1) {
                String repeatedRHS = actualTypeList.toString();
                if (!RHS.get(0).getStoredType().getType().equals("return")) {
                    repeatedRHS =
                            actualTypeList.toString().repeat(LHS.size());
                }

                String[] repeatedRHSWords = repeatedRHS.split(" ");
                if (!compareTypeLists(repeatedRHSWords,expectedTypeListWords)) {
                    throw new SemanticError(getPos() +
                            " error: cannot assign " + actualTypeList
                            + "to objects of type " + expectedTypeList);
                }
            }
        }

        UnitType unitType = new UnitType();
        setNodeType(unitType);
        return unitType;
    }

    private boolean compareTypeLists(String[] actualWords,
                                     String[] expectedWords) {
        if (actualWords.length != expectedWords.length) {
            return false;
        }

        for (int i=0;i<actualWords.length;i++) {
            if (!actualWords[i].equals(expectedWords[i])) {
                if (!expectedWords[i].trim().equals("unit")) {
                    return false;
                }
            }
        }
        return true;
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
        c.startList();
        for (Expression e : LHS) {
            e.prettyPrint(c);
        }
        c.endList();
        for (Expression e : RHS) {
            e.prettyPrint(c);
        }
        c.endList();
        return c;
    }

    @Override
    public IRStmt translate(IRNodeFactory factory) {
        if (RHS.size() != 1) {
            ArrayList<IRStmt> stmts = new ArrayList<>();
            for (int i = 0; i < LHS.size(); i++) {
                Expression LHS_expr = LHS.get(i);
                Expression RHS_expr = RHS.get(i);
                // need to make a special sequence if we're doing an array index assignment
                if (LHS_expr.getStoredType().toString().contains("access")) {
                    stmts.add(aaTranslate(factory, (ArrayAccess) LHS_expr, RHS_expr));
                } else {
                    IRExpr leftSideExpr = LHS.get(i).translate(factory);
                    IRExpr rightSideExpr = RHS.get(i).translate(factory);
                    String endLabel = factory.generateFreshEndLabel();
                    if (rightSideExpr.isShortCircuit()) {
                        IRESeq eseq = (IRESeq) rightSideExpr;
                        stmts.add(factory.IRSeq(
                                eseq.stmt(),
                                factory.IRMove(leftSideExpr,
                                        factory.IRConst(rightSideExpr.getShortCircuitFTVal())),
                                factory.IRJump(factory.IRName(endLabel)),
                                factory.IRLabel(factory.getCurrentLabel()),
                                factory.IRMove(leftSideExpr,
                                        factory.IRConst(rightSideExpr.getOppShortCircuitFTVal())),
                                factory.IRLabel(endLabel)
                        ));
                    } else {
                        stmts.add(factory.IRMove(leftSideExpr, rightSideExpr));
                    }
                }
            }
            return factory.IRSeq(stmts);
        } else {
            IRExpr rightSideExpr = RHS.get(0).translate(factory);
            if (rightSideExpr.isShortCircuit()) {
                ArrayList<IRStmt> trueMoves = new ArrayList<>();
                ArrayList<IRStmt> falseMoves = new ArrayList<>();
                IRESeq eseq = (IRESeq) rightSideExpr;
                for (int i = 0; i < LHS.size(); i++) {
                    IRExpr leftSideExpr = LHS.get(i).translate(factory);
                    trueMoves.add(factory.IRMove(leftSideExpr,
                            factory.IRConst(1)));
                }

                for (int i = 0; i < LHS.size(); i++) {
                    IRExpr leftSideExpr = LHS.get(i).translate(factory);
                    falseMoves.add(factory.IRMove(leftSideExpr,
                            factory.IRConst(0)));
                }

                String endLabel = factory.generateFreshEndLabel();
                return (factory.IRSeq(
                        eseq.stmt(),
                        factory.IRSeq(trueMoves),
                        factory.IRJump(factory.IRName(endLabel)),
                        factory.IRLabel(factory.getCurrentFalseLabel()),
                        factory.IRSeq(falseMoves),
                        factory.IRLabel(endLabel)
                ));
            } else {
                if (RHS.get(0).getStoredType().getType().equals("return")) {
                    IRCall rhsFunc =
                            (IRCall)((IRESeq) RHS.get(0).translate(factory)).expr();
                    ArrayList<IRStmt> moveReturns = new ArrayList<>();
                    int i = 1;
                    for (Expression leftExpr : LHS) {
                        moveReturns.add(factory.IRMove(leftExpr.translate(factory),
                                factory.IRTemp("_RV"+i)));
                        i++;
                    }
                    return factory.IRSeq(
                        factory.IRCallStmt(rhsFunc.target(),
                            (long) LHS.size(), rhsFunc.args()),
                        factory.IRSeq(moveReturns)
                    );

                } else { // non short circuit expression case
                    ArrayList<IRStmt> literals = new ArrayList<>();
                    IRExpr rhs = RHS.get(0).translate(factory);
                    for (Expression lh : LHS) {
                        IRExpr leftSideExpr = lh.translate(factory);
                        literals.add(factory.IRMove(leftSideExpr, rhs));
                    }
                    return factory.IRSeq(literals);
                }
            }

        }
    }

}