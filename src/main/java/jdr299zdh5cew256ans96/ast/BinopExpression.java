package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.cli;
import main.java.jdr299zdh5cew256ans96.ir.IRBinOp;
import main.java.jdr299zdh5cew256ans96.ir.IRCJump;
import main.java.jdr299zdh5cew256ans96.ir.IRConst;
import main.java.jdr299zdh5cew256ans96.ir.IRESeq;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRJump;
import main.java.jdr299zdh5cew256ans96.ir.IRLabel;
import main.java.jdr299zdh5cew256ans96.ir.IRMem;
import main.java.jdr299zdh5cew256ans96.ir.IRMove;
import main.java.jdr299zdh5cew256ans96.ir.IRName;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;
import main.java.jdr299zdh5cew256ans96.types.ArrayType;
import main.java.jdr299zdh5cew256ans96.types.BoolType;
import main.java.jdr299zdh5cew256ans96.types.IntType;
import main.java.jdr299zdh5cew256ans96.types.NullType;
import main.java.jdr299zdh5cew256ans96.types.RecordType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * AST Node for the Binary operator expression. It can be used anywhere
 * where an expression can be used. Contains two other expressions
 * which goes around the binary operator.
 */
public class BinopExpression extends Expression {

        /**
         * Expressions which are part of the binary operator along with the
         * operator between the expressions
         */
        private Operator.BinaryOperator operator;
        private Expression leftExpr;
        private Expression rightExpr;

        /**
         * Constructor for creating a binary expression node in the AST
         * 
         * @param leftExpr  - left expression in the binary expression
         * @param operator  - binary operator
         * @param rightExpr - right expression in the binary expression
         * @param pos       - position in the program file
         */
        public BinopExpression(Expression leftExpr, Operator.BinaryOperator operator,
                        Expression rightExpr, String pos) {
                super(pos);
                this.leftExpr = leftExpr;
                this.operator = operator;
                this.rightExpr = rightExpr;
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
                Type leftExprType = leftExpr.typeCheck(c);
                Type rightExprType = rightExpr.typeCheck(c);

                String opType = operator.toString();
                if (opType.equals("+")) {
                        if (leftExprType.equalsStr("int") &&
                                        rightExprType.equalsStr("int")) {
                                IntType intType = new IntType();
                                setNodeType(intType);
                                return intType;
                        } else if (leftExprType.equalsStr("int array") &&
                                        rightExprType.equalsStr("int array")) {
                                ArrayType intArrayType = new ArrayType("int");
                                setNodeType(intArrayType);
                                return intArrayType;
                        } else if (leftExprType.equalsStr("bool array") &&
                                        rightExprType.equalsStr("bool array")) {
                                ArrayType boolArrayType = new ArrayType("bool");
                                setNodeType(boolArrayType);
                                return boolArrayType;
                        } else {
                                throw typeMismatchError(leftExprType, rightExprType, opType);
                        }
                }

                if ((opType.equals("*")) || opType.equals("-") || opType.equals("/") ||
                                opType.equals("%") || opType.equals("*>>")) {
                        if (leftExprType.equalsStr("int") &&
                                        rightExprType.equalsStr("int")) {
                                IntType intType = new IntType();
                                setNodeType(intType);
                                return intType;
                        } else {
                                throw typeMismatchError(leftExprType, rightExprType, opType);
                        }
                }

                if (opType.equals("|") || opType.equals("&") || opType.equals("==")
                                || opType.equals("!=")) {
                        if (leftExprType.equalsStr("bool") &&
                                        rightExprType.equalsStr("bool")) {
                                BoolType boolType = new BoolType();
                                setNodeType(boolType);
                                return boolType;
                        }

                }

                if (opType.equals("==") && leftExprType instanceof RecordType && rightExprType instanceof RecordType) {
                        BoolType boolType = new BoolType();
                        setNodeType(boolType);
                        return boolType;
                }

                if (opType.equals("==") && leftExprType instanceof RecordType && rightExprType instanceof NullType) {
                        BoolType boolType = new BoolType();
                        setNodeType(boolType);
                        return boolType;
                }

                if (opType.equals("!=") && leftExprType instanceof RecordType && rightExprType instanceof RecordType) {
                        BoolType boolType = new BoolType();
                        setNodeType(boolType);
                        return boolType;
                }

                if (opType.equals("!=") && leftExprType instanceof RecordType && rightExprType instanceof NullType) {
                        BoolType boolType = new BoolType();
                        setNodeType(boolType);
                        return boolType;
                }

                if (opType.equals("==") || opType.equals("!=") || opType.equals("<") ||
                                opType.equals("<=") || opType.equals(">") ||
                                opType.equals(">=")) {
                        if (leftExprType.equalsStr("int") &&
                                        rightExprType.equalsStr("int")) {
                                BoolType boolType = new BoolType();
                                setNodeType(boolType);
                                return boolType;
                        } else if ((leftExprType.equalsStr("int array") &&
                                        rightExprType.equalsStr("int array")) ||
                                        (leftExprType.equalsStr("bool array") &&
                                                        rightExprType.equalsStr("bool array"))) {
                                BoolType boolType = new BoolType();
                                setNodeType(boolType);
                                return boolType;
                        } else if ((leftExprType.equalsStr("int array array") &&
                                        rightExprType.equalsStr("int array array")) ||
                                        (leftExprType.equalsStr("bool array array") &&
                                                        rightExprType.equalsStr("bool array array"))) {
                                BoolType boolType = new BoolType();
                                setNodeType(boolType);
                                return boolType;
                        } else {
                                throw typeMismatchError(leftExprType, rightExprType, opType);
                        }
                }

                throw typeMismatchError(leftExprType, rightExprType, opType);
        }

