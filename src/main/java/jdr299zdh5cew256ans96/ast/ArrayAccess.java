package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.*;
import main.java.jdr299zdh5cew256ans96.types.*;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.Optional;
import java.util.ArrayList;

/**
 * AST Node for Array access expressions. It can be used anywhere
 * where an expression can be used. Contains an identifier
 * for the array we are trying to access and an expression
 * to access an element.
 */
public class ArrayAccess extends Expression {

    /**
     * Expressions representing the array, and two expressions for accessing
     * the elements in the array. The second one can be null since the array
     * doesn't have to be a multi type array + AST node type
     */
    private Expression id;
    private Expression expr1;
    private Optional<Expression> expr2;

    /**
     * Constructor for accessing a single dimension array
     *
     * @param id         - array to be accessed
     * @param expression - index to access in the array
     * @param pos        - position of node in AST
     */
    public ArrayAccess(Expression id, Expression expression, String pos) {
        super(pos);
        this.id = id;
        this.expr1 = expression;
        this.expr2 = Optional.empty();
    }

    /**
     * Constructor for accessing a multi dimension array
     *
     * @param id    - array to be accessed
     * @param expr1 - first index to access in the array
     * @param expr2 - second index to access in the array
     * @param pos   - position of node in AST
     */
    public ArrayAccess(Expression id, Expression expr1, Expression expr2,
            String pos) {
        super(pos);
        this.id = id;
        this.expr1 = expr1;
        this.expr2 = Optional.of(expr2);
    }

    public Expression getId() {
        return id;
    }

    public Expression getExpr1() {
        return expr1;
    }

    public boolean expr2isPresent() {
        return expr2.isPresent();
    }

    public Expression getExpr2() {
        if (expr2.isPresent()) {
            return expr2.get();
        }
        System.out.println("expr2 called with empty expr2");
        return null;
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
        Type expr1Type = expr1.typeCheck(c);
        if (!expr1Type.equalsStr("int")) {
            throw new SemanticError(expr1.getPos() +
                    " error: provided non-int type in array access expression");
        }
        if (expr2.isPresent()) {
            Type expr2Type = expr2.get().typeCheck(c);
            if (!expr2Type.equalsStr("int")) {
                throw new SemanticError(expr2.get().getPos() + " error: " +
                        "provided non-int type in array access expression");
            }
        }

        // context check is taken care of in the Identifier typecheck
        Type arrType = id.typeCheck(c);

        if (expr2.isEmpty()) {
            if (arrType.equalsStr("int array")) {
                IntType intType = new IntType();
                setNodeType(new ArrayAccessType("int"));
                return intType;
            }
            if (arrType.equalsStr("bool array")) {
                BoolType boolType = new BoolType();
                setNodeType(new ArrayAccessType("bool"));
                return boolType;
            }
            if (arrType.equalsStr("int array array")) {
                ArrayType intArrayType = new ArrayType("int");
                setNodeType(new ArrayAccessType("int array"));
                return intArrayType;
            }
            if (arrType.equalsStr("bool array array")) {
                ArrayType boolArrayType = new ArrayType("bool");
                setNodeType(new ArrayAccessType("bool array"));
                return boolArrayType;
            }

        } else {
            // expr2 is not null
            if (arrType.equalsStr("int array")) {
                throw new SemanticError(id.getPos() +
                        " error: identifier is not a multi-array");
            }
            if (arrType.equalsStr("bool array")) {
                throw new SemanticError(id.getPos() +
                        " error: identifier is not a multi-array");
            }
            if (arrType.equalsStr("int array array")) {
                IntType intType = new IntType();
                setNodeType(new ArrayAccessType("int"));
                ;
                return intType;
            }
            if (arrType.equalsStr("bool array array")) {
                BoolType boolType = new BoolType();
                setNodeType(new ArrayAccessType("bool"));
                return boolType;
            }

        }
        throw new SemanticError(id.getPos() + " error: identifier is not " +
                "an array");
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
        c.printAtom("[]");

        if (expr2.isPresent()) {
            c.startList();
            c.printAtom("[]");
            id.prettyPrint(c);
            expr1.prettyPrint(c);
            expr2.get().prettyPrint(c);
            c.endList();
        } else {
            id.prettyPrint(c);
            expr1.prettyPrint(c);
        }
        c.endList();
        return c;
    }

