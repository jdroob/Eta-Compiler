package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRData;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.
        CodeWriterSExpPrinter;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.types.UnitType;

import java.util.ArrayList;

/**
 * AST Node for global multi assignments. It can be used anywhere where a
 * definition can be used.
 */
public class GlobalMultiDeclareAssign extends Global {

    /**
     * List of literals on the right hand side of multi assignment. List of
     * parameters on the left hand side of multi assignment
     */
    private ArrayList<Expression> literals;
    private ArrayList<Parameter> parameters;

    /**
     * Constructor to create a new global multi assignment object
     * @param parameters - list of parameters on left hand side of assignment
     * @param literals - list of literals on right hand side of assignment
     * @param pos - position of global multi declaration in program file
     */
    public GlobalMultiDeclareAssign(ArrayList<Parameter> parameters,
                                    ArrayList<Expression> literals, String pos) {
        super(pos);
        this.parameters = parameters;
        this.literals = literals;
    }

    /**
     * Function to type check an AST node in the tree.
     *
     * @param c - Context that represents the symbol table of storing variables
     * @return the type associated with the AST node after type checking
     * @throws SemanticError if the AST node does not type check
     */
    @Override
    public Type typeCheck(Context c) throws SemanticError{
        StringBuilder expectedTypeList = new StringBuilder();
        for (Parameter p : parameters) {
            TypeNode typeNode = p.getType();
            expectedTypeList.append(typeNode.toString()).append(" ");
        }

        StringBuilder actualTypeList = new StringBuilder();
        for (Expression e : literals) {
            Type exprType = e.typeCheck(c);
            actualTypeList.append(exprType.toString()).append(" ");
        }

        if (parameters.size() != literals.size() && literals.size() != 1) {
            throw new SemanticError(parameters.get(0).getPos()+
                    " error: expected argument types "
                    +expectedTypeList+" " +"but found "+actualTypeList);
        }

        UnitType unitType = new UnitType();
        setNodeType(unitType);
        return unitType;

    }

    public ArrayList<IRData> translate(IRNodeFactory factory) {
        /**
         * Same thing as in global declare assign (may need to change global
         * declare assigns type to arraylist too because of how we are
         * extracting them from generic definitions in program.
         */
        ArrayList<IRData> dataVals = new ArrayList<>();
        if (literals.size() == 1) {
            for (Parameter parameter : parameters) {
                dataVals.add(new IRData("_"+parameter.getName(),
                        literals.get(0).getLiteralValue()));
            }
            return dataVals;
        }

        for (int i=0;i<literals.size();i++) {
            dataVals.add(new IRData("_"+parameters.get(i).getName(),
                    literals.get(i).getLiteralValue()));
        }

        return dataVals;
    }

    /**
     * Method for adding this definition to context before type checking it.
     * This is done to allow for programs to use this definition before
     * it is declared
     * @param c - context symbol table where this definition will be added
     * @throws SemanticError when there is an error putting definition into the
     * context
     */
    @Override
    public void add(Context c) throws SemanticError {
        for (Parameter p : parameters) {
            TypeNode typeNode = p.getType();
            	Type type = typeNode.typeCheck(c);
                c.put(p.getId(), type);
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
        c.startUnifiedList();
        c.printAtom("=");
        c.startList();
        for (Parameter p : parameters) {
            p.prettyPrint(c);
        }
        c.endList();
        for (Expression n : literals) {
            n.prettyPrint(c);
        }
        c.endList();
        return c;
    }

    public boolean isMethod() {
        return false;
    }

}