        private IRESeq concatFromPointers(ArrayList<IRTemp> temps,
                        IRNodeFactory factory) {
                ArrayList<IRStmt> moveStmts = new ArrayList<>();

                // move lengths into temps for each array pointer
                ArrayList<IRTemp> lengthTemps = new ArrayList<>();
                for (IRTemp t : temps) {
                        IRTemp len1Temp = factory.generateFreshTemp();
                        lengthTemps.add(len1Temp);
                        IRMove len1Move = factory.IRMove(
                                        len1Temp,
                                        factory.IRMem(
                                                        factory.IRBinOp(IRBinOp.OpType.SUB, t,
                                                                        factory.IRConst(8)))

                        );
                        moveStmts.add(len1Move);
                }

                // add together all the lengths
                IRTemp lenConcatTemp = factory.generateFreshTemp();
                IRMove moveFirstTemp = factory.IRMove(
                                lenConcatTemp,
                                lengthTemps.get(0));
                moveStmts.add(moveFirstTemp);

                for (int i = 1; i < lengthTemps.size(); i++) {
                        IRMove totalLengthMove = factory.IRMove(
                                        lenConcatTemp,
                                        factory.IRBinOp(IRBinOp.OpType.ADD, lengthTemps.get(i),
                                                        lenConcatTemp));
                        moveStmts.add(totalLengthMove);
                }

                // allocate memory for concat string
                IRTemp concatPtr = factory.generateFreshTemp();
                IRMove alloc = factory.IRMove(
                                concatPtr,
                                factory.IRCall(
                                                factory.IRName("_eta_alloc"),
                                                factory.IRBinOp(
                                                                IRBinOp.OpType.ADD,
                                                                factory.IRBinOp(
                                                                                IRBinOp.OpType.MUL,
                                                                                lenConcatTemp, factory.IRConst(8)),
                                                                factory.IRConst(8))));
                moveStmts.add(alloc);

                // store concatenated length in mem[-1]
                IRMove storeLength = factory.IRMove(
                                factory.IRMem(
                                                concatPtr),
                                lenConcatTemp);
                moveStmts.add(storeLength);

                // iterator through array
                IRTemp ti = factory.generateFreshTemp();
                IRMove initTi = factory.IRMove(ti,
                                factory.IRBinOp(
                                                IRBinOp.OpType.ADD,
                                                concatPtr,
                                                factory.IRConst(8)));
                moveStmts.add(initTi);

                for (int i = 0; i < temps.size(); i++) {

                        //System.out.println("current temp:" + temps.get(i)
                        // .name());

                        IRTemp tiTemp = factory.generateFreshTemp();

                        IRMove saveTi = factory.IRMove(tiTemp, temps.get(i));
                        moveStmts.add(saveTi);

                        // header for first loop
                        String freshHeaderLabel = factory.generateFreshHeaderLabel();
                        IRLabel lh = factory.IRLabel(freshHeaderLabel);
                        moveStmts.add(lh);

                        // first loop check
                        String falseLabel = factory.generateFreshLabel();
                        IRCJump checkCounter = factory.IRCJump(
                                        factory.IRBinOp(IRBinOp.OpType.XOR, factory.IRConst(1),
                                                        factory.IRBinOp(IRBinOp.OpType.GT,
                                                                        lengthTemps.get(i),
                                                                        factory.IRConst(0))),
                                        falseLabel);
                        moveStmts.add(checkCounter);
                        // For Basic Block purposes
                        moveStmts.add(factory.IRLabel(factory.generateFreshTrueLabel()));

                        // move logic
                        IRMove moveFromArray1 = factory.IRMove(
                                        factory.IRMem(ti),
                                        factory.IRMem(tiTemp));
                        moveStmts.add(moveFromArray1);

                        // update counter variables for loop
                        IRMove decrementLen1 = factory.IRMove(
                                        lengthTemps.get(i),
                                        factory.IRBinOp(IRBinOp.OpType.SUB, lengthTemps.get(i), factory.IRConst(1)));
                        moveStmts.add(decrementLen1);

                        IRMove incTI = factory.IRMove(
                                        ti,
                                        factory.IRBinOp(IRBinOp.OpType.ADD, ti, factory.IRConst(8)));
                        moveStmts.add(incTI);

                        IRMove incT1 = factory.IRMove(
                                        tiTemp,
                                        factory.IRBinOp(IRBinOp.OpType.ADD, tiTemp,
                                                        factory.IRConst(8)));
                        moveStmts.add(incT1);

                        IRJump restartLoop = factory.IRJump(factory.IRName(freshHeaderLabel));
                        moveStmts.add(restartLoop);

                        // exit loop
                        moveStmts.add(factory.IRLabel(falseLabel));
                }

                IRMove movePtr = factory.IRMove(concatPtr,
                        factory.IRBinOp(IRBinOp.OpType.ADD, concatPtr,
                                factory.IRConst(8)));
                moveStmts.add(movePtr);

                return factory.IRESeq(
                                factory.IRSeq(moveStmts),
                                concatPtr);

        }

