package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRESeq;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRSeq;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;
import main.java.jdr299zdh5cew256ans96.types.FuncType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.types.UnitType;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

/**
 * AST Node for procedure calls. It can be used anywhere
 * where a single line statement can be used. Contains a list of
 * expressions that are arguments for the procedure.
 */
public class ProcedureCall extends SingleLine {

    /**
     * Contains identifier for procedure name and a list of expressions that
     * get passed into the procedure
     */
    private Identifier id;
    private ArrayList<Expression> argList;
    private String abiName;

    /**
     * Constructor for creating a procedure call with a list of arguments
     * @param id - procedure name
     * @param argList - list of arguments procedure call takes
     * @param pos - position of procedure call in program file
     */
    public ProcedureCall(String id, ArrayList<Expression> argList, String pos) {
        super(pos);
        this.id = new Identifier(id, pos);
        this.argList = argList;
    }

    /**
     * Constructor for creating a procedure call that doesn't have any arguments
     * @param id - procedure name
     * @param pos - position of procedure call in program file
     */
    public ProcedureCall(String id, String pos) {
        super(pos);
        this.id = new Identifier(id, pos);
        argList = new ArrayList<>();
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
        Type procCallType = id.typeCheck(c);
        
        FuncType procCall = (FuncType) procCallType;
        abiName = procCall.getABIName();
        if (!procCall.equalsStr("function")) {
            throw new SemanticError(id.getPos()+" error: "+
                    id.getName()+" is not a procedure");
        }
        ArrayList<Parameter> paramTypeList = procCall.getArguments();
        StringBuilder expectedTypeString = new StringBuilder();
        StringBuilder actualTypeString = new StringBuilder();

        // expected
        for (Parameter p : paramTypeList) {
            TypeNode typeNode = p.getType();
                Type type = typeNode.typeCheck(c);
                expectedTypeString.append(type.toString()).append(" ");
        }
        
        // actual
        for (Expression e : argList) {
            Type exprType = e.typeCheck(c);
            actualTypeString.append(exprType.toString()).append(" ");
        }
 
        if (!expectedTypeString.toString().equals(actualTypeString.toString())) {
            throw new SemanticError(id.getPos()+
                    " error: expected arg types: "+
                    expectedTypeString+" but found "+actualTypeString);
        }

        UnitType unitType = new UnitType();
        setNodeType(unitType);
        return unitType;
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
        c.printAtom(id.getName());
        for (Expression e : argList) {
            e.prettyPrint(c);
        }
        c.endList();
        return c;
    }

    public String getAbiName() {
        return abiName;
    }

    @Override
    public IRSeq translate(IRNodeFactory factory) {
        ArrayList<IRExpr> irExprs = new ArrayList<>();
        ArrayList<IRStmt> sideEffects = new ArrayList<>();
        for (Expression arg : argList) {
            IRExpr irExpr = arg.translate(factory);
            if (irExpr.isShortCircuit()) {
                IRESeq eseq = (IRESeq) irExpr;
                IRTemp boolValTemp = factory.generateFreshTemp();
                String endLabel = factory.generateFreshEndLabel();
                IRSeq seq = IRSeq.getShortCircuitSeq(eseq.stmt(), boolValTemp, irExpr,
                        endLabel, factory.getCurrentLabel());
                sideEffects.addAll(seq.stmts());
                irExprs.add(boolValTemp);
            } else {
                irExprs.add(irExpr);
            }
        }
        return factory.IRSeq(
                factory.IRSeq(sideEffects),
                factory.IRCallStmt(factory.IRName(abiName), (long) 0,
                        irExprs)
        );
    }

}