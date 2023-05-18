package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.types.RecordType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.types.UnitType;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.Optional;

/**
 * AST Node for a local declaration object. It consists of an identifier and
 * a type without any assignment
 */
public class LocalDeclaration extends SingleLine {

    /**
     * Parameter consisting of an identifier and a type for local declaration
     */
    private Parameter parameter;

    /**
     * Constructor for creating a local declaration object
     * 
     * @param parameter - parameter that is being declared
     * @param pos       - position of local declaration in program file
     */
    public LocalDeclaration(Parameter parameter, String pos) {
        super(pos);
        this.parameter = parameter;
    }

    /**
     *
     * @return parameter of local declaration
     */
    public Parameter getParameter() {
        return parameter;
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
        Type type = parameter.typeCheck(c);
        c.put(parameter.getId(), type);
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
        parameter.prettyPrint(c);
        return c;
    }

    @Override
    public IRStmt translate(IRNodeFactory factory) {
        String varType = parameter.getStoredType().toString();
        if (varType.contains("array array")) {
            return adTranslate(factory, parameter, true, Optional.empty());
        } else if (varType.contains("array")) {
            return adTranslate(factory, parameter, false, Optional.empty());
        }

        else {
            IRExpr leftSideExpr = getParameter().translate(factory);
            if (getParameter().getStoredType() instanceof RecordType) {
                return factory.IRMove(leftSideExpr, factory.IRMem(factory.IRConst(0)));
            }
            return factory.IRMove(leftSideExpr, factory.IRConst(0));
        }

    }

}