package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRBinOp;
import main.java.jdr299zdh5cew256ans96.ir.IRESeq;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRMove;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRSeq;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;
import main.java.jdr299zdh5cew256ans96.types.FuncType;
import main.java.jdr299zdh5cew256ans96.types.IntType;
import main.java.jdr299zdh5cew256ans96.types.NullType;
import main.java.jdr299zdh5cew256ans96.types.RecordType;
import main.java.jdr299zdh5cew256ans96.types.ReturnType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

/**
 * AST Node for function calls. It can be used anywhere
 * where an expression can be used. Contains a list of
 * expressions that are arguments for the function.
 */
public class FunctionCall extends Expression {

    /**
     * Contains identifier for function name and a list of expressions that
     * get passed into the function
     */
    private ArrayList<Expression> arguments;
    private Identifier functionName;
    private String abiName;

    /**
     * Constructor for creating a function call with a list of arguments
     * @param arguments - list of arguments for function call
     * @param functionName - name of function
     * @param pos - position of function call in program file
     */
    public FunctionCall(ArrayList<Expression> arguments, String functionName,
                        String pos) {
        super(pos);
        this.arguments = arguments;
        this.functionName = new Identifier(functionName, pos);
    }

    /**
     * Constructor for creating a function call with one argument
     * @param argument - argument in function call
     * @param functionName - name of function
     * @param pos - position of function call in program file
     */
    public FunctionCall(Expression argument, String functionName, String pos) {
        super(pos);
        arguments = new ArrayList<>();
        arguments.add(argument);
        this.functionName = new Identifier(functionName, pos);
    }

    /**
     * Constructor for creating a function call with no arguments
     * @param functionName - name of function
     * @param pos - position of function call in program file
     */
    public FunctionCall(String functionName, String pos) {
        super(pos);
        arguments = new ArrayList<>();
        this.functionName = new Identifier(functionName, pos);
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
        StringBuilder actualTypeString = new StringBuilder();
        for (Expression e : arguments) {
            Type exprType = e.typeCheck(c);
            actualTypeString.append(exprType.toString()).append(" ");
        }

        if (functionName.getName().equals("length")) {
            if (arguments.size() == 1 && actualTypeString.toString().contains("array")) {
                ArrayList<Type> intReturnType = new ArrayList<>();
                intReturnType.add(new IntType());
                ReturnType returnType = new ReturnType(intReturnType);
                setNodeType(returnType);
                return returnType;
            } else {
                throw new SemanticError(functionName.getPos()+
                        " error: expected arg types: array but found "+
                        actualTypeString);
            }
        }


        Type funCallType = functionName.typeCheck(c);

        if (funCallType instanceof RecordType r) {
            for (int i=0;i<arguments.size();i++) {
                Type expressionType = arguments.get(i).typeCheck(c);
                try {
                    if (!expressionType.equals(r.getArgType(i))) {
                        if (expressionType instanceof NullType && r.getArgType(i) instanceof RecordType) {
                            setNodeType(r);
                            return r;
                        }
                        throw new SemanticError(functionName.getPos() +
                                " error: invalid argument types to " +
                                "constructor " + functionName.getName());
                    }
                } catch (IndexOutOfBoundsException io) {
                    throw new SemanticError(functionName.getPos() +
                            " error: invalid argument types to " +
                            "constructor " + functionName.getName());
                }
            }
            setNodeType(r);
            return r;
        }

        if (!funCallType.equalsStr("function")) {
            throw new SemanticError(functionName.getPos()+" error: "
                    +functionName.getName()+" is not a function");
        }
        FuncType funCall = (FuncType) funCallType;
        abiName = funCall.getABIName();

        // confirming provided args match expected
        ArrayList<Parameter> paramTypeList = funCall.getArguments();
        StringBuilder expectedTypeString = new StringBuilder();

        // expected
        for (Parameter p : paramTypeList) {
            TypeNode type = p.getType();
            Type paramType = type.typeCheck(c);
            expectedTypeString.append(paramType.toString()).append(" ");
        }
 
        if (!expectedTypeString.toString().equals(actualTypeString.toString())) {
            throw new SemanticError(functionName.getPos()+
                    " error: expected arg types: " +
                    ""+expectedTypeString+" but found "+actualTypeString);
        }

        ReturnType returnType = new ReturnType(funCall.getReturnTypes());
        setNodeType(returnType);
        return returnType;
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
        c.printAtom(functionName.getName());
        for (Expression e : arguments) {
            e.prettyPrint(c);
        }
        c.endList();
        return c;
    }

    public String getAbiName() {
        return abiName;
    }

    public ArrayList<Expression> getArgs() {
        return arguments;
    }

    @Override
    public IRExpr translate(IRNodeFactory factory) {
        if (functionName.getStoredType() instanceof RecordType rt) {
            return translateRecord(factory,rt.getNumFields(),arguments);
        }
        ArrayList<IRExpr> irExprs = new ArrayList<>();
        ArrayList<IRStmt> shortCircuitSideEffects = new ArrayList<>();
        for (Expression arg : arguments) {
            IRExpr irExpr = arg.translate(factory);
            if (irExpr.isShortCircuit()) {
                IRESeq eseq = (IRESeq) irExpr;
                IRTemp boolValTemp = factory.generateFreshTemp();
                String endLabel = factory.generateFreshEndLabel();
                IRSeq seq = IRSeq.getShortCircuitSeq(eseq.stmt(), boolValTemp, irExpr,
                        endLabel, factory.getCurrentLabel());
                shortCircuitSideEffects.addAll(seq.stmts());
                irExprs.add(boolValTemp);
            } else {
                irExprs.add(irExpr);
            }
        }
        return factory.IRESeq(
                factory.IRSeq(shortCircuitSideEffects),factory.IRCall(factory.IRName(abiName),
                        irExprs)
        );
    }

    private IRExpr translateRecord(IRNodeFactory factory, int numFields,
                                   ArrayList<Expression> args) {
        ArrayList<IRStmt> moveStmts = new ArrayList<>();

        IRTemp size = factory.generateFreshTemp();
        IRMove moveNumFields = factory.IRMove(size, factory.IRConst(numFields));
        moveStmts.add(moveNumFields);

        IRTemp recordPtr = factory.generateFreshTemp();

        IRMove alloc = factory.IRMove(
                recordPtr,
                factory.IRCall(
                        factory.IRName("_eta_alloc"),
                        factory.IRBinOp(
                                IRBinOp.OpType.ADD,
                                factory.IRBinOp(
                                        IRBinOp.OpType.MUL,
                                        size, factory.IRConst(8)),
                                factory.IRConst(8))
                )
        );
        moveStmts.add(alloc);

        IRMove storeLength = factory.IRMove(
                factory.IRMem(recordPtr),
                size
        );
        moveStmts.add(storeLength);

        int offset = 8;
        for (Expression e : args) {
            IRExpr irArg = e.translate(factory);
            IRMove storeArg = factory.IRMove(
                    factory.IRMem(
                            factory.IRBinOp(
                                    IRBinOp.OpType.ADD,
                                    factory.IRConst(offset),
                                    recordPtr
                            )
                    ),
                    irArg
            );
            moveStmts.add(storeArg);
            offset+=8;
        }

        return factory.IRESeq(
                factory.IRSeq(moveStmts),
                recordPtr
        );
    }
}