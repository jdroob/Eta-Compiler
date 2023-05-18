package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRData;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.types.UnitType;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

/**
 * AST Node for global assignments. It extends global declarations
 * since it also has an identifier and a type, but also has a
 * literal on the right side it gets assigned to.
 */
public class GlobalDeclareAssign extends GlobalDeclaration {

    /**
     * Literal on the right hand side of the declaration. Will be an integer,
     * character, string, or boolean enforced by the grammar
     */
    private Expression literal;

    /**
     * Constructor for creating a new global assignment statement
     * @param param - id and type to assign literal to
     * @param literal - expression literal to assign
     * @param pos - position of declaration in program file
     */
    public GlobalDeclareAssign(Parameter param, Expression literal, String pos) {
        super(param, pos);
        this.literal = literal;
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
        Type literalType = literal.typeCheck(c);
        Parameter param = getParameter();
        TypeNode paramTypeNode = param.getType();

        Type paramType = paramTypeNode.typeCheck(c);

        if (!paramType.equals(literalType)) {
            throw new SemanticError(literal.getPos()+
                    " error: type mismatch between "
                    +paramType.toString()+" and "+literalType.toString());
        }

        if (literal.getStoredType().getType().contains("array")) {
            throw new SemanticError(literal.getPos()+
                    " error: cannot declare an array globally");
        }

        UnitType unitType = new UnitType();
        setNodeType(unitType);
        return unitType;
    }

    @Override
    public ArrayList<IRData> translate(IRNodeFactory factory) {
        /**
         * add getLiteralValue function in expression - only implement
         * it in IntegerLiteral, BoolLiteral, CharLiteral, and String which
         * gets the long value of data - set this in type check function, and
         * then we can use it here for IRData's constructor
         */
        ArrayList<IRData> dataVals = new ArrayList<>();
        dataVals.add(new IRData("_"+getParameter().getName(), literal.getLiteralValue()));
        return dataVals;
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
        getParameter().prettyPrint(c);
        literal.prettyPrint(c);
        c.endList();
        return c;
    }
}