        private IRESeq getPointer(Expression e, IRNodeFactory factory) {
                if (e instanceof Array) {
                        Array arrayLiteral = (Array) e;
                        int length = arrayLiteral.length();

                        IRTemp arrayPtr = factory.generateFreshTemp();
                        IRTemp lenArrayTemp = factory.generateFreshTemp();

                        ArrayList<IRStmt> moveStmts = new ArrayList<>();

                        IRMove lengthMove = factory.IRMove(
                                        lenArrayTemp,
                                        factory.IRConst(length));
                        moveStmts.add(lengthMove);

                        IRMove alloc = factory.IRMove(
                                        arrayPtr,
                                        factory.IRCall(
                                                        factory.IRName("_eta_alloc"),
                                                        factory.IRBinOp(
                                                                        IRBinOp.OpType.ADD,
                                                                        factory.IRBinOp(
                                                                                        IRBinOp.OpType.MUL,
                                                                                        lenArrayTemp,
                                                                                        factory.IRConst(8)),
                                                                        factory.IRConst(8))));
                        moveStmts.add(alloc);

                        IRMove lengthToMem = factory.IRMove(
                                        factory.IRMem(arrayPtr),
                                        lenArrayTemp);
                        moveStmts.add(lengthToMem);

                        IRMove moveArrPtrToTemp = factory.IRMove(
                                        arrayPtr,
                                        factory.IRBinOp(
                                                        IRBinOp.OpType.ADD,
                                                        arrayPtr,
                                                        factory.IRConst(8)));
                        moveStmts.add(moveArrPtrToTemp);

                        ArrayList<Expression> vals = arrayLiteral.getArrayContents();

                        IRTemp ti = factory.generateFreshTemp();
                        IRMove initTi = factory.IRMove(ti, arrayPtr);
                        moveStmts.add(initTi);

                        for (Expression expr : vals) {
                                int intVal = (int) expr.getLiteralValue()[0];
                                IRMove moveIntVal = factory.IRMove(
                                                factory.IRMem(ti), factory.IRConst(intVal));
                                moveStmts.add(moveIntVal);

                                IRMove incTI = factory.IRMove(
                                                ti,
                                                factory.IRBinOp(IRBinOp.OpType.ADD, ti, factory.IRConst(8)));
                                moveStmts.add(incTI);
                        }

                        return factory.IRESeq(
                                        factory.IRSeq(moveStmts),
                                arrayPtr);

                        // TODO: global arrays are currently overwriting each other
                        // so we can currently only have one at a time
                } else if (e instanceof Identifier) {
                        Identifier id = (Identifier) e;
                        // TODO: Try creating a new array here?
                        // global identifiers point to address where
                        // corresponding value is stored
                        if (id.global()) {
                                String name = "_" + id.getName();
                                IRExpr globalPtr = factory.IRBinOp(
                                                IRBinOp.OpType.ADD,
                                                factory.IRName(name),
                                                factory.IRConst(8));

                                IRTemp tempPtr = factory.generateFreshTemp();
                                IRMove movePtrToTemp = factory.IRMove(
                                                tempPtr,
                                                globalPtr);

                                return factory.IRESeq(
                                                movePtrToTemp,
                                                tempPtr);
                        }

//                        ArrayList<IRStmt> stmts = new ArrayList<>();
//                        IRExpr translate = e.translate(factory);
//                        if (translate instanceof IRName name) {
//                                translate = factory.generateFreshTemp();
//                                IRExpr globalPtr = factory.IRBinOp(
//                                                IRBinOp.OpType.ADD,
//                                                name,
//                                                factory.IRConst(0));
//                                IRMove movePtrToTemp = factory.IRMove(
//                                                translate,
//                                                globalPtr);
//                                stmts.add(movePtrToTemp);
//                        }

                        return factory.IRESeq(
                                        factory.IRSeq(),
                                e.translate(factory));

                } else if (e instanceof StringLiteral) {
                        String name = ((StringLiteral) e).getName();
                        IRExpr stringPtr = factory.IRBinOp(
                                        IRBinOp.OpType.ADD,
                                        factory.IRName(name),
                                        factory.IRConst(8));

                        IRTemp tempPtr = factory.generateFreshTemp();
                        IRMove movePtrToTemp = factory.IRMove(
                                        tempPtr,
                                        stringPtr);

                        return factory.IRESeq(
                                        movePtrToTemp,
                                        tempPtr);

                } else if (e instanceof FunctionCall) {
                        IRESeq funcCall = e.translate(factory).lower(factory);
                        IRTemp freshTemp = factory.generateFreshTemp();
                        IRMove moveFromRV = factory.IRMove(freshTemp,
                                funcCall.expr());
                        return factory.IRESeq(
                                        factory.IRSeq(funcCall.stmt(),
                                                moveFromRV),
                                freshTemp);
                } else if (e instanceof BinopExpression) {
                        IRESeq binopEseq = (IRESeq) e.translate(factory);
                        if (binopEseq.expr() instanceof IRESeq) {
                                IRESeq eseqExpr = (IRESeq) binopEseq.expr();
                                return factory.IRESeq(
                                        factory.IRSeq(
                                                binopEseq.stmt(),
                                                eseqExpr.stmt()),
                                        eseqExpr.expr());
                        }
                        return factory.IRESeq(
                                        binopEseq.stmt(),
                                        binopEseq.expr());
                } else if (e instanceof ArrayAccess) {
                        IRESeq eseq = (IRESeq) e.translate(factory);
                        IRTemp temp = (IRTemp) eseq.expr();
                        return factory.IRESeq(
                                eseq.stmt(),
                                temp
                        );
                }

                return null;
        }

