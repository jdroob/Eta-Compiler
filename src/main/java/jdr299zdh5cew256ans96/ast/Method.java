package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRFuncDecl;
import main.java.jdr299zdh5cew256ans96.ir.IRMove;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRSeq;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;
import main.java.jdr299zdh5cew256ans96.ir.IRLabel;
import main.java.jdr299zdh5cew256ans96.types.FuncType;
import main.java.jdr299zdh5cew256ans96.types.ReturnType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.types.VoidType;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

/**
 * AST Node for method definitions. Can be used anywhere where a definition
 * can be used
 */
public class Method extends Definition {

    /**
     * Function name identifier, parameter list for argument declarations,
     * node type list for return types, and a block of statements for the
     * inside of the method
     */
    private Identifier identifier; 
    private ArrayList<Parameter> parameters;
    private ArrayList<TypeNode> return_type;
    private Block block;
    private String abiName;

    /**
     * Constructor for creating a method object with all properties
     * @param id - function name
     * @param p - argument list
     * @param t - return type list
     * @param b - block of statements
     * @param pos - position of method in program file
     */
    public Method(String id, ArrayList<Parameter> p, ArrayList<TypeNode> t,
                  Block b, String pos) {
        super(pos);
        identifier = new Identifier(id, pos);
        parameters = p;
        return_type = t;
        block = b;
    }

    /**
     * Constructor for creating a method object without a block (for interfaces)
     * @param id - function name
     * @param p - argument list
     * @param t - return type list
     * @param pos - position of method in program file
     */
    public Method(String id, ArrayList<Parameter> p, ArrayList<TypeNode> t,
                  String pos) {
        super(pos);
        identifier = new Identifier(id, pos);
        parameters = p;
        return_type = t;
        block = null;
    }

    /**
     * Constructor for creating a method object that doesn't return anything
     * @param id - function name
     * @param p - argument list
     * @param b - block of statements
     * @param pos - position of method in program file
     */
    public Method(String id, ArrayList<Parameter> p, Block b, String pos) {
        super(pos);
        identifier = new Identifier(id, pos);
        parameters = p;
        block = b;
        return_type = new ArrayList<>();
    }

    /**
     * Constructor for creating a method object that doesn't return anything
     * and doesn't have block (procedure interfaces)
     * @param id - function name
     * @param p - argument list
     * @param pos - position of method in program file
     */
    public Method(String id, ArrayList<Parameter> p, String pos) {
        super(pos);
        identifier = new Identifier(id, pos);
        parameters = p;
        return_type = new ArrayList<>();
        block = null;
    }

    public String getFuncName() {
        return identifier.getName();
    }

    public String getAbiName() {
        return abiName;
    }

    /**
     *
     * @return argument list that the method takes in
     */
    public ArrayList<Parameter> getArgs() {
        return parameters;
    }

    /**
     *
     * @return return type list that the method returns
     */
    public ArrayList<TypeNode> getReturnTypes() {
        return return_type;
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
        c.push();
        for (Parameter p : parameters) {
            TypeNode typeNode = p.getType();
            Type type = typeNode.typeCheck(c);
            c.put(p.getId(),type);
        }

        // expected
        StringBuilder expectedTypeString = new StringBuilder();
        for (TypeNode returnTypeNode : return_type) {
            Type actualReturnType = returnTypeNode.typeCheck(c);
            expectedTypeString.append(actualReturnType.toString()).append(" ");
        }

        Type blockReturnTypes = block.typeCheck(c);
        if (blockReturnTypes.equalsStr("void")) {
            VoidType voidType = (VoidType) blockReturnTypes;
            ReturnType returnType = voidType.getReturns();
            ArrayList<Type> types = returnType.getReturnTypes();

            StringBuilder actualTypeString = new StringBuilder();
            
            // actual
            for (Type t : types) {
                actualTypeString.append(t.toString()).append(" ");
            }
    
            if (!expectedTypeString.toString().equals(actualTypeString.toString())) {
                throw new SemanticError(getPos()+
                        " error: expected return type " +
                        ""+expectedTypeString+" but found "+actualTypeString);
            }
        }

        if (blockReturnTypes.equalsStr("unit")) {
            if (return_type.size() != 0) {
                throw new SemanticError(getPos()+
                        " error: expected return type "
                        +expectedTypeString+" but found none");
            }
        }
        c.pop();

        VoidType voidType = new VoidType();
        setNodeType(voidType);
        return voidType;
    }

    public IRFuncDecl translate(IRNodeFactory factory) {
        ArrayList<IRStmt> moveArgStmts = new ArrayList<>();

        // Add label to beginning of each IRFuncDecl
        // for Basic Block purposes
        IRLabel funcNameLabel = new IRLabel("l_"+getAbiName());
        moveArgStmts.add(funcNameLabel);

        int i = 1;
        for (Parameter parameter : parameters) {
            IRTemp dest = new IRTemp(parameter.getName());
            IRTemp argName = new IRTemp("_ARG"+i);
            IRMove move = new IRMove(dest, argName);
            moveArgStmts.add(move);
            i++;
        }

        // generate ABI method name
        String abiMethod = "_I"+identifier.getName()+"_";
        if (return_type.isEmpty()) {
            abiMethod += "p";
        } else if (return_type.size() == 1) {
            abiMethod += return_type.get(0).getABIName();
        } else {
            abiMethod += "t"+return_type.size();
            for (TypeNode type : return_type) {
                abiMethod += type.getABIName();
            }
        }

        for (Parameter parameter : parameters) {
            abiMethod += parameter.getType().getABIName();
        }

        IRSeq restOfBlock = block.translate(factory);
        moveArgStmts.addAll(restOfBlock.stmts());
        if (return_type.isEmpty()) {
            moveArgStmts.add(factory.IRReturn());
        }
        return factory.IRFuncDecl(abiMethod,
                factory.IRSeq(moveArgStmts));
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
        ArrayList<Type> returnTypes = new ArrayList<>();
        for (TypeNode typeNode: return_type) {
            returnTypes.add(typeNode.typeCheck(c));
        }
        FuncType funcType = new FuncType(identifier, parameters, returnTypes);
        abiName = funcType.getABIName();
        c.put(identifier,funcType);
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
        c.printAtom(identifier.getName());

        c.startList();
        for (Parameter p : parameters) {
            p.prettyPrint(c);
        }
        c.endList();

        c.startList();
        for (TypeNode t : return_type) {
            t.prettyPrint(c);
        }
        c.endList();
        if (block != null){
            block.prettyPrint(c);
        }
        c.endList();
        return c;
    }

    /**
     *
     * @return function name
     */
    public Identifier getId() {
        return identifier;
    }

    public boolean isMethod() {
        return true;
    }

}