    @Override
    public IRExpr translate(IRNodeFactory factory) {
        IRExpr arrayExpr = id.translate(factory);
        IRExpr access1 = expr1.translate(factory);

        IRTemp t_a = factory.generateFreshTemp();
        IRTemp t_i = factory.generateFreshTemp();
        ArrayList<IRStmt> stmts = new ArrayList<>();
        if (expr2isPresent()) {
            IRExpr access2 = getExpr2().translate(factory);
            /*
             * The simplest thing to do is to sort of "remanuver" such that you
             * can reuuse the code below. To do this, you just need to move the inner
             * array into t_a, and access2 into t_i. You can get the inner array mem
             * location by accessing the mem location within the outer array the same
             * way you do for a single array access.
             */

            // index to access from outer array
            IRTemp t_n = factory.generateFreshTemp();
            // starting mem location of outer array
            IRTemp t_m = factory.generateFreshTemp();

            IRMove tnMove = factory.IRMove(t_n, access1);
            stmts.add(tnMove);
            IRMove tmMove = factory.IRMove(t_m, arrayExpr);
            stmts.add(tmMove);

            IRExpr UGT = factory.IRBinOp(IRBinOp.OpType.XOR, factory.IRConst(1),
                    factory.IRBinOp(
                            IRBinOp.OpType.ULT,
                            t_n,
                            factory.IRMem(
                                    factory.IRBinOp(
                                            IRBinOp.OpType.SUB,
                                            t_m,
                                            factory.IRConst(8)))));

            // bounds checking
            String ok_label_outer = factory.generateFreshTrueLabel();
            String error_label_outer = factory.generateFreshFalseLabel();
            String safeGuard_outer = factory.generateFreshTrueLabel();

            IRCJump cond_outer = factory.IRCJump(UGT, error_label_outer);
            IRLabel okLabel_outer = factory.IRLabel(ok_label_outer);
            IRLabel error_Label_outer = factory.IRLabel(error_label_outer);
            IRLabel safeGuardLabel_outer = factory.IRLabel(safeGuard_outer);
            ArrayList<IRExpr> exprList_outer = new ArrayList<>();
            IRCallStmt out_of_bounds_error_outer = factory.IRCallStmt(
                    factory.IRName("_eta_out_of_bounds"), (long) 0, exprList_outer);

            IRSeq errorState_outer = factory.IRSeq(factory.IRJump(factory.IRName(safeGuard_outer)),
                    error_Label_outer, out_of_bounds_error_outer, safeGuardLabel_outer);

            stmts.add(errorState_outer);
            stmts.add(cond_outer);
            stmts.add(okLabel_outer);
            /*
             * get inner array mem location from outer array
             */

            IRMem arrayExpr2 = factory.IRMem(
                    factory.IRBinOp(
                            IRBinOp.OpType.ADD,
                            t_m,
                            factory.IRBinOp(
                                    IRBinOp.OpType.MUL,
                                    factory.IRConst(8),
                                    t_n)));
            // put index to access in inner array inside t_i
            IRMove tiMove = factory.IRMove(t_i, access2);
            // put mem location of inner array in t_a
            IRMove taMove = factory.IRMove(t_a, arrayExpr2);
            stmts.add(taMove);
            stmts.add(tiMove);
        } else {
            IRMove taMove = factory.IRMove(t_a, arrayExpr);
            IRMove tiMove = factory.IRMove(t_i, access1);
            stmts.add(taMove);
            stmts.add(tiMove);
        }
        IRExpr UGT = factory.IRBinOp(IRBinOp.OpType.XOR, factory.IRConst(1),
                factory.IRBinOp(
                        IRBinOp.OpType.ULT,
                        t_i,
                        factory.IRMem(
                                factory.IRBinOp(
                                        IRBinOp.OpType.SUB,
                                        t_a,
                                        factory.IRConst(8)))));

        String ok_label = factory.generateFreshTrueLabel();
        String error_label = factory.generateFreshFalseLabel();
        String safeGuard = factory.generateFreshTrueLabel();
        IRCJump cond = factory.IRCJump(UGT, error_label);
        IRLabel okLabel = factory.IRLabel(ok_label);
        IRLabel error_Label = factory.IRLabel(error_label);
        IRLabel safeGuardLabel = factory.IRLabel(safeGuard);
        ArrayList<IRExpr> exprList = new ArrayList<>();
        IRCallStmt out_of_bounds_error = factory.IRCallStmt(
                factory.IRName("_eta_out_of_bounds"), (long) 0, exprList);

        IRSeq errorState = factory.IRSeq(factory.IRJump(factory.IRName(safeGuard)),
                error_Label, out_of_bounds_error, safeGuardLabel);

        IRMem access = factory.IRMem(
                factory.IRBinOp(
                        IRBinOp.OpType.ADD,
                        t_a,
                        factory.IRBinOp(
                                IRBinOp.OpType.MUL,
                                factory.IRConst(8),
                                t_i)));

        IRTemp accessTemp = factory.generateFreshTemp();

        IRMove accessMove = factory.IRMove(accessTemp, access);

        stmts.add(accessMove);
        stmts.add(errorState);
        stmts.add(cond);
        stmts.add(okLabel);

        return factory.IRESeq(
                factory.IRSeq(stmts), accessTemp);

    }
}