        @Override
        public IRExpr translate(IRNodeFactory factory) {
                /**
                 * Translate left and right expressions, which will give back IRExpr
                 * nodes. Pass these along with the operator into the IRBinop
                 * constructor. Their operator is an enum, so we may have to do a
                 * switch statement to pass in the correct one? there might be a
                 * better way though
                 */
                IRExpr leftIRExpr = leftExpr.translate(factory);
                // equivalent to !leftIRExpr
                IRExpr invertedLeftIRExpr = factory.IRBinOp(IRBinOp.OpType.XOR, factory.IRConst(1), leftIRExpr);
                IRExpr rightIRExpr = rightExpr.translate(factory);
                // equivalent to !rightIRExpr
                IRExpr invertedRightIRExpr = factory.IRBinOp(IRBinOp.OpType.XOR, factory.IRConst(1), rightIRExpr);

                if (Arrays.asList(cli.turnedOnOpts).contains("cf")) {
                        if (leftIRExpr.isConstant() && rightIRExpr.isConstant()) {
                                long leftConst = leftIRExpr.constant();
                                long rightConst = rightIRExpr.constant();
                                return performConstantFold(leftConst, rightConst, factory);
                        }
                }

                switch (operator) {
                        case PLUS:
                                if (leftExpr.getStoredType().toString().contains("array") &&
                                                rightExpr.getStoredType().toString().contains("array")) {
                                        IRESeq eseq1 = getPointer(leftExpr, factory);
                                        IRESeq eseq2 = getPointer(rightExpr, factory);
                                        IRTemp t1 = (IRTemp) eseq1.expr();
                                        IRTemp t2 = (IRTemp) eseq2.expr();
                                        IRTemp sideEffectReg = factory.generateFreshTemp();
                                        ArrayList<IRTemp> tempPtrs = new ArrayList<>();
                                        tempPtrs.add(sideEffectReg);
                                        tempPtrs.add(t2);

                                        return factory.IRESeq(
                                                        factory.IRSeq(
                                                                        eseq1.stmt(),
                                                                        factory.IRMove(sideEffectReg, t1),
                                                                        eseq2.stmt()),
                                                        concatFromPointers(tempPtrs, factory));
                                }
                                return factory.IRBinOp(IRBinOp.OpType.ADD, leftIRExpr,
                                                rightIRExpr);
                        case MINUS:
                                return factory.IRBinOp(IRBinOp.OpType.SUB, leftIRExpr,
                                                rightIRExpr);
                        case TIMES:
                                return factory.IRBinOp(IRBinOp.OpType.MUL, leftIRExpr,
                                                rightIRExpr);
                        case DIVIDE:
                                return factory.IRBinOp(IRBinOp.OpType.DIV, leftIRExpr,
                                                rightIRExpr);
                        case MODULO:
                                return factory.IRBinOp(IRBinOp.OpType.MOD, leftIRExpr,
                                                rightIRExpr);
                        case HIGH_MULT:
                                return factory.IRBinOp(IRBinOp.OpType.HMUL, leftIRExpr,
                                                rightIRExpr);
                        case LT:
                                return factory.IRBinOp(IRBinOp.OpType.LT, leftIRExpr,
                                                rightIRExpr);
                        case LEQ:
                                return factory.IRBinOp(IRBinOp.OpType.LEQ, leftIRExpr,
                                                rightIRExpr);
                        case GT:
                                return factory.IRBinOp(IRBinOp.OpType.GT, leftIRExpr,
                                                rightIRExpr);
                        case GEQ:
                                return factory.IRBinOp(IRBinOp.OpType.GEQ, leftIRExpr,
                                                rightIRExpr);
                        case AND:
                                String falseLabel = factory.generateFreshLabel();
                                IRExpr andExpr = IRESeq.getShortCircuitForm(
                                                invertedLeftIRExpr, invertedRightIRExpr, falseLabel,
                                                factory.IRBinOp(IRBinOp.OpType.AND, leftIRExpr, rightIRExpr),
                                                factory);
                                andExpr.setShortCircuit("and");
                                andExpr.setShortCircuitFTVal(1);
                                return andExpr;
                        case OR:
                                String trueLabel = factory.generateFreshLabel();
                                IRExpr orExpr = IRESeq.getShortCircuitForm(
                                                leftIRExpr, rightIRExpr, trueLabel,
                                                factory.IRBinOp(IRBinOp.OpType.OR, leftIRExpr, rightIRExpr),
                                                factory);
                                orExpr.setShortCircuit("or");
                                orExpr.setShortCircuitFTVal(0);
                                return orExpr;
                        case NOT_EQUAL:
                                return factory.IRBinOp(IRBinOp.OpType.NEQ,
                                                leftIRExpr, rightIRExpr);
                        case EQUALS:
                                return factory.IRBinOp(IRBinOp.OpType.EQ, leftIRExpr,
                                                rightIRExpr);
                        default:
                                return null;
                }
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
                c.printAtom(operator.toString());
                leftExpr.prettyPrint(c);
                rightExpr.prettyPrint(c);
                c.endList();
                return c;
        }

