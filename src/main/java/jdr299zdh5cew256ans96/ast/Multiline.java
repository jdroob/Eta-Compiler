package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.
        CodeWriterSExpPrinter;

/**
 * Abstract class to represent multi line statements. There are multiple
 * types of multi line statements, so a multi line statement itself can never
 * be instantiated directly
 */
public abstract class Multiline extends Statement {

    /**
     * Guard expression in multi line statement and body within multi line
     * statement
     */
    private Expression expression;
    private Statement body;

    /**
     * Constructor for creating a multi line statement
     * @param expression - guard expression
     * @param body - body of multi line statement
     * @param pos - position of where multi line statement is in program file
     */
    public Multiline(Expression expression, Statement body, String pos) {
        super(pos);
        this.expression = expression;
        this.body = body;
    }

    /**
     *
     * @return guard expression of multi line statement
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     *
     * @return body of multi line statement
     */
    public Statement getBody() {
        return body;
    }

    /**
     *
     * @return which multi line statement (if or while)
     */
    public abstract String getName();

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
        c.printAtom(getName());
        expression.prettyPrint(c);
        body.prettyPrint(c);
        c.endList();
        return c;
    }

}