        private IRConst performConstantFold(long const1, long const2,
                        IRNodeFactory factory) {
                switch (operator) {
                        case PLUS:
                                return factory.IRConst(const1 + const2);
                        case MINUS:
                                return factory.IRConst(const1 - const2);
                        case TIMES:
                                return factory.IRConst(const1 * const2);
                        case DIVIDE:
                                return factory.IRConst(const1 / const2);
                        case MODULO:
                                return factory.IRConst(const1 % const2);
                        case LT:
                                if (const1 < const2)
                                        return factory.IRConst(1);
                                return factory.IRConst(0);
                        case LEQ:
                                if (const1 <= const2)
                                        return factory.IRConst(1);
                                return factory.IRConst(0);
                        case GT:
                                if (const1 > const2)
                                        return factory.IRConst(1);
                                return factory.IRConst(0);
                        case GEQ:
                                if (const1 >= const2)
                                        return factory.IRConst(1);
                                return factory.IRConst(0);
                        case AND:
                                boolean leftAnd = const1 == 1;
                                boolean rightAnd = const2 == 1;
                                if (leftAnd && rightAnd)
                                        return factory.IRConst(1);
                                return factory.IRConst(0);
                        case OR:
                                boolean leftOr = const1 == 1;
                                boolean rightOr = const2 == 1;
                                if (leftOr || rightOr)
                                        return factory.IRConst(1);
                                return factory.IRConst(0);
                        case NOT_EQUAL:
                                if (const1 != const2)
                                        return factory.IRConst(1);
                                return factory.IRConst(0);
                        case EQUALS:
                                if (const1 == const2)
                                        return factory.IRConst(1);
                                return factory.IRConst(0);
                        case HIGH_MULT:
                                BigInteger b1 = BigInteger.valueOf(const1);
                                BigInteger b2 = BigInteger.valueOf(const2);
                                BigInteger product = b1.multiply(b2);
                                BigInteger sr = product.shiftRight(64);
                                long result = sr.longValue();
                                return factory.IRConst(result);
                        default:
                                return null;
                }
        }

        /**
         *
         * @param leftExprType  - type of left expression
         * @param rightExprType - type of right expression
         * @param opType        - type of binary operator
         * @return Semantic error object which is thrown
         */
        private SemanticError typeMismatchError(Type leftExprType, Type rightExprType,
                        String opType) {

                return new SemanticError(leftExpr.getPos() + " error: cannot " +
                                "use binary operator " + opType + " with " +
                                leftExprType.toString() + " and " + rightExprType.toString());
